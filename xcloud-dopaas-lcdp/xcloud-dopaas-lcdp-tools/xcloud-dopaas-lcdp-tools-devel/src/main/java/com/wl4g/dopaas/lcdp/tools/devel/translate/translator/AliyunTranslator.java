package com.wl4g.dopaas.lcdp.tools.devel.translate.translator;

import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.alimt20181012.models.TranslateGeneralResponse;
import com.aliyun.teaopenapi.models.Config;

/**
 * {@link AliyunTranslator}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-09-26 v1.0.0
 * @since v1.0.0
 */
// https://next.api.aliyun.com/api/alimt/2018-10-12/TranslateGeneral?spm=api-workbench..0.0.484e1e0fH7zWMX&params={}
public class AliyunTranslator {

    public static final String ALIYUN_APP_ID = System.getenv("ALIYUN_APP_ID");
    public static final String ALIYUN_APP_SECRET = System.getenv("ALIYUN_APP_SECRET");

    public static com.aliyun.alimt20181012.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret);
        config.endpoint = "mt.aliyuncs.com";
        return new com.aliyun.alimt20181012.Client(config);
    }

    public static String doTranslate(String fromLang, String toLang, String text) throws Exception {
        com.aliyun.alimt20181012.Client client = AliyunTranslator.createClient(ALIYUN_APP_ID, ALIYUN_APP_SECRET);
        TranslateGeneralRequest req = new TranslateGeneralRequest();
        req.setSourceLanguage(fromLang);
        req.setTargetLanguage(toLang);
        req.setFormatType("json");
        req.setSourceText(text);
        TranslateGeneralResponse resp = client.translateGeneral(req);
        if (resp.getBody().getCode() > 0) {
            throw new RuntimeException(resp.getBody().getMessage());
        }
        return resp.getBody().getData().getTranslated();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(doTranslate("zh", "en", "中华帝国"));
    }

}
