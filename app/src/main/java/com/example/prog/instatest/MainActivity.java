package com.example.prog.instatest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements AuthDialog.OnSuccesListener {
    private String token;
    private Button getCollageButton;
    private EditText editText;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        getCollageButton = (Button) findViewById(R.id.get_collage);
        getCollageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().trim().length() > 0) {
                    if (token == null || token.equals("")) {
                        showDialog();
                    } else {
                        dialog = new ProgressDialog(MainActivity.this);
                        dialog.setCancelable(true);
                        dialog.setMessage("Поиск");
                        dialog.show();
                        findUser();
                    }
                }
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (token == null || token.equals("")){
            showDialog();
        }
    }

    @Override
    public void actSucces(String code) {
        token = code;
    }

    private void showDialog(){
        String url = "https://api.instagram.com/oauth/authorize/?client_id="+getString(R.string.client_id)+"&redirect_uri="+getString(R.string.redirect)+"&response_type=code";
        AuthDialog dialog = new AuthDialog(this, url);
        dialog.setListener(this);
        dialog.show();
    }
    private void findUser(){
        AsyncModel model = new AsyncModel();
        model.setUrl("https://api.instagram.com/v1/users/search");
        model.addPairs("q", editText.getText().toString());
        model.addPairs("access_token", token);
        model.setListener(new AsyncListener() {
            @Override
            public void success(JSONObject response) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
                int code = response.optJSONObject("meta").optInt("code");
                if(code == 200){
                    JSONArray data = response.optJSONArray("data");
                    for( int i = 0; i < data.length(); i++){
                        JSONObject item = data.optJSONObject(i);
                        String userName = item.optString("username");
                        if(userName.equals(editText.getText().toString())){
                            Intent intent = new Intent(MainActivity.this, PickerActivity.class);
                            intent.putExtra("token", token);
                            long id = item.optLong("id");
                            intent.putExtra("id", id);
                            MainActivity.this.startActivity(intent);
                            return;
                        }
                    }
                    Toast.makeText(MainActivity.this, "Пользователь не найден", Toast.LENGTH_LONG).show();
                }
            }
        });
        new ApiGETAsyncTask(model).execute();
    }
}
