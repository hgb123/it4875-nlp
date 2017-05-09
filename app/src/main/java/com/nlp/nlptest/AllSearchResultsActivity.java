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
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

import vn.edu.hust.student.lucenesearch.SearchResult;

public class AllSearchResultsActivity extends AppCompatActivity {

    ArrayList<ParcelableSearchResult> model = null;
    ArrayAdapter<ParcelableSearchResult> adapter = null;
    ListView lvSearchResults;

    int truyenId;
    static String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_search_results);
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

        lvSearchResults = (ListView) findViewById(R.id.lvSearchResults);

        Intent intent = getIntent();
//        ParcelableSearchResult[] psrs = (ParcelableSearchResult[]) intent.getParcelableArrayExtra("parcel");
        ArrayList<ParcelableSearchResult> psrs = intent.getParcelableArrayListExtra("parcel");
        truyenId = intent.getIntExtra("truyenId", -1);
        query = intent.getStringExtra("query");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < psrs.size(); i++){
            ParcelableSearchResult psr = psrs.get(i);
            sb.append("chapter: ");
            sb.append(psr.getChapter());
            sb.append("\n");
            sb.append("lineIndex: ");
            sb.append(psr.getLineIndex());
            sb.append("\n=====\n");
        }
//        String s = sb.toString();
        String s = "";
        Toast.makeText(AllSearchResultsActivity.this, s + "\nlength: " + psrs.size(), Toast.LENGTH_SHORT).show();
        model = psrs;
        adapter = new SearchResultAdapter();
        lvSearchResults.setAdapter(adapter);

        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displaySearchResult(model.get(position), query);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void displaySearchResult(ParcelableSearchResult sr, String query){
        if (truyenId == -1){
            Toast.makeText(AllSearchResultsActivity.this, "Invalid truyenId", Toast.LENGTH_SHORT).show();
            return;
        }
        String LOG_TAG = "nlp_log";
        InputStream is = null;
        Intent intent = new Intent(AllSearchResultsActivity.this, SearchResultActivity.class);
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

    class SearchResultAdapter extends ArrayAdapter<ParcelableSearchResult>{
        SearchResultAdapter(){
            super(getApplicationContext(), android.R.layout.simple_list_item_1, model);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            SearchResultHolder searchResultHolder = null;

            if (row == null){
                row = getLayoutInflater().inflate(R.layout.row, parent, false);
                searchResultHolder = new SearchResultHolder(row);
                row.setTag(searchResultHolder);
            }
            else {
                searchResultHolder = (SearchResultHolder) row.getTag();
            }

            searchResultHolder.populateFrom(model.get(position));
            return row;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    static class SearchResultHolder{
        private TextView tvRowChapter;
        private TextView tvRowDetails;

        SearchResultHolder(View row){
            tvRowChapter = (TextView) row.findViewById(R.id.tvRowChapter);
            tvRowDetails = (TextView) row.findViewById(R.id.tvRowDetails);
        }

        void populateFrom(ParcelableSearchResult p){
            tvRowChapter.setText(p.getChapter());
            tvRowDetails.setText(Html.fromHtml(reduce(p.getContent(), query)));
        }
    }

    private static String reduce(String content, String query){
        String[] words = query.split(" ");
        for(String word: words){
            content = content.replace(word, "<b>" + word + "</b>");
        }
        int firstHit = content.indexOf("<b>");
        if (firstHit > -1){
            return content.substring(firstHit);
        }
        return content;
    }
}
