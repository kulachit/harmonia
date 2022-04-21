package com.example.harmonia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingAnimation extends AppCompatActivity {

    Timer timer;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_animation);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();

    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUIFirebase(currentUser);
    }

    public void updateUIFirebase(FirebaseUser user) {

        if(user == null){
            ImageView imageView = findViewById(R.id.loading);
            Glide
                    .with(this)
                    .load(R.drawable.loading)
                    .into(imageView);

            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(LoadingAnimation.this,LandingPageActivity.class));
                    finish();
                }
            },6000);
        }
        else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference refOrg = db.collection("instructor").document(user.getEmail());
            refOrg.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            instructor();
                            return;
                        }
                        else {
                            user();
                        }
                    }
                }
            });
        }
    }

    public void instructor() {
        FirebaseUser org = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("instructor").document(org.getEmail()).update("userID",org.getUid());

        startActivity(new Intent(this, InstructorPage.class));
        finish();
    }

    public void user() {
        startActivity(new Intent(this, HomePage.class));
        finish();
    }
}