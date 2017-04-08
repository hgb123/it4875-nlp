package com.nlp.nlptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String LOG_TAG = "nlp_log";
        Log.d(LOG_TAG, "onstart searchresult");
        final ScrollView svTruyen = (ScrollView) findViewById(R.id.svTruyen);
        final TextView tvTruyen = (TextView) findViewById(R.id.tvTruyen);
        Intent intent = getIntent();

        String chapter = intent.getExtras().getString("chapter");
        Log.d(LOG_TAG, chapter);
        int truyenId = Integer.parseInt(intent.getExtras().getString("truyenId"));
        String content = intent.getExtras().getString("content");
        final int lineIndex = intent.getExtras().getInt("lineIndex", 1);

        Log.d(LOG_TAG, "line index: " + lineIndex);
        String fullChapter = "";
        String[] words = intent.getExtras().getString("query").split(" ");
        int noOfLines = 0;
        int noOfChars = 0;
        int charIndex = 0;
        try {
            InputStream is = getAssets().open("truyen/" + truyenId + "/" + chapter);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String buf = br.readLine();
            StringBuilder sb = new StringBuilder();

            while (buf != null){
                noOfLines++;
                sb.append(buf);
                if (noOfLines < lineIndex){
                    charIndex += buf.length();
                }
                noOfChars += buf.length();
                sb.append("<br>");
                buf = br.readLine();
            }
            br.close();
            isr.close();
            is.close();
//            Toast.makeText(SearchResultActivity.this, "lineIndex: " + lineIndex + " / Total lines: " + noOfLines, Toast.LENGTH_LONG).show();
            fullChapter = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String s : words){
            fullChapter = fullChapter.replace(s, "<b>" + s + "</b>");
        }

        final int totalLines = noOfLines;
        final int totalChars = noOfChars;
        final int charIndexFinal = charIndex;

        tvTruyen.setText(Html.fromHtml(fullChapter));
        svTruyen.post(new Runnable() {
            @Override
            public void run() {
//                svTruyen.fullScroll(ScrollView.FOCUS_DOWN);
                //
//                svTruyen.scrollTo(0, (int) Math.floor(lineIndex * 1.0 / totalLines * tvTruyen.getHeight()));
                svTruyen.scrollTo(0, (int) Math.floor(charIndexFinal * 1.0 / totalChars * tvTruyen.getHeight()));
//                Toast.makeText(SearchResultActivity.this, "scroll view scroll: " + svTruyen.getScrollY() + " / " + svTruyen.getMaxScrollAmount(), Toast.LENGTH_LONG).show();
//                Toast.makeText(SearchResultActivity.this, "text view height: " + tvTruyen.getHeight(), Toast.LENGTH_LONG).show();


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
