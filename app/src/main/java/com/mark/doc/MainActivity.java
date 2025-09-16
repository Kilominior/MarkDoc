package com.mark.doc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {
    private Context context;
    private LayoutInflater inflater;
    private LinearLayout titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleView = (LinearLayout) findViewById(R.id.linearLayout_title_list);
        context = getApplicationContext();
        inflater = getLayoutInflater();
        TextFileManager.GetInstance().Init(context);

        Button createButton = findViewById(R.id.button_create);
        createButton.setOnClickListener(v -> {
            IntentToTextEditor("");
        });

        UpdateTitleView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateTitleView();
    }

    private void UpdateTitleView() {
        if (TryShowNoDocHint()) {
            return;
        }

        ClearTitleView();
        List<String> titleList = TextFileManager.GetInstance().GetTitleList();
        for (String title: titleList) {
            CreateTitleCellView(title);
        }
    }

    /**
     * 尝试显示“暂无Doc”提示
     * @return true：已显示提示
     */
    private boolean TryShowNoDocHint() {
        if (TextFileManager.GetInstance().IsTitleListEmpty()) {
            ClearTitleView();
            View noFileExistView = inflater.inflate(R.layout.text_file_no_file_exist_hint, titleView, true);
            return true;
        }
        return false;
    }

    private void ClearTitleView() {
        if (titleView.getChildCount() > 0) {
            titleView.removeAllViews();
        }
    }

    private void CreateTitleCellView(String title) {
        View titleCellView = inflater.inflate(R.layout.title_cell_view, titleView, false);
        TextView titleTextView = titleCellView.findViewById(R.id.text_title);
        titleTextView.setText(title);

        Button openButton = titleCellView.findViewById(R.id.button_open);
        openButton.setOnClickListener(v -> {
            IntentToTextEditor(title);
        });

        Button deleteButton = titleCellView.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(v -> {
            if (TextFileManager.GetInstance().DeleteFile(title)) {
                titleView.removeView(titleCellView);
                TryShowNoDocHint();
            }
        });

        titleView.addView(titleCellView);
    }

    private void IntentToTextEditor(String title) {
        Intent intent = new Intent();
        ComponentName textEditorName = new ComponentName("com.mark.doc", "com.mark.doc.TextEditorActivity");
        intent.setComponent(textEditorName);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, title);
        startActivity(intent);
    }
}
