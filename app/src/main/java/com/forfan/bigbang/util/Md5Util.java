package com.forfan.bigbang.util;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

	public static String md5LowerCase(String string) {
		if (TextUtils.isEmpty(string)) {
			return "";
		}
		try {
			MessageDigest digester = MessageDigest.getInstance(ALGORITHM);
			byte[] buffer = string.getBytes(DEFAULT_CHARSET);
			digester.update(buffer);
			buffer = digester.digest();
			string = toLowerCaseHex(buffer);
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (Exception e) {
		}
		return string;
	}

	public static String md5(String string) {
		try {
			MessageDigest digester = MessageDigest.getInstance(ALGORITHM);
			byte[] buffer = string.getBytes(DEFAULT_CHARSET);
			digester.update(buffer);

			buffer = digester.digest();
			string = toHex(buffer);
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (Exception e) {
		}
		return string;
	}

	public static String md5(byte[] buff) {
		try {
			MessageDigest digester = MessageDigest.getInstance(ALGORITHM);
			digester.update(buff);
			byte[] buffer = digester.digest();
			return toHex(buffer);
		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}

	public static String md5(File file) {
		InputStream fis = null;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;

		try {
			fis = new FileInputStream(file);
			md5 = MessageDigest.getInstance(ALGORITHM);
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}

			return toHex(md5.digest());
		} catch (Exception e) {
			//LogUtils.e(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					//LogUtils.e(e);
				}
			}
		}

		return null;
	}

	private static String toHex(byte[] b) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			int v = b[i];
			builder.append(HEX[(0xF0 & v) >> 4]);
			builder.append(HEX[0x0F & v]);
		}
		return builder.toString();
	}

	private static String toLowerCaseHex(byte[] b) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			int v = b[i];
			builder.append(HEX_LOWER_CASE[(0xF0 & v) >> 4]);
			builder.append(HEX_LOWER_CASE[0x0F & v]);
		}
		return builder.toString();
	}	
	
	

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static final char[] HEX_LOWER_CASE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final String ALGORITHM = "MD5";
	private static final String DEFAULT_CHARSET = "utf-8";

}
