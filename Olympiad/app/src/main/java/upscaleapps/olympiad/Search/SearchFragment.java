package upscaleapps.olympiad.Search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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


    public static SearchFragment newInstance() {

        SearchFragment searchFragment = new SearchFragment();
        return searchFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        lv = (ListView) view.findViewById(R.id.profiles_list_view);
        listAdapter = new CustomAdapter(getContext(), userList);
        lv.setAdapter(listAdapter);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");
        signedUser = FirebaseAuth.getInstance().getCurrentUser();

        signedUserFirebaseListener();

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


    private void fetchData(DataSnapshot dataSnapshot){

        userList.clear();

        for (DataSnapshot ds : dataSnapshot.getChildren()){
            String name = ds.child("name").getValue().toString();
            String gender = ds.child("gender").getValue().toString();
            String age = ds.child("age").getValue().toString();
            String location = ds.child("location").getValue().toString();
            String image = ds.child("image").getValue().toString();
            String reason = ds.child("reason").getValue().toString();
            String skill = ds.child("skill").getValue().toString();

            UserObject user = new UserObject();

            //Add to arraylist of User Objects
            user.setName(name);
            user.setGender(gender);
            user.setAge(age);
            user.setLocation(location);
            user.setImage(image);
            user.setReason(reason);
            user.setSkill(skill);

            userList.add(user);


            if (signedAge.equals(user.getAge())){
                ageList.add(user);
            }

            if (signedGender.equals(user.getGender())){
                genderList.add(user);
            }

            if (signedReason.equals(user.getReason())){
                reasonList.add(user);
            }

            if (signedSkill.equals(user.getSkill())){
                skillList.add(user);
            }
        }
        listAdapter = new CustomAdapter(getContext(), userList);
        lv.setAdapter(listAdapter);
    }
}
