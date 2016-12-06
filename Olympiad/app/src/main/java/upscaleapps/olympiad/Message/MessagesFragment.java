package upscaleapps.olympiad.Message;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.text.format.DateFormat;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import java.util.ArrayList;


import upscaleapps.olympiad.R;

public class MessagesFragment extends Fragment {

    ListView conversationsList;
    ArrayList<JSONObject> jsonMessages = new ArrayList<>();
    private FirebaseDatabase db;
    private DatabaseReference fb;
    ListAdapter adapter;
    ListView messages;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");

        View view = inflater.inflate(R.layout.activity_message, container, false);
        Button sendMessage = (Button)   view.findViewById(R.id.sendMessage);
        messages = (ListView) view.findViewById(R.id.messagesList);
        final EditText newMessage = (EditText) view.findViewById(R.id.newMessageText);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                fb.child(user.getUid()).child("messagesNew").push().setValue(
                        new Message(user.getEmail().toString(),newMessage.getText().toString()));
                newMessage.setText("");
            }
        });
        loadMessages();
        return view;
    }

    private void loadMessages() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       adapter = new FirebaseListAdapter<Message>(getActivity(),Message.class,R.layout.message_row,
               fb.child(user.getUid()).child("messagesNew")) {

           @Override
           protected void populateView(View v, Message model, int position) {
               TextView userName, userMessage, userTime;

               userName    = (TextView) v.findViewById(R.id.userName);
               userMessage = (TextView) v.findViewById(R.id.userMessage);
               userTime    = (TextView) v.findViewById(R.id.userTime);

               if (user.getEmail() == model.getFrom()) {
                   v.findViewById(R.id.message_row)
                           .setBackgroundColor(getResources()
                                   .getColor(R.color.wallet_holo_blue_light));
               }

               userName.setText(model.getFrom());
               userMessage.setText(model.getText());
               userTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTimestamp()));
           }
       };
        messages.setAdapter(adapter);
    }
}
