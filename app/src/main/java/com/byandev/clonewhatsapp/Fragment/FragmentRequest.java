package com.byandev.clonewhatsapp.Fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byandev.clonewhatsapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRequest extends Fragment {


    private RecyclerView recyclerView;
    private Context context;

    public FragmentRequest() {
        // Required empty public constructor Coding Cafe 41 / 61 yt tutorial
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_request, container, false);

        context = getContext();
        recyclerView = view.findViewById(R.id.rv_chat_request_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

}
