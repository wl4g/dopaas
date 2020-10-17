// ${watermark}

${javaSpecs.wrapMultiComment(copyright)}

package ${organType}.${organName};

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.wl4g.iam.test.mock.annotation.EnableIamMockAutoConfiguration;
import com.wl4g.components.data.annotation.AutoConfigureComponentsDataSource;

<#assign basePackagePrefix = organType +'.'+ organName +'.'+ projectName>
/**
 * {@link ${projectName?cap_first}Server}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
@EnableIamMockAutoConfiguration
<#-- TODO migration com.wl4g.devops.dao => com.wl4g.iam.dao -->
@MapperScan(basePackages = { "${basePackagePrefix}.*.dao", "${organType}.${basePackagePrefix}.*.dao.*", "com.wl4g.devops.dao" })
@AutoConfigureComponentsDataSource
@SpringBootApplication(scanBasePackages = { "com.wl4g", "${organType}.${organName}" })
public class ${projectName?cap_first}ServerTests {

	public static void main(String[] args) {
		SpringApplication.run(${projectName?cap_first}ServerTests.class, args);
	}

}