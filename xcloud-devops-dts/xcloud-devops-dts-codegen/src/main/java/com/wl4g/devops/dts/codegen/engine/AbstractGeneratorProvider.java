/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.dts.codegen.engine;

import com.wl4g.components.common.io.FileIOUtils;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.resource.StreamResource;
import com.wl4g.components.common.resource.resolver.ClassPathResourcePatternResolver;
import com.wl4g.components.core.utils.expression.SpelExpressions;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.core.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.naming.CSharpNamingSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.GolangNamingSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.JavaNamingSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.PythonNamingSpecs;
import com.wl4g.devops.dts.codegen.utils.FreemarkerUtils2;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

/**
 * {@link AbstractGeneratorProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public abstract class AbstractGeneratorProvider implements GeneratorProvider {

    protected final SmartLogger log = getLogger(getClass());

    final private static String tplSuffix = ".tpl";
    final private static String BASEPATH = "projects-template";
    final private static String ENTITY_NAME = "entity_name";

    final private static Map<String, List<TemplateFile>> templatesMap = new HashMap<>();
    final private static Configuration configuration = FreemarkerUtils2.defaultGenConfigurer;

    protected static final SpelExpressions defaultSpelExpressions = SpelExpressions.create(CSharpNamingSpecs.class,
            GolangNamingSpecs.class, JavaNamingSpecs.class, PythonNamingSpecs.class);

    /**
     * {@link GenerateContext}
     */
    protected final GenerateContext context;

    public AbstractGeneratorProvider(GenerateContext context) {
        this.context = notNullOf(context, "context");
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

    void genFileToLoacl(String templateName) throws Exception {
        GenProject genProject = context.getGenProject();
        File jobDir = context.getJobDir();
        genCode(templateName, genProject, jobDir.getAbsolutePath());
    }


    private static void genCode(String provider, GenProject genProject, String jobPath) throws IOException, TemplateException {
        List<TemplateFile> templateFiles = getTemplateFiles(provider);
        build(templateFiles, genProject, jobPath);
    }

    private static List<TemplateFile> getTemplateFiles(String provider) throws IOException {
        List<TemplateFile> templateFiles = templatesMap.get(provider);
        if (null == templateFiles) {
            templateFiles = new ArrayList<>();
            ClassPathResourcePatternResolver resolver = new ClassPathResourcePatternResolver();
            Set<StreamResource> resources = resolver.getResources("classpath:/" + BASEPATH + "/" + provider + "/**/*.*");
            for (StreamResource res : resources) {
                res.getURI();
                TemplateFile templateFile = wrapTemplateFile(res, provider);
                templateFiles.add(templateFile);
            }
            templatesMap.put(provider, templateFiles);
        }
        return templateFiles;
    }

    private static TemplateFile wrapTemplateFile(StreamResource res, String provider) throws IOException {
        String path = res.getURI().getPath();
        String splitStr = BASEPATH + "/" + provider + "/";
        int i = path.indexOf(splitStr);
        if (i >= 0) {
            path = path.substring(i + splitStr.length());
        }
        TemplateFile templateFile = new TemplateFile();
        templateFile.setRelativePath(path);
        templateFile.setFileName(res.getFilename());
        templateFile.setTpl(isTpl(res.getFilename()));
        templateFile.setFileContent(inputstream2Str(res.getInputStream()));
        return templateFile;
    }

    private static String inputstream2Str(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static boolean isTpl(String fileName) {
        return fileName.endsWith(tplSuffix);
    }

    private static void build(List<TemplateFile> templateFiles, GenProject genProject, String targetPath) throws IOException, TemplateException {
        for (GenTable genTable : genProject.getGenTables()) {
            genTable.setCompanyName(genProject.getCompanyName());
            genTable.setProjectName(genProject.getProjectName());
            genTable.setPackageName(genProject.getPackageName());
        }
        for (TemplateFile templateFile : templateFiles) {
            build(templateFile, genProject, targetPath);
        }
    }

    private static void build(TemplateFile templateFile, GenProject genProject, String targetBasePath) throws IOException, TemplateException {
        if (templateFile.getRelativePath().contains(ENTITY_NAME)) {//根据表来
            for (GenTable genTable : genProject.getGenTables()) {
                String targetPath = targetBasePath + "/" + parseTablePath(templateFile.getRelativePath(), genTable);
                Template template = new Template(templateFile.getFileName(), templateFile.getFileContent(), configuration);
                String fileContent = processTemplateIntoString(template, genTable);
                FileIOUtils.writeFile(new File(targetPath), fileContent, false);
            }
        } else if (templateFile.isTpl()) {
            String targetPath = targetBasePath + "/" + parsePackagePath(templateFile.getRelativePath(), genProject);
            Template template = new Template(templateFile.getFileName(), templateFile.getFileContent(), configuration);
            String fileContent = processTemplateIntoString(template, genProject);
            FileIOUtils.writeFile(new File(targetPath), fileContent, false);
        } else {
            //write static file
            String targetPath = targetBasePath + "/" + parsePackagePath(templateFile.getRelativePath(), genProject);
            FileIOUtils.writeFile(new File(targetPath), templateFile.getFileContent(), false);
        }
    }

    private static String parsePackagePath(String relativePath, GenProject genProject) {
        if (relativePath.endsWith(".ftl")) {
            relativePath = relativePath.substring(0, relativePath.length() - 4);
        }
        relativePath = "" + defaultSpelExpressions.resolve(relativePath, genProject);
        return relativePath;
    }

    private static String parseTablePath(String relativePath, GenTable genTable) {
        if (relativePath.endsWith(".ftl")) {
            relativePath = relativePath.substring(0, relativePath.length() - 4);
        }
        relativePath = "" + defaultSpelExpressions.resolve(relativePath, genTable);
        return relativePath;
    }

    @Setter
    @Getter
    private static class TemplateFile {

        private String relativePath;

        private String fileName;

        private String fileContent;

        private boolean isTpl;
    }

}