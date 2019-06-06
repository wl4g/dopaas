package com.wl4g.devops.scm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//import static com.wl4g.devops.common.constants.SCMDevOpsConstants.URI_S_BASE;

@Configuration
public class IntercpetorConfig extends WebMvcConfigurerAdapter {


    @Bean
    RepeatInterceptor getRepeatInterceptor(){
        return new RepeatInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getRepeatInterceptor()).addPathPatterns("/*");
    }
}

