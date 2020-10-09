// ${watermark}

${javaSpecs.wrapMultiComment(copyright)}

package ${organType?uncap_first}.${organName?uncap_first};

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
<#-- TODO migration com.wl4g.devops.dao => com.wl4g.iam.dao -->
@MapperScan(basePackages = { "${basePackagePrefix}.*.dao", "${organType?uncap_first}.${basePackagePrefix}.*.dao.*", "com.wl4g.devops.dao" })
@AutoConfigureComponentsDataSource
@SpringBootApplication(scanBasePackages = { "com.wl4g", "${organType?uncap_first}.${organName?uncap_first}" })
public class ${projectName?cap_first}Server {

	public static void main(String[] args) {
		SpringApplication.run(${projectName?cap_first}Server.class, args);
	}

}