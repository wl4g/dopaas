package com.wl4g.devops.doc.bean;

/**
 * @author vjay
 * @date 2020-12-22 10:32:00
 */
public class FileInfo {

    private String fileName;

    //相对路径(带文件名)
    private String path;

    private String content;

    private boolean isDir;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }
}
