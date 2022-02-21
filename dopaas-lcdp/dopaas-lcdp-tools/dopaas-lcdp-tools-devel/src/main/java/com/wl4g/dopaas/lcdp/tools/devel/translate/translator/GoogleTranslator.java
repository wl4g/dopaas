/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.dopaas.lcdp.tools.devel.translate.translator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.component.common.serialize.JacksonUtils;

/**
 * {@link GoogleTranslator}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-09-26 v1.0.0
 * @since v1.0.0
 */
public class GoogleTranslator {

    public static String doTranslate(String fromLang, String toLang, String text) throws Exception {
        URL url = new URL(String.format("https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s",
                fromLang, toLang, URLEncoder.encode(text, "UTF-8")));

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));) {
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // Parsing, for example:
            /*
             * [[["China","中国",null,null,10]],null,"zh-CN",null,null,null,null,[
             * ]]
             */
            JsonNode node = JacksonUtils.parseJSON(response.toString(), JsonNode.class);
            JsonNode nodeArr0 = node.get(0);
            String result = "";
            for (int i = 0; i < nodeArr0.size(); i++) {
                result += nodeArr0.get(i).get(0).toString();
            }
            return result;
        } catch (Exception e) {
            String errmsg = String.format("Failed to call google translation. text: %s, cause by: %s", text, e.getMessage());
            System.err.println(errmsg);
        }
        return text;
    }

}
