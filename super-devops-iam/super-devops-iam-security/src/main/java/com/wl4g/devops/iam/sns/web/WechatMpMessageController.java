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
package com.wl4g.devops.iam.sns.web;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;
import com.wl4g.devops.common.exception.iam.IllegalRequestException;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.common.web.BaseController;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_WECHAT_MP_RECEIVE;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

/**
 * Annotation to enabled WechatMp controller configuration.<br/>
 * *See:<a
 * href=*"https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1472017492_58YV5">https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1472017492_58YV5</a>
 * *
 *
 * @author Wangl.sir<9   8   3   7   0   8   4   0   8       @   qq.com    >*@version v1.02018年9月17日*@since
 */
public abstract class WechatMpMessageController extends BaseController {

    /**
     * WeChat public platform and basic configuration, checking tokens with User
     * message interaction API See:<code>
     * <a href=
     * "https://mp.weixin.qq.com/advanced/advanced?action=interface&t=advanced/interface&lang=zh_CN">https://mp.weixin.qq.com/advanced/advanced?action=interface&t=advanced/interface&lang=zh_CN</a>
     * </code>
     */
    protected String validateToken;

    public WechatMpMessageController(String validateToken) {
        Assert.notNull(validateToken, "'validateToken' is null please check configure");
        this.validateToken = validateToken;
    }

    /**
     * Pre-verification authentication GET request from wechat server <br/>
     * See:<a href=
     * "https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1472017492_58YV5">https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1472017492_58YV5</a>
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @param response
     * @throws IOException
     */
    @GetMapping(URI_S_WECHAT_MP_RECEIVE)
    public void preReceive(@NotBlank @RequestParam(name = "signature") String signature,
                           @NotBlank @RequestParam(name = "timestamp") String timestamp, @NotBlank @RequestParam(name = "nonce") String nonce,
                           @NotBlank @RequestParam(name = "echostr") String echostr, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (log.isInfoEnabled()) {
            log.info("Verify from wechat get request. [{}]", WebUtils2.getFullRequestURI(request));
        }
        Assert.isTrue(!StringUtils.isEmpty(echostr), "'echostr' must not be empty");

        // Validation
        this.signatureValidate(signature, timestamp, nonce);

        this.write(response, HttpServletResponse.SC_OK, MediaType.PLAIN_TEXT_UTF_8.toString(), echostr.getBytes(Charsets.UTF_8));
    }

    /**
     * Receiving messages sent by the wechat server
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws Exception
     */
    @PostMapping(path = URI_S_WECHAT_MP_RECEIVE, consumes = {"application/xml;charset=UTF-8",
            "text/xml;charset=UTF-8"}, produces = {"application/xml;charset=UTF-8", "text/xml;charset=UTF-8"})
    public void postReceive(@NotBlank @RequestParam(name = "signature") String signature,
                            @NotBlank @RequestParam(name = "timestamp") String timestamp, @NotBlank @RequestParam(name = "nonce") String nonce,
                            @RequestParam(name = "openid", required = false) String openId, HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        if (log.isInfoEnabled()) {
            log.info("Receive from wechat post request. [{}]", WebUtils2.getFullRequestURI(request));
        }

        // Validation
        this.signatureValidate(signature, timestamp, nonce);

        // Processing
        System.out.println(CharStreams.toString(new InputStreamReader(request.getInputStream(), Charsets.UTF_8)));
        String respMsg = ""/* wechatService.processRequest(request) */;
        if (log.isInfoEnabled()) {
            log.info("Reply to wechat as: {}", respMsg);
        }

        // Response
        this.write(response, HttpServletResponse.SC_OK, MediaType.APPLICATION_XML_UTF_8.toString(),
                respMsg.getBytes(Charsets.UTF_8));
    }

    /**
     * WeChat platform signature validation
     *
     * @param signature
     * @param timestamp
     * @param nonce
     */
    private void signatureValidate(String signature, String timestamp, String nonce) {
        Assert.isTrue(!StringUtils.isEmpty(signature), "'signature' must not be empty");
        Assert.isTrue(!StringUtils.isEmpty(timestamp), "'timestamp' must not be empty");
        Assert.isTrue(!StringUtils.isEmpty(nonce), "'nonce' must not be empty");

        String[] combinedArr = {this.validateToken, timestamp, nonce};
        Arrays.sort(combinedArr); // Dictionary ordering
        // Combined
        String plaintext = combinedArr[0] + combinedArr[1] + combinedArr[2];

        // Hashing
        String ciphertext = Hashing.sha1().hashString(plaintext, Charsets.UTF_8).toString();

        // Check validation
        if (!(ciphertext != null && ciphertext.equalsIgnoreCase(String.valueOf(signature)))) {
            throw new IllegalRequestException(
                    String.format("Illegal request signature[%s], timestamp[%s], nonce[%s]", signature, timestamp, nonce));
        }
    }

}