package com.hankcs.hanlp.corpus.io;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.hankcs.hanlp.utility.Predefine.logger;

/**
 * 智能搜索文件
 * Created by IDEA
 * User: hztancong
 * Date: 2017/12/14
 * Time: 14:06
 */

public class SmartFileIOAdapter implements IIOAdapter {
    /**
     * 1. 尝试直接读取文件
     * 2. 针对web性，如果读取不到，则尝试在根目录下进行搜索并读取
     *
     * 打开一个文件以供读取
     *
     * @param path 文件路径
     * @return 一个输入流
     * @throws IOException 任何可能的IO异常
     */
    @Override
    public InputStream open(String path) throws IOException {
        File file = new File(path);
        if(file.exists()){
            return new FileInputStream(file);
        }

        // 尝试从根目录下读取文件
        URI uri = getUri(path);
        // 尝试从jar包中读取资源文件
        if (uri == null || uri.getPath() == null){
            logger.warning("Try get file from jar " + path);
            return IOUtil.getResourceAsStream("/" + path);
        }

        return new FileInputStream(uri.getPath());
    }

    /**
     * 1. 尝试直接创建文件
     * 2. 针对web应用，尝试在根目录下进行搜索
     *
     * 创建一个新文件以供输出
     *
     * @param path 文件路径
     * @return 一个输出流
     * @throws IOException 任何可能的IO异常
     */
    @Override
    public OutputStream create(String path) throws IOException {
        int idx = path.lastIndexOf("/");
        String prefix = path;
        String fileName = path;
        if(idx != -1){
            prefix = path.substring(0, idx);
            fileName = path.substring(idx + 1, path.length());
        }

        File file = new File(prefix);
        if(file.exists() && file.isDirectory()){
            return new FileOutputStream(path);
        }

        URI uri = getUri(prefix);
        if (uri == null){
            return null;
        }

        fileName = uri.getPath() + "/" + fileName;
        return new FileOutputStream(fileName);
    }

    private URI getUri(String path) {
        URI uri ;
        try {
            logger.info("read from: " + path);
            URL url = this.getClass().getClassLoader().getResource(path);
            if(url == null){
                return null;
            }
            uri = url.toURI();
            logger.info("the whole path: " + uri.getPath());
        } catch (URISyntaxException e) {
            return null;
        }
        return uri;
    }
}