package com.wl4g.devops.doc.service.formater;

import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.md.FlexmarkUtil;
import com.wl4g.devops.common.bean.doc.EnterpriseApi;
import com.wl4g.devops.common.bean.doc.EnterpriseApiProperties;
import com.wl4g.devops.doc.config.DocProperties;
import com.wl4g.devops.doc.model.TemplateFormatModel;
import com.wl4g.devops.doc.service.EnterpriseApiService;
import com.wl4g.devops.doc.service.md.MdLocator;
import com.wl4g.devops.doc.service.md.MdResource;
import com.wl4g.devops.doc.service.template.GenTemplateLocator;
import com.wl4g.devops.doc.service.template.TemplateResource;
import com.wl4g.devops.doc.util.ResourceBundleUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.component.common.view.Freemarkers.createDefault;
import static com.wl4g.component.common.view.Freemarkers.renderingTemplateToString;
import static java.util.Collections.singletonList;

/**
 * md文件转html文件, 并组装成可展示的html文件集合
 *
 * @author vjay
 * @date 2021-01-13 15:15:00
 */
@Component
public class Md2Html {

    final private static String TEMPLATE_PATH = "/template";
    final private static String MD_PATH = "/md";
    final private static String HTML_OUTPUT_PATH = "/output";

    final private static String MATCH_START = "##[";
    final private static String MATCH_END = "]##";

    final private static String REQUEST = "Request";
    final private static String RESPONSE = "Response";

    /**
     * Default freemarker {@link Configuration}
     */
    private static final Configuration defaultGenConfigurer = createDefault().withVersion(Configuration.VERSION_2_3_27)
            .withTemplateLoaders(singletonList(new StringTemplateLoader())).build();

    @Autowired
    private DocProperties docProperties;

    @Autowired
    private EnterpriseApiService enterpriseApiService;

    @Autowired
    private GenTemplateLocator genTemplateLocator;

    @Autowired
    private MdLocator mdLocator;


    /**
     * 遍历模版和md文件，md转成html后渲染进template: 得到的文件是带有api标记的html文件
     */
    public String formatTemplate(String provider) throws Exception {

        List<MdResource> mdResources = mdLocator.locate(provider);

        // 暂时使用这种规则的输出路径地址
        String baseWritePath = splicePath(docProperties.getBasePath(), HTML_OUTPUT_PATH, provider, String.valueOf(System.currentTimeMillis()));

        List<TemplateResource> templateResources = genTemplateLocator.locate(provider);
        for (TemplateResource res : templateResources) {
            if (res.isRender()) {
                if (res.isForeachMds()) {
                    Template template = new Template(res.getShortFilename(), res.getContentAsString(), defaultGenConfigurer);

                    for (MdResource mdResource : mdResources) {
                        String md2html = FlexmarkUtil.md2html(mdResource.getContentAsString());
                        TemplateFormatModel templateFormatModel = new TemplateFormatModel();
                        templateFormatModel.setPath(mdResource.getRawFilename());


                        //TODO 在这里可以直接把api信息渲染成template，节省后续遍历次数

                        String filling = filling(md2html);

                        templateFormatModel.setMdHtml(filling);

                        String renderedString = renderingTemplateToString(template, templateFormatModel);

                        // output: write output file
                        File writeFile = new File(splicePath(baseWritePath, res.getRawFilename()));
                        FileIOUtils.writeFile(writeFile, renderedString, false);

                    }

                } else {
                    // output: write output file
                    File writeFile = new File(splicePath(baseWritePath, res.getRawFilename()));
                    FileIOUtils.writeFile(writeFile, res.getContent(), false);
                }
            } else {
                // output: write output file
                File writeFile = new File(splicePath(baseWritePath, res.getRawFilename()));
                FileIOUtils.writeFile(writeFile, res.getContent(), false);
            }

        }

        return baseWritePath;
    }


    //======================================================================


    /**
     * 向html文件填充api信息
     */
    private String filling(String html) throws IOException, TemplateException {
        if(html.contains("MATCH_START")){
            String macro = ResourceBundleUtil.readResource(Md2Html.class, "template", "macro-property.ftl", true);
            html = macro + "\n" + html;
        }
        StringBuilder sb = new StringBuilder(html);

        String apiTemplate = ResourceBundleUtil.readResource(Md2Html.class, "template", "api-info.ftl", true);
        while (sb.indexOf(MATCH_START) >= 0) {
            int i = sb.indexOf(MATCH_START);
            int j = sb.indexOf(MATCH_END, i);
            String apiId = sb.substring(i + MATCH_START.length(), j);

            // get api md
            String apiMd = buildTemplate(apiTemplate, apiId);

            // replace
            sb.delete(i, j + MATCH_END.length());
            sb.insert(i, apiMd);
        }

        return sb.toString();
    }


    /**
     * 把api转成template
     */
    private String buildTemplate(String apiTemplate, String apiId) throws IOException, TemplateException {
        EnterpriseApi enterpriseApi = enterpriseApiService.detail(Long.valueOf(apiId));

        //分类
        List<EnterpriseApiProperties> request = new ArrayList();
        List<EnterpriseApiProperties> response = new ArrayList();
        for (EnterpriseApiProperties property : enterpriseApi.getProperties()) {
            if (REQUEST.equals(property.getScope())) {
                request.add(property);
            }
            if (RESPONSE.equals(property.getScope())) {
                response.add(property);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("enterpriseApi",enterpriseApi);
        map.put("requestProperties",request);
        map.put("responseProperties",response);

        Template template = new Template(apiId, apiTemplate, defaultGenConfigurer);

        return renderingTemplateToString(template, map);
    }

    private String splicePath(String... paths) {

        if (null == paths || paths.length <= 0) {
            return null;
        }

        if (paths.length == 1) {
            return paths[0];
        }

        StringBuilder path = new StringBuilder(paths[0]);
        for (int i = 1; i < paths.length; i++) {

            if (!paths[i].startsWith("/")) {
                path.append("/").append(paths[i]);
            } else {
                path.append(paths[i]);
            }
        }
        return path.toString();
    }


}
