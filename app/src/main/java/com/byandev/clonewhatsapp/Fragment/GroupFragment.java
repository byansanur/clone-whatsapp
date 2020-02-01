package com.byandev.clonewhatsapp.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.byandev.clonewhatsapp.Chat.GroupChatActivity;
import com.byandev.clonewhatsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

  private View groupFragmentView;
  private ListView listView;
  private ArrayAdapter<String> adapterArray;
  private ArrayList<String> listGroup = new ArrayList<>();

  private Context context;

  private DatabaseReference refGroup;

  public GroupFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    // Inflate the layout for this fragment
    groupFragmentView = inflater.inflate(R.layout.fragment_group, container, false);
    context = getContext();
    refGroup = FirebaseDatabase.getInstance().getReference().child("groups");
    initialize();
    retetriveDisplayGroup();
    listener();
    return groupFragmentView;
  }

  private void initialize() {
    listView = groupFragmentView.findViewById(R.id.listView);
    adapterArray = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listGroup);
    listView.setAdapter(adapterArray);
  }


  private void listener() {
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       String currentGroupName = parent.getItemAtPosition(position).toString();
        Intent c = new Intent(context, GroupChatActivity.class);
        c.putExtra("groupName", currentGroupName);
        startActivity(c);
      }
    });
  }

  private void retetriveDisplayGroup() {
    refGroup.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        Set<String> set = new HashSet<>();

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()) {
          set.add(((DataSnapshot)iterator.next()).getKey());
        }
        listGroup.clear();
        listGroup.addAll(set);
        adapterArray.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}
