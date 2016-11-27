package upscaleapps.olympiad.Search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DatabaseReference;

import upscaleapps.olympiad.R;


public class SearchFragment extends Fragment {


    public static final String TAG = "SearchFragment.TAG";
    private DatabaseReference mDatabase;
    private String mUserId;


    public static SearchFragment newInstance(Bundle bundleExtras) {

        SearchFragment searchFragment = new SearchFragment();
        return searchFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Initialize Firebase Auth and Database Reference
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        if (mFirebaseUser == null) {
//            // Not logged in, launch the Log In activity
//            loadLogInView();
//        } else {
//            mUserId = mFirebaseUser.getUid();
//
//            // Set up ListView
//            final ListView listView = (ListView) findViewById(R.id.profiles_list_view);
//            final ArrayAdapter<string> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
//            listView.setAdapter(adapter);
//
//            // Use Firebase to populate the list.
//            mDatabase.child("users").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    adapter.add((String) dataSnapshot.child("title").getValue());
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                    adapter.remove((String) dataSnapshot.child("title").getValue());
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }

        return view;
    }
}
