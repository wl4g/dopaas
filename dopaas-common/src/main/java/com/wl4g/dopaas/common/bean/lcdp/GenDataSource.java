/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.common.bean.lcdp;

import static com.wl4g.dopaas.common.constant.LcdpConstants.ModelAttributeConstants.GEN_DB_DATABAES;
import static com.wl4g.dopaas.common.constant.LcdpConstants.ModelAttributeConstants.GEN_DB_HOST;
import static com.wl4g.dopaas.common.constant.LcdpConstants.ModelAttributeConstants.GEN_DB_NAME;
import static com.wl4g.dopaas.common.constant.LcdpConstants.ModelAttributeConstants.GEN_DB_PASSWORD;
import static com.wl4g.dopaas.common.constant.LcdpConstants.ModelAttributeConstants.GEN_DB_PORT;
import static com.wl4g.dopaas.common.constant.LcdpConstants.ModelAttributeConstants.GEN_DB_TYPE;
import static com.wl4g.dopaas.common.constant.LcdpConstants.ModelAttributeConstants.GEN_DB_USERNAME;
import static com.wl4g.dopaas.common.constant.LcdpConstants.ModelAttributeConstants.GEN_DB_VERSION;

import com.wl4g.infra.core.bean.BaseBean;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link GenDataSource}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
@Getter
@Setter
public class GenDataSource extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    @RenderProperty(propertyName = GEN_DB_NAME)
    private String name;

    /**
     * {@link com.wl4g.dopaas.lcdp.codegen.engine.converter.DbTypeConverter.DbType}
     */
    @RenderProperty(propertyName = GEN_DB_TYPE)
    private String type;

    @RenderProperty(propertyName = GEN_DB_HOST)
    private String host;

    @RenderProperty(propertyName = GEN_DB_PORT)
    private String port;

    @RenderProperty(propertyName = GEN_DB_DATABAES)
    private String database;

    @RenderProperty(propertyName = GEN_DB_USERNAME)
    private String username;

    @RenderProperty(propertyName = GEN_DB_PASSWORD)
    private String password;

    @RenderProperty(propertyName = GEN_DB_VERSION)
    private String dbversion;

    public GenDataSource() {
        super();
    }

    public GenDataSource withName(String name) {
        this.name = name;
        return this;
    }

    public GenDataSource withType(String type) {
        this.type = type;
        return this;
    }

    public GenDataSource withHost(String host) {
        this.host = host;
        return this;
    }

    public GenDataSource withPort(String port) {
        this.port = port;
        return this;
    }

    public GenDataSource withDatabase(String database) {
        this.database = database;
        return this;
    }

    public GenDataSource withUsername(String username) {
        this.username = username;
        return this;
    }

    public GenDataSource withPassword(String password) {
        this.password = password;
        return this;
    }

    public GenDataSource withDbversion(String dbversion) {
        this.dbversion = dbversion;
        return this;
    }

}