package com.coCloud.core.utils;

import cn.hutool.core.date.DateUtil;
import com.coCloud.core.constants.CoCloudConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

/**
 * 公用的文件工具类
 */
public class FileUtils {

    /**
     * 获取文件的后缀（包括点号）
     *
     * @param filename 文件名
     * @return 文件后缀（包括点号），如果文件名为空或不包含点号，则返回空字符串
     */
    public static String getFileSuffix(String filename) {
        // 检查文件名是否为空或者不包含点号
        if (StringUtils.isBlank(filename) || filename.lastIndexOf(CoCloudConstants.POINT_STR) == CoCloudConstants.MINUS_ONE_INT) {
            return CoCloudConstants.EMPTY_STR;
        }
        // 返回文件名中最后一个点号之后的部分，并转为小写
        return filename.substring(filename.lastIndexOf(CoCloudConstants.POINT_STR)).toLowerCase();
    }

    /**
     * 获取文件的类型（后缀，不包括点号）
     *
     * @param filename 文件名
     * @return 文件后缀（不包括点号），如果文件名为空或不包含点号，则返回空字符串
     */
    public static String getFileExtName(String filename) {
        // 检查文件名是否为空或者不包含点号
        if (StringUtils.isBlank(filename) || filename.lastIndexOf(CoCloudConstants.POINT_STR) == CoCloudConstants.MINUS_ONE_INT) {
            return CoCloudConstants.EMPTY_STR;
        }
        return filename.substring(filename.lastIndexOf(CoCloudConstants.POINT_STR) + CoCloudConstants.ONE_INT).toLowerCase();
    }

    /**
     * 通过文件大小转化文件大小的展示名称
     *
     * @param totalSize 文件大小（字节数）
     * @return 文件大小的字符串表示，如果文件大小为 null，则返回空字符串
     */
    public static String byteCountToDisplaySize(Long totalSize) {
        if (Objects.isNull(totalSize)) {
            return CoCloudConstants.EMPTY_STR;
        }
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(totalSize);
    }

    /**
     * 批量删除物理文件
     *
     * @param realFilePathList 文件路径列表
     */
    public static void deleteFiles(List<String> realFilePathList) throws IOException {
        if (CollectionUtils.isEmpty(realFilePathList)) {
            return;
        }
        // 遍历文件路径列表并删除每个文件
        for (String realFilePath : realFilePathList) {
            org.apache.commons.io.FileUtils.forceDelete(new File(realFilePath));
        }
    }

    /**
     * 生成文件的存储路径
     * <p>
     * 生成规则：基础路径 + 年 + 月 + 日 + 随机的文件名称
     *
     * @param basePath
     * @param filename
     * @return
     */
    public static String generateStoreFileRealPath(String basePath, String filename) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(getFileSuffix(filename))
                .toString();

    }

    /**
     * 将文件的输入流写入到文件中
     * 使用底层的sendfile零拷贝来提高传输效率
     *
     * @param inputStream
     * @param targetFile
     * @param totalSize
     */
    public static void writeStream2File(InputStream inputStream, File targetFile, Long totalSize) throws IOException {
        // 创建目标文件（如果父目录不存在，则创建父目录）
        createFile(targetFile);
        // 使用“rw”模式打开目标文件的随机访问文件流
        RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
        // 获取目标文件的文件通道
        FileChannel outputChannel = randomAccessFile.getChannel();
        // 将输入流包装成可读字节通道
        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
        // 将输入流包装成可读字节通道
        outputChannel.transferFrom(inputChannel, 0L, totalSize);

        // 关闭
        inputChannel.close();
        outputChannel.close();
        randomAccessFile.close();
        inputStream.close();
    }

    /**
     * 创建文件
     * 包含父文件一起视情况去创建
     *
     * @param targetFile
     */
    public static void createFile(File targetFile) throws IOException {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        targetFile.createNewFile();
    }

    /**
     * 生成默认的文件存储路径
     * <p>
     * 生成规则：当前登录用户的文件目录 + coCloud
     *
     * @return
     */
    public static String generateDefaultStoreFileRealPath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("coCloud")
                .toString();
    }

    /**
     * 生成默认的文件分片的存储路径前缀
     *
     * @return
     */
    public static String generateDefaultStoreFileChunkRealPath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("coCloud")
                .append(File.separator)
                .append("chunks")
                .toString();
    }

    /**
     * 生成文件分片的存储路径
     * <p>
     * 生成规则：基础路径 + 年 + 月 + 日 + 唯一标识 + 随机的文件名称 + __,__ + 文件分片的下标
     *
     * @param basePath
     * @param identifier
     * @param chunkNumber
     * @return
     */
    public static String generateStoreFileChunkRealPath(String basePath, String identifier, Integer chunkNumber) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(identifier)
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(CoCloudConstants.COMMON_SEPARATOR)
                .append(chunkNumber)
                .toString();
    }

    /**
     * 追加写文件
     *
     * @param target
     * @param source
     */
    public static void appendWrite(Path target, Path source) throws IOException {
        Files.write(target, Files.readAllBytes(source), StandardOpenOption.APPEND);
    }

    /**
     * 利用零拷贝技术读取文件内容并写入到文件的输出流中
     *
     * @param fileInputStream
     * @param outputStream
     * @param length
     * @throws IOException
     */
    public static void writeFile2OutputStream(FileInputStream fileInputStream, OutputStream outputStream, long length) throws IOException {
        FileChannel fileChannel = fileInputStream.getChannel();
        WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
        fileChannel.transferTo(CoCloudConstants.ZERO_LONG, length, writableByteChannel);
        outputStream.flush();
        fileInputStream.close();
        outputStream.close();
        fileChannel.close();
        writableByteChannel.close();
    }

    /**
     * 普通的流对流数据传输
     *
     * @param inputStream
     * @param outputStream
     */
    public static void writeStream2StreamNormal(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != CoCloudConstants.MINUS_ONE_INT) {
            outputStream.write(buffer, CoCloudConstants.ZERO_INT, len);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
    }

}
