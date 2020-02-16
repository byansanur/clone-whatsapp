package com.byandev.clonewhatsapp.Fragment;


import android.content.Context;
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

import com.byandev.clonewhatsapp.Find.FindFriendsActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;

    private DatabaseReference contactRef, userRef;

    private FirebaseAuth auth;
    private String currentUserID;

  public ContactsFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_contacts, container, false);

    context = getContext();

      auth = FirebaseAuth.getInstance();
      currentUserID = auth.getCurrentUser().getUid();
      contactRef = FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUserID);
      userRef = FirebaseDatabase.getInstance().getReference().child("users");

    recyclerView = view.findViewById(R.id.rv_contacts);
    recyclerView.setLayoutManager(new LinearLayoutManager(context));

    return view;
  }

    @Override
    public void onStart() {
        super.onStart();
        rvFirebase();

    }

    private void rvFirebase() {
        FirebaseRecyclerOptions options =
            new FirebaseRecyclerOptions.Builder<ModelsUsersContacts>()
            .setQuery(contactRef, ModelsUsersContacts.class).build();

        FirebaseRecyclerAdapter<ModelsUsersContacts,ViewHolder> adapter =
            new FirebaseRecyclerAdapter<ModelsUsersContacts, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull ModelsUsersContacts model) {
                final String usersId = getRef(position).getKey();
                userRef.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")) {
                            String upp = dataSnapshot.child("image").getValue().toString();
                            String uname = dataSnapshot.child("aname").getValue().toString();
                            String ustatus = dataSnapshot.child("status").getValue().toString();

                            holder.userName.setText(uname);
                            holder.userStatus.setText(ustatus);
                            Picasso.get()
                                .load(upp)
                                .placeholder(R.drawable.ic_profile_default_foreground)
                                .into(holder.profileImage);
                        } else {
                            String uname = dataSnapshot.child("aname").getValue().toString();
                            String ustatus = dataSnapshot.child("status").getValue().toString();

                            holder.userName.setText(uname);
                            holder.userStatus.setText(ustatus);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false);
                ContactsFragment.ViewHolder viewHolder = new ContactsFragment.ViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;
        Button cancle, accept;

        public ViewHolder(@NonNull View itemView) {
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
