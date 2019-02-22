package com.example.androidchatclone.Controller.Main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.androidchatclone.Controller.Home.HomeActivity;
import com.example.androidchatclone.Controller.Login.SignInActivity;
import com.example.androidchatclone.Controller.Util.Helper;
import com.example.androidchatclone.R;

public class MainActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Helper.isOnline()) {
            Intent intent = new Intent(context,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        } else {
            Intent intent = new Intent(context,SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
