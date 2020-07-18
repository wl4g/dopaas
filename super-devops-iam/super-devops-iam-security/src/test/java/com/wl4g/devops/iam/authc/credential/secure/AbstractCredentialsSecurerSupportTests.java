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
package com.wl4g.devops.iam.authc.credential.secure;

import static com.google.common.base.Charsets.UTF_8;

import java.security.spec.KeySpec;

import com.wl4g.devops.components.tools.common.codec.CodecSource;
import com.wl4g.devops.components.tools.common.crypto.asymmetric.RSACryptor;

public class AbstractCredentialsSecurerSupportTests {

	static String credentialsHex = "30820275020100300d06092a864886f70d01010105000482025f3082025b02010002818100891cf7d40c937b59e30a57173b106907311fa52affebf53cc9198e5acb9200b364c82634af48a517c32b8650e2edb5317e68c72e187fb46831f668fd8c35389ab0b0d36bfa31b80a6ae90c544aa9abce7179aa0733615938bdfb98ea6f260549ee1c84e2e756b46632f1d2db2dbf38c520039a7d71153bd02b86634ff86e10dd020301000102818018411891d91ba9a14fe93f58aba90e7b283469da3e6535ff67af6ea01f0c97b52429c7a060356086e08e6d77177d6724f7307fb366c00860ba1dd59aac57a9c866685444822763a3024dd78f978cdf6a9d5ff34270b6ee62ba68b11adf28b855eaee97cca0abf1a75d1437890119c6f654c835a3c07bd42ffcfe37063e175a9d024100f8f2cea7f6275195c296e664faa0d63fa520a8cd921e1b6801df6600099000be5b9e7e35deeb63637de250ef27f1ad54429598fd23b13fba90cee637a6c52b230241008cff36745564e040b8a49b3f99f4ac7efdee992a2c4331f14f8b598c13d8ac5b45b9147e9b02cb3509e8f5d95f052b28e1e0de5e7dd92ec46d0625382e6193ff02400fa865eb16865e9146cdeaebcaffb6ba060ec9cf34338491c41c62f3f9a5ff05fa093bf2f37466a9e94f04bc260d0353db64415d1a93996888971acfc1438175024036c1f6f7ff220a25988ff4eb22736f7a724bf397f950af58b437be333491f57e0cf9f007ab53ce6f6ec5d694f0de2500df986143f9441b719595c966a43ca92902407c2b9ad6dce144a2974dc93d145d43c49a80fd3d507cec082b53eda531da1e23fdd9e844dbcae7969e05c17f3977ed68ab97f38ed835694626962f6563788769";
	static byte[] privateKey = "xxx".getBytes(UTF_8);

	public static void main(String[] args) {
		resolveCredentialsTests();
	}

	public static void resolveCredentialsTests() {
		byte[] credentials = CodecSource.fromHex(credentialsHex).getBytes();
		RSACryptor rsa = new RSACryptor();
		KeySpec _privateKey = rsa.generateKeySpec(privateKey);
		CodecSource src = rsa.decrypt(_privateKey, CodecSource.fromHex(credentials));
		System.out.println(src.toString());
	}

}