package com.msg.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class IOUtil {
    private final static int NULL_LENGTH = -1;

    private final static long MAX_SPACE = 8 * 1024 * 1024;

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private IOUtil() {

    }

    /**
     * 将多个字符串组拼为路径
     * @param paths 类似："sdcard","sharesns","20111012.txt"
     * @return
     */
    public static String concatPath(String... paths) {
        StringBuilder concatenatedPath = new StringBuilder();
        for (int i = 0; i < paths.length - 1; ++i) {
            concatenatedPath.append(paths[i]);
            if (!endsWithSeparator(paths[i])) {
                concatenatedPath.append(File.separator);
            }
        }
        concatenatedPath.append(paths[paths.length - 1]);
        return concatenatedPath.toString();
    }

    private static boolean endsWithSeparator(String path) {
        return path.endsWith("/") || path.endsWith("\\");
    }

    /**
     * 获取规范路径名称
     * @param file
     * @return
     */
    public static String getCanonicalPath(File file) {
        if (file == null) {
            return null;
        }
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        } catch (NoSuchElementException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * 复制文件
     * @param srcFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File srcFile, File destFile) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean success = false;
        try {
            fis = new FileInputStream(srcFile);
        } catch (FileNotFoundException e) {
            throw new IOException(e.getMessage());
        }

        try {
            File parentFile = destFile.getAbsoluteFile().getParentFile();
            if ((parentFile != null) && (!parentFile.exists())) {
                makeDirs(parentFile);
            }

            fos = new FileOutputStream(destFile);
            int readCount;
            byte[] buffer = new byte[1024];
            while ((readCount = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, readCount);
            }
            closeStream(fis);
            closeStream(fos);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(fis);
            closeStream(fos);
            if (!success) {
                destFile.delete();
            }
        }
    }

    /**
     * 复制文件
     * @param srcFileStream
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(InputStream srcFileStream, File destFile)
            throws IOException {
        InputStream fis = null;
        FileOutputStream fos = null;
        boolean success = false;
        fis = srcFileStream;
        try {
            File parentFile = destFile.getAbsoluteFile().getParentFile();
            if ((parentFile != null) && (!parentFile.exists())) {
                makeDirs(parentFile);
            }

            fos = new FileOutputStream(destFile);
            int readCount;
            byte[] buffer = new byte[1024];
            while ((readCount = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, readCount);
            }
            closeStream(fis);
            closeStream(fos);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(fis);
            closeStream(fos);
            if (!success) {
                destFile.delete();
            }
        }
    }

    /**
     * 关闭流
     * @param stream
     */
    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    /**
     * 创建文件夹
     * @param dir
     * @throws IOException
     */
    public static void makeDirs(File dir) throws IOException {
        if (dir.exists()) {
            return;
        }
        boolean success = dir.mkdirs();
        if (!success) {
            throw new IOException("cannot create folder "
                    + dir.getAbsolutePath());
        }
    }

    /**
     * 创建文件
     * @param filename
     * @throws IOException
     */
    public static void createFile(String filename) throws IOException {
        File file = new File(filename);
        File dir = file.getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }
        file.createNewFile();
    }

    /**
     * 用来截取字符串最后一个.后面的内容
     * @param filename
     * @return
     */
    public static String getPostfix(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 用来截取字符串最后一个/后面的内容
     * @param filename
     * @return
     */
    public static String getFilename(String filename) {
        int lastSlashIndex = filename.lastIndexOf("/");
        if (lastSlashIndex == -1 || lastSlashIndex == filename.length() - 1) {
            return filename;
        }
        return filename.substring(lastSlashIndex + 1);
    }

    public static void delete(String path) {
        delete(new File(path));
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delete(f);
            }
        }
        file.delete();
    }

    public static void deleteFilesInDir(String path) {
        deleteFilesInDir(new File(path));
    }

    public static void deleteFilesInDir(File file) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            IOUtil.delete(f);
        }
    }

    public static void writeInt(ByteArrayOutputStream baos, int value) {
        baos.write(value);
        baos.write(value >> 8);
        baos.write(value >> 16);
        baos.write(value >> 24);
    }

    public static int readInt(ByteArrayInputStream bais) {
        int b1 = bais.read();
        int b2 = bais.read();
        int b3 = bais.read();
        int b4 = bais.read();
        return b1 + (b2 << 8) + (b3 << 16) + (b4 << 24);
    }

    public static void writeBytes(ByteArrayOutputStream baos, byte[] bytes) {
        if (bytes == null) {
            writeNull(baos);
        } else {
            writeInt(baos, bytes.length);
            baos.write(bytes, 0, bytes.length);
        }
    }

    public static byte[] readBytes(ByteArrayInputStream bais) {
        int length = readInt(bais);
        if (length == NULL_LENGTH) {
            return null;
        }
        byte[] bytes = new byte[length];
        bais.read(bytes, 0, bytes.length);
        return bytes;
    }

    public static void writeNull(ByteArrayOutputStream baos) {
        writeInt(baos, NULL_LENGTH);
    }

    public static BufferedReader getBufferedFileReader(File file)
            throws IOException {
        return new BufferedReader(new FileReader(file), DEFAULT_BUFFER_SIZE);
    }

    public static long getUsedSpace(File dir) {
        if ((dir == null) || (!dir.exists())) {
            return 0;
        }

        long length = 0;
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                length += getUsedSpace(file);
            }
        } else {
            length = dir.length();
        }
        return length;
    }

    /**
     * 判断指定路径文件是否超过限定大小
     * @param filename
     * @return
     */
    public static boolean isExceedLimitation(String filename) {
        return (getUsedSpace(new File(filename)) > MAX_SPACE);
    }
}