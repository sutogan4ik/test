package com.example.prog.instatest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PickerActivity extends ActionBarActivity {
    private String token;
    private long id;
    private ProgressDialog dialog;
    private List<Map<String, String>> data;
    private GridView gridView;
    private Context context;
    private Button button;
    private List<Bitmap> bitmaps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        context = PickerActivity.this;
        bitmaps = new ArrayList<>();
        button = (Button) findViewById(R.id.button);
        gridView = (GridView) findViewById(R.id.grid);
        data = new ArrayList<>();
        token = getIntent().getExtras().getString("token");
        id = getIntent().getExtras().getLong("id");
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setMessage("Загрузка");
        dialog.show();
        String link = "https://api.instagram.com/v1/users/" + id + "/media/recent/";
        AsyncModel model = new AsyncModel();
        model.setUrl(link);
        model.addPairs("access_token", token);
        model.setListener(new AsyncListener() {
            @Override
            public void success(JSONObject response) {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                int code = response.optJSONObject("meta").optInt("code");
                if(code == 200){
                    JSONArray data = response.optJSONArray("data");
                    parseJsonPhoto(data);
                }
                Log.d("Andrey", response.toString());
            }
        });
        new ApiGETAsyncTask(model).execute();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!(Boolean)view.getTag(R.string.key)) {
                    view.setBackgroundColor(Color.GREEN);
                    view.setTag(R.string.key, Boolean.TRUE);
                    Bitmap bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                    bitmaps.add(bitmap);
                }else{
                    view.setBackgroundColor(Color.TRANSPARENT);
                    view.setTag(R.string.key, Boolean.FALSE);
                    Bitmap bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                    bitmaps.remove(bitmap);
                }

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmaps.size() > 2) {
                    Canvas comboImage = null;
                    Bitmap first = null;
                    int width = 0;
                    int count = 0;
                    for (int i = 0; i < bitmaps.size(); i++) {
                        if (i == 0) {
                            first = bitmaps.get(i).copy(Bitmap.Config.ARGB_8888, true);
                            comboImage = new Canvas(first);
                            if (bitmaps.size() < 6) {
                                width = comboImage.getWidth() / (bitmaps.size() - 1);
                            } else {
                                width = comboImage.getWidth() / ((bitmaps.size() - 1) / 2);
                            }

                        } else {
                            if (bitmaps.size() < 6) {
                                comboImage.drawBitmap(Bitmap.createScaledBitmap(bitmaps.get(i), width, width, false), comboImage.getWidth() - width, width * (i - 1), null);
                            } else {
                                if (i < bitmaps.size() / 2) {
                                    comboImage.drawBitmap(Bitmap.createScaledBitmap(bitmaps.get(i), width, width, false), comboImage.getWidth() - width, width * (i - 1), null);

                                } else {
                                    comboImage.drawBitmap(Bitmap.createScaledBitmap(bitmaps.get(i), width, width, false), comboImage.getWidth() - width * count, comboImage.getHeight() - width, null);
                                    count++;
                                }
                            }
                        }
                    }
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream("/sdcard/DCIM/Camera/collage.png");
                        first.compress(Bitmap.CompressFormat.PNG, 50, os);
                        Log.d("Andrey", "Complite");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(PickerActivity.this, PreviewActivity.class));
                }else{
                    Toast.makeText(PickerActivity.this, "Давай еще!!!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void parseJsonPhoto(JSONArray jsonArray){
        int count = 0;
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject item = jsonArray.optJSONObject(i);
            String type = item.optString("type");
            if(type.equals("image")){
                JSONObject images = item.optJSONObject("images");
                Map<String, String> dataItem = new HashMap<>();
                dataItem.put("thumbnail", images.optJSONObject("thumbnail").optString("url"));
                dataItem.put("standard_resolution", images.optJSONObject("standard_resolution").optString("url"));
                dataItem.put("low_resolution", images.optJSONObject("low_resolution").optString("url"));
                data.add(dataItem);
                count++;
                if(count > 10){
                   break;
                }
            }
        }
        gridView.setAdapter(new PickerAdapter());
    }

    class PickerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setTag(R.string.key, Boolean.FALSE);
            Drawable drawable = context.getResources().getDrawable(android.R.drawable.ic_popup_sync);
            ImageLoader.getImage(PickerActivity.this, imageView, data.get(position).get("low_resolution"));
            imageView.setImageDrawable(drawable);
            return imageView;
        }
    }
}
