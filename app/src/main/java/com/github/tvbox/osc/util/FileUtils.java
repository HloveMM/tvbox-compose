package com.github.tvbox.osc.util;

import com.github.tvbox.osc.base.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {
    public static boolean isWeekAgo(File file) {
        return System.currentTimeMillis() - file.lastModified() > 7L * 24 * 60 * 60 * 1000;
    }

    public static File getCacheDir() {
        return App.getInstance().getCacheDir();
    }

    public static void setCacheByte(String name, byte[] data) {
        try (FileOutputStream out = new FileOutputStream(new File(getCacheDir(), name))) {
            out.write(data);
        } catch (Exception ignored) {
        }
    }

    public static String loadModule(String name) {
        String path = name.endsWith(".js") ? name : name + ".js";
        try (InputStream is = App.getInstance().getAssets().open("js/" + path)) {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int len;
            while ((len = is.read(buf)) != -1) bos.write(buf, 0, len);
            return bos.toString("UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
}
