package com.mark.doc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class TextEditorActivity extends Activity {
    private android.widget.EditText contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        Button returnBtn = (Button) findViewById(R.id.button_back);
        returnBtn.setOnClickListener(v -> {
            ReturnToMainActivity();
        });

        contentText = (EditText) findViewById(R.id.editText_content);
        Button saveBtn = (Button) findViewById(R.id.button_save);
        saveBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, contentText.getText().toString());
            startActivity(intent);
        });
    }

    private void ReturnToMainActivity() {
        Intent intent = new Intent();
        ComponentName textEditorName = new ComponentName("com.mark.doc", "com.mark.doc.TextEditorActivity");
        intent.setComponent(textEditorName);
    }
}