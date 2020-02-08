package com.byandev.clonewhatsapp.Find;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byandev.clonewhatsapp.Models.ModelsUsersContacts;
import com.byandev.clonewhatsapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reference = FirebaseDatabase.getInstance().getReference().child("users");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("FInd friends");
        getSupportActionBar().setSubtitle("For chat one to one");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerViewFirebase();
    }

    private void recyclerViewFirebase() {
        FirebaseRecyclerOptions<ModelsUsersContacts> options =
            new FirebaseRecyclerOptions.Builder<ModelsUsersContacts>()
                .setQuery(reference, ModelsUsersContacts.class)
                .build();

        FirebaseRecyclerAdapter<ModelsUsersContacts, FindFriendsViewHolder> adapter =
            new FirebaseRecyclerAdapter<ModelsUsersContacts, FindFriendsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull ModelsUsersContacts model) {
                    holder.userName.setText(model.getAname());
                    Picasso.get().load(model.getImage()).placeholder(R.drawable.ic_profile_default_foreground).into(holder.profileImage);
                    holder.userStatus.setText(model.getStatus());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String visit_user_id = getRef(position).getKey();
                            Intent i = new Intent(FindFriendsActivity.this, UserProfileActivity.class);
                            i.putExtra("visit_user_id", visit_user_id);
                            startActivity(i);
                        }
                    });
                }

                @NonNull
                @Override
                public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false);
                    FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                    return viewHolder;
                }
            };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircleImageView profileImage;
        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tvUsername);
            userStatus = itemView.findViewById(R.id.tvUserStatus);
            profileImage = itemView.findViewById(R.id.imgUsers);
        }
    }
}
