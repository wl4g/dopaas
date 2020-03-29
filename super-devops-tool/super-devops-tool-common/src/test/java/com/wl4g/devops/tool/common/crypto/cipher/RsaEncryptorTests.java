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
package com.wl4g.devops.tool.common.crypto.cipher;

import com.wl4g.devops.tool.common.crypto.asymmetric.RSACryptor;
import com.wl4g.devops.tool.common.crypto.asymmetric.spec.KeyPairSpec;

//import java.security.KeyFactory;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.spec.KeySpec;
//import java.security.spec.RSAPrivateCrtKeySpec;
//import java.security.spec.RSAPublicKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.binary.Hex;
//import sun.security.rsa.RSAPublicKeyImpl;
//import javax.crypto.Cipher;
//import com.google.common.base.Charsets;

//@SuppressWarnings("restriction")
public class RsaEncryptorTests {

	public static void main(String[] args) throws Exception {
		// Get algorithm instance
		RSACryptor rsa = new RSACryptor();

		// Create keySpec pair
		KeyPairSpec keySpecPair = rsa.generateKeySpecPair();
		// Deserialize
		System.out.println("Create keySpec pair:\t" + keySpecPair);

		// for (int i = 0; i < 2_000; i++) {
		// new Thread(() -> {
		// An instance of serialized key load algorithms
		rsa.getInstance(keySpecPair);

		String plainText = "abcd";
		System.out.println("Origin plain text:\t" + plainText);

		// Encrypt plain text
		String cipherText = rsa.encrypt(plainText);
		System.out.println("Encrypt cipher text:\t" + cipherText);

		// Decrypt cipher text
		plainText = rsa.decrypt(cipherText);
		System.out.println("Decrypt plain text:\t" + plainText);
		// }).start();
		// }

		//
		// KeySpec serialization testing:
		//

		// KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		// kpg.initialize(1024);
		// KeyPair keyPair = kpg.generateKeyPair();
		// KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		// KeySpec pubKeySepc = keyFactory.getKeySpec(keyPair.getPublic(),
		// RSAPublicKeySpec.class);
		// KeySpec privKeySepc = keyFactory.getKeySpec(keyPair.getPrivate(),
		// RSAPrivateCrtKeySpec.class);
		//
		// System.out.println("==========serial public================");
		// System.out.println("pub serial before:" +
		// JacksonUtils.toJSONString(pubKeySepc));
		// byte[] pubKeySpecData = ProtostuffUtils.serialize(pubKeySepc);
		// KeySpec pubKeySpec = ProtostuffUtils.deserialize(pubKeySpecData,
		// RSAPublicKeySpec.class);
		// System.out.println("pub serial after:" +
		// JacksonUtils.toJSONString(pubKeySpec));
		//
		// System.out.println("==========serial private================");
		// System.out.println("priv serial before:" +
		// JacksonUtils.toJSONString(privKeySepc));
		// byte[] privKeySpecData = ProtostuffUtils.serialize(privKeySepc);
		// KeySpec privKeySpec = ProtostuffUtils.deserialize(privKeySpecData,
		// RSAPrivateCrtKeySpec.class);
		// System.out.println("priv serial after:" +
		// JacksonUtils.toJSONString(privKeySpec));
		//
		// System.out.println("--------(generate-key)cipher testing---------");
		// Cipher encryptCipher = Cipher.getInstance("RSA");
		// Cipher decryptCipher = Cipher.getInstance("RSA");
		// encryptCipher.init(Cipher.ENCRYPT_MODE,
		// keyFactory.generatePublic(pubKeySpec));
		// decryptCipher.init(Cipher.DECRYPT_MODE,
		// keyFactory.generatePrivate(privKeySpec));
		// // encrypt
		// String res =
		// Hex.encodeHexString(encryptCipher.doFinal("abc".getBytes(Charsets.UTF_8)));
		// System.out.println("encrypt:" + res);
		// // decrypt
		// System.out.println("decrypt:" + new
		// String(decryptCipher.doFinal(Hex.decode(res))));
		//
		// System.out.println("--------Hex to base64 testing---------");
		// String hex =
		// "30820275020100300d06092a864886f70d01010105000482025f3082025b02010002818100b1a9cd15b69d408c5017564468ac2e76ee6e8f7cd2a824f407a729ff90117d2c94449f93966f6444dd2a5a44903a6f23e1838f4672f5bd35443f8b4c97c6e794aa010408e0deab5572027f32ebbfb319fd94e9f30d7dccdbccb9c2e9e4494040482cf084f75ec084e111e1ed65324bf574788731ce4a7866e666a1af0ace26af02030100010281802d222ee88a38ceb21692a726bfd4a6052eb3459e7741a209a07c160e478239e37e53249af0c7a19860fb266d6e9a79ab8ad9ca2722834d2ae008a891afa927214ec16464bc8e95eef8b10553ea8de57c09a67fccb79e5a3b36abf099694a35f0b20cebca2869bf4a6bbd3374bfa0feef6129c7235aa9c3598a65d83f3c7b15a1024100f6aa532e59e3c604724f18e14b1a181a4c0bc0fdb83ced5a7e88650155256515fdf7c751d93da876ae09cf01ecdb4e557157ed08905a4ca39babf6785fe0d55f024100b862fd5556a9abad6e78c501cf9be7052e46831f227ec3d59d7c6e047415661b5c10c097efdd0b0387197e04398034b669fa1c55b6981c55f476539d100960b10240291c8c4e9e66cb3b347e206c7462fdff6c0b4d783c3bd36790cd54e05afba79479c6d16ebfe179f185e256a14083f0d366d7bbc700a03c43cc8f65bdfc64f1cb024057b960e5d5116c485b22c238165a0a1380ecb33d80dfe6b41ef530329638081844390428454a590f189b9c44ce469ccd9ca60e0d098e5d0cd7fde3d1f9eb3c41024061ca374b2852e7ee9524177d35f813c1266d30ab00af8334a99b19923fae7f8f942d65c1b9ca37e06b050e14169ef07621c25dee9f8d749bc51f6c435ba13536";
		// System.out.println("Base64:");
		// System.out.println(Base64.encodeBase64String(Hex.decodeHex(hex)));
		//
		// System.out.println("--------(deserial-key)encrypt testing---------");
		// String pubKey =
		// "30819f300d06092a864886f70d010101050003818d0030818902818100b1a9cd15b69d408c5017564468ac2e76ee6e8f7cd2a824f407a729ff90117d2c94449f93966f6444dd2a5a44903a6f23e1838f4672f5bd35443f8b4c97c6e794aa010408e0deab5572027f32ebbfb319fd94e9f30d7dccdbccb9c2e9e4494040482cf084f75ec084e111e1ed65324bf574788731ce4a7866e666a1af0ace26af0203010001";
		// String plainText = "123456";
		// KeyFactory kf = KeyFactory.getInstance("RSA");
		// X509EncodedKeySpec keySpec = new
		// X509EncodedKeySpec(Hex.decode(pubKey));
		// Cipher cip = Cipher.getInstance("RSA");
		// cip.init(Cipher.ENCRYPT_MODE, kf.generatePublic(keySpec));
		// System.out.println(Hex.encodeToString(cip.doFinal(plainText.getBytes())));
		//
		// RSAPublicKeyImpl pubKey = new RSAPublicKeyImpl(Hex.decode(
		// "30819f300d06092a864886f70d010101050003818d003081890281810097df71dc220a34b1f0ac8d3ec5b4dbef83b938c15ddaa48bda04c94671361adacfef61887f2799ae6790db346de9a1eb6af8e58d8f09190844b99af32a1cbaed9529baa9ba48a52439a31c795e963cee740c0e218cf00cf4ecb1aed3dc5590e2f344de74e30066b5ba9886f834a55c7a1bf1cd04251ca4238235bf4a964e78d50203010001"));
		// Cipher encryptCipher = Cipher.getInstance("RSA");
		// encryptCipher.init(Cipher.DECRYPT_MODE, pubKey);
		// System.out.println(Hex.encodeToString(encryptCipher.doFinal("123456".getBytes())));

	}

}