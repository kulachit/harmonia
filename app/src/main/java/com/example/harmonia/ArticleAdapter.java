package com.example.harmonia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ArticleAdapter extends FirestoreRecyclerAdapter<Article, ArticleAdapter.ArticleHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ArticleAdapter(@NonNull FirestoreRecyclerOptions<Article> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ArticleHolder articleHolder, int i, @NonNull Article article){
        articleHolder.cardTitle.setText(article.getArticleName());
        articleHolder.cardDescription.setText(article.getDescription());
        articleHolder.cardContent.setText(article.getContent());
        articleHolder.cardDate.setText(article.getDate());
        articleHolder.cardImageLink.setText(article.getImageLink());
        Picasso.get()
                .load(article.getImageLink())
                .fit()
                .centerCrop()
                .into(articleHolder.cardImageView);

    }

    @NonNull
    @Override
    public ArticleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card, parent, false);
        return new ArticleHolder(v);
    }

    class ArticleHolder extends RecyclerView.ViewHolder{

        public TextView cardTitle;
        public TextView cardDescription;
        public TextView cardContent;
        public TextView cardDate;
        public TextView cardImageLink;
        public ImageView cardImageView;


        public ArticleHolder(@NonNull View itemView) {
            super(itemView);

            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardDescription = itemView.findViewById(R.id.cardDescription);
            cardContent = itemView.findViewById(R.id.cardContent);
            cardDate = itemView.findViewById(R.id.cardDate);
            cardImageLink = itemView.findViewById(R.id.cardImageLink);
            cardImageView = itemView.findViewById(R.id.cardImage);
        }
    }


}