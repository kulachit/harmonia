package com.example.harmonia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CategoryDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        Intent intent = getIntent();
        String category = intent.getStringExtra("Category");
        String imageName = "ic_" + category;
        ImageView imageView = (ImageView) findViewById(R.id.category_choice);
        imageView.setBackgroundResource(getResources().getIdentifier(imageName, "drawable", getPackageName()));
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void beginClick(View beingCLicked){
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        startActivity(new Intent(CategoryDetail.this, MainPage.class));
    }
}