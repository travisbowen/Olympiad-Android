package upscaleapps.olympiad.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import upscaleapps.olympiad.Login.LoginActivity;
import upscaleapps.olympiad.R;
import upscaleapps.olympiad.Register.RegisterActivityA;
import upscaleapps.olympiad.User;


public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment.TAG";

    TextView labelName;
    TextView labelGenderAge;
    TextView labelLocation;
    TextView labelReason;
    TextView labelSkill;
    TextView labelMotivation;
    TextView labelTime;
    ImageView imageView;
    Button buttonEdit;

    private FirebaseDatabase db;
    private DatabaseReference fb;

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

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

        // If Edit button click
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivityA.class);
                startActivity(intent);
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
}
