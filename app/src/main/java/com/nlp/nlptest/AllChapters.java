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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import vn.edu.hust.student.lucenesearch.Data;

public class AllChapters extends AppCompatActivity {

    ListView lvChapters = null;
    int truyenId;

    String[] model = null;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chapters);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        lvChapters = (ListView) findViewById(R.id.lvChapters);
        Intent intent = getIntent();
        truyenId = intent.getIntExtra("truyenId", -1);
        if (truyenId != -1){
            String[] list = null;
            ArrayList<InputStream> iss = new ArrayList<InputStream>();
            ArrayList<Data> datas = new ArrayList<Data>();
            try {
                list = getAssets().list("truyen/" + truyenId);
                Vector<String> list_ = new Vector<String>();
                for(String l : list){
                    list_.add(l);
                }
                Collections.sort(list_, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        int start = o1.indexOf("chuong");
                        int finish = o1.lastIndexOf(".");
                        int c1 = Integer.parseInt(o1.substring(start + "chuong".length(), finish));
                        start = o2.indexOf("chuong");
                        finish = o2.lastIndexOf(".");
                        int c2 = Integer.parseInt(o2.substring(start + "chuong".length(), finish));
                        return c1 - c2;
                    }
                });
//                list_.sort(new Comparator<String>() {
//                    @Override
//                    public int compare(String o1, String o2) {
//                        int start = o1.indexOf("chuong");
//                        int finish = o1.lastIndexOf(".");
//                        int c1 = Integer.parseInt(o1.substring(start + "chuong".length(), finish));
//                        start = o2.indexOf("chuong");
//                        finish = o2.lastIndexOf(".");
//                        int c2 = Integer.parseInt(o2.substring(start + "chuong".length(), finish));
//                        return c1 - c2;
//                    }
//                });
                Log.d("nlp_log", "list size: " + list.length);
                model = new String[list.length];
//                model = list;
                int index = 0;
                for(int i = 0; i < list_.size(); i++){
                    String l = list_.get(i);
                    int start = l.indexOf("chuong");
                    int finish = l.lastIndexOf(".");
                    model[index] = "Chương " + l.substring(start + "chuong".length(), finish);
                    InputStream is = getAssets().open("truyen/" + truyenId + "/" + l);
                    if (is != null){
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        model[index] += " " + br.readLine().trim();
                    }
                    index++;
                }
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, model);
                lvChapters.setAdapter(adapter);
                lvChapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent1 = new Intent(AllChapters.this, ChapterViewActivity.class);
                        intent1.putExtra("truyenId", truyenId);
                        intent1.putExtra("chapter", position + 1);
                        startActivity(intent1);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}
