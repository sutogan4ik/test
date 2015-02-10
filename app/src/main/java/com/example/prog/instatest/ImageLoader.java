package com.example.prog.instatest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by prog on 14-Dec-14.
 */
public class ImageLoader {

    public static void getImage(Activity context, ImageView view, String url){
        new LoadImage(view, url, context).execute();
    }

    private static class LoadImage extends AsyncTask<Void, Void, Void> {
        private ImageView imageView;
        private String urlImage;
        private Activity context;

        private LoadImage(ImageView imageView, String urlImage, Activity context) {
            this.imageView = imageView;
            this.urlImage = urlImage;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(urlImage);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(input);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
