package com.theroboticsforum.trfchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theroboticsforum.trfchat.Model.Message;
import com.theroboticsforum.trfchat.R;

import java.util.ArrayList;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class CustomChatAdapter extends RecyclerView.Adapter<CustomChatAdapter.ViewHolder> {

    //vars
    private Context mCtx;
    private ArrayList<Message> chats;

    // public constructor
    public CustomChatAdapter(Context mCtx, ArrayList<Message> chats) {
        this.mCtx = mCtx;
        this.chats = chats;
    }

    @NonNull
    @Override
    // Usually involves inflating a layout from XML and returning the holder
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Inflate the custom layout
        View v = inflater.inflate(R.layout.layout_message, parent, false);
        // Return a new holder instance
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TextView nameTextView = holder.nameTextView;
        TextView message = holder.messageTextView;

        // Get the data model based on position
        Message msg = chats.get(position);

        String[] names = msg.getSenderEmail().split("@");

        nameTextView.setText(names[0]);
        message.setText(msg.getMessage());

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return chats.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView nameTextView;
        private TextView messageTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(@NonNull View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.senderEmail);
            messageTextView = itemView.findViewById(R.id.messageTextView);

        }
    }
}
