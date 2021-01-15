package com.wl4g.devops.doc.service.formater;

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
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.wl4g.component.common.view.Freemarkers.createDefault;
import static com.wl4g.component.common.view.Freemarkers.renderingTemplateToString;
import static java.util.Collections.singletonList;

/**
 * md文件转html文件, 并组装成可展示的html文件集合
 * @author vjay
 * @date 2021-01-13 15:15:00
 */
@Component
public class Md2Html {

    final private static String TEMPLATE_PATH = "/template";
    final private static String MD_PATH = "/md";

    final private static String MATCH_START = "##[";
    final private static String MATCH_END = "]##";

    final private static String REQUEST = "Request";
    final private static String RESPONSE = "Response";

    /** Default freemarker {@link Configuration} */
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

    public void format(){
        //TODO 获取template文件
        String templatePath = docProperties.getBasePath()+TEMPLATE_PATH;


        //TODO 获取md文件
        String mdPath = docProperties.getBasePath()+MD_PATH;


        //TODO 获取html文件，注意：template是有目录结构的，md文件也有目录结构，如何渲染待定

        //TODO 将md转html 同时 将html渲染进template




    }


    /**
     * Step 2 : template渲染，内容是Step1生成的html，Step1 和Step 2 同步进行
     */
    public String formatTemplate(String provider) throws Exception {


        List<MdResource> mdResources = mdLocator.locate(provider);

        for(MdResource mdResource : mdResources){
            String md2html = FlexmarkUtil.md2html(mdResource.getContentAsString());
            TemplateFormatModel templateFormatModels = new TemplateFormatModel();
            templateFormatModels.setPath(mdResource.getRawFilename());
            templateFormatModels.setMdHtml(md2html);
        }



        List<TemplateResource> templateResources = genTemplateLocator.locate(provider);
        for(TemplateResource res : templateResources){
            if(res.isRender()){
                if(res.isForeachMds()){
                    Template template = new Template(res.getShortFilename(), res.getContentAsString(), defaultGenConfigurer);

                    for(MdResource mdResource : mdResources){
                        String md2html = FlexmarkUtil.md2html(mdResource.getContentAsString());
                        TemplateFormatModel templateFormatModel = new TemplateFormatModel();
                        templateFormatModel.setPath(mdResource.getRawFilename());
                        templateFormatModel.setMdHtml(md2html);

                        String renderedString = renderingTemplateToString(template, templateFormatModel);

                        //TODO output: write output file

                    }


                }else{

                }
            }else{

            }

        }



        return "";
    }


    /**
     * Step 3 : Step2渲染完成后，将特殊标记渲染转话成template格式
     */
    public void replateApiToTemplate(){



        String filling = filling("1");
    }

    /**
     * Step 4 : Step3生成了template的格式后，再进行多一次渲染，把api的信息渲染进去
     */
    public String formatApiTemplate(){



        return "";
    }


    //======================================================================



    /**
     * 向html文件填充api信息
     */
    private String filling(String html){
        StringBuilder sb = new StringBuilder(html);

        while(sb.indexOf(MATCH_START)>=0){
            int i = sb.indexOf(MATCH_START);
            int j = sb.indexOf(MATCH_END, i);
            String apiId = sb.substring(i + MATCH_START.length(), j);

            // get api md
            String apiMd = buildTemplate(apiId);

            // replace
            sb.delete(i,j+MATCH_END.length());
            sb.insert(i,apiMd);
        }

        return sb.toString();
    }


    /**
     * 把api转成template
     */
    private String buildTemplate(String apiId){
        EnterpriseApi enterpriseApi = enterpriseApiService.detail(Long.valueOf(apiId));

        //分类
        List<EnterpriseApiProperties> request = new ArrayList();
        List<EnterpriseApiProperties> response = new ArrayList();
        for(EnterpriseApiProperties property : enterpriseApi.getProperties()){
            if(REQUEST.equals(property.getScope())){
                request.add(property);
            }
            if(RESPONSE.equals(property.getScope())){
                response.add(property);
            }
        }



        return "";
    }

    /*private String api2Md(String apiId){

        //TODO find api by id
        EnterpriseApi enterpriseApi = new EnterpriseApi();




        return assemblyApi(enterpriseApi);
    }

    private String assemblyApi(EnterpriseApi enterpriseApi){
        StringBuilder stringBuilder = new StringBuilder();

        List<EnterpriseApiProperties> properties = enterpriseApi.getProperties();
        //接口名
        stringBuilder.append("# ").append(enterpriseApi.getName()).append("\n");
        //接口id
        stringBuilder.append("接口ID: ").append(enterpriseApi.getId()).append("\n");
        //接口类型
        stringBuilder.append("类型: ").append(enterpriseApi.getMethod()).append("\n");


        //分类
        List<EnterpriseApiProperties> request = new ArrayList();
        List<EnterpriseApiProperties> response = new ArrayList();
        for(EnterpriseApiProperties property : properties){
            if(REQUEST.equals(property.getScope())){
                request.add(property);
            }
            if(RESPONSE.equals(property.getScope())){
                response.add(property);
            }
        }
        //请求参数
        stringBuilder.append("*请求参数*").append("\n");
        //TODO 考虑下嵌套属性时怎么办
        stringBuilder.append("| 名称 | 必选 | 类型 | 初始值 | 简介 |").append("\n");
        stringBuilder.append("| ---- | ---- | ---- | ---- | ---- |").append("\n");
        assemblyProperties(stringBuilder,request,0);

        //响应内容
        stringBuilder.append("*响应内容*").append("\n");
        stringBuilder.append("| 名称 | 必选 | 类型 | 初始值 | 简介 |").append("\n");
        stringBuilder.append("| ---- | ---- | ---- | ---- | ---- |").append("\n");
        assemblyProperties(stringBuilder,response,0);

        return stringBuilder.toString();
    }

    private void assemblyProperties(StringBuilder stringBuilder, List<EnterpriseApiProperties> properties, int deep){
        for(EnterpriseApiProperties property : properties){
            if(REQUEST.equals(property.getScope())){

                stringBuilder.append("| ");
                for(int i = 0; i<deep;i++){// 暂时通过空格这种方式体现层级关系
                    stringBuilder.append("&nbsp &nbsp");
                }
                stringBuilder.append(property.getName()).append(" |");
                stringBuilder.append(property.getRequired()).append(" |");
                stringBuilder.append(property.getType()).append(" |");
                stringBuilder.append(property.getValue()).append(" |");
                stringBuilder.append(property.getDescription()).append(" |");

                if(!CollectionUtils.isEmpty(property.getChildren())){
                    assemblyProperties(stringBuilder,property.getChildren(),deep+1);
                }
            }
        }
    }*/

}
