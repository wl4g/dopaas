package com.wl4g.devops.dts.codegen.utils;

import com.wl4g.components.common.io.FileIOUtils;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

/**
 * @author vjay
 * @date 2020-09-15 15:36:00
 */
@Deprecated
public class GenCodeUtils {

    final private static String tplSuffix = ".tpl";

    public static final String PACKAGE_NAME;
    final private static String COMPANY_NAME;
    final private static String PROJECT_NAME;
    final private static String ENTITY_NAME;
    final private static String ENTITY_NAME_LOWER_CASE;

    static {
        PACKAGE_NAME = "{{to_path(@package_name)}}";
        COMPANY_NAME = "{{@company_name}}";
        PROJECT_NAME = "{{@project_name}}";
        ENTITY_NAME = "{{foreach(@entity_name)}}";
        ENTITY_NAME_LOWER_CASE = "{{foreach_to_lowcase(@entity_name)}}";
    }

    final private static String[] ignoreFile = new String[]{".DS_Store"};

    final private static Map<String, List<TemplateFile>> templatesMap = new HashMap<>();

    //Configuration build = Freemarkers.createDefault().build();
    final private static Configuration configuration = configuration();

    public static void  genCode(String templateBasePath,GenProject genProject,String jobPath) throws IOException, TemplateException {
        List<TemplateFile> templateFiles = getTemplateFiles(templateBasePath);
        build(templateFiles,genProject, jobPath);
    }

    private static List<TemplateFile> getTemplateFiles(String path) throws IOException {
        List<TemplateFile> templateFiles = templatesMap.get(path);
        if(null==templateFiles){
            templateFiles = new ArrayList<>();
            traverseFolder(templateFiles, path, path);
            templatesMap.put(path,templateFiles);
        }
        return templateFiles;
    }


    private static void traverseFolder(List<TemplateFile> templateFiles,String path, String basePath) throws IOException {
        File file=new File(path);
        if(file.exists()){
            File[] files=file.listFiles();
            if(files == null){
                return;
            }
            for(File thisFile : files){
                if(thisFile.isDirectory()){
                    traverseFolder(templateFiles, thisFile.getAbsolutePath(), basePath);
                }else{
                    if(Arrays.asList(ignoreFile).contains(thisFile.getName())){//ignore file
                        continue;
                    }
                    TemplateFile templateFile = wrapTemplateFile(thisFile,basePath);
                    templateFiles.add(templateFile);
                    System.out.println(thisFile.getName());
                }
            }
        }else{
            throw new IOException("template path not found");
        }
    }


    private static TemplateFile wrapTemplateFile(File file,String basePath) throws IOException {
        String subPath = subFilePath(file.getAbsolutePath(), basePath);
        TemplateFile templateFile = new TemplateFile();
        templateFile.setRelativePath(subPath);
        templateFile.setFileName(file.getName());
        templateFile.setTpl(isTpl(file.getName()));
        templateFile.setFileContent(FileIOUtils.readFileToString(file,"UTF-8"));
        return templateFile;
    }

    private static boolean isTpl(String fileName){
        return fileName.endsWith(tplSuffix);
    }

    private static String subFilePath(String path, String basePath){
        if(path.startsWith(basePath)){
            return path.substring(basePath.length());
        }
        return path;
    }

    private static void build(List<TemplateFile> templateFiles, GenProject genProject, String targetPath) throws IOException, TemplateException {
        dealwithGenProject(genProject);
        for(TemplateFile templateFile : templateFiles){
            build(templateFile,genProject,targetPath);
        }
    }

    private static void dealwithGenProject(GenProject genProject){
        for(GenTable genTable : genProject.getGenTables()){
            genTable.setCompanyName(genProject.getCompanyName());
            genTable.setProjectName(genProject.getProjectName());
            genTable.setPackageName(genProject.getPackageName());
        }
    }

    private static void build(TemplateFile templateFile, GenProject genProject, String targetBasePath) throws IOException, TemplateException {
        if(templateFile.getRelativePath().contains(ENTITY_NAME)) {//根据表来
            for(GenTable genTable : genProject.getGenTables()){
                String targetPath = targetBasePath + "/" +parseTablePath(templateFile.getRelativePath(), genTable);
                Template template = new Template(templateFile.getFileName(), templateFile.getFileContent(), configuration);
                String fileContent = processTemplateIntoString(template, genTable);
                FileIOUtils.writeFile(new File(targetPath), fileContent,false);
            }
        }else if(templateFile.isTpl()){
            String targetPath = targetBasePath + "/" +parsePackagePath(templateFile.getRelativePath(), genProject);
            Template template = new Template(templateFile.getFileName(), templateFile.getFileContent(), configuration);
            String fileContent = processTemplateIntoString(template, genProject);
            FileIOUtils.writeFile(new File(targetPath), fileContent,false);
        }else{
            //write static file
            String targetPath = targetBasePath + "/" +parsePackagePath(templateFile.getRelativePath(), genProject);
            FileIOUtils.writeFile(new File(targetPath), templateFile.getFileContent(),false);
        }
    }




    private static String parsePackagePath(String relativePath, GenProject genProject){
        if(relativePath.endsWith(".ftl")){
            relativePath = relativePath.substring(0, relativePath.length()-4);
        }
        relativePath = relativePath.replaceAll(COMPANY_NAME, genProject.getCompanyName());
        relativePath = relativePath.replaceAll(PROJECT_NAME, genProject.getProjectName());
        relativePath = relativePath.replaceAll(PACKAGE_NAME, genProject.getPackageName().replaceAll(".","/"));
        return relativePath;
    }

    private static String parseTablePath(String relativePath, GenTable genTable){
        relativePath = parsePackagePath(relativePath, genTable);
        relativePath = relativePath.replaceAll(ENTITY_NAME, genTable.getClassName());
        relativePath = relativePath.replaceAll(ENTITY_NAME_LOWER_CASE, genTable.getClassName().toLowerCase());
        return relativePath;
    }


    /**
     * 配置 freemarker configuration
     *
     * @return
     */
    private static Configuration  configuration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        configuration.setTemplateLoader(templateLoader);
        configuration.setDefaultEncoding("UTF-8");
        return configuration;
    }

    @Setter
    @Getter
    private static class TemplateFile{

        private String relativePath;

        private String fileName;

        private String fileContent;

        private boolean isTpl;
    }

}
