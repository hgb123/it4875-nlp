package com.nlp.nlptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import vn.edu.hust.CheckSpell.VietnameseSpell;

public class ChapterViewActivity extends AppCompatActivity {

    TextView tvChapter;
    int truyenId;
    int chapter;

    String originalContent = "";
    String htmlContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Check spell", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                String paragraph = tvChapter.getText().toString();
                VietnameseSpell vs = new VietnameseSpell();
                ArrayList<String> errors = vs.check(paragraph);
                Toast.makeText(ChapterViewActivity.this, "error: " + errors.size(), Toast.LENGTH_SHORT).show();
                String s = "";
                for(String e : errors){
                    s += e + "\n";
                    htmlContent = htmlContent.replaceAll(e, "<b>" + e + "</b>");
                }
                Log.d("nlp_log", s);
                System.out.println(s);
                tvChapter.setText(Html.fromHtml(htmlContent));
            }
        });

        tvChapter = (TextView) findViewById(R.id.tvChapter);

        Intent intent = getIntent();
        truyenId = intent.getIntExtra("truyenId", -1);
        chapter = intent.getIntExtra("chapter", -1);
        if ((truyenId == -1) || (chapter == -1)){
            Toast.makeText(ChapterViewActivity.this, "Missing param", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            InputStream is = getAssets().open("truyen/" + truyenId + "/truyen" + truyenId + "-chuong" + chapter + ".txt");
            if (is != null){
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String buf = br.readLine();
                if (buf != null){
                    setTitle(buf.trim());
                }
                StringBuilder sb = new StringBuilder();
                while (buf != null){
                    sb.append(buf);
                    sb.append("<br>");
                    buf = br.readLine();
                }
                htmlContent = sb.toString();
//                tvChapter.setText(s);
                tvChapter.setText(Html.fromHtml(htmlContent));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
