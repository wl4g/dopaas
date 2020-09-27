// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

package ${organType?uncap_first}.${organName?uncap_first};

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.wl4g.components.data.annotation.AutoConfigureComponentsDataSource;
<#if javaSpecs.checkConfigured(extraOptions, "gen.iam.security-mode", "cluster")>
import com.wl4g.iam.client.annotation.EnableIamClient;
<#elseif javaSpecs.checkConfigured(extraOptions, "gen.iam.security-mode", "local")>
import com.wl4g.iam.annotation.EnableIamServer;
</#if>

/**
 * {@link ${projectName?cap_first}Server}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
<#-- refer: service/pom.xml -->
<#if javaSpecs.checkConfigured(extraOptions, "gen.iam.security-mode", "cluster")>
@EnableIamClient
<#elseif javaSpecs.checkConfigured(extraOptions, "gen.iam.security-mode", "local")>
@EnableIamServer
</#if>
@MapperScan(basePackages = {"${organType?uncap_first}.${organName?uncap_first}.${projectName?uncap_first}.*.dao", "${organType?uncap_first}.${organName?uncap_first}.${projectName?uncap_first}.*.dao.*"})
@AutoConfigureComponentsDataSource
@SpringBootApplication(scanBasePackages = { "com.wl4g", "${organType?uncap_first}.${organName?uncap_first}" })
public class ${projectName?cap_first}Server {

	public static void main(String[] args) {
		SpringApplication.run(${projectName?cap_first}Server.class, args);
	}

}