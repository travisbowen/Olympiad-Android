package upscaleapps.olympiad.Search;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import upscaleapps.olympiad.Profile.ProfileFragment;
import upscaleapps.olympiad.R;


public class SearchFragment extends Fragment{


    public static final String TAG = "SearchFragment.TAG";
    private FirebaseDatabase db;
    private DatabaseReference fb;
    private Spinner spinner;
    private FirebaseUser signedUser;
    private ListView lv;
    private ArrayList<UserObject> userList = new ArrayList<UserObject>();
    private ArrayList<UserObject> ageList = new ArrayList<UserObject>();
    private ArrayList<UserObject> genderList = new ArrayList<UserObject>();
    private ArrayList<UserObject> reasonList = new ArrayList<UserObject>();
    private ArrayList<UserObject> skillList = new ArrayList<UserObject>();
    private CustomAdapter listAdapter;
    private String signedAge;
    private String signedGender;
    private String signedReason;
    private String signedSkill;
    private String signedEmail;
    private Double currentUsersLatitude;
    private Double currentUsersLongitude;

    private AdView mAdView;


    public static SearchFragment newInstance() {

        SearchFragment searchFragment = new SearchFragment();
        return searchFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        lv = (ListView) view.findViewById(R.id.profiles_list_view);
        listAdapter = new CustomAdapter(getContext(), userList);
        lv.setAdapter(listAdapter);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");
        signedUser = FirebaseAuth.getInstance().getCurrentUser();

        signedUserFirebaseListener();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                ProfileFragment fragment = new ProfileFragment();
                Bundle b = new Bundle();
                b.putString("name", userList.get(i).getName());
                b.putDouble("average", userList.get(i).getAverage());
                b.putString("age", userList.get(i).getAge());
                b.putString("gender", userList.get(i).getGender());
                b.putString("location", userList.get(i).getLocation());
                b.putString("reason", userList.get(i).getReason());
                b.putString("time", userList.get(i).getTime());
                b.putString("skill", userList.get(i).getSkill());
                b.putString("email", userList.get(i).getEmail());
                b.putString("motivation", userList.get(i).getMotivation());
                b.putString("image", userList.get(i).getImage());

                fragment.setArguments(b);
                ft.add(android.R.id.content, fragment);
                ft.commit();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        listAdapter = new CustomAdapter(getContext(), userList);
                        lv.setAdapter(listAdapter);
                        break;
                    case 1:
                        listAdapter = new CustomAdapter(getContext(), ageList);
                        lv.setAdapter(listAdapter);
                        break;
                    case 2:
                        listAdapter = new CustomAdapter(getContext(), genderList);
                        lv.setAdapter(listAdapter);
                        break;
                    case 3:
                        listAdapter = new CustomAdapter(getContext(), reasonList);
                        lv.setAdapter(listAdapter);
                        break;
                    case 4:
                        listAdapter = new CustomAdapter(getContext(), skillList);
                        lv.setAdapter(listAdapter);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }


    private void signedUserFirebaseListener(){
        fb.child(signedUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                signedAge = snapshot.child("age").getValue().toString();
                signedGender = snapshot.child("gender").getValue().toString();
                signedReason = snapshot.child("reason").getValue().toString();
                signedSkill = snapshot.child("skill").getValue().toString();
                signedEmail = snapshot.child("email").getValue().toString();
                currentUsersLatitude = (Double) snapshot.child("latitude").getValue();
                currentUsersLongitude = (Double) snapshot.child("longitude").getValue();
                usersFirebaseListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });
    }


    private void usersFirebaseListener(){
        fb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                fetchData(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });
    }


    private void fetchData(DataSnapshot dataSnapshot) {

        userList.clear();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (!ds.child("name").getValue().toString().equals("")) {
                String name = ds.child("name").getValue().toString();
                String gender = ds.child("gender").getValue().toString();
                String age = ds.child("age").getValue().toString();
                String location = ds.child("location").getValue().toString();
                Double latitude = (Double) ds.child("latitude").getValue();
                Double longitude = (Double) ds.child("longitude").getValue();
                String image = ds.child("image").getValue().toString();
                String reason = ds.child("reason").getValue().toString();
                String skill = ds.child("skill").getValue().toString();
                String email = ds.child("email").getValue().toString();
                String time = ds.child("time").getValue().toString();
                String motivation = ds.child("motivation").getValue().toString();
                Double average = Double.valueOf(String.valueOf(ds.child("average").getValue()));

                UserObject user = new UserObject();

                //Add to arraylist of User Objects
                user.setName(name);
                user.setGender(gender);
                user.setAge(age);
                user.setLocation(location);
                user.setLatitude(latitude);
                user.setLongitude(longitude);
                user.setImage(image);
                user.setReason(reason);
                user.setSkill(skill);
                user.setTime(time);
                user.setMotivation(motivation);
                user.setEmail(email);
                user.setAverage(average);

                // "As the Crow Flies" distance
                Location currentUserLocation = new Location("Current User");
                currentUserLocation.setLatitude(currentUsersLatitude);
                currentUserLocation.setLongitude(currentUsersLongitude);

                Location foundUserLocation = new Location("Found User");
                foundUserLocation.setLatitude(user.getLatitude());
                foundUserLocation.setLongitude(user.getLongitude());

                Double distanceAwayInMiles = (currentUserLocation
                        .distanceTo(foundUserLocation) / 1000 * 0.62137119);

                user.setDistance(distanceAwayInMiles);
                if (signedUser.getEmail() != user.getEmail()) {
                    userList.add(user);
                }
                if (signedAge.equals(user.getAge())) {
                    ageList.add(user);
                    Collections.sort(ageList, new Comparator<UserObject>() {
                        @Override
                        public int compare(UserObject uo1, UserObject uo2) {
                            return uo1.getDistance().compareTo(uo2.getDistance());
                        }
                    });
                }

                if (signedGender.equals(user.getGender())) {
                    genderList.add(user);
                    Collections.sort(genderList, new Comparator<UserObject>() {
                        @Override
                        public int compare(UserObject uo1, UserObject uo2) {
                            return uo1.getDistance().compareTo(uo2.getDistance());
                        }
                    });
                }

                if (signedReason.equals(user.getReason())) {
                    reasonList.add(user);
                    Collections.sort(reasonList, new Comparator<UserObject>() {
                        @Override
                        public int compare(UserObject uo1, UserObject uo2) {
                            return uo1.getDistance().compareTo(uo2.getDistance());
                        }
                    });
                }

                if (signedSkill.equals(user.getSkill())) {
                    skillList.add(user);
                    Collections.sort(skillList, new Comparator<UserObject>() {
                        @Override
                        public int compare(UserObject uo1, UserObject uo2) {
                            return uo1.getDistance().compareTo(uo2.getDistance());
                        }
                    });
                }
            }

            Collections.sort(userList, new Comparator<UserObject>() {
                @Override
                public int compare(UserObject uo1, UserObject uo2) {
                    return uo1.getDistance().compareTo(uo2.getDistance());
                }
            });

            listAdapter = new CustomAdapter(getContext(), userList);
            lv.setAdapter(listAdapter);
        }
    }
}
