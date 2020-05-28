package com.wl4g.devops.erm.util;

import com.wl4g.devops.tool.common.codec.CodecSource;
import com.wl4g.devops.tool.common.crypto.symmetric.AES128CBCPKCS5;

import static com.google.common.base.Charsets.UTF_8;

/**
 * @author vjay
 * @date 2020-04-24 16:13:00
 */
public class SshkeyUtils {

	public static String encryptSshkeyToHex(String cipherKey, String sshKey) {
		AES128CBCPKCS5 aes = new AES128CBCPKCS5();
		return aes.encrypt(cipherKey.getBytes(UTF_8), new CodecSource(sshKey)).toHex();
	}

	public static String decryptSshkeyFromHex(String cipherKey, String hexSshKey) {
		AES128CBCPKCS5 aes = new AES128CBCPKCS5();
		return aes.decrypt(cipherKey.getBytes(UTF_8), CodecSource.fromHex(hexSshKey)).toString();
	}

}
