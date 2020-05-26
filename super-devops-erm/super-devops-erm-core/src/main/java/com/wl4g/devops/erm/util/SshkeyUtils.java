package com.wl4g.devops.erm.util;

import com.wl4g.devops.tool.common.codec.CodecSource;
import com.wl4g.devops.tool.common.crypto.symmetric.AESCryptor;

import static com.google.common.base.Charsets.UTF_8;

/**
 * @author vjay
 * @date 2020-04-24 16:13:00
 */
public class SshkeyUtils {

	public static String encryptSshkeyToHex(String cipherKey, String sshKey) {
		AESCryptor aes = new AESCryptor();
		return aes.encrypt(cipherKey.getBytes(UTF_8), new CodecSource(sshKey)).toHex();
	}

	public static String decryptSshkeyFromHex(String cipherKey, String hexSshKey) {
		AESCryptor aes = new AESCryptor();
		return aes.decrypt(cipherKey.getBytes(UTF_8), CodecSource.fromHex(hexSshKey)).toString();
	}

}
