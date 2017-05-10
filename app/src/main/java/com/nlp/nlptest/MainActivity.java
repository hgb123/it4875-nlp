package com.nlp.nlptest;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.speech.RecognizerIntent;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.hust.student.lucenesearch.Data;
import vn.edu.hust.student.lucenesearch.LuceneIndex;
import vn.edu.hust.student.lucenesearch.SearchResult;

public class MainActivity extends AppCompatActivity {

    EditText txtQuery;
    Button btnSearch;
    Button btnClear;
    Button btnIndexAndSearch;
    Spinner spTruyen;
    Button btnVoiceSearch;
    Button btnRead;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    int truyenId;
    Truyen[] truyens = SharedData.truyens;
    String[] model;
    ArrayAdapter<String> adapter;
    ArrayList<SearchResult> asr = null;
    String query;

    private int progress = 0;
    ProgressDialog progressDialog;
    Handler handler = new Handler();
    Boolean canIndex = true;

    static String LOG_TAG = "nlp_log";

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
        btnIndexAndSearch = (Button) findViewById(R.id.btnIndexAndSearch);
        spTruyen = (Spinner) findViewById(R.id.spTruyen);
        btnVoiceSearch = (Button) findViewById(R.id.btn_voicesearch);
        btnRead = (Button) findViewById(R.id.btnRead);

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (truyenId == -1){
                    Toast.makeText(MainActivity.this, "Chưa chọn truyện", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, AllChapters.class);
                intent.putExtra("truyenId", truyenId);
                startActivity(intent);
            }
        });


        spTruyen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                truyenId = truyens[position].getId();
//                Toast.makeText(MainActivity.this, truyenId, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, truyenId + " selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                truyenId = -1;
            }
        });

        model = new String[truyens.length];
        for (int i = 0; i < truyens.length; i++){
            model[i] = new String(truyens[i].getName());
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, model);
        spTruyen.setAdapter(adapter);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtQuery.setText("");

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = txtQuery.getText().toString();
                System.out.println("search.");
                if (truyenId == -1){
                    Toast.makeText(MainActivity.this, "Chưa chọn truyện", Toast.LENGTH_SHORT).show();
                    return;
                }
                File indexDir = getDir("nlp." + truyenId, Context.MODE_PRIVATE);
//                indexDir.mkdir();
                Toast.makeText(MainActivity.this, indexDir.toString(), Toast.LENGTH_SHORT).show();
                LuceneIndex li = null;
                try {
                    li = new LuceneIndex(indexDir);
                    ArrayList<SearchResult> searchResults = li.search(query);
                    if (searchResults.size() < 1){
                        Toast.makeText(MainActivity.this, "Truyện chưa được index.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
//                        displaySearchResult(searchResults.get(0), query);
                        displayAllSearchResults(searchResults, query);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Cannot open Index Directory", Toast.LENGTH_SHORT).show();
                    return;
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "ParseException", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        btnIndexAndSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (truyenId == -1){
                    Toast.makeText(MainActivity.this, "Chưa chọn truyện", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (canIndex){
                    canIndex = false;
                    indexTruyenHandler();
                }
                else {
                    Toast.makeText(MainActivity.this, "indexing...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput(){
        Intent intent = new Intent((RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a){
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT:{
                if (resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtQuery.setText(result.get(0));
                }
            }
        }
    }



    private void displayAllSearchResults(ArrayList<SearchResult> srs, String query){
        Intent intent = new Intent(MainActivity.this, AllSearchResultsActivity.class);
//        ParcelableSearchResult[] psrs = new ParcelableSearchResult[srs.size()];
        ArrayList<ParcelableSearchResult> psrs = new ArrayList<ParcelableSearchResult>();

        for(int i = 0; i < srs.size(); i++){
            psrs.add(new ParcelableSearchResult(srs.get(i)));
        }
//        intent.putExtra("parcel", psrs);
        intent.putParcelableArrayListExtra("parcel", psrs);
        intent.putExtra("truyenId", truyenId);
        intent.putExtra("query", query);
        startActivity(intent);

    }

    private void displaySearchResult(SearchResult sr, String query){
        String LOG_TAG = "nlp_log";
        InputStream is = null;
        Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
        intent.putExtra("query", query);
        intent.putExtra("truyenId", truyenId + "");
        Log.d(LOG_TAG, "chapter: " + sr.getChapter());
        intent.putExtra("chapter", sr.getChapter());
        Log.d(LOG_TAG, "lineIndex: " + sr.getLineIndex());
        intent.putExtra("lineIndex", sr.getLineIndex());
        Log.d(LOG_TAG, "content:\n" + sr.getContent());
        intent.putExtra("content", sr.getContent());
        Log.d(LOG_TAG, "==============");

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void indexTruyen(){
        File indexDir = getDir("nlp." + truyenId, Context.MODE_PRIVATE);
        indexDir.mkdir();
        LuceneIndex li = null;
        try {
            li = new LuceneIndex(indexDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] list = null;
        ArrayList<InputStream> iss = new ArrayList<InputStream>();
        ArrayList<Data> datas = new ArrayList<Data>();
        try {
            list = getAssets().list("truyen/" + truyenId);
            for(String l : list){
                InputStream is = getAssets().open("truyen/" + truyenId + "/" + l);
                if (is != null){
                    datas.add(li.ISToArrStrings(is, l, "\\.\\s*\n"));
////                    datas.add(li.ISToArrStrings(is, l, "\n"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        query = txtQuery.getText().toString();
        try {
            asr = li.runFromData(datas, query);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void indexTruyenHandler(){
        progress = 0;
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMax(1);
        progressDialog.setMessage("Indexing...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setProgress(progress);
                        }
                    });
                    if (progress == 1)
                        break;
                }
                progressDialog.dismiss();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                indexTruyen();
                System.out.println("done indexing");
                Intent intent = new Intent(SharedData.KEY_BROADCAST_DONE_LIST);
                canIndex = true;
                sendBroadcast(intent);
            }
        }).start();
    }

    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(SharedData.KEY_BROADCAST_DONE_LIST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SharedData.KEY_BROADCAST_DONE_LIST)){
                progress = 1;
//                updateBalance();
//                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                if (asr.size() > 0){
//                        displaySearchResult(srs.get(0), query);
                    displayAllSearchResults(asr, query);
                }
                else {
                    Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    };

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
