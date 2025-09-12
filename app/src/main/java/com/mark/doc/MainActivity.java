package com.mark.doc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private Context context;
    private LayoutInflater inflater;
    private RecyclerView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleView = (RecyclerView) findViewById(R.id.recyclerView_title_list);
        context = getApplicationContext();
        inflater = getLayoutInflater();
        TextFileManager.GetInstance().Init(context);

        Button createButton = findViewById(R.id.button_create);
        createButton.setOnClickListener(v -> {
            IntentToTextEditor("");
        });

        RefreshTitleView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshTitleView();
    }

    private void RefreshTitleView() {
        List<String> titleList = TextFileManager.GetInstance().GetTitleList();
        if (titleList.isEmpty()) {
            View view = inflater.inflate(R.layout.text_file_no_file_exist_hint, titleView, true);
            return;
        }

        // 存在现有的文本文件
        ArrayList<View> viewList = new ArrayList<>(titleList.size());
        for (String title: titleList)
        {
            viewList.add(CreateTitleCellView(title));
        }

        titleView.addTouchables(viewList);
    }

    private View CreateTitleCellView(String title)
    {
        View view = inflater.inflate(R.layout.title_cell_view, titleView);

        TextView titleTextView = view.findViewById(R.id.text_title);
        titleTextView.setText(title);

        Button openButton = view.findViewById(R.id.button_open);
        openButton.setOnClickListener(v -> {
            IntentToTextEditor(title);
        });

        Button deleteButton = view.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(v -> {
            if (TextFileManager.GetInstance().DeleteFile(title)) {
                titleView.removeView(view);
            }
        });

        return view;
    }

    private void IntentToTextEditor(String title)
    {
        Intent intent = new Intent();
        ComponentName textEditorName = new ComponentName("com.mark.doc", "com.mark.doc.TextEditorActivity");
        intent.setComponent(textEditorName);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
