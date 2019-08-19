package com.skysean.travel.password.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.skysean.travel.password.mysql.model.Password;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

/**
 * AES 256位加密 
 * java 1.6 link:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html 
 * java 1.7 link:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html 
 * java 1.8 link:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * 
 * @author skysean
 *
 */
public class AES {
	private static String ALGORITHM = "AES/CBC/PKCS5Padding";
	private static String ENCODING = "utf-8";
	private static String MESSAGE_DIGEST = "SHA-256";
	private static String SECRET_KEY_SPEC = "AES";
	private static int IV_LEN = 16;

	public static String decrypt(String encrypted, String key, String iv) throws Exception {
		byte[] keyBytes = key.getBytes(ENCODING);
		MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST);
		byte[] digest = md.digest(keyBytes);
		SecretKeySpec secretKey = new SecretKeySpec(digest, SECRET_KEY_SPEC);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(complementKey(IV_LEN, iv.getBytes())));
		byte[] clearByte = cipher.doFinal(DatatypeConverter.parseHexBinary(encrypted));
		return new String(clearByte);
	}

	public static String encrypt(String content, String key, String iv) throws Exception {
		byte[] input = content.getBytes(ENCODING);
		MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST);
		byte[] digest = md.digest(key.getBytes(ENCODING));
		SecretKeySpec skc = new SecretKeySpec(digest, SECRET_KEY_SPEC);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(complementKey(IV_LEN, iv.getBytes())));
		byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
		int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
		cipher.doFinal(cipherText, ctLength);
		return DatatypeConverter.printHexBinary(cipherText);
	}

	private static byte[] complementKey(int len, byte[] src) {

		byte[] dest = new byte[len];
		for (int i = 0; i < len && i < src.length; i++) {
			dest[i] = src[i];
		}
		return dest;
	}
}