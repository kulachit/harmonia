package com.example.harmonia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;


public class HomePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private com.google.android.gms.auth.api.signin.GoogleSignInClient mGoogleSignInClient;
    private ArticleAdapter adapter;

    private DatabaseReference mDatabase;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference postRef = db.collection("article");
    RecyclerView recyclerViewHome;
    RecyclerView recyclerViewArticle;
    FirebaseUser currentUser;

    private static final String CLIENT_ID = "6f999ef8bb814c07b699fee380638eb5";
    private static final String REDIRECT_URI ="http://com.example.harmonia/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    ConnectionParams connectionParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("559979544383-90q7v930qcdb21hhd5on8kimnpnec1mf.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewInstructor);

        connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();


        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.home: {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerViewUser, UserHome.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // name can be null
                            .commit();
                    onResume();
                    break;
                }
                case R.id.article: {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerViewUser, UserArticle.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // name can be null
                            .commit();
                    onResume();
                    break;
                }
                case R.id.music: {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerViewUser, UserMusic.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // name can be null
                            .commit();

                    System.out.print("I HERE HEHE >> HALLOOOO");
                    connectToSpotify();
//                    SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
//
//                        @Override
//                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
//                            mSpotifyAppRemote = spotifyAppRemote;
//                            Log.d("MainActivity", "Connected! Yay!");
//
//                            // Now you can start interacting with App Remote
//                            connected();
//                        }
//
//                        @Override
//                        public void onFailure(Throwable throwable) {
//                            Log.e("MainActivity", throwable.getMessage(), throwable);
//
//                            // Something went wrong when attempting to connect! Handle errors here
//                        }
//                    });
                    break;
                }
                case R.id.profile: {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerViewUser, UserProfile.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // name can be null
                            .commit();
                    onResume();
                    break;
                }
            }

            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home); // change to whichever id should be default
        }
    }

    public void connectToSpotify(){
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {

            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("MainActivity", "Connected! Yay!");

                // Now you can start interacting with App Remote
                connected();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("MainActivity", throwable.getMessage(), throwable);

                // Something went wrong when attempting to connect! Handle errors here
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void home(){

        Query query = postRef.orderBy("dateOfCreation", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Article> options = new FirestoreRecyclerOptions.Builder<Article>().setQuery(query, Article.class).build();

        adapter = new ArticleAdapter(options);
        recyclerViewHome.setHasFixedSize(true);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHome.setAdapter(adapter);

        adapter.startListening();
    }

    public void article(){

        Query query = postRef.orderBy("dateOfCreation", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Article> options = new FirestoreRecyclerOptions.Builder<Article>().setQuery(query, Article.class).build();

        adapter = new ArticleAdapter(options);
        recyclerViewArticle.setHasFixedSize(true);
        recyclerViewArticle.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewArticle.setAdapter(adapter);

        adapter.startListening();
    }

    public void connected(){

        System.out.print("HERE BOI");
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewHome = findViewById(R.id.userRecyclerView);
        if(recyclerViewHome != null){
            home();
        }
        recyclerViewArticle = findViewById(R.id.articleRecyclerView);
        if(recyclerViewArticle != null){
            article();
        }
        TextView emailID = findViewById(R.id.userEmailId);
        if(emailID != null){
            emailID.setText(currentUser.getEmail());
        }

    }

    public void showMore(View view){
        try {
            TextView name = view.findViewById(R.id.cardTitle);
            String nem = name.getText().toString();
            Log.println(Log.ERROR, "HELLO", nem);
            Article newArticle = new Article();

            TextView cardTitle = view.findViewById(R.id.cardTitle);
            TextView cardDescription = view.findViewById(R.id.cardDescription);
            TextView cardContent = view.findViewById(R.id.cardContent);
            TextView cardDate = view.findViewById(R.id.cardDate);
            TextView cardImageLink = view.findViewById(R.id.cardImageLink);

            String title = cardTitle.getText().toString();
            String description = cardDescription.getText().toString();
            String content = cardContent.getText().toString();
            String date = cardDate.getText().toString();
            String imageLink = cardImageLink.getText().toString();

            newArticle.setArticleName(title);
            newArticle.setDescription(description);
            newArticle.setContent(content);
            newArticle.setDate(date);
            newArticle.setImageLink(imageLink);;

            Intent intent = new Intent(this, ArticleDetails.class);
            intent.putExtra("Article", newArticle);
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void logOut(View logout){
        try{
            mAuth.getInstance().signOut();
        }
        catch (Exception e){
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(null);
                        }

                    });
        }

        startActivity(new Intent(this,MainPage.class));
        finish();
    }

    public void updateUI (GoogleSignInAccount gsia){
        if(gsia == null){
            startActivity(new Intent(this, MainPage.class));
            finish();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "couldn't logout",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,100);
            toast.show();
        }
    }
}