package zhong.zip;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * zip解压工具
 *
 * @author aszswaz
 * @date 2021/3/3 10:42:51
 */
public class UNZIP {
    /**
     * 将windows下压缩的zip压缩包，解压到linux系统下
     *
     * @param zipFile zip文件
     * @param out     输出路径
     */
    public static void unzip(File zipFile, File out) throws Exception {
        if (!zipFile.exists()) throw new FileNotFoundException(zipFile.getAbsolutePath());
        if (!out.exists()) {
            if (!out.mkdirs()) throw new IOException("文件夹：" + out.getAbsolutePath() + "创建失败");
        }

        // 开始解压
        ZipInputStream zipInputStream = null;
        FileOutputStream fileOutputStream = null;
        byte[] buffer = new byte[1024];

        try {
            zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (Objects.nonNull(zipEntry)) {
                String fileName = zipEntry.getName().replaceAll("[\\\\/]", File.separator);// 替换成当前系统的路径分隔符号
                System.out.println("un: " + fileName);
                File file = new File(out.getAbsolutePath() + File.separator + fileName);
                // 判断文件的末尾是否为路径分隔符
                if (fileName.charAt(fileName.length() - 1) == File.separatorChar) {
                    if (!file.mkdirs()) throw new IOException("文件夹：" + file.getAbsolutePath() + "创建失败");
                } else {
                    File folder = file.getParentFile();
                    if (!folder.exists()) {
                        if (!folder.mkdirs()) throw new IOException("文件夹：" + folder.getAbsolutePath() + "创建失败");
                    }
                    fileOutputStream = new FileOutputStream(file);
                    while (true) {
                        int len = zipInputStream.read(buffer);
                        if (len == -1) break;
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.close();
                }
                zipInputStream.closeEntry();// 关闭zip中单个文件输入流
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();// 解压完毕
        } catch (Exception e) {
            if (Objects.nonNull(zipInputStream)) zipInputStream.close();
            if (Objects.nonNull(fileOutputStream)) fileOutputStream.close();
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        unzip(new File("dump/gitea-repo.zip"), new File("dump/repo"));
    }
}
