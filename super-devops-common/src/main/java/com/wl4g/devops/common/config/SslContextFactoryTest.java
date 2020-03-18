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
package com.wl4g.devops.common.config;
//
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.security.KeyStore;
// import java.security.NoSuchAlgorithmException;
//
// import javax.net.ssl.KeyManager;
// import javax.net.ssl.KeyManagerFactory;
// import javax.net.ssl.SSLContext;
// import javax.net.ssl.SSLException;
// import javax.net.ssl.TrustManager;
// import javax.net.ssl.TrustManagerFactory;
//
// import org.springframework.core.io.ClassPathResource;
//
// import io.netty.handler.ssl.SslContextBuilder;
// import io.netty.handler.ssl.SslProvider;
//
// public class SslContextFactoryTest {
//
// private static final SSLContext SSL_CONTEXT_S;
//
// private static final SSLContext SSL_CONTEXT_C;
//
// static {
// try {
// SslContextBuilder.forServer(new File(""), new
/// File("")).sslProvider(SslProvider.OPENSSL_REFCNT).build()
// .newEngine(null);
// } catch (SSLException e2) {
// // TODO Auto-generated catch block
// e2.printStackTrace();
// }
// SSLContext sslContext = null;
// SSLContext sslContext2 = null;
// try {
// sslContext = SSLContext.getInstance("SSLv3");
// sslContext2 = SSLContext.getInstance("SSLv3");
// } catch (NoSuchAlgorithmException e1) {
// e1.printStackTrace();
// }
// try {
// if (getKeyManagersServer() != null && getTrustManagersServer() != null) {
// sslContext.init(getKeyManagersServer(), getTrustManagersServer(), null);
// }
// if (getKeyManagersClient() != null && getTrustManagersClient() != null) {
// sslContext2.init(getKeyManagersClient(), getTrustManagersClient(), null);
// }
//
// } catch (Exception e) {
// e.printStackTrace();
// }
// sslContext.createSSLEngine().getSupportedCipherSuites();
// sslContext2.createSSLEngine().getSupportedCipherSuites();
// SSL_CONTEXT_S = sslContext;
// SSL_CONTEXT_C = sslContext2;
// }
//
// public SslContextFactoryTest() {
//
// }
//
// public static SSLContext getSslContext() {
// return SSL_CONTEXT_S;
// }
//
// public static SSLContext getSslContext2() {
// return SSL_CONTEXT_C;
// }
//
// private static TrustManager[] getTrustManagersServer() {
// FileInputStream is = null;
// KeyStore ks = null;
// TrustManagerFactory keyFac = null;
//
// TrustManager[] kms = null;
// try {
// // 获得KeyManagerFactory对象. 初始化位默认算法
// keyFac = TrustManagerFactory.getInstance("SunX509");
// is = new FileInputStream((new
/// ClassPathResource("main/java/conf/sChat.jks")).getFile());
// ks = KeyStore.getInstance("JKS");
// String keyStorePass = "sNetty";
// ks.load(is, keyStorePass.toCharArray());
// keyFac.init(ks);
// kms = keyFac.getTrustManagers();
// } catch (Exception e) {
// e.printStackTrace();
// } finally {
// if (is != null) {
// try {
// is.close();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
// }
// return kms;
// }
//
// private static TrustManager[] getTrustManagersClient() {
// FileInputStream is = null;
// KeyStore ks = null;
// TrustManagerFactory keyFac = null;
//
// TrustManager[] kms = null;
// try {
// // 获得KeyManagerFactory对象. 初始化位默认算法
// keyFac = TrustManagerFactory.getInstance("SunX509");
// is = new FileInputStream((new
/// ClassPathResource("main/java/conf/cChat.jks")).getFile());
// ks = KeyStore.getInstance("JKS");
// String keyStorePass = "sNetty";
// ks.load(is, keyStorePass.toCharArray());
// keyFac.init(ks);
// kms = keyFac.getTrustManagers();
// } catch (Exception e) {
// e.printStackTrace();
// } finally {
// if (is != null) {
// try {
// is.close();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
// }
// return kms;
// }
//
// private static KeyManager[] getKeyManagersServer() {
// FileInputStream is = null;
// KeyStore ks = null;
// KeyManagerFactory keyFac = null;
//
// KeyManager[] kms = null;
// try {
// // 获得KeyManagerFactory对象. 初始化位默认算法
// keyFac = KeyManagerFactory.getInstance("SunX509");
// is = new FileInputStream((new
/// ClassPathResource("main/java/conf/sChat.jks")).getFile());
// ks = KeyStore.getInstance("JKS");
// String keyStorePass = "sNetty";
// ks.load(is, keyStorePass.toCharArray());
// keyFac.init(ks, keyStorePass.toCharArray());
// kms = keyFac.getKeyManagers();
// } catch (Exception e) {
// e.printStackTrace();
// } finally {
// if (is != null) {
// try {
// is.close();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
// }
// return kms;
// }
//
// private static KeyManager[] getKeyManagersClient() {
// FileInputStream is = null;
// KeyStore ks = null;
// KeyManagerFactory keyFac = null;
//
// KeyManager[] kms = null;
// try {
// // 获得KeyManagerFactory对象. 初始化位默认算法
// keyFac = KeyManagerFactory.getInstance("SunX509");
// is = new FileInputStream((new
/// ClassPathResource("main/java/conf/cChat.jks")).getFile());
// ks = KeyStore.getInstance("JKS");
// String keyStorePass = "sNetty";
// ks.load(is, keyStorePass.toCharArray());
// keyFac.init(ks, keyStorePass.toCharArray());
// kms = keyFac.getKeyManagers();
// } catch (Exception e) {
// e.printStackTrace();
// } finally {
// if (is != null) {
// try {
// is.close();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
// }
// return kms;
// }
// }