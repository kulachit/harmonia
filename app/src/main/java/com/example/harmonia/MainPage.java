package com.example.harmonia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void signIn(View signIn){
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        startActivity(new Intent(MainPage.this, SignInPage.class));
    }

    public void signUp(View signUp){
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        startActivity(new Intent(MainPage.this, SignUpPage.class));
    }
}