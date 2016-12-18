package upscaleapps.olympiad.Register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import upscaleapps.olympiad.R;
import upscaleapps.olympiad.TabBar.TabBarActivity;
import upscaleapps.olympiad.User;

public class RegisterActivityB extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_REQUEST = 101;
    private static final int PHOTO_REQUEST = 202;

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
    private TextView uploadHint;

    private FirebaseDatabase db;
    private DatabaseReference fb;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_b);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");

        regIV           = (ImageView)findViewById(R.id.regIV);
        regReasonSP     = (Spinner)findViewById(R.id.regReasonSP);
        regTimeSP       = (Spinner)findViewById(R.id.regTimeSP);
        regMotivationSP = (Spinner)findViewById(R.id.regMotivationSP);
        regSkillSP      = (Spinner)findViewById(R.id.regSkillSP);
        uploadHint      = (TextView)findViewById(R.id.uploadHint);
        Button backBT   = (Button)findViewById(R.id.backBT);
        Button nextBT   = (Button)findViewById(R.id.nextBT);


        String[] reasons    = {"App Use Reason", "Martial Arts", "Weightlifting", "Swimming", "Running",
                "Weight Loss", "Hiking", "Sports", "Commuting to Gym", "Going to Events"};
        String[] time       = {"Workout Time", "Morning", "Noon", "Afternoon", "Evening"};
        String[] motivation = {"Motivation", "Low", "Average", "High"};
        String[] skill      = {"Skill Level", "Beginner", "Intermediate", "Expert"};

        final ArrayAdapter<String> adapterReason =  new ArrayAdapter<String>(this,
                R.layout.spinner_item, reasons);
        final ArrayAdapter<String> adapterTime =  new ArrayAdapter<String>(this,
                R.layout.spinner_item, time);
        final ArrayAdapter<String> adapterMotivation =  new ArrayAdapter<String>(this,
                R.layout.spinner_item, motivation);
        final ArrayAdapter<String> adapterSkill =  new ArrayAdapter<String>(this,
                R.layout.spinner_item, skill);

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
        // Select Image
        regIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog ad = new AlertDialog.Builder(RegisterActivityB.this).create();
                ad.setTitle("Upload Image");
                ad.setMessage("Select Sorce");
                ad.setButton(AlertDialog.BUTTON_NEUTRAL, "Take Photo",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent takePictureIntent =
                                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                                { startActivityForResult(takePictureIntent, PHOTO_REQUEST); }
                                dialog.dismiss();
                            }
                        });
                ad.setButton(AlertDialog.BUTTON_POSITIVE, "Choose Photo",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent().setType("image/*")
                                        .setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent
                                        .createChooser(i, "Select Picture"), IMAGE_REQUEST);
                                dialog.dismiss();
                            }
                        });
                ad.show();
            }
        });

        backBT.setOnClickListener(this);
        nextBT.setOnClickListener(this);

        // Read Data if set
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fb.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user != null) {

                    String imageURL = user.getImage();
                    new RegisterActivityB.getProfileImage(regIV).execute(imageURL);

                    String reasonText = user.getReason();
                    if (reasonText != null) {
                        int i = adapterReason.getPosition(reasonText);
                        regReasonSP.setSelection(i);
                    }

                    String motivationText = user.getMotivation();
                    if (motivationText != null) {
                        int i = adapterMotivation.getPosition(motivationText);
                        regMotivationSP.setSelection(i);
                    }

                    String timeText = user.getTime();
                    if (timeText != null) {
                        int i = adapterTime.getPosition(timeText);
                        regTimeSP.setSelection(i);
                    }

                    String skillText = user.getSkill();
                    if (skillText != null) {
                        int i = adapterSkill.getPosition(skillText);
                        regSkillSP.setSelection(i);
                    }
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                regIV.setImageBitmap(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            regIV.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBT:
                back();
                break;
            case R.id.nextBT:
                if (motivationText.equals("App Use Reason") ||
                        skillText.equals("Workout Time") ||
                        reasonText.equals("Motivation") ||
                        timeText.equals("Skill Level") ||
                        regIV.getDrawable() == null ) {
                    Toast.makeText(this, "Please Enter in All Fields.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    next();
                }
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
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Get Image from ImageView
            regIV.setDrawingCacheEnabled(true);
            regIV.buildDrawingCache();
            Bitmap b = regIV.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] y = baos.toByteArray();
            final Intent i = new Intent(this, TabBarActivity.class);

            // Upload Image to Storage
            String uid = UUID.randomUUID().toString();
            StorageReference storage = FirebaseStorage.getInstance().getReference()
                    .child("Profile Images").child(uid);
            UploadTask ut = storage.putBytes(y);
            ut.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageText = taskSnapshot.getDownloadUrl().toString();

                    fb.child(user.getUid()).child("reason").setValue(reasonText);
                    fb.child(user.getUid()).child("time").setValue(timeText);
                    fb.child(user.getUid()).child("motivation").setValue(motivationText);
                    fb.child(user.getUid()).child("skill").setValue(skillText);
                    fb.child(user.getUid()).child("image").setValue(imageText);
                    fb.child(user.getUid()).child("average").setValue(5);
                    fb.child(user.getUid()).child("rating")
                            .child(user.getUid()).child("rating").setValue(5);

                    startActivity(i);
                }
            });
        }
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
            uploadHint.setVisibility(View.GONE);
            imageView.setImageBitmap(result);
        }
    }
}
