package com.theroboticsforum.trfchat.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.theroboticsforum.trfchat.Activities.MainActivity;
import com.theroboticsforum.trfchat.R;

/**
 * Created by User on 5/14/2018.
 */


public class NewChatDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "NewNoteDialog";

    //widgets
    private EditText mEmail;
    private RelativeLayout mSend;

    //vars


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_chat, container, false);

        mEmail = view.findViewById(R.id.email);
        mSend = view.findViewById(R.id.send);
        mSend.setOnClickListener(this);
        getDialog().setTitle("New Chat");

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.send:{

                // insert the new note

                String email = mEmail.getText().toString();


                if(!email.equals("")){
                    ((MainActivity)getActivity()).createNewChatRoom(email);
                    Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Enter a title", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

}