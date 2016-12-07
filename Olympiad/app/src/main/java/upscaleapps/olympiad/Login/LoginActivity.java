package upscaleapps.olympiad.Login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import upscaleapps.olympiad.*;
import upscaleapps.olympiad.Login.LoginActivity;
import upscaleapps.olympiad.Register.RegisterActivityA;
import upscaleapps.olympiad.R;
import upscaleapps.olympiad.TabBar.TabBarActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText userEmailET;
    private EditText userPassET;
    private ImageView logoImage;
    private ImageView backgroundImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        userEmailET = (EditText)findViewById(R.id.userEmailET);
        userPassET = (EditText)findViewById(R.id.userPassET);
        logoImage = (ImageView)findViewById(R.id.app_logo);
        backgroundImage = (ImageView)findViewById(R.id.background);
        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button signUpButton = (Button)findViewById(R.id.signUpButton);

        InputStream rawLogo = getResources().openRawResource(R.raw.app_logo);
        InputStream rawBg = getResources().openRawResource(R.raw.app_background);
        Bitmap logo = BitmapFactory.decodeStream(rawLogo);
        Bitmap bg = BitmapFactory.decodeStream(rawBg);
        backgroundImage.setImageBitmap(bg);
        logoImage.setImageBitmap(logo);

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

        // Try Login from Saved Data
        if (readData()!=null){
        JSONObject u = readData();
            try {
                login(u.getString("email"), u.getString("password"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.loginButton:
                login(userEmailET.getText().toString(), userPassET.getText().toString());
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
        mAuth.addAuthStateListener(mAuthListener);
    }


    //Remove the listener to FirebaseAuth instance
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Login to Firebase
    public void login(final String email, final String pass){
        if(email != "" && pass != "" && email.contains("@") && email.contains(".com")){

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("SignIn Success", "signInWithEmail:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.w("SignIn Error", "signInWithEmail:failed", task.getException());
                    } else {
                        // Save User Locally
                        saveData(email, pass);

                        //Start tab bar activity
                        startTab();
                    }
                }
            });
        }
    }

    //Sign Up to Firebase
    public void signUp(){
        mAuth.createUserWithEmailAndPassword(userEmailET.getText().toString(), userPassET.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SignUp: \n", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mAuth.signInWithEmailAndPassword(userEmailET.getText().toString(), userPassET.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("SignIn Error", "signInWithEmail:failed", task.getException());
                        } else {
                            // Save User Locally
                            saveData(userEmailET.getText().toString(), userPassET.getText().toString());

                            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid())
                                    .child("email").setValue(userEmailET.getText().toString());

                            Intent intent = new Intent(LoginActivity.this, RegisterActivityA.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    //Start Tab Bar Activity
    public void startTab(){
        Intent intent = new Intent(this, TabBarActivity.class);
        startActivity(intent);
    }

    // Save User Info To Local Storage
    private Boolean saveData(String email, String pass){
        JSONObject object = new JSONObject();
        try {
            object.put("email", email);
            object.put("password", pass);
            File external = getExternalFilesDir(null);
            File file = new File(external, "user.txt");
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(object.toString());
            osw.flush();
            osw.close();
            return true;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get User Info From Local Storage
    private JSONObject readData(){
        String result = "";
        File external = this.getExternalFilesDir(null);
        File file = new File(external, "user.txt");
        if (file.exists()) {
            try {
                FileInputStream fin = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fin);
                char[] data = new char[2048];
                int size;
                try {
                    while ((size = isr.read(data)) > 0) {
                        String readData = String.copyValueOf(data, 0, size);
                        result += readData;
                        data = new char[2048];
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            JSONObject object = null;
            try {
                object = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object;
        } else {
            return null;
        }
    }
}
