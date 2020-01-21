package com.theroboticsforum.trfchat.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theroboticsforum.trfchat.Adapters.CustomChatAdapter;
import com.theroboticsforum.trfchat.Model.Message;
import com.theroboticsforum.trfchat.R;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    //vars
    private static final String TAG = "ChatActivity";
    private  String key;
    private ArrayList<Message> chats;
    private static final String chatrooms = "chatrooms";
    private static final String messages = "messages";


    //firebase database
    private DatabaseReference mChatRef;
    private FirebaseUser currentUser;

    //widgets
    private RecyclerView mRecyclerView;
    private EditText mMessage;
    private FloatingActionButton sendBtn;

    //custom adapter
    private CustomChatAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //appBar settings
        getSupportActionBar().setTitle("Chats");


        //get the current user object
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //get the chatkey and create a reference to it
        key = getIntent().getStringExtra("key");
        mChatRef = FirebaseDatabase.getInstance().getReference().child(chatrooms).child(key);


        //find the widgets from the layout
        chats = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_view);
        mMessage = findViewById(R.id.messageEdt);
        sendBtn = findViewById(R.id.sendBtn);


        //setup the recycler view
        adapter = new CustomChatAdapter(this , chats);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.smoothScrollToPosition(chats.size());

        //set onClickListener to sendBtn
        sendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendMsg();
                    }
                }
        );

        //get chats from firebase
        getChats();

    }



    private void getChats()
    {
        mChatRef.child(messages).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot msg : dataSnapshot.getChildren())
                {
                    Message message = msg.getValue(Message.class);
                    chats.add(message);
                }
                adapter.notifyItemChanged(chats.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ",databaseError.toException() );
            }
        });


    }

    private void sendMsg()
    {
        final String msg = mMessage.getText().toString().trim();
        //check if the messageEdt is empty
        if(msg.equals(""))
        {
            //edit text is empty
            Toast.makeText(this, "Type a message", Toast.LENGTH_SHORT).show();
        }
        else {
            mMessage.setText("");
            Message message = new Message(msg , currentUser.getEmail());
            mChatRef.child(messages).push().setValue(message).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Message sent");
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: Message Not Sent",e );
                }
            });


        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        getChats();
        mRecyclerView.smoothScrollToPosition(chats.size());

    }

}
