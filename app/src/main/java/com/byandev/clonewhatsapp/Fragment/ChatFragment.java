package com.byandev.clonewhatsapp.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.byandev.clonewhatsapp.Chat.ChatActivity;
import com.byandev.clonewhatsapp.Models.ModelsUsersContacts;
import com.byandev.clonewhatsapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private RecyclerView recyclerView;
    private Context context;

    private DatabaseReference chatRef, userRef;
    private FirebaseAuth auth;

    private String currentUserId;



  public ChatFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_chat, container, false);

    context = getContext();

    auth = FirebaseAuth.getInstance();
    currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    chatRef = FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUserId);
    userRef = FirebaseDatabase.getInstance().getReference().child("users");

    recyclerView = view.findViewById(R.id.recyclerViewListChat);
    recyclerView.setLayoutManager(new LinearLayoutManager(context));


    return view;
  }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRvAdapter();
    }

    private void firebaseRvAdapter() {
        FirebaseRecyclerOptions<ModelsUsersContacts> options =
            new FirebaseRecyclerOptions.Builder<ModelsUsersContacts>()
            .setQuery(chatRef, ModelsUsersContacts.class)
            .build();
        FirebaseRecyclerAdapter<ModelsUsersContacts, RecyclerViewHolder> adapter =
            new FirebaseRecyclerAdapter<ModelsUsersContacts, RecyclerViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position, @NonNull ModelsUsersContacts model) {

                    final String usersIds = getRef(position).getKey();
                    final String[] retImage = {"default_image"};

                    assert usersIds != null;
                    userRef.child(usersIds).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                if (dataSnapshot.hasChild("image")) {
                                    retImage[0] = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                                    Picasso.get()
                                        .load(retImage[0])
                                        .into(holder.profileImage);
                                }

                                final String usn = Objects.requireNonNull(dataSnapshot.child("aname").getValue()).toString();
                                final String uss = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();

                                holder.userName.setText(usn);
                                holder.userStatus.setText("Last seen: " + "\n" + "Date: " + "Time:");


                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatAct = new Intent(context, ChatActivity.class);
                                        chatAct.putExtra("visit_user_id", usersIds);
                                        chatAct.putExtra("visit_user_name", usn);
                                        chatAct.putExtra("visit_image", retImage[0]);
                                        startActivity(chatAct);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }) ;
                }

                @NonNull
                @Override
                public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false);
                    return new RecyclerViewHolder(view);
                }
            };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button cancle, accept;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tvUsername);
            userStatus = itemView.findViewById(R.id.tvUserStatus);
            profileImage = itemView.findViewById(R.id.imgUsers);
            cancle = itemView.findViewById(R.id.reqCancel);
            cancle.setVisibility(View.GONE);
            accept = itemView.findViewById(R.id.reqAccept);
            accept.setVisibility(View.GONE);
        }
    }
}
