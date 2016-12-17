package upscaleapps.olympiad.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import upscaleapps.olympiad.Login.LoginActivity;
import upscaleapps.olympiad.R;
import upscaleapps.olympiad.Register.RegisterActivityA;
import upscaleapps.olympiad.User;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = "ProfileFragment.TAG";
    private static final int REQUEST_INVITE = 1;
    TextView labelName;
    TextView labelGenderAge;
    TextView labelLocation;
    TextView labelReason;
    TextView labelSkill;
    TextView labelMotivation;
    TextView labelTime;
    ImageView imageView;
    Button buttonEdit;
    Button buttonLogout;

    private AdView mAdView;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseDatabase db;
    private DatabaseReference fb;

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();


        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");

        labelName = (TextView) view.findViewById(R.id.labelName);
        labelGenderAge = (TextView) view.findViewById(R.id.labelGenderAge);
        labelLocation = (TextView) view.findViewById(R.id.labelLocation);
        labelMotivation = (TextView) view.findViewById(R.id.labelMotivation);
        labelSkill = (TextView) view.findViewById(R.id.labelSkill);
        labelTime = (TextView) view.findViewById(R.id.labelTime);
        labelReason = (TextView) view.findViewById(R.id.labelReason);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        buttonEdit = (Button) view.findViewById(R.id.buttonEdit);
        buttonLogout = (Button) view.findViewById(R.id.buttonLogout);

        // If Edit button click
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivityA.class);
                startActivity(intent);
            }
        });
        // If Logout button click
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (readData()!=null) {
                    // Delete User Saved Login
                    File external = getActivity().getExternalFilesDir(null);
                    File file = new File(external, "user.txt");
                    if (file.exists()) {
                        file.delete();
                        // Go to Login
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                    }
                }
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
                    new ProfileFragment.getProfileImage(imageView).execute(imageURL);
                    
                    labelName.setText(user.getName());

                    labelLocation.setText(user.getLocation());

                    labelMotivation.setText(user.getMotivation());

                    labelReason.setText(user.getReason());

                    labelSkill.setText(user.getSkill());

                    labelTime.setText(user.getTime());

                    String gender = user.getGender().substring(0, 1);
                    labelGenderAge.setText(gender + " - " + user.getAge());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });
        return view;
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode +
                ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent.
                String[] ids = AppInviteInvitation
                        .getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Sending failed or it was canceled, show failure message to
                // the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
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
            imageView.setImageBitmap(result);
        }
    }

    // Get User Info From Local Storage
    private JSONObject readData(){
        String result = "";
        File external = getActivity().getExternalFilesDir(null);
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
