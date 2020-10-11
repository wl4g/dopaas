// ${watermark}

${javaSpecs.wrapMultiComment(copyright)}

package ${organType?uncap_first}.${organName?uncap_first};

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<#if javaSpecs.isConf(extraOptions, "gen.swagger.ui", "bootstrapSwagger2")>
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
<#elseif javaSpecs.isConf(extraOptions, "gen.swagger.ui", "officialOas")>
import springfox.documentation.oas.annotations.EnableOpenApi;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
</#if>

import com.wl4g.components.data.annotation.AutoConfigureComponentsDataSource;
<#if javaSpecs.isConf(extraOptions, "gen.iam.security-mode", "cluster")>
import com.wl4g.iam.client.annotation.EnableIamClient;
<#elseif javaSpecs.isConf(extraOptions, "gen.iam.security-mode", "local")>
import com.wl4g.iam.annotation.EnableIamServer;
</#if>

<#assign basePackagePrefix = organType?uncap_first +'.'+ organName?uncap_first +'.'+ projectName?uncap_first>
/**
 * {@link ${projectName?cap_first}Server}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
<#-- refer: service/pom.xml -->
<#if javaSpecs.isConf(extraOptions, "gen.iam.security-mode", "cluster")>
@EnableIamClient
<#elseif javaSpecs.isConf(extraOptions, "gen.iam.security-mode", "local")>
@EnableIamServer
</#if>
<#if javaSpecs.isConf(extraOptions, "gen.swagger.ui", "bootstrapSwagger2")>
@EnableSwagger2
@EnableSwaggerBootstrapUI
<#elseif javaSpecs.isConf(extraOptions, "gen.swagger.ui", "officialOas")>
@EnableOpenApi
@EnableWebMvc
</#if>
<#-- TODO migration com.wl4g.devops.dao => com.wl4g.iam.dao -->
@MapperScan(basePackages = { "${basePackagePrefix}.*.dao", "${organType?lower_case}.${basePackagePrefix}.*.dao.*", "com.wl4g.devops.dao" })
@AutoConfigureComponentsDataSource
@SpringBootApplication(scanBasePackages = { "com.wl4g", "${organType?lower_case}.${organName?lower_case}" })
public class ${projectName?cap_first}Server {

	public static void main(String[] args) {
		SpringApplication.run(${projectName?cap_first}Server.class, args);
	}

}