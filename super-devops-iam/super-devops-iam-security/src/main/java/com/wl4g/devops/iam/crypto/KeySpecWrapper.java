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
package com.wl4g.devops.iam.crypto;

import java.util.UUID;

import org.springframework.util.Assert;

/**
 * KeySpec wrapper, include e.g.: RSA key pair algorithm and AES single key
 * algorithm key packaging.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-28
 * @since
 */
public abstract class KeySpecWrapper {

    /**
     * KeySpec unique identification.
     */
    private String keySpecId;

    public KeySpecWrapper() {
        setKeySpecId("keySpec" + UUID.randomUUID().toString().replaceAll("-", "").substring(12));
    }

    public KeySpecWrapper(String keySpecId) {
        Assert.hasText(keySpecId, "KeySpecId must not be empty.");
        this.keySpecId = keySpecId;
    }

    public String getKeySpecId() {
        return keySpecId;
    }

    public void setKeySpecId(String keySpecId) {
        this.keySpecId = keySpecId;
    }

}