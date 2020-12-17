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
package com.wl4g.devops.umc.timing;

import javax.sql.DataSource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.component.common.lang.Assert2;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author vjay
 * @date 2020-04-07 15:26:00
 */
public class SingleDataSource implements DataSource, Closeable {

    private DataSource delegate;

    private Connection singleConnection;

    public SingleDataSource(@NotNull DataSource delegate) {
        Assert2.notNullOf(delegate, "delegate");
        this.delegate = delegate;
    }

    public SingleDataSource(@NotBlank String driverClassName, @NotBlank String url, @NotBlank String username, @NotBlank String password) {
        try {
            ServiceLoader.load(Class.forName(driverClassName),Thread.currentThread().getContextClassLoader());
            singleConnection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public synchronized Connection getConnection() throws SQLException {
        if (isNull(singleConnection)) {
            singleConnection = delegate.getConnection();
        }
        return singleConnection;
    }

    @Override
    public synchronized Connection getConnection(String username, String password) throws SQLException {
        if (isNull(singleConnection)) {
            singleConnection = delegate.getConnection(username, password);
        }
        return singleConnection;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegate.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegate.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return delegate.getParentLogger();
    }

    @Override
    public synchronized void close() throws IOException {
        if (nonNull(singleConnection)) {
            try {
                singleConnection.close();
            } catch (SQLException e) {
                throw new IOException(e);
            }finally {
                singleConnection = null;
                delegate = null;
            }
        }

    }
}