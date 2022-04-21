package com.example.harmonia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.ncorti.slidetoact.SlideToActView;

public class SignUpPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailTextSU;
    private EditText passwordTextSU;
    private EditText confPasswordTextSU;
    private com.google.android.gms.auth.api.signin.GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("559979544383-90q7v930qcdb21hhd5on8kimnpnec1mf.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SlideToActView sta = (SlideToActView) findViewById(R.id.swipeSU);
        sta.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                emailTextSU = findViewById(R.id.emailSU);
                passwordTextSU = findViewById(R.id.passwordSU);
                confPasswordTextSU = findViewById(R.id.confPasswordSU);
                String emailSU = emailTextSU.getText().toString();
                String passwordSU = passwordTextSU.getText().toString();
                String confPasswordSU = confPasswordTextSU.getText().toString();
                int flag = checkForInput(emailSU, passwordSU, confPasswordSU);
                if(flag == 1){
                    slideToActView.resetSlider();
                }
            }
        });

        SlideToActView staGoogle = (SlideToActView) findViewById(R.id.googleSignUp);
        staGoogle.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                SignInButton signInButton = findViewById(R.id.sign_in_button);
                signInButton.performClick();
                signIn();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int checkForInput(String emailSU, String passwordSU, String confPasswordSU){
        int error = 0;
        if(passwordSU.isEmpty()){
            error = 1;
            passwordTextSU.setText("");
            passwordTextSU.setBackground(getDrawable(R.drawable.red_round_border));
            passwordTextSU.setHint("Please Enter Password");
            passwordTextSU.setHintTextColor(Color.RED);
        } else {
            passwordTextSU.setBackground(getDrawable(R.drawable.grey_border));
            passwordTextSU.setHint("Password");
            passwordTextSU.setHintTextColor(Color.GRAY);

        }
        if(emailSU.isEmpty()){
            error = 1;
            emailTextSU.setBackground(getDrawable(R.drawable.red_round_border));
            emailTextSU.setHint("Please enter email ID");
            emailTextSU.setHintTextColor(Color.RED);
        }
        else{
            emailTextSU.setBackground(getDrawable(R.drawable.grey_border));
            emailTextSU.setHint("Email");
            emailTextSU.setHintTextColor(Color.GRAY);
        }


        if(confPasswordSU.isEmpty()){
            error = 1;
            confPasswordTextSU.setText("");
            confPasswordTextSU.setBackground(getDrawable(R.drawable.red_round_border));
            confPasswordTextSU.setHint("Please re-enter password");
            confPasswordTextSU.setHintTextColor(Color.RED);

        } else {
            confPasswordTextSU.setBackground(getDrawable(R.drawable.grey_border));
            confPasswordTextSU.setHint("Confirm Password");
            confPasswordTextSU.setHintTextColor(Color.GRAY);
        }
        if(error == 0){
            if(!(confPasswordSU.contentEquals(passwordTextSU.getText()))){
                error = 1;
                confPasswordTextSU.setText("");
                confPasswordTextSU.setBackground(getDrawable(R.drawable.red_round_border));
                confPasswordTextSU.setHint("Passwords do not match");
                confPasswordTextSU.setHintTextColor(Color.RED);
            }
            else{
                confPasswordTextSU.setBackground(getDrawable(R.drawable.red_round_border));
                confPasswordTextSU.setHint("Password");
                confPasswordTextSU.setHintTextColor(Color.GRAY);
                passwordTextSU.setBackground(getDrawable(R.drawable.red_round_border));
                passwordTextSU.setHint("Confirm Password");
                passwordTextSU.setHintTextColor(Color.GRAY);
            }
        }
        if(error == 0){
            login(emailSU,passwordSU);
        }
        return error;
    }

    public void login(String email, String password){
        int flag = 0;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        SlideToActView sta = (SlideToActView) findViewById(R.id.swipeSU);
                        notifyUser(e.getLocalizedMessage());
                        sta.resetSlider();
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success", "createUserWithEmail:success");
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
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,10);
        toast.show();
    }

//    public void signUp(View signup){
//        startActivity(new Intent(sign_up.this, sign_in.class));
//    }

    private void signIn () {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    public void updateUI (GoogleSignInAccount gsia){
        if(gsia == null){
            //error stuff
        }
        else{
            startActivity(new Intent(SignUpPage.this, HomePage.class));
            finish();
        }
    }

    public void backToHome(View backHome) {
        startActivity(new Intent(this,MainPage.class));
    }


    public void updateUIFirebase(FirebaseUser user) {

        if(user == null){
            //error stuff
        }
        else{
            startActivity(new Intent(SignUpPage.this, HomePage.class));
            finish();
        }
    }
}