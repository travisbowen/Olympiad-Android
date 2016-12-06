package upscaleapps.olympiad.Message;

import android.app.Fragment;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import upscaleapps.olympiad.R;

public class MessageActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference fb;
    static String fromEmail;

    ListView messages;
    Button sendMessage;
    EditText newMessage;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        db = FirebaseDatabase.getInstance();
        fb = db.getReference("users");

        if (getIntent().hasExtra("fromEmail")) {
            sendMessage = (Button) findViewById(R.id.sendMessage);
            messages    = (ListView) findViewById(R.id.messagesList);
            newMessage  = (EditText) findViewById(R.id.newMessageText);
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
        }
    }

    private void loadMessages() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        adapter = new FirebaseListAdapter<Message>(this, Message.class,R.layout.message_row,
                fb.child(user.getUid()).child("messagesNew")) {

            @Override
            protected void populateView(View v, Message model, int position) {
                TextView userName, userMessage, userTime;

                userName    = (TextView) v.findViewById(R.id.userName);
                userMessage = (TextView) v.findViewById(R.id.userMessage);
                userTime    = (TextView) v.findViewById(R.id.userTime);

                if (model.getFrom().equals(user.getEmail())) {
                    v.findViewById(R.id.message_row).setBackgroundColor(getResources()
                            .getColor(R.color.wallet_holo_blue_light));
                }

                userName.setText(model.getFrom());
                userMessage.setText(model.getText());
                userTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTimestamp()));
            }
        };
        messages.setAdapter(adapter);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        if (getArguments()!=null) {
//            fromEmail = getArguments().getString("userEmail");
//        }
//        View view = inflater.inflate(R.layout.fragment_message, container, false);
//        Log.d("We Made IT! \n", fromEmail);
//        return view;
//    }
}
