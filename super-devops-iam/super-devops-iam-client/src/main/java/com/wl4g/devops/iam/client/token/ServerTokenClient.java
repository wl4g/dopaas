package com.wl4g.devops.iam.client.token;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_GET_TOKEN;

/**
 * @author vjay
 * @date 2019-06-07 16:22:00
 */
@Component
public class ServerTokenClient {

    final protected RestTemplate restTemplate;

    final protected IamClientProperties config;

    /*@Value("#{'${spring.application.name}'}")
    private String from;*/

    public ServerTokenClient(IamClientProperties config) {
        Assert.notNull(config, "Scm client properties must not be null");
        this.config = config;
        this.restTemplate = createSecureRestTemplate(config);
    }

    protected RestTemplate createSecureRestTemplate(IamClientProperties client) {
        Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
        RestTemplate template = new RestTemplate(factory);
        return template;
    }

    public String getToken(String from,String to){
        String url = config.getBaseUri()+URI_S_GET_TOKEN;
        url = url+"?from="+from+"&to="+to;
        String token = this.restTemplate
                .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }).getBody();
        return token;
    }


    public boolean authToken(String from,String to,String token){
        String url = config.getBaseUri()+URI_S_GET_TOKEN;
        url = url+"?from="+from+"&to="+to+"&token="+token;
        RespBase respBase = this.restTemplate
                .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<RespBase>() {
                }).getBody();

        if(RespBase.RetCode.OK.getCode()==respBase.getCode()){
            return true;
        }else{
            return false;
        }
    }

}
