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
package com.wl4g.dopaas.lcdp.tools.hbase.rdbms.handler;

import static com.wl4g.infra.common.lang.Assert2.notNull;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.contains;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import com.wl4g.dopaas.lcdp.tools.hbase.rdbms.RdbmsRepository;
import com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;

/**
 * {@link RdbmsHandler}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月17日 v1.0.0
 * @see
 */
public abstract class RdbmsHandler {

    /**
     * Register
     */
    private static final Map<String[], Class<? extends RdbmsHandler>> registers = unmodifiableMap(
            new HashMap<String[], Class<? extends RdbmsHandler>>() {
                private static final long serialVersionUID = 410424241261771123L;
                {
                    put(new String[] { "jdbc:mysql" }, MySQL57Handler.class);
                    put(new String[] { "jdbc:oracle" }, Oracle11gHandler.class);
                    put(new String[] { "jdbc:postgresql" }, PostgreSQLHandler.class);
                }
            });

    /**
     * Table Name
     */
    private final String tableName;

    /**
     * Rmdb holder
     */
    private final RdbmsRepository repository;

    public RdbmsHandler(CommandLineFacade line) {
        try {
            this.tableName = HBaseTools.getShortTableName(line.getString("tabname"));
            this.repository = createRepository0(line);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Gets {@link RdbmsHandler} instance.
     * 
     * @param alias
     * @return
     */
    public static RdbmsHandler getInstance(CommandLineFacade line) throws Exception {
        String jdbcUrl = line.getString("jdbcUrl");

        Class<? extends RdbmsHandler> cls = null;
        Iterator<Entry<String[], Class<? extends RdbmsHandler>>> it = registers.entrySet().iterator();
        ok: while (it.hasNext()) {
            Entry<String[], Class<? extends RdbmsHandler>> entry = it.next();
            for (String _alias : entry.getKey()) {
                if (contains(jdbcUrl, _alias)) {
                    cls = entry.getValue();
                    break ok;
                }
            }
        }
        notNull(cls, "Invalid rmdb database provider alias: %s", jdbcUrl);
        return cls.getConstructor(CommandLine.class).newInstance(line);
    }

    /**
     * Create RDMSB repository
     * 
     * @param line
     * @return
     * @throws ParseException
     */
    private RdbmsRepository createRepository0(CommandLineFacade line) throws ParseException {
        String driver = getDriverClass();
        String url = line.getString("jdbcUrl");
        String username = line.getString("username");
        String password = line.getString("password");
        String maxConnections = line.getString("maxConnections");
        return new RdbmsRepository(driver, url, username, password, Integer.parseInt(maxConnections));
    }

    public String getTableName() {
        return tableName;
    }

    public RdbmsRepository getRepository() {
        return repository;
    }

    public abstract String getDriverClass();

    public abstract String buildInsertSQLs(Map<String, String> fields);

}