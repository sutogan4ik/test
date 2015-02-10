package com.example.prog.instatest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prog on 14-Dec-14.
 */
public class AsyncModel {
    private String url;
    private AsyncListener listener;
    private List<NameValuePair> pairs;

    public AsyncModel(){
        pairs = new ArrayList<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AsyncListener getListener() {
        return listener;
    }

    public void setListener(AsyncListener listener) {
        this.listener = listener;
    }

    public List<NameValuePair> getPairs() {
        return pairs;
    }

    public void addPairs(NameValuePair pair){
        pairs.add(pair);
    }

    public void addPairs(String name, String value) {
        pairs.add(new BasicNameValuePair(name, value));
    }
}
