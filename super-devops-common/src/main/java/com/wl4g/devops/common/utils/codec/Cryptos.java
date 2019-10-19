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
package com.wl4g.devops.common.utils.codec;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.RSAPublicKeySpec;

import java.util.regex.Pattern;
import java.util.Random;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Crypto utility
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月20日
 * @since http://cryptojs.altervista.org/publickey/doc/doc_rsa_java.html
 */
public abstract class Cryptos {

	/**
	 * AES
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年1月20日
	 * @since
	 */
	public class AES {
		private final String ALGORITHM = "AES"; // symmetric algorithm for data
												// encryption
		private final String MODE = "CBC";
		private final String PADDING = "PKCS5Padding"; // Padding for symmetric
														// algorithm
		private final String CIPHER_TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;
		private Charset PLAIN_TEXT_ENCODING = Charset.forName("UTF-8"); // character-encoding
		/**
		 * symmetric key size (128, 192, 256) if using 256 you must have the
		 * Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction
		 * Policy Files installed
		 */
		private int KEY_SIZE_BITS = 128;
		private int KEY_SIZE_BYTES = KEY_SIZE_BITS / 8;

		private Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
		private byte[] ivBytes = new byte[KEY_SIZE_BYTES];
		private SecretKey key;

		public AES() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException,
				InvalidParameterSpecException, InvalidKeyException, InvalidAlgorithmParameterException {
			KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
			kgen.init(KEY_SIZE_BITS);
			this.key = kgen.generateKey();
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
			this.ivBytes = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		}

		public String getIVAsHex() {
			return Util.byteArrayToHexString(ivBytes);
		}

		public String getKeyAsHex() {
			return Util.byteArrayToHexString(key.getEncoded());
		}

		public void setStringToKey(String keyText) throws NoSuchAlgorithmException, UnsupportedEncodingException {
			setKey(keyText.getBytes());
		}

		public void setHexToKey(String hexKey) {
			setKey(Util.hexStringToByteArray(hexKey));
		}

		public void setKey(byte[] bArray) {
			byte[] bText = new byte[KEY_SIZE_BYTES];
			int end = Math.min(KEY_SIZE_BYTES, bArray.length);
			System.arraycopy(bArray, 0, bText, 0, end);
			key = new SecretKeySpec(bText, ALGORITHM);
		}

		public void setStringToIV(String ivText) {
			setIV(ivText.getBytes());
		}

		public void setHexToIV(String hexIV) {
			setIV(Util.hexStringToByteArray(hexIV));
		}

		public void setIV(byte[] bArray) {
			byte[] bText = new byte[KEY_SIZE_BYTES];
			int end = Math.min(KEY_SIZE_BYTES, bArray.length);
			System.arraycopy(bArray, 0, bText, 0, end);
			ivBytes = bText;
		}

		public byte[] generateIV() {
			byte[] iv = Util.getRandomBytes(KEY_SIZE_BYTES);
			return iv;
		}

		public String encrypt(String plaintext, String passphrase) throws NoSuchAlgorithmException, UnsupportedEncodingException,
				InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
			if (plaintext.length() == 0)
				return null;

