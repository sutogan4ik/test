package com.example.prog.instatest;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;


public class PreviewActivity extends ActionBarActivity {
    private ImageView imageView;
    private Button sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(BitmapFactory.decodeFile("/sdcard/DCIM/Camera/collage.png"));
        sendButton = (Button) findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("image/jpeg");
                File bitmapFile = new File("/sdcard/DCIM/Camera/collage.png");
                Uri myUri = Uri.fromFile(bitmapFile);
                emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
                startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
            }
        });
    }
}
