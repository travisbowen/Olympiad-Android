package upscaleapps.olympiad.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import upscaleapps.olympiad.Login.LoginActivity;
import upscaleapps.olympiad.R;
import upscaleapps.olympiad.User;
import upscaleapps.olympiad.UserMessages;

public class MessageFragment extends Fragment {

    public static final String TAG = "MessageFragment.TAG";

    ListView conversationsList;
    ArrayList<JSONObject> jsonMessages = new ArrayList<>();
    private FirebaseDatabase db;
    private DatabaseReference fb;
    Context context;

    public static MessageFragment newInstance(Context context) {
        MessageFragment messageFragment = new MessageFragment();
        return messageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");
        jsonMessages.clear();
        // Get Data from Firebase
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fb.child(user.getUid()).child("messagesNew")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
            for (DataSnapshot eachSnap: snapshot.getChildren()) {
            final UserMessages um = eachSnap.getValue(UserMessages.class);
            if (um != null) fb.orderByChild("email").equalTo(um.getFrom())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot eachSnap : snapshot.getChildren()) {
                                User userSnap = eachSnap.getValue(User.class);

                                if (userSnap != null) {
                                    JSONObject conversation = new JSONObject();
                                    // Name age gender builder
                                    String nag = userSnap.getName() + ", " +
                                            userSnap.getGender().substring(0, 1) + " - " +
                                            userSnap.getAge();
                                    try {
                                        if (!um.getFrom().equals(user.getEmail())) {
                                            conversation.put("messageTitle", nag);
                                            conversation.put("messageText", um.getText());
                                            conversation.put("messageTime", um.getTimestamp());
                                            conversation.put("userImage", userSnap.getImage());
                                            conversation.put("fromEmail", um.getFrom());
                                            jsonMessages.add(conversation);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            conversationsList.setAdapter(
                                    new ConversationRow(getActivity(), jsonMessages));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
               }
           }
           @Override public void onCancelled(DatabaseError databaseError) {}
        });
        // Populate listView
        conversationsList = (ListView) view.findViewById(R.id.conversationsList);
        conversationsList.setAdapter(new ConversationRow(getActivity(), jsonMessages));
        conversationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int x, long l) {
                try {
                    Intent i = new Intent(getActivity(), MessageActivity.class);
                    i.putExtra("fromEmail", jsonMessages.get(x).getString("fromEmail").toString());
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}

