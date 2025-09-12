package com.mark.doc;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TextFileManager {
    private static String FILE_EXTENSION;
    private static TextFileManager instance = null;

    private File fileRoot;
    private List<String> fileList;
    private List<String> titleList;

    public static TextFileManager GetInstance() {
        if (instance == null) {
            instance = new TextFileManager();
        }
        return instance;
    }

    public void Init(Context context) {
        // 创建文本文件根目录
        fileRoot = new File(context.getFilesDir(), context.getString(R.string.text_file_root_dir));
        if (!fileRoot.exists()) {
            fileRoot.mkdirs();
        }
        FILE_EXTENSION = context.getString(R.string.text_file_ext);
        fileList = Arrays.asList(Objects.requireNonNull(fileRoot.list()));
        titleList = new LinkedList<>();
        for (String fileName : fileList) {
            String title = fileName.substring(0, fileName.length() - FILE_EXTENSION.length());
            titleList.add(title);
        }
    }

    public List<String> GetTitleList() {
        return titleList;
    }

    public void SaveTextToFile(String title, String content) {
        // 检查文件是否存在，按需创建
        if(!CreateFile(title)) {
            return;
        }

        // 写入文件
        String fileName = GetFileName(title);
        File targetFile = new File(fileRoot, fileName);
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String LoadTextFromFile(String title) {
        String fileName = GetFileName(title);
        if(!fileList.contains(fileName)) {
            return "";
        }

        File targetFile = new File(fileRoot, fileName);
        try (FileInputStream fis = new FileInputStream(targetFile)) {
            byte[] buffer = new byte[fis.available()];
            if (fis.read(buffer) > 0) {
                return new String(buffer, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    /**
     * 创建文件
     * @param title 标题
     * @return true 创建成功/文件已存在
     */
    private boolean CreateFile(String title) {
        String fileName = GetFileName(title);
        if (fileList.contains(fileName)) {
            return true;
        }

        File targetFile = new File(fileRoot, fileName);
        try {
            if (targetFile.createNewFile()) {
                AddFileIntoList(title);
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * 删除文件
     * @param title 标题
     * @return true 删除成功/文件不存在
     */
    public boolean DeleteFile(String title) {
        String fileName = GetFileName(title);
        if (fileList.contains(fileName)) {
            return true;
        }

        File targetFile = new File(fileRoot,fileName);
        return targetFile.delete();
    }

    private void AddFileIntoList(String title) {
        String fileName = GetFileName(title);
        titleList.add(title);
        fileList.add(fileName);
    }

    private void RemoveFileFromList(String title) {
        String fileName = GetFileName(title);
        titleList.remove(title);
        fileList.remove(fileName);
    }

    private static String GetFileName(String title) {
        return title + FILE_EXTENSION;
    }
}
