package upscaleapps.olympiad.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import upscaleapps.olympiad.R;
import upscaleapps.olympiad.Register.RegisterActivity;
import upscaleapps.olympiad.TabBar.TabBarActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText userEmailET;
    private EditText userPassET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        userEmailET = (EditText)findViewById(R.id.userEmailET);
        userPassET = (EditText)findViewById(R.id.userPassET);
        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button signUpButton = (Button)findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Success", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Error", "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.loginButton:
                login();
                break;

            case R.id.signUpButton:
                signUp();
                break;
        }
    }


    //Attach the listener to FirebaseAuth instance
    @Override
    public void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
    }


    //Remove the listener to FirebaseAuth instance
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    //Login to Firebase
    public void login(){
        if(userEmailET.getText().toString() != "" && userPassET.getText().toString() != "" &&
                userEmailET.getText().toString().contains("@") && userEmailET.getText().toString().contains(".com")){

            mAuth.signInWithEmailAndPassword(userEmailET.getText().toString(), userPassET.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("SignIn Success", "signInWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.

                    //Start tab bar activity
                    startTab();

                    if (!task.isSuccessful()) {
                        Log.w("SignIn Error", "signInWithEmail:failed", task.getException());
                    }
                }
            });
        }
    }


    //Sign Up to Firebase
    public void signUp(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    //Start Tab Bar Activity
    public void startTab(){
        Intent intent = new Intent(this, TabBarActivity.class);
        startActivity(intent);
    }
}
