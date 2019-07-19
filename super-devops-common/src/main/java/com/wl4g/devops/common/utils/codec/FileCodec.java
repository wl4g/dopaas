package com.wl4g.devops.common.utils.codec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author vjay
 * @date 2019-07-19 17:16:00
 */
public class FileCodec {

    public static String getFileSha1(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1024 * 1024 * 10];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                digest.update(buffer, 0, len);
            }
            String sha1 = new BigInteger(1, digest.digest()).toString(16);
            int length = 40 - sha1.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    sha1 = "0" + sha1;
                }
            }
            return sha1;
        } catch (IOException e) {
            System.out.println(e);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return null;
    }


    public static String getFileMD5(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024 * 1024 * 10];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                digest.update(buffer, 0, len);
            }
            String md5 = new BigInteger(1, digest.digest()).toString(16);
            int length = 32 - md5.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    md5 = "0" + md5;
                }
            }
            return md5;
        } catch (IOException e) {
            System.out.println(e);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return null;
    }


}
