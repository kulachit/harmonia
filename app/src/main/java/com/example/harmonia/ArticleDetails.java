package com.example.harmonia;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ArticleDetails extends AppCompatActivity {

    TextView cardTitle;
    TextView cardDescription;
    TextView cardContent;
    TextView cardDate;
    ImageView imageViewImage;
    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        article = (Article) getIntent().getSerializableExtra("Article");

        cardTitle = findViewById(R.id.detailsTitle);
        cardDescription = findViewById(R.id.detailsDescription);
        cardContent = findViewById(R.id.detailsContent);
        cardDate = findViewById(R.id.detailsDate);
        imageViewImage = findViewById(R.id.detailsImage);

        cardTitle.setText(article.getArticleName());
        cardDescription.setText(article.getDescription());
        cardContent.setText(article.getContent());
        cardDate.setText(article.getDate());
        Picasso.get()
                .load(article.getImageLink())
                .fit()
                .centerCrop()
                .into(imageViewImage);
    }
}