package com.example.harmonia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class CategoriesPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_page);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void categoryClicked(View categoryClicked){
        String category = getResources().getResourceEntryName(categoryClicked.getId()).toString();
        Intent intent = new Intent(CategoriesPage.this, CategoryDetail.class);
        intent.putExtra("Category", category);
        startActivity(intent);
    }
}