package upscaleapps.olympiad.Register;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;

import upscaleapps.olympiad.Login.LoginActivity;
import upscaleapps.olympiad.Profile.ProfileFragment;
import upscaleapps.olympiad.Register.RegisterActivityA;
import upscaleapps.olympiad.R;


public class RegisterActivityA extends AppCompatActivity implements View.OnClickListener{

    private ImageView regIV;
    private EditText regNameET;
    private EditText regAgeET;
    private Spinner  regGenderSP;
    private EditText regLocationET;

    private String imageText;
    private String nameText;
    private String ageText;
    private String genderText;
    private String locationText;

    private FirebaseDatabase db;
    private DatabaseReference fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_a);

        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");

        regIV = (ImageView) findViewById(R.id.regIV);
        regNameET = (EditText) findViewById(R.id.regReasonET);
        regAgeET = (EditText) findViewById(R.id.regAgeET);
        regGenderSP = (Spinner) findViewById(R.id.regGenderSP);
        regLocationET = (EditText) findViewById(R.id.regLocationET);
        Button backBT = (Button) findViewById(R.id.backBT);
        Button nextBT = (Button) findViewById(R.id.nextBT);

        backBT.setOnClickListener(this);
        nextBT.setOnClickListener(this);

        String[] gender = {"Gender", "Male", "Female", "Other"};

        final ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, gender);

        regGenderSP.setAdapter(adapterGender);
        // Selected Gender
        regGenderSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderText = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.backBT:
                back();
                break;

            case R.id.nextBT:
                next();
                break;
        }
    }

    //Go back to login
    public void back(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //Go to register B
    public void next(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            nameText = regNameET.getText().toString();
            ageText = regAgeET.getText().toString();
            locationText = regLocationET.getText().toString();

            fb.child(user.getUid()).child("name").setValue(nameText);
            fb.child(user.getUid()).child("age").setValue(ageText);
            fb.child(user.getUid()).child("gender").setValue(genderText);
            fb.child(user.getUid()).child("location").setValue(locationText);
        }
        Intent intent = new Intent(this, RegisterActivityB.class);
        startActivity(intent);
    }

    private class getProfileImage extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        getProfileImage(ImageView imageView){
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap b = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                b = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return b;
        }

        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
}
