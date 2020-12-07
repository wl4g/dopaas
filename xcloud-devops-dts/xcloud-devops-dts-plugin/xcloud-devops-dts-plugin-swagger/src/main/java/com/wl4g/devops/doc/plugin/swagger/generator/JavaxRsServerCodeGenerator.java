package com.wl4g.devops.doc.plugin.swagger.generator;

import com.alibaba.fastjson.JSON;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.wl4g.devops.doc.plugin.swagger.codegen.JavaxRsServerCodeGen;
import com.wl4g.devops.doc.plugin.swagger.entity.*;

import io.swagger.codegen.DefaultGenerator;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;

import static com.wl4g.devops.doc.plugin.swagger.constant.CodeGenConstant.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class JavaxRsServerCodeGenerator extends DefaultGenerator {

	private final Map<String, String> modelNameMap = new ConcurrentHashMap<>(16);

	private final Map<String, PathEntity> pathEntityMap = new ConcurrentHashMap<>(32);

	private final Set<ImportClassEntity> apiImportClassList = new HashSet<>(16);

	@Override
	public List<File> generate() {

		// 清理缓存
		this.clearCache();

		// 生成model类
		final Map<String, Model> definitions = swagger.getDefinitions();
		definitions.forEach(this::generateModel);

		// 存在不同path中有相同api类的场景, 先进行合并
		final Map<String, Path> paths = swagger.getPaths();
		paths.forEach(this::generatePathEntity);

		System.out.println("pathEntityMap: --------------------" + JSON.toJSONString(pathEntityMap));

		// 生成api类
		this.pathEntityMap.forEach((key, value) -> this.writeToFile(value, key + "Api", API_TEMPLATE_FILE));

		// 生成service类
		this.pathEntityMap.forEach((key, value) -> this.writeToFile(value, key + "Service", SERVICE_TEMPLATE_FILE));

		return new ArrayList<>();
	}

	private void clearCache() {
		modelNameMap.clear();
		pathEntityMap.clear();
		apiImportClassList.clear();
	}

	private void generateModel(String key, Model model) {
		JavaxRsServerCodeGen codeGenCfg = (JavaxRsServerCodeGen) config;

		final ModelClassEntity modelClassEntity = ModelClassEntity.builder().modelPackage(codeGenCfg.modelPackage())
				.className(key).modelProps(this.getModelProps(model)).build();

		// 缓存model全类名
		modelNameMap.put(key, codeGenCfg.modelPackage() + "." + key);

		writeToFile(modelClassEntity, key, MODEL_TEMPLATE_FILE);
	}

	private void generatePathEntity(String key, Path path) {
		final String className = this.getClassName(path);
		if (Objects.isNull(className)) {
			return;
		}

		final JavaxRsServerCodeGen codeGenCfg = (JavaxRsServerCodeGen) config;
		final String[] splitKeys = key.split("/\\{");
		String methodPath = null;
		if (splitKeys.length == 2) {
			methodPath = splitKeys[1];
		}

		final PathEntity pathEntity = PathEntity.builder().apiPackage(codeGenCfg.apiPackage())
				.servicePackage(codeGenCfg.servicePackage()).className(className).urlPath(splitKeys[0])
				.pathGetEntity(getPathOperationProps(path.getGet(), methodPath))
				.pathDeleteEntity(getPathOperationProps(path.getDelete(), methodPath))
				.pathPostEntity(getPathOperationProps(path.getPost(), methodPath))
				.pathPutEntity(getPathOperationProps(path.getPut(), methodPath)).importPackageList(apiImportClassList).build();

		pathEntityMap.merge(className, pathEntity, (oldValue, newValue) -> {
			// 合并importList
			Set<ImportClassEntity> oldImportSet = oldValue.getImportPackageList();
			newValue.getImportPackageList().addAll(oldImportSet);

			// 整合最短路径
			if (oldValue.getUrlPath().length() < newValue.getUrlPath().length()) {
				newValue.setUrlPath(oldValue.getUrlPath());
			}

			if (Objects.isNull(newValue.getPathGetEntity())) {
				newValue.setPathGetEntity(oldValue.getPathGetEntity());
			}
			if (Objects.isNull(newValue.getPathDeleteEntity())) {
				newValue.setPathDeleteEntity(oldValue.getPathDeleteEntity());
			}
			if (Objects.isNull(newValue.getPathPostEntity())) {
				newValue.setPathPostEntity(oldValue.getPathPostEntity());
			}
			if (Objects.isNull(newValue.getPathPutEntity())) {
				newValue.setPathPutEntity(oldValue.getPathPutEntity());
			}
			return newValue;
		});
	}

	private List<ModelPropEntity> getModelProps(Model model) {
		JavaxRsServerCodeGen codeGenCfg = (JavaxRsServerCodeGen) config;
		return model.getProperties().entrySet().stream()
				.map(entrySet -> ModelPropEntity.builder().paramName(entrySet.getKey())
						.modelParamDesc(ModelParamDescEntity.builder().required(entrySet.getValue().getRequired())
								.type(codeGenCfg.getSwaggerType(entrySet.getValue())).build())
						.build())
				.collect(Collectors.toList());
	}

	private String getClassName(Path path) {
		if (Objects.nonNull(path.getGet())) {
			Operation operation = path.getGet();
			return operation.getTags().get(0);
		}

		if (Objects.nonNull(path.getDelete())) {
			Operation operation = path.getDelete();
			return operation.getTags().get(0);
		}

		if (Objects.nonNull(path.getPost())) {
			Operation operation = path.getPost();
			return operation.getTags().get(0);
		}

		if (Objects.nonNull(path.getPut())) {
			Operation operation = path.getPut();
			return operation.getTags().get(0);
		}
		return null;
	}

	private PathOperationEntity getPathOperationProps(Operation operation, String urlPath) {
		return Optional.ofNullable(operation)
				.map(opera -> PathOperationEntity.builder().methodName(opera.getOperationId())
						.urlPath(Objects.nonNull(urlPath) ? "/{" + urlPath : null)
						.responseType(getResponseType(opera.getResponses())).params(getPathParams(opera.getParameters())).build())
				.orElse(null);
	}

	private List<PathParamsEntity> getPathParams(List<Parameter> params) {
		if (Objects.isNull(params) || params.isEmpty()) {
			return new ArrayList<>();
		}

		// 存在多个入参的场景, mustache处理逻辑有限, 只能特殊处理
		String paramName;
		Map<String, String> paramNameMap = new HashMap<>(params.size());
		for (int i = 0; i < params.size(); i++) {
			paramName = params.get(i).getName();
			if (i != params.size() - 1) {
				paramNameMap.put(paramName, paramName + ", ");
			} else {
				paramNameMap.put(paramName, paramName);
			}
		}

		// get方法的参数按照协议只能是基础类型
		return params.stream().map(param -> PathParamsEntity.builder().paramName(paramNameMap.get(param.getName()))
				.paramType(getParamType(param)).build()).collect(Collectors.toList());
	}

	private String getResponseType(Map<String, Response> responseMap) {
		Optional<Map.Entry<String, Response>> entryOptional = responseMap.entrySet().stream()
				.filter(entrySet -> entrySet.getKey().equals(String.valueOf(HttpStatus.SC_OK))).findAny();

		final Property scheme = entryOptional.map(Map.Entry::getValue).map(Response::getSchema).orElse(new ObjectProperty());

		if (scheme.getType().equals("ref")) {
			String className = ((RefProperty) scheme).getSimpleRef();
			apiImportClassList.add(ImportClassEntity.builder().className(modelNameMap.get(className)).build());
			return className;
		} else {
			JavaxRsServerCodeGen codeGenCfg = (JavaxRsServerCodeGen) config;
			return codeGenCfg.getSwaggerType(scheme);
		}
	}

	private String getParamType(Parameter parameter) {
		final String parameterIn = parameter.getIn();
		if (parameterIn.equals("body")) {
			final Model schema = ((BodyParameter) parameter).getSchema();
			String className = ((RefModel) schema).getSimpleRef();
			apiImportClassList.add(ImportClassEntity.builder().className(modelNameMap.get(className)).build());
			return className;
		} else {
			final String paramType = ((QueryParameter) parameter).getType();
			return config.typeMapping().get(paramType);
		}
	}

	private String getOutputFilePath(String fileName, String templateName) {
		JavaxRsServerCodeGen codeGenCfg = (JavaxRsServerCodeGen) config;
		if (API_TEMPLATE_FILE.equals(templateName)) {
			return codeGenCfg.outputFolder() + File.separator
					+ codeGenCfg.apiPackage().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator
					+ fileName + ".java";
		} else if (MODEL_TEMPLATE_FILE.equals(templateName)) {
			return codeGenCfg.outputFolder() + File.separator
					+ codeGenCfg.modelPackage().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator
					+ fileName + ".java";
		} else {
			return codeGenCfg.outputFolder() + File.separator
					+ codeGenCfg.servicePackage().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator
					+ fileName + ".java";
		}
	}

	private String getTempFileString(String fileName) {
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
		if (Objects.isNull(resourceAsStream)) {
			return EMPTY_STRING;
		}

		return new BufferedReader(new InputStreamReader(resourceAsStream)).lines()
				.collect(Collectors.joining(System.lineSeparator()));
	}

	private void writeToFile(Object context, String fileName, String templateName) {
		try {
			// 获取模板信息
			String templateFileString = this.getTempFileString(templateName);

			// 生成模板
			Template template = Mustache.compiler().compile(templateFileString);

			// 解析模板
			String executeResult = template.execute(context);

			// 生成java类
			String outputPath = this.getOutputFilePath(fileName, templateName);
			FileUtils.writeStringToFile(new File(outputPath), executeResult, StandardCharsets.UTF_8, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}