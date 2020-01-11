package com.theroboticsforum.trfchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theroboticsforum.trfchat.Model.ChatRoom;
import com.theroboticsforum.trfchat.Dialogs.NewChatDialog;
import com.theroboticsforum.trfchat.R;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
         SwipeRefreshLayout.OnRefreshListener {

    //widgets
    private ListView mListView;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipe;

    //vars
    private static final String TAG = "MainActivity";
    private ArrayList<ChatRoom> mChatRoomList = new ArrayList<>();
    private ArrayList<String> mSenderList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String currentUserEmail;
    private final String chatrooms = "chatrooms";
    private final String to = "to";
    private final String from = "from";


    //firebase authetication
    private FirebaseAuth.AuthStateListener mAuthListener;


    //Firebase Database
    private DatabaseReference mChatData = FirebaseDatabase.getInstance().getReference(chatrooms);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");


        setupFirebaseAuth();
        //getting the intentExtra passed from login and register activity
        currentUserEmail = getIntent().getStringExtra("email");
        Log.d(TAG, "onCreate: Current Email" + currentUserEmail);

        //set title to the action bar
        getSupportActionBar().setTitle("TRF Chat");


        //find the widgets
        mListView = findViewById(R.id.list_view);
        fab = findViewById(R.id.fab);
        swipe = findViewById(R.id.swipe);
        //set onRefresh Listener to the swipe refresh layout
        swipe.setOnRefreshListener(this);
        //set onClick listener to floating action button
        fab.setOnClickListener(this);
        //set up list view
        adapter = new ArrayAdapter<String>(this,
                R.layout.layout_chat_room, R.id.chat_room_email, mSenderList);
        mListView.setAdapter(adapter);

        //get the chatrooms from database
        getChatRooms();

        //add onItem click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onChatClick(i);
            }
        });


    }


    //inflate the menu in options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //get the clicks in options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.sign_out) {
            //signOut button is pressed
            signOut();
        }
        return true;
    }

    //log out the current user
    private void signOut() {
        Log.d(TAG, "signOut: sigining out.");
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Bye! Bye! ", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "signOut: User signed out");
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");
        FirebaseApp.initializeApp(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    currentUserEmail = user.getEmail();

                } else {
                    //user is signed out... revert to login page
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }


    //executed when fab is clicked
    @Override
    public void onClick(View view) {

        //TODO: code for sending the request to user using dialog box
        Toast.makeText(this, "Add Chat", Toast.LENGTH_SHORT).show();
        NewChatDialog dialog = new NewChatDialog();
        dialog.show(getSupportFragmentManager(), "New Chat");

    }


    private void getChatRooms() {
        Log.d(TAG, "getChatRooms: started");
        //get chat room in which current user is added
        mChatData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear the chatroom list or else the chatrooms will repeat
                mChatRoomList.clear();
                mSenderList.clear();
                for (DataSnapshot room : dataSnapshot.getChildren()) {
                    ChatRoom chatRoom = room.getValue(ChatRoom.class);
                    if (currentUserEmail.equals(chatRoom.getTo()) ||
                            currentUserEmail.equals(chatRoom.getFrom())) {
                        //user is present in this chatroom
                        mChatRoomList.add(chatRoom);
                        if (currentUserEmail.equals(chatRoom.getFrom())) {
                            String [] names = chatRoom.getTo().split("@");
                            mSenderList.add(names[0]);
                        } else {
                            String [] names = chatRoom.getFrom().split("@");
                            mSenderList.add(names[0]);
                        }
                    }
                }
                //notify the adapter about the change
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });

        Log.d(TAG, "getChatRooms: current chat rooms" + mChatRoomList.toString());
    }

    //called from inside onClick
    public void createNewChatRoom(final String email) {

        String key = mChatData.push().getKey();
        ChatRoom mChatRoom = new ChatRoom(email, currentUserEmail, key);

        mChatData.child(key).setValue(mChatRoom).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: chatroom added successfully");
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: chat room not added", e);
            }
        });
    }


    //called when any of the list view item is clicked
    public void onChatClick(int position) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        ChatRoom mchatRoom = mChatRoomList.get(position);
        intent.putExtra("key", mchatRoom.getId());
        Log.d(TAG, "onChatClick: passing key " + mchatRoom.getId());
        startActivity(intent);
    }

    //called every time the window is refreshed
    @Override
    public void onRefresh() {

        getChatRooms();
        swipe.setRefreshing(false);
        Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();

    }
}
