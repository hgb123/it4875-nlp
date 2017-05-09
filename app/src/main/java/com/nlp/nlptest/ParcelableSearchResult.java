package com.nlp.nlptest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import vn.edu.hust.student.lucenesearch.SearchResult;

/**
 * Created by NgocDon on 5/10/2017.
 */

public class ParcelableSearchResult implements Parcelable {
    String truyenId;
    String chapter;
    int lineIndex;
    String content;

//    SearchResult[] srs;

    public ParcelableSearchResult(SearchResult sr){
        this.truyenId = sr.getTruyenId();
        this.chapter =  sr.getChapter();
        this.lineIndex = sr.getLineIndex();
        this.content = sr.getContent();
//        this.srs = srs;
    }

    private ParcelableSearchResult(Parcel in){
        this.truyenId = in.readString();
        this.chapter =  in.readString();
        this.lineIndex = in.readInt();
        this.content = in.readString();
    }

    public String getTruyenId() {
        return truyenId;
    }

    public String getChapter() {
        return chapter;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getTruyenId());
        dest.writeString(this.getChapter());
        dest.writeInt(this.getLineIndex());
        dest.writeString(this.getContent());
    }

    public static final Parcelable.Creator<ParcelableSearchResult> CREATOR = new Parcelable.Creator<ParcelableSearchResult>() {

        @Override
        public ParcelableSearchResult createFromParcel(Parcel source) {
            return new ParcelableSearchResult(source);
        }

        @Override
        public ParcelableSearchResult[] newArray(int size) {
            return new ParcelableSearchResult[size];
        }
    };
}
