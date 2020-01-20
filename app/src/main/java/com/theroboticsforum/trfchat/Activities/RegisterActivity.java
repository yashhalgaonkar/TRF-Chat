package com.theroboticsforum.trfchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theroboticsforum.trfchat.R;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    //widgets
    private RelativeLayout mRegisterButton;
    private TextView mLoginTextView;
    private TextView mEmail,mPassword;
    private ProgressBar mProgress;

    //firebase authentication
    private FirebaseAuth mAuth;

    //firebase DAtabase
    private DatabaseReference mUserData = FirebaseDatabase.getInstance().getReference().child(USERS);

    //vars
    private static final String TAG = "RegisterActivity";
    private static final String USERS = "users";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        mRegisterButton = findViewById(R.id.register);
        mLoginTextView = findViewById(R.id.login);
        mEmail = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mProgress = findViewById(R.id.loading);

        mProgress.setVisibility(View.INVISIBLE);


        mLoginTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logIn();
                    }
                }
        );

        mRegisterButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideSoftKeyboard();
                        register();
                    }
                }
        );


    }

    private void register()
    {
        //register the new user here
        mAuth = FirebaseAuth.getInstance();
        final String email,password;
        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();

        if(!isEmpty(email) && !isEmpty(password))
        {
            //none of the field is empty... Can continue with registration

            mAuth.createUserWithEmailAndPassword(email,password).
                    addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    //registration is successfull... direct user to MAinActivity
                                    Log.d(TAG, "onSuccess: Registration Successfull");
                                    Toast.makeText(RegisterActivity.this, "Welcome!!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
                                    intent.putExtra("email" , email);
                                    startActivity(intent);

                                }
                            }
                    ).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: Registration Failed!! ",e );
                            Toast.makeText(RegisterActivity.this, "Cannot Register", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            //addToDatabase(email,password);
        }

        else
        {
            //one of the email or password field is empty
            if(isEmpty(email))
            {
                //email field is empty
                Toast.makeText(this, "Enter valid Email", Toast.LENGTH_SHORT).show();
            }
            else{
                //password field is empty
                Toast.makeText(this, "Enter the password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //optional
    private void addToDatabase(String email , String password)
    {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String , Object> user = new HashMap<>();
        user.put("email" , email);
        user.put("uid" , userUid);
        user.put("password" , password);


        mUserData.child(email).setValue(user).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e );
                    }
                }
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: User added to database successfully");
            }
        });

    }

    //to revert back to LogIn activity
    private void logIn()
    {
        //move the user to the LogIn activity
        startActivity(new Intent(this, LoginActivity.class));
    }

    //to check if given string is empty
    private boolean isEmpty(String string) {return string.equals("");}

    //to hide the keyboard
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
