package com.byandev.clonewhatsapp.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.byandev.clonewhatsapp.Models.ModelsUsersContacts;
import com.byandev.clonewhatsapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class FragmentRequest extends Fragment {


    private RecyclerView recyclerView;
    private Context context;
    private DatabaseReference chatRequestRef, userRef, contacsRef;
    private FirebaseAuth auth;
    private String currentUserId;

    public FragmentRequest() {
        // Required empty public constructor Coding Cafe 41 / 61 yt tutorial
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_request, container, false);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat request");
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        contacsRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        context = getContext();
        recyclerView = view.findViewById(R.id.rv_chat_request_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseRecyclerOptions<ModelsUsersContacts> options =
            new FirebaseRecyclerOptions.Builder<ModelsUsersContacts>()
            .setQuery(chatRequestRef.child(currentUserId), ModelsUsersContacts.class)
            .build();

        FirebaseRecyclerAdapter<ModelsUsersContacts, RequestViewHolder> adapter =
            new FirebaseRecyclerAdapter<ModelsUsersContacts, RequestViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull ModelsUsersContacts model) {
//                    holder.userStatus.setText();
                    holder.itemView.findViewById(R.id.reqAccept).setVisibility(View.VISIBLE);
                    holder.itemView.findViewById(R.id.reqCancel).setVisibility(View.VISIBLE);
//                    holder.userName.setText(model.getAname());
//                    holder.userStatus.setText(model.getStatus());
                    final String list_users_id = getRef(position).getKey();
                    DatabaseReference getTypeRef = getRef(position).child("req_type").getRef();
                    getTypeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String type = dataSnapshot.getValue().toString();
                                if (type.equals("received")) {
                                    assert list_users_id != null;
                                    userRef.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            Log.d("userRef", String.valueOf(userRef.child(list_users_id)));
                                            if (dataSnapshot.hasChild("image")) {

//                                                final String reqUname = dataSnapshot.child("aname").getValue().toString();
                                                final String reqUimage = dataSnapshot.child("image").getValue().toString();
//                                                final String reqUstatus = dataSnapshot.child("status").getValue().toString();

//                                                holder.userName.setText(reqUname);
                                                Picasso.get()
                                                    .load(reqUimage)
                                                    .into(holder.imageView);
//                                                holder.userStatus.setText(reqUstatus);
//                                                Log.d("liat", reqUname);
                                            }
//                                            } else {
//
//                                                final String reqUname = dataSnapshot.child("aname").getValue().toString();
//                                                final String reqUstatus = dataSnapshot.child("status").getValue().toString();
//
//                                                holder.userName.setText(reqUname);
//                                                holder.userStatus.setText(reqUstatus);
//                                            }
                                            final String reqUname = dataSnapshot.child("aname").getValue().toString();
                                            final String reqUstatus = dataSnapshot.child("status").getValue().toString();

                                            holder.userName.setText(reqUname);
                                            holder.userStatus.setText("Wants to connect with you.");

                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    CharSequence options[] = new CharSequence[] {
                                                        "Accept",
                                                        "Cancel"
                                                    };
                                                    AlertDialog.Builder ad = new AlertDialog.Builder(context);
                                                    ad.setTitle(reqUname +" Chat Request");
                                                    ad.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (which == 0) {
                                                                contacsRef.child(currentUserId).child(list_users_id).child("contacts")
                                                                    .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            contacsRef.child(list_users_id).child(currentUserId).child("contacts")
                                                                                .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        chatRequestRef.child(currentUserId).child(list_users_id)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        chatRequestRef.child(list_users_id).child(currentUserId)
                                                                                                            .removeValue()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        Toast.makeText(context, "New Contacts Added", Toast.LENGTH_SHORT).show();
                                                                                                                    }
                                                                                                                }
                                                                                                            });
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            if (which == 1) {
                                                                chatRequestRef.child(currentUserId).child(list_users_id)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                chatRequestRef.child(list_users_id).child(currentUserId)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Toast.makeText(context, "Contacts Deleted", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                            }
                                                                        }
                                                                    });

                                                            }
                                                        }
                                                    });
                                                    ad.show();

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @NonNull
                @Override
                public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false);
                    RequestViewHolder holder = new RequestViewHolder(view);
                    return holder;
                }
            };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView imageView;
        Button btnAccept, btnCancle;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tvUsername);
            userStatus = itemView.findViewById(R.id.tvUserStatus);
            imageView = itemView.findViewById(R.id.imgUsers);
            btnAccept = itemView.findViewById(R.id.reqAccept);
            btnCancle = itemView.findViewById(R.id.reqCancel);
        }
    }
}
