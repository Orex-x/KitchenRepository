package com.example.kitchentest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InfoAboutAcceptWork extends Fragment {
    public static String userNameFragmentAcceptWork = null, idGroupUserFragmentAcceptWork = null;
    private Button BFragmentNotAcceptWork, BFragmentAcceptWork;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_about_accept_work, null);

        BFragmentNotAcceptWork = (Button) v.findViewById(R.id.BFragmentNotAcceptWork);
        BFragmentAcceptWork = (Button) v.findViewById(R.id.BFragmentAcceptWork);

        BFragmentAcceptWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.NotshowFragmentInfoAboutAcceptWork();
            }
        });



        BFragmentNotAcceptWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.AcceptWorkIsFalse();
                HomeActivity.NotshowFragmentInfoAboutAcceptWork();
            }
        });
        return v;
    }
}