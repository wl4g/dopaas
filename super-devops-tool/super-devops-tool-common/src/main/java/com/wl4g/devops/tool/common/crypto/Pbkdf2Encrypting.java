package com.wl4g.devops.tool.common.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.wl4g.devops.tool.common.lang.Assert2;

/**
 * PBKDF2 deriveKey utility.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月20日
 * @since
 */
public class Pbkdf2Encrypting {

	/**
	 * Calculate pbkdf2 value.
	 * 
	 * @param password
	 * @param salt
	 * @param iterationCount
	 * @param dkLen
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	public static String pbkdf2(String password, String salt, int iterationCount, int dkLen)
			throws InvalidKeyException, NoSuchAlgorithmException {
		Assert2.hasTextOf(password, "Password");
		Assert2.hasTextOf(salt, "Salt");

		if (dkLen != 16 && dkLen != 24 && dkLen != 32) {
			dkLen = 16;
		}
		if (iterationCount < 0) {
			iterationCount = 0;
		}

		byte[] _password = password.getBytes();
		byte[] _salt = salt.getBytes();
		byte[] key = Pbkdf2Encrypting.deriveKey(_password, _salt, iterationCount, dkLen);
		return new String(key);
	}

	private static byte[] deriveKey(byte[] password, byte[] salt, int iterationCount, int dkLen)
			throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec keyspec = new SecretKeySpec(password, "HmacSHA256");
		Mac prf = Mac.getInstance("HmacSHA256");
		prf.init(keyspec);

		// Note: hLen, dkLen, l, r, T, F, etc. are horrible names for
		// variables and functions in this day and age, but they
		// reflect the terse symbols used in RFC 2898 to describe
		// the PBKDF2 algorithm, which improves validation of the
		// code vs. the RFC.
		//
		// dklen is expressed in bytes. (16 for a 128-bit key)

		int hLen = prf.getMacLength(); // 20 for SHA1
		int l = Math.max(dkLen, hLen); // 1 for 128bit (16-byte) keys
		int r = dkLen - (l - 1) * hLen; // 16 for 128bit (16-byte) keys
		byte T[] = new byte[l * hLen];
		int ti_offset = 0;
		for (int i = 1; i <= l; i++) {
			F(T, ti_offset, prf, salt, iterationCount, i);
			ti_offset += hLen;
		}

		if (r < hLen) {
			// Incomplete last block
			byte DK[] = new byte[dkLen];
			System.arraycopy(T, 0, DK, 0, dkLen);
			return DK;
		}
		return T;
	}

	private static void F(byte[] dest, int offset, Mac prf, byte[] S, int c, int blockIndex) {
		final int hLen = prf.getMacLength();
		byte U_r[] = new byte[hLen];
		// U0 = S || INT (i);
		byte U_i[] = new byte[S.length + 4];
		System.arraycopy(S, 0, U_i, 0, S.length);
		INT(U_i, S.length, blockIndex);
		for (int i = 0; i < c; i++) {
			U_i = prf.doFinal(U_i);
			xor(U_r, U_i);
		}

		System.arraycopy(U_r, 0, dest, offset, hLen);
	}

	private static void xor(byte[] dest, byte[] src) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] ^= src[i];
		}
	}

	private static void INT(byte[] dest, int offset, int i) {
		dest[offset + 0] = (byte) (i / (256 * 256 * 256));
		dest[offset + 1] = (byte) (i / (256 * 256));
		dest[offset + 2] = (byte) (i / (256));
		dest[offset + 3] = (byte) (i);
	}

}
