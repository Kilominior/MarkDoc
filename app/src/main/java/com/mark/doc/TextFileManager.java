package com.mark.doc;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TextFileManager {
    private static String FILE_EXTENSION;
    private static String DEFAULT_FILE_TITLE;
    private static String TEXT_FILE_POSTFIX_LEFT;
    private static String TEXT_FILE_POSTFIX_RIGHT;
    private static int TEXT_FILE_POSTFIX_ORIGIN_INDEX;

    private static TextFileManager instance = null;

    private File fileRoot;
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

        // 获取各类strings
        FILE_EXTENSION = context.getString(R.string.text_file_ext);
        DEFAULT_FILE_TITLE = context.getString(R.string.text_file_default_title);
        TEXT_FILE_POSTFIX_LEFT = context.getString(R.string.text_file_postfix_left);
        TEXT_FILE_POSTFIX_RIGHT = context.getString(R.string.text_file_postfix_right);
        TEXT_FILE_POSTFIX_ORIGIN_INDEX = context.getResources().getInteger(R.integer.text_file_postfix_origin_index);

        List<String> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(fileRoot.list())));
        titleList = new LinkedList<>();
        for (String fileName : fileList) {
            String title = fileName.substring(0, fileName.length() - FILE_EXTENSION.length());
            titleList.add(title);
        }
    }

    public List<String> GetTitleList() {
        return titleList;
    }

    public boolean IsTitleListEmpty() {
        return titleList.isEmpty();
    }

    public boolean IsFileExist(String title) {
        return titleList.contains(title);
    }

    /**
     * 将内容写入到标题对应的文本文件中
     * @param title 标题，若为空，则采用默认标题，且关闭覆写
     * @param content 内容
     * @param override 发现同名文件时是否覆写，若为false，则会创建带有后缀的新文件
     * @return 写入的文件标题
     */
    public String SaveTextToFile(String title, String content, boolean override) {
        if(title == null || title.equals("")) {
            title = DEFAULT_FILE_TITLE;
            override = false;
        }

        if (!IsFileExist(title)) {
            CreateFile(title);
        }
        else {
            if (!override) {
                // 递增后缀，直到找到不存在的文件名，再创建
                int postfixIndex = TEXT_FILE_POSTFIX_ORIGIN_INDEX;
                String originTitle = title;
                while (IsFileExist(title)) {
                    title = originTitle + TEXT_FILE_POSTFIX_LEFT + postfixIndex++ + TEXT_FILE_POSTFIX_RIGHT;
                }
                CreateFile(title);
            }
        }


        // 写入文件
        File targetFile = GetFile(title);
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return title;
    }

    public String LoadTextFromFile(String title) {
        if(!IsFileExist(title)) {
            return "";
        }

        File targetFile = GetFile(title);
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
     * 删除文件
     * @param title 标题
     * @return true 删除成功/文件不存在
     */
    public boolean DeleteFile(String title) {
        if (!IsFileExist(title)) {
            return true;
        }

        File targetFile = GetFile(title);
        if (targetFile.delete()) {
            RemoveFileFromList(title);
            return true;
        }
        return false;
    }

    /**
     * 重命名文件
     * @param oldTitle 旧标题
     * @param newTitle 新标题
     * @return true 重命名成功
     */
    public boolean RenameFile(String oldTitle, String newTitle) {
        if(Objects.equals(oldTitle, newTitle)) {
            return true;
        }

        if(IsFileExist(oldTitle)) {
            File oldFile = GetFile(oldTitle);
            File newFile = GetFile(newTitle);
            return oldFile.renameTo(newFile);
        }

        return false;
    }

    /**
     * 创建文件
     * @param title 标题
     * @return true 创建成功
     */
    private boolean CreateFile(String title) {
        File targetFile = GetFile(title);
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

    private void AddFileIntoList(String title) {
        titleList.add(title);
    }

    private void RemoveFileFromList(String title) {
        titleList.remove(title);
    }

    private File GetFile(String title) {
        return new File(fileRoot, GetFileName(title));
    }

    private static String GetFileName(String title) {
        return title + FILE_EXTENSION;
    }
}
