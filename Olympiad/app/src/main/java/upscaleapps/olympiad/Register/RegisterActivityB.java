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
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import upscaleapps.olympiad.R;
import upscaleapps.olympiad.Register.RegisterActivityB;
import upscaleapps.olympiad.TabBar.TabBarActivity;


public class RegisterActivityB extends AppCompatActivity implements View.OnClickListener {

    private ImageView regIV;
    private Spinner   regReasonSP;
    private Spinner   regTimeSP;
    private Spinner   regMotivationSP;
    private Spinner   regSkillSP;

    private String imageText;
    private String reasonText;
    private String timeText;
    private String motivationText;
    private String skillText;

    private FirebaseDatabase db;
    private DatabaseReference fb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_b);

        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");

        regIV           = (ImageView)findViewById(R.id.regIV);
        regReasonSP     = (Spinner)findViewById(R.id.regReasonSP);
        regTimeSP       = (Spinner)findViewById(R.id.regTimeSP);
        regMotivationSP = (Spinner)findViewById(R.id.regMotivationSP);
        regSkillSP      = (Spinner)findViewById(R.id.regSkillSP);
        Button backBT   = (Button)findViewById(R.id.backBT);
        Button nextBT   = (Button)findViewById(R.id.nextBT);

        String[] reasons    = {"App Use Reason", "Martial Arts", "Weightlifting", "Swimming", "Running",
                "Weight Loss", "Hiking", "Sports", "Commuting to Gym", "Going to Events"};
        String[] time       = {"Workout Time", "Morning", "Noon", "Afternoon", "Evening"};
        String[] motivation = {"Motivation", "Low", "Average", "High"};
        String[] skill      = {"Skill Level", "Beginner", "Intermediate", "Expert"};

        final ArrayAdapter<String> adapterReason =  new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, reasons);
        final ArrayAdapter<String> adapterTime =  new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, time);
        final ArrayAdapter<String> adapterMotivation =  new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, motivation);
        final ArrayAdapter<String> adapterSkill =  new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, skill);

        regReasonSP.setAdapter(adapterReason);
        regTimeSP.setAdapter(adapterTime);
        regMotivationSP.setAdapter(adapterMotivation);
        regSkillSP.setAdapter(adapterSkill);

        // Selected Reason
        regReasonSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reasonText = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        // Selected Time
        regTimeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeText = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        // Selected Motivation
        regMotivationSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                motivationText = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        // Selected Skill
        regSkillSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                skillText = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        backBT.setOnClickListener(this);
        nextBT.setOnClickListener(this);

        // Read Data if set
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        fb.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                if (snapshot != null) {
//                    // Get Image if set
//                    if (snapshot.child("image").getValue() != null) {
//                        String imageURL = snapshot.child("image").getValue().toString();
//                        new RegisterActivityB.getProfileImage(regIV).execute(imageURL);
//                    }
//                    // Get Spinner Value
//                    if (snapshot.child("reason").getValue() != null) {
//                        String reasonText = snapshot.child("reason").getValue().toString();
//                        if (reasonText != null) {
//                            int i = adapterReason.getPosition(reasonText);
//                            regReasonSP.setSelection(i);
//                        }
//                    }
//
//                    // Get Spinner Value
//                    if (snapshot.child("motivation").getValue() != null) {
//                        String motivationText = snapshot.child("motivation").getValue().toString();
//                        if (motivationText != null) {
//                            int i = adapterMotivation.getPosition(motivationText);
//                            regMotivationSP.setSelection(i);
//                        }
//                    }
//                    // Get Spinner Value
//                    if (snapshot.child("time").getValue() != null) {
//                        String timeText = snapshot.child("time").getValue().toString();
//                        if (timeText != null) {
//                            int i = adapterTime.getPosition(timeText);
//                            regTimeSP.setSelection(i);
//                        }
//                    }
//
//                    // Get Spinner Value
//                    if (snapshot.child("skill").getValue() != null) {
//                        String skillText = snapshot.child("skill").getValue().toString();
//                        if (skillText != null) {
//                            int i = adapterSkill.getPosition(skillText);
//                            regSkillSP.setSelection(i);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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

    //Go back to register A
    public void back(){
        Intent intent = new Intent(this, RegisterActivityA.class);
        startActivity(intent);
    }

    //Go to Main
    public void next(){
        // Store Data to Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            fb.child(user.getUid()).child("reason").setValue(reasonText);
            fb.child(user.getUid()).child("time").setValue(timeText);
            fb.child(user.getUid()).child("motivation").setValue(motivationText);
            fb.child(user.getUid()).child("skill").setValue(skillText);
        }
        Intent intent = new Intent(this, TabBarActivity.class);
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
