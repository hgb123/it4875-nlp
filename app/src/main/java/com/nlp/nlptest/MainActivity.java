package com.nlp.nlptest;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;

import vn.edu.hust.student.lucenesearch.Data;
import vn.edu.hust.student.lucenesearch.LuceneIndex;
import vn.edu.hust.student.lucenesearch.SearchResult;

public class MainActivity extends AppCompatActivity {

    EditText txtQuery;
    Button btnSearch;
    Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        txtQuery = (EditText) findViewById(R.id.txtQuery);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnClear = (Button) findViewById(R.id.btnClear);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtQuery.setText("");
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = txtQuery.getText().toString();
                System.out.println("on start ne.");
//        InputStream Is = this.getResources().openRawResource();
//        LuceneIndexOnAndroid li = new LuceneIndexOnAndroid();
                LuceneIndex li = new LuceneIndex();
//        try {
//            li.test();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
                String[] list = new String[0];
                ArrayList<InputStream> iss = new ArrayList<InputStream>();
                ArrayList<Data> datas = new ArrayList<Data>();
                try {
                    list = getAssets().list("truyen/14");
                    for(String l : list){
                        InputStream is = getAssets().open("truyen/14/" + l);
                        if (is != null){
                            datas.add(li.ISToArrStrings(is, l, "\\.\\s*\n"));
////                    datas.add(li.ISToArrStrings(is, l, "\n"));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }



                String LOG_TAG = "nlp_log";
                InputStream is = null;
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                intent.putExtra("query", query);
                try {
                    ArrayList<SearchResult> searchResults = li.runFromData(datas, query);
                    for(int i = 0; (i < searchResults.size()) && (i < 1); i++){
                        Log.d(LOG_TAG, "======= " + i + " =======");
                        SearchResult sr = searchResults.get(i);
                        intent.putExtra("truyenId", "14");
                        Log.d(LOG_TAG, "chapter: " + sr.getChapter());
                        intent.putExtra("chapter", sr.getChapter());
                        Log.d(LOG_TAG, "lineIndex: " + sr.getLineIndex());
                        intent.putExtra("lineIndex", sr.getLineIndex());
                        Log.d(LOG_TAG, "content:\n" + sr.getContent());
                        intent.putExtra("content", sr.getContent());
                        Log.d(LOG_TAG, "==============");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();



//        for(String l : list){
//            System.out.println(l);
//        }
//        InputStream is = null;
//        try {
//            is = getAssets().open("truyen/14/truyen14-chuong1.txt");
//            InputStreamReader isr = new InputStreamReader(is);
//            BufferedReader br = new BufferedReader(isr);
//            String buf = br.readLine();
//            while (buf != null){
//                System.out.println(buf);
//                buf = br.readLine();
//            }
//            br.close();
//            isr.close();
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }





        System.out.println("end of on start.");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
