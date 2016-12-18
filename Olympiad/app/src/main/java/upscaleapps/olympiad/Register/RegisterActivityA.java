package upscaleapps.olympiad.Register;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import upscaleapps.olympiad.R;
import upscaleapps.olympiad.User;


public class RegisterActivityA extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_REQUEST = 101;
    private static final int PHOTO_REQUEST = 202;

    private ImageView regIV;
    private EditText regNameET;
    private EditText regAgeET;
    private Spinner regGenderSP;
    private TextView regLocationTV;

    private String imageText;
    private String nameText;
    private String ageText;
    private String genderText;
    private String locationText;

    private TextView uploadHint;
    private FirebaseDatabase db;
    private DatabaseReference fb;

    private AdView mAdView;

    GPSCoordinates gps;
    Double latitude = 0.0;
    Double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_a);

        //Setting Admob
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");

        regIV = (ImageView) findViewById(R.id.regIV);
        regNameET = (EditText) findViewById(R.id.regReasonET);
        regAgeET = (EditText) findViewById(R.id.regAgeET);
        regGenderSP = (Spinner) findViewById(R.id.regGenderSP);
        regLocationTV = (TextView) findViewById(R.id.reglocationTV);
        uploadHint = (TextView) findViewById(R.id.uploadHint);
        final Button locationBT = (Button) findViewById(R.id.locationBT);
        final Button backBT = (Button) findViewById(R.id.backBT);
        Button nextBT = (Button) findViewById(R.id.nextBT);

        locationBT.setOnClickListener(this);
        backBT.setOnClickListener(this);
        nextBT.setOnClickListener(this);

        String[] gender = {"Gender", "Male", "Female", "Other"};

        final ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(this,
                R.layout.spinner_item, gender);

        regGenderSP.setAdapter(adapterGender);
        // Selected Gender
        regGenderSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderText = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        regIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog ad = new AlertDialog.Builder(RegisterActivityA.this).create();
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

        // Read Data if set
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fb.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user != null) {

                    String imageURL = user.getImage();
                    new RegisterActivityA.getProfileImage(regIV).execute(imageURL);

                    regNameET.setText(user.getName());
                    regAgeET.setText(user.getAge());
                    regLocationTV.setText(user.getLocation());
                    genderText = user.getGender();
                    longitude = user.getLongitude();
                    latitude = user.getLatitude();
                    if (genderText != null) {
                        int i = adapterGender.getPosition(genderText);
                        regGenderSP.setSelection(i);
                    }

                } else {
                    // Hide back button
                    backBT.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
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
                // if empty set toast
                if (regNameET.getText().equals("") ||
                        regAgeET.getText().equals("")  ||
                        genderText.equals("Gender") ||
                        regLocationTV.getText().equals("")) {
                    Toast.makeText(this, "Please Enter in All Fields.",
                            Toast.LENGTH_LONG).show();
                } else {
                    next();
                }
                break;

            case R.id.locationBT:
                getUserLocation();
                break;
        }
    }

    // Go back to profile
    public void back() {
       finish();
    }

    // Go to register B
    public void next() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            nameText = regNameET.getText().toString();
            ageText = regAgeET.getText().toString();
            locationText = regLocationTV.getText().toString();

            // Get Image from ImageView
            regIV.setDrawingCacheEnabled(true);
            regIV.buildDrawingCache();
            Bitmap b = regIV.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] y = baos.toByteArray();
            final Intent i = new Intent(this, RegisterActivityB.class);

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

                    fb.child(user.getUid()).child("name").setValue(nameText);
                    fb.child(user.getUid()).child("age").setValue(ageText);
                    fb.child(user.getUid()).child("gender").setValue(genderText);
                    fb.child(user.getUid()).child("location").setValue(locationText);
                    fb.child(user.getUid()).child("latitude").setValue(latitude);
                    fb.child(user.getUid()).child("longitude").setValue(longitude);
                    fb.child(user.getUid()).child("image").setValue(imageText);

                    startActivity(i);
                }
            });
        }
    }


    //Retrieve users location from button tap
    private void getUserLocation(){
        gps = new GPSCoordinates(RegisterActivityA.this);

        //Retrieving location
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }

        //Geocoder to get city/state
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            //Setting address from lat and long
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            String address = addresses.get(0).getAddressLine(1);
            String[] array = address.split(" ");
            String cityState = array[0] + " " + array[1];
            regLocationTV.setText(cityState);
        } else {
            // do error handling
        }
    }


    // Download Image form URL -> Display Profile Image
    private class getProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        getProfileImage(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap b = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                b = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return b;
        }
        protected void onPostExecute(Bitmap result) {
            uploadHint.setVisibility(View.GONE);
            imageView.setImageBitmap(result);
        }
    }
}