package com.wl4g.dopaas.uds.service.elasticjobcloud.config;

/// *
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements. See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License. You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
// package com.wl4g.dopaas.uds.elasticjobcloud.config;
//
// import org.springframework.beans.factory.ObjectProvider;
// import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
// import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
// import
/// org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
// import
/// org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
// import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
// import org.springframework.transaction.jta.JtaTransactionManager;
//
// import javax.sql.DataSource;
// import java.util.HashMap;
// import java.util.Map;
//
// @Configuration
// @EnableConfigurationProperties(JpaProperties.class)
// public class OpenJPAConfig extends JpaBaseConfiguration {
//
// protected OpenJPAConfig(DataSource dataSource, JpaProperties properties,
// ObjectProvider<JtaTransactionManager> jtaTransactionManager,
// ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers)
/// {
// super(dataSource, properties, jtaTransactionManager,
/// transactionManagerCustomizers);
// }
//
// @Override
// protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
// return new OpenJpaVendorAdapter();
// }
//
// @Override
// protected Map<String, Object> getVendorProperties() {
// final Map<String, Object> result = new HashMap<>();
// result.put("openjpa.jdbc.SynchronizeMappings",
/// "buildSchema(ForeignKeys=true)");
// result.put("openjpa.ClassLoadEnhancement", "false");
// result.put("openjpa.DynamicEnhancementAgent", "false");
// result.put("openjpa.RuntimeUnenhancedClasses", "supported");
// result.put("openjpa.Log", "slf4j");
// return result;
// }
//
// }