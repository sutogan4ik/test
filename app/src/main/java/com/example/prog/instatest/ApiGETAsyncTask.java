package com.example.prog.instatest;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by prog on 14-Dec-14.
 */
public class ApiGETAsyncTask extends AsyncTask<Void, Void, JSONObject> {

    private AsyncModel model;

    public ApiGETAsyncTask(AsyncModel model) {
        this.model = model;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 5000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 7000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        String s = URLEncodedUtils.format(model.getPairs(), "UTF-8");
        HttpGet httpGet = new HttpGet(model.getUrl() + "?" + s);
        try {
            HttpResponse response = httpclient.execute(httpGet);
            String resp = EntityUtils.toString(response.getEntity());
            return new JSONObject(resp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(jsonObject != null){
            model.getListener().success(jsonObject);
        }
        super.onPostExecute(jsonObject);
    }
}