			setStringToKey(passphrase);

			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));
			byte[] encrypted = cipher.doFinal(plaintext.getBytes(PLAIN_TEXT_ENCODING));
			return Util.byteArrayToBase64String(encrypted);
		}

		public String decrypt(String ciphertext, String passphrase) throws NoSuchAlgorithmException, UnsupportedEncodingException,
				InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
			if (ciphertext.length() == 0)
				return null;

			setStringToKey(passphrase);

			byte[] dec = Util.base64StringToByteArray(ciphertext);
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
			byte[] decrypted = cipher.doFinal(dec);
			return new String(decrypted, PLAIN_TEXT_ENCODING);
		}
	}

	/**
	 * PBKDF2 deriveKey
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年1月20日
	 * @since
	 */
	private static class PBKDF2 {

		private PBKDF2() {
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

	/**
	 * HASH
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年1月20日
	 * @since
	 */
	public static class HASH {

		private HASH() {
		}

		public String MD2(String message) throws NoSuchAlgorithmException {
			MessageDigest md2 = MessageDigest.getInstance("MD2");
			byte[] array = md2.digest(message.getBytes());
			return arrayToString(array);
		}

		public String MD5(String message) throws NoSuchAlgorithmException {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] array = md5.digest(message.getBytes());
			return arrayToString(array);
		}

		public String SHA1(String message) throws NoSuchAlgorithmException {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			byte[] array = sha1.digest(message.getBytes());
			return arrayToString(array);
		}

		public String SHA256(String message) throws NoSuchAlgorithmException {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] array = sha256.digest(message.getBytes());
			return arrayToString(array);
		}

		public String SHA384(String message) throws NoSuchAlgorithmException {
			MessageDigest sha384 = MessageDigest.getInstance("SHA-384");
			byte[] array = sha384.digest(message.getBytes());
			return arrayToString(array);
		}

		public String SHA512(String message) throws NoSuchAlgorithmException {
			MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
			byte[] array = sha512.digest(message.getBytes());
			return arrayToString(array);
		}

		private String arrayToString(byte[] array) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		}

	}

	/**
	 * RSA
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年1月20日
	 * @since
	 */
	public static class RSA {
		private Charset PLAIN_TEXT_ENCODING = Charset.forName("UTF-8");
		private int KEY_SIZE_BITS = 1024;

		private PublicKey publicKey;
		private PrivateKey privateKey;
		private BigInteger modulus;
		private BigInteger exponent;
		private Cipher cipher;
		private KeyFactory fact;

		public RSA() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
				UnsupportedEncodingException, InvalidKeyException {
			cipher = Cipher.getInstance("RSA");
			fact = KeyFactory.getInstance("RSA");
			this.initialCryptoKeys(KEY_SIZE_BITS);
		}

		public boolean initialCryptoKeys(int KeySize) throws NoSuchAlgorithmException, InvalidKeySpecException {
			if (KeySize <= 0)
				return false;
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(KeySize);
			KeyPair kp = kpg.genKeyPair();
			this.publicKey = kp.getPublic();
			this.privateKey = kp.getPrivate();

			RSAPublicKeySpec pub = (RSAPublicKeySpec) fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
			this.modulus = pub.getModulus();
			this.exponent = pub.getPublicExponent();
			return true;
		}

		public BigInteger getModulus() {
			return modulus;
		}

		public BigInteger getExponent() {
			return exponent;
		}

		public PublicKey getPublicKey() {
			return publicKey;
		}

		public PrivateKey getPrivateKey() {
			return privateKey;
		}

		public String encrypt(String plaintext) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
			if (plaintext.length() == 0)
				return null;
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encrypted = cipher.doFinal(plaintext.getBytes());
			return Util.byteArrayToBase64String(encrypted);
		}

		public String decrypt(String ciphertext) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
			if (ciphertext.length() == 0)
				return null;
			byte[] dec = Util.base64StringToByteArray(ciphertext);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decrypted = cipher.doFinal(dec);
			return new String(decrypted, PLAIN_TEXT_ENCODING);
		}

	}

	/**
	 * Crypto utils
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年1月20日
	 * @since
	 */
	public static abstract class Util {

		public static String pbkdf2(String password, String salt, int iterationCount, int dkLen)
				throws InvalidKeyException, NoSuchAlgorithmException {
			if (dkLen != 16 && dkLen != 24 && dkLen != 32) {
				dkLen = 16;
			}
			if (iterationCount < 0) {
				iterationCount = 0;
			}

			byte[] _password = password.getBytes();
			byte[] _salt = salt.getBytes();
			byte[] key = PBKDF2.deriveKey(_password, _salt, iterationCount, dkLen);
			return new String(key);
		}

		public static byte[] getRandomBytes(int len) {
			if (len < 0) {
				len = 8;
			}
			Random ranGen = new SecureRandom();
			byte[] aesKey = new byte[len];
			ranGen.nextBytes(aesKey);
			return aesKey;
		}

		public static String byteArrayToHexString(byte[] raw) {
			StringBuilder sb = new StringBuilder(2 + raw.length * 2);
			sb.append("0x");
			for (int i = 0; i < raw.length; i++) {
				sb.append(String.format("%02X", Integer.valueOf(raw[i] & 0xFF)));
			}
			return sb.toString();
		}

		public static byte[] hexStringToByteArray(String hex) {
			Pattern replace = Pattern.compile("^0x");
			String s = replace.matcher(hex).replaceAll("");

			byte[] b = new byte[s.length() / 2];
			for (int i = 0; i < b.length; i++) {
				int index = i * 2;
				int v = Integer.parseInt(s.substring(index, index + 2), 16);
				b[i] = (byte) v;
			}
			return b;
		}

		public static String byteArrayToBase64String(byte[] raw) {
			return new String(Base64Coder.encode(raw));
		}

		public static byte[] base64StringToByteArray(String str) {
			return Base64Coder.decode(str);
		}

		public static String base64Encode(String str) {
			return Base64Coder.encodeString(str);
		}

		public static String base64Decode(String str) {
			return Base64Coder.decodeString(str);
		}
	}

	/**
	 * Base64 coder
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年1月20日
	 * @since
	 */
	private static abstract class Base64Coder {

		/**
		 * Mapping table from 6-bit nibbles to Base64 characters.
		 */
		private static final char[] map1 = new char[64];

		static {
			int i = 0;
			for (char c = 'A'; c <= 'Z'; c++)
				map1[i++] = c;
			for (char c = 'a'; c <= 'z'; c++)
				map1[i++] = c;
			for (char c = '0'; c <= '9'; c++)
				map1[i++] = c;
			map1[i++] = '+';
			map1[i++] = '/';
		}

		/**
		 * Mapping table from Base64 characters to 6-bit nibbles.
		 */
		private static final byte[] map2 = new byte[128];

		static {
			for (int i = 0; i < map2.length; i++)
				map2[i] = -1;
			for (int i = 0; i < 64; i++)
				map2[map1[i]] = (byte) i;
		}

		private Base64Coder() {
		}

		/**
		 * Encodes a string into Base64 format. No blanks or line breaks are
		 * inserted.
		 * 
		 * @param s
		 *            A String to be encoded.
		 * @return A String containing the Base64 encoded data.
		 */
		public static String encodeString(String s) {
			return new String(encode(s.getBytes()));
		}

		/**
		 * Encodes a byte array into Base64 format. No blanks or line breaks are
		 * inserted in the output.
		 * 
		 * @param in
		 *            An array containing the data bytes to be encoded.
		 * @return A character array containing the Base64 encoded data.
		 */
		public static char[] encode(byte[] in) {
			return encode(in, 0, in.length);
		}

		/**
		 * Encodes a byte array into Base64 format. No blanks or line breaks are
		 * inserted in the output.
		 * 
		 * @param in
		 *            An array containing the data bytes to be encoded.
		 * @param iOff
		 *            Offset of the first byte in <code>in</code> to be
		 *            processed.
		 * @param iLen
		 *            Number of bytes to process in <code>in</code>, starting at
		 *            <code>iOff</code>.
		 * @return A character array containing the Base64 encoded data.
		 */
		public static char[] encode(byte[] in, int iOff, int iLen) {
			int oDataLen = (iLen * 4 + 2) / 3; // output length without padding
			int oLen = ((iLen + 2) / 3) * 4; // output length including padding
			char[] out = new char[oLen];
			int ip = iOff;
			int iEnd = iOff + iLen;
			int op = 0;
			while (ip < iEnd) {
				int i0 = in[ip++] & 0xff;
				int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
				int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
				int o0 = i0 >>> 2;
				int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
				int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
				int o3 = i2 & 0x3F;
				out[op++] = map1[o0];
				out[op++] = map1[o1];
				out[op] = op < oDataLen ? map1[o2] : '=';
				op++;
				out[op] = op < oDataLen ? map1[o3] : '=';
				op++;
			}
			return out;
		}

		/**
		 * Decodes a string from Base64 format. No blanks or line breaks are
		 * allowed within the Base64 encoded input data.
		 * 
		 * @param s
		 *            A Base64 String to be decoded.
		 * @return A String containing the decoded data.
		 * @throws IllegalArgumentException
		 *             If the input is not valid Base64 encoded data.
		 */
		public static String decodeString(String s) {
			return new String(decode(s));
		}

		/**
		 * Decodes a byte array from Base64 format. No blanks or line breaks are
		 * allowed within the Base64 encoded input data.
		 * 
		 * @param s
		 *            A Base64 String to be decoded.
		 * @return An array containing the decoded data bytes.
		 * @throws IllegalArgumentException
		 *             If the input is not valid Base64 encoded data.
		 */
		public static byte[] decode(String s) {
			return decode(s.toCharArray());
		}

		/**
		 * Decodes a byte array from Base64 format. No blanks or line breaks are
		 * allowed within the Base64 encoded input data.
		 * 
		 * @param in
		 *            A character array containing the Base64 encoded data.
		 * @return An array containing the decoded data bytes.
		 * @throws IllegalArgumentException
		 *             If the input is not valid Base64 encoded data.
		 */
		public static byte[] decode(char[] in) {
			return decode(in, 0, in.length);
		}

		/**
		 * Decodes a byte array from Base64 format. No blanks or line breaks are
		 * allowed within the Base64 encoded input data.
		 * 
		 * @param in
		 *            A character array containing the Base64 encoded data.
		 * @param iOff
		 *            Offset of the first character in <code>in</code> to be
		 *            processed.
		 * @param iLen
		 *            Number of characters to process in <code>in</code>,
		 *            starting at <code>iOff</code>.
		 * @return An array containing the decoded data bytes.
		 * @throws IllegalArgumentException
		 *             If the input is not valid Base64 encoded data.
		 */
		public static byte[] decode(char[] in, int iOff, int iLen) {
			if (iLen % 4 != 0)
				throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
			while (iLen > 0 && in[iOff + iLen - 1] == '=')
				iLen--;
			int oLen = (iLen * 3) / 4;
			byte[] out = new byte[oLen];
			int ip = iOff;
			int iEnd = iOff + iLen;
			int op = 0;
			while (ip < iEnd) {
				int i0 = in[ip++];
				int i1 = in[ip++];
				int i2 = ip < iEnd ? in[ip++] : 'A';
				int i3 = ip < iEnd ? in[ip++] : 'A';
				if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
					throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
				int b0 = map2[i0];
				int b1 = map2[i1];
				int b2 = map2[i2];
				int b3 = map2[i3];
				if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
					throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
				int o0 = (b0 << 2) | (b1 >>> 4);
				int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
				int o2 = ((b2 & 3) << 6) | b3;
				out[op++] = (byte) o0;
				if (op < oLen)
					out[op++] = (byte) o1;
				if (op < oLen)
					out[op++] = (byte) o2;
			}
			return out;
		}

	}

	public static void main(String[] args) throws Exception {
		RSA rsa = new RSA();
		System.out.println(rsa.encrypt("123456"));
		System.out.println("private====");
		System.out.println(Util.byteArrayToBase64String(rsa.getPrivateKey().getEncoded()));
		System.out.println("public====");
		System.out.println(Util.byteArrayToBase64String(rsa.getPublicKey().getEncoded()));
	}
}