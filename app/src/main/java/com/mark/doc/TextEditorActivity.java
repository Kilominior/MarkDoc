package com.mark.doc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class TextEditorActivity extends Activity {
    private android.widget.EditText titleText;
    private android.widget.EditText contentText;

    private String savedTitle;
    private String savedContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        titleText = (EditText) findViewById(R.id.editText_title);
        contentText = (EditText) findViewById(R.id.editText_content);


        Button returnBtn = (Button) findViewById(R.id.button_back);
        returnBtn.setOnClickListener(v -> {
            ReturnToMainActivity();
        });

        Button saveBtn = (Button) findViewById(R.id.button_save);
        saveBtn.setOnClickListener(v -> {
            SaveText();
        });

        Button exportBtn = (Button) findViewById(R.id.button_export);
        exportBtn.setOnClickListener(v -> {
            ExportText();
        });

        Button deleteBtn = (Button) findViewById(R.id.button_delete);
        deleteBtn.setOnClickListener(v -> {
            DeleteText();
        });

        Init(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Init(intent);
    }

    private void Init(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            String title = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (title != null && !title.equals("")) {
                String content = TextFileManager.GetInstance().LoadTextFromFile(title);
                SaveTitleAndContent(title, content);
            }
            else {
                SaveTitleAndContent("", "");
            }
        }
    }

    private void SaveText() {
        String title = titleText.getText().toString();
        String content = contentText.getText().toString();
        if (IsTitleChanged() && !savedTitle.equals("")) {
            if(IsContentChanged()) {
                TextFileManager.GetInstance().DeleteFile(savedTitle);
            }
            else {
                // 标题改变后，如果没有重名的文件，可直接重命名原有的文件
                if(!TextFileManager.GetInstance().IsFileExist(title)) {
                    TextFileManager.GetInstance().RenameFile(savedTitle, title);
                    return;
                }
            }
        }

        // 若尝试把一个文件改为与现有文件同名，则不覆写现有文件
        boolean override = true;
        if (IsTitleChanged() && TextFileManager.GetInstance().IsFileExist(title)) {
            TextFileManager.GetInstance().DeleteFile(savedTitle);
            override = false;
        }
        String writtenTitle = TextFileManager.GetInstance().SaveTextToFile(title, content, override);
        SaveTitleAndContent(writtenTitle, content);
    }

    private void ExportText() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, contentText.getText().toString());
        Intent shareIntent = Intent.createChooser(intent, null);
        startActivity(shareIntent);
    }

    private void DeleteText() {
        TextFileManager.GetInstance().DeleteFile(savedTitle);
        ReturnToMainActivity();
    }

    private void ReturnToMainActivity() {
        Intent intent = new Intent();
        ComponentName textEditorName = new ComponentName("com.mark.doc", "com.mark.doc.MainActivity");
        intent.setComponent(textEditorName);
        startActivity(intent);
    }

    private void SaveTitleAndContent(String title, String content) {
        titleText.setText(title);
        contentText.setText(content);
        savedTitle = title;
        savedContent = content;
    }

    private boolean IsChanged() {
        return IsTitleChanged() || IsContentChanged();
    }

    private boolean IsTitleChanged() {
        return !savedTitle.equals(titleText.getText().toString());
    }

    private boolean IsContentChanged() {
        return !savedContent.equals(contentText.getText().toString());
    }
}