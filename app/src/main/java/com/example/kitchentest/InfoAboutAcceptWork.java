package com.example.kitchentest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class InfoAboutAcceptWork extends Fragment {
   // public static String userNameFragmentAcceptWork = null, idGroupUserFragmentAcceptWork = null;
    private Button BFragmentNotAcceptWork, BFragmentAcceptWork, BFVSendNotes;
    private ListView LVFAVListDuties;
    private ArrayList listDuties;
    private AdapterCheckBoxs adapterCheckBoxs;
    private DatabaseReference mDataBaseGroup, mDataBasePOD;
    public static TextView TVFragmentAcceptWork;
    public static ArrayList<String> listDutiesUndone;
    String uri = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_about_accept_work, null);

        BFragmentNotAcceptWork = (Button) v.findViewById(R.id.BFragmentNotAcceptWork);
        BFragmentAcceptWork = (Button) v.findViewById(R.id.BFragmentAcceptWork);
        BFVSendNotes = (Button) v.findViewById(R.id.BFVSendNotes);
        LVFAVListDuties = (ListView) v.findViewById(R.id.LVFAVListDuties);
        TVFragmentAcceptWork = (TextView)v.findViewById(R.id.TVFragmentAcceptWork);
        listDuties = new ArrayList();
        listDutiesUndone = new ArrayList();
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        adapterCheckBoxs = new AdapterCheckBoxs(getActivity(), listDuties);
        LVFAVListDuties.setAdapter(adapterCheckBoxs);
        BFragmentAcceptWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.NotshowFragmentInfoAboutAcceptWork();
                HomeActivity.editScheduleStatus("1?");
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                            assert personOnDuty != null;
                            if(personOnDuty.getIdGroup().equals(HomeActivity.IDGroupUser)) {
                              if(personOnDuty.getName().equals(HomeActivity.todayPODStr)){
                                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                                    uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/" + ds.getKey() + "/statusHandOverWork";
                                    DatabaseReference reference = db.getReferenceFromUrl(uri);
                                    reference.setValue("0");
                                    uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/"
                                          + ds.getKey() + "/undoneDuties";
                                  reference = db.getReferenceFromUrl(uri);
                                  reference.setValue("-");
                                }
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
            }
        });



        BFragmentNotAcceptWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BFragmentNotAcceptWork.setVisibility(View.GONE);
                BFragmentAcceptWork.setVisibility(View.GONE);
                BFVSendNotes.setVisibility(View.VISIBLE);
                LVFAVListDuties.setVisibility(View.VISIBLE);
                TVFragmentAcceptWork.setText("Выбирите не выполненую обязанность");
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(listDuties.size()>0){
                            listDuties.clear();
                        }
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Group group = ds.getValue(Group.class);
                            assert group != null;
                            if(group.getIdGroup().equals(HomeActivity.IDGroupUser)){
                                String dutiesStr = group.getDuties();
                                if(!dutiesStr.equals("-")){
                                    StringBuilder buf = new StringBuilder();
                                    for(int i = 0; i<dutiesStr.length(); i++){
                                        if(dutiesStr.charAt(i) == ';'){
                                            listDuties.add(String.valueOf(buf));
                                            buf.delete(0, buf.length());
                                        }else buf.append(dutiesStr.charAt(i));
                                    }
                                }
                            }
                        }
                        adapterCheckBoxs.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mDataBaseGroup.addValueEventListener(valueEventListener);


            }
        });
        BFVSendNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.editScheduleStatus("?0");
                HomeActivity.NotshowFragmentInfoAboutAcceptWork();
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                            assert personOnDuty != null;
                            if(personOnDuty.getIdGroup().equals(HomeActivity.IDGroupUser) &&
                                    personOnDuty.getName().equals(HomeActivity.todayPODStr)){
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/" + ds.getKey() + "/statusHandOverWork";
                                DatabaseReference reference2 = db.getReferenceFromUrl(uri);
                                reference2.setValue("0");
                                if(listDutiesUndone.size() > 0) {
                                    String str = "";
                                    for (String s : listDutiesUndone) {
                                        str += s + ", ";
                                    }
                                    uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/" + ds.getKey() + "/undoneDuties";
                                    DatabaseReference reference = db.getReferenceFromUrl(uri);
                                    reference.setValue(str);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);

            }
        });
        return v;
    }


}