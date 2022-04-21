package com.example.harmonia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void beginClick(View beginClick){
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        startActivity(new Intent(LandingPageActivity.this, ExperiencePage.class));
        finish();
    }
}