package com.example.harmonia;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InstructorPage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private com.google.android.gms.auth.api.signin.GoogleSignInClient mGoogleSignInClient;
    private ArticleAdapter adapter;

    Article article = new Article();
    ImageView imageToUpload;
    TextView imageHint;
    TextView title;
    TextView description;
    TextView date;
    TextView content;
    String dateStr;
    Uri selectedImage;
    String imageUrl;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("images");
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;
    FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference postRef = db.collection("article");
    RecyclerView recyclerViewHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_page);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("559979544383-90q7v930qcdb21hhd5on8kimnpnec1mf.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        bottomNavigationView = findViewById(R.id.bottomNavigationViewInstructor);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.home: {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerViewInstructor, InstructorHome.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // name can be null
                            .commit();
                    onResume();
                    break;
                }
                case R.id.article: {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerViewInstructor, InstructorArticle.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // name can be null
                            .commit();
                    break;
                }
                case R.id.profile: {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerViewInstructor, InstructorProfile.class, null)
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

    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
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

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewHome = findViewById(R.id.instructor_recycler_home);
        if(recyclerViewHome != null){
            home();
        }
        TextView emailID = findViewById(R.id.instructorEmailId);
        if(emailID != null){
            emailID.setText(currentUser.getEmail());
            profile();
        }
    }

    public void profile(){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Query query = postRef.whereEqualTo("userID", currentUser.getUid());

        FirestoreRecyclerOptions<Article> options = new FirestoreRecyclerOptions.Builder<Article>().setQuery(query, Article.class).build();

        RecyclerView recyclerViewProfile = findViewById(R.id.instructor_profile_recycler_view);

        adapter = new ArticleAdapter(options);
        recyclerViewProfile.setHasFixedSize(true);
        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProfile.setAdapter(adapter);

        adapter.startListening();
    }

    public void uploadImage(View uploadimage){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void createArticle(View createevent) throws ParseException {
        addEvent();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addEvent() throws ParseException {
//        eventType = findViewById(R.id.eventType);

        title = findViewById(R.id.articleTitle);
        description = findViewById(R.id.articleDescription);
        content = findViewById(R.id.articleContent);
        date = findViewById(R.id.articleDate);
        String articleTitle = title.getText().toString();
        String articleDescription = description.getText().toString();
        String articleContent = content.getText().toString();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into edittext in this format.

        Date dateObject;

        String time;
        String dob_var = "";
        try{
            dob_var=(date.getText().toString());

            dateObject = formatter.parse(dob_var);

            dateStr = new SimpleDateFormat("dd/MM/yyyy").format(dateObject);
            //time = new SimpleDateFormat("h:mmaa").format(dateObject);
        }

        catch (java.text.ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("E11111111111", e.toString());
        }

        Toast.makeText(getBaseContext(), dateStr, Toast.LENGTH_LONG).show();
        int flag = checkForInput(articleTitle, articleDescription, dob_var, articleContent);
        if(flag == 0) {
            article.setArticleName(articleTitle);
            article.setDescription(articleDescription);
            article.setContent(articleContent);
            article.setDate(dateStr);
            article.setDateOfCreation((int) System.currentTimeMillis());

            FirebaseUser currentUser = mAuth.getCurrentUser();
            article.setUserID(currentUser.getUid());
            String myDate = dateStr;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(myDate);

            StorageReference pathReference = storageRef.child(".jpg");

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            try {
                article.setImageLink(imageUrl);

            }
            catch (Exception e){
                article.setImageLink("no image");
            }


            db.collection("article")
                    .add(article)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("success", "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Article added successfully",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 10);
                            toast.show();
                            eventAdded();
                            goBack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("error", "Error adding document", e);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Article not added, retry",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 10);
                            toast.show();
                        }
                    });
        }
    }

    public void goBack(){
        bottomNavigationView.setSelectedItemId(R.id.home);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainerViewInstructor, InstructorHome.class, null)
//                .setReorderingAllowed(true)
//                .addToBackStack("name") // name can be null
//                .commit();
    }

    public void eventAdded(){
        Upload image = new Upload();
        image.setmImageUrl(imageUrl);
        image.setName(article.getImageName());
        mDatabase.child(article.getDateOfCreation().toString()).setValue(image);;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int checkForInput(String articleTitle, String articleDescription, String dateStr, String articleContent){
        int error = 0;
        if(articleTitle.isEmpty()){
            error = 1;
            title.setBackground(getDrawable(R.drawable.red_round_border));
            title.setHint("Please enter article title");
            title.setHintTextColor(Color.RED);
        }
        else{
            title.setBackground(getDrawable(R.drawable.grey_border));
            title.setHint("Article title");
            title.setHintTextColor(Color.GRAY);
        }

        if(articleDescription.isEmpty()){
            error = 1;
            description.setBackground(getDrawable(R.drawable.red_round_border));
            description.setHint("Please enter article description");
            description.setHintTextColor(Color.RED);
        }
        else{
            description.setBackground(getDrawable(R.drawable.grey_border));
            description.setHint("Article description");
            description.setHintTextColor(Color.GRAY);
        }

        if(articleContent.isEmpty()){
            error = 1;
            content.setBackground(getDrawable(R.drawable.red_round_border));
            content.setHint("Please enter article content");
            content.setHintTextColor(Color.RED);
        }
        else{
            content.setBackground(getDrawable(R.drawable.grey_border));
            content.setHint("Article content");
            content.setHintTextColor(Color.GRAY);
        }

        if(selectedImage == null){
            error = 1;
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No image selected",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,10);
            toast.show();
        }

        return error;

    }

    private String getExtention(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }

    private void fileUploader(){
        String name = System.currentTimeMillis()+"."+getExtention(selectedImage);
        StorageReference ref = storageRef.child(name);
        article.setImageName(name);
        ref.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata() != null) {
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUrl = uri.toString();
                                        //createNewPost(imageUrl);
                                    }
                                });
                            }
                        }
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Image added",
                                Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,10);
                        toast.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Image not added",
                                Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,10);
                        toast.show();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        imageToUpload = (ImageView)findViewById(R.id.detailsImage);
        imageHint = findViewById(R.id.articleImageHint);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
            imageHint.setText("Click image to re-upload");
        }
        fileUploader();
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