package com.github.tvbox.osc.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5 {
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) { sb.append(HEX[(b >> 4) & 0xf]).append(HEX[b & 0xf]); }
        return sb.toString();
    }

    public static String encode(String res) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return toHex(md.digest(res.getBytes("UTF-8")));
        } catch (Exception e) {
            return "";
        }
    }

    public static String string2MD5(String inStr) {
        return encode(inStr == null ? "" : inStr);
    }

    public static String getFileMd5(File f) {
        try (FileInputStream in = new FileInputStream(f)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[16384];
            int len;
            while ((len = in.read(buf)) != -1) md.update(buf, 0, len);
            return toHex(md.digest());
        } catch (Exception e) {
            return "";
        }
    }
}
