package com.example.harmonia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ncorti.slidetoact.SlideToActView;

public class SignInPage extends AppCompatActivity {

    EditText emailText;
    EditText passwordText;
    private com.google.android.gms.auth.api.signin.GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.println(Log.ERROR,"hullloo","I HEREEE");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("559979544383-90q7v930qcdb21hhd5on8kimnpnec1mf.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();

        SlideToActView sta = findViewById(R.id.swipeSI);
        sta.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                emailText = findViewById(R.id.username);
                passwordText = findViewById(R.id.password);
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                int flag = checkForInput(email, password);
                if (flag == 1) {
                    slideToActView.resetSlider();
                }
            }
        });

        SlideToActView staGoogle = (SlideToActView) findViewById(R.id.googleSignIn);
        staGoogle.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                SignInButton signInButton = findViewById(R.id.sign_in_button);
                signInButton.performClick();
                signIn();
            }
        });
    }

    private void signIn () {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    public int checkForInput(String email, String password) {
        int error = 0;
        if (email.isEmpty()) {
            error = 1;
            emailText.setBackground(getDrawable(R.drawable.red_round_border));
            emailText.setHint("Please enter email ID");
            emailText.setHintTextColor(Color.RED);
        } else {
            emailText.setBackground(getDrawable(R.drawable.grey_border));
            emailText.setHint("Email");
            emailText.setHintTextColor(Color.WHITE);
        }

        if (password.isEmpty()) {
            error = 1;
            passwordText.setBackground(getDrawable(R.drawable.red_round_border));
            passwordText.setHint("Please Enter Password");
            passwordText.setHintTextColor(Color.RED);


        } else {
            passwordText.setBackground(getDrawable(R.drawable.grey_border));
            passwordText.setHint("Password");
            passwordText.setHintTextColor(Color.WHITE);

        }
        if (error == 0) {
            login(email, password);
        }
        return 0;
    }

    public void login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(this, new OnFailureListener() {
                    public void onFailure(@NonNull Exception e) {
                        SlideToActView sta = (SlideToActView) findViewById(R.id.swipeSI);
                        notifyUser(e.getLocalizedMessage());
                        sta.resetSlider();
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIFirebase(user);
                        }
                    }
                });
    }

    public void notifyUser(String error){
        Toast toast = Toast.makeText(getApplicationContext(),
                error,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,100);
        toast.show();
    }

    public void updateUIFirebase(FirebaseUser user) {

        if(user == null){
            //error stuff
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Success", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("error", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIFirebase(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("error", "signInWithCredential:failure", task.getException());
                            updateUIFirebase(null);
                        }
                    }
                });
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

    public void backToHome(View backToHome){
        SignInPage.this.finish();
    }


}