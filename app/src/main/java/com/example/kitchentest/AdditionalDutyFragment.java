package com.example.kitchentest;

import java.util.GregorianCalendar;
import java.util.Calendar;
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

import java.util.ArrayList;


public class AdditionalDutyFragment extends Fragment {
    private Button BFragmentAdditionalDutyONE, BFragmentAdditionalDutyTHREE,
            BFragmentAdditionalDutyFIVE, BFragmentAdditionalDutyBack, BFragmentAdditionalDutyADD;
    private String uri;
    public static String IDGroupUserAddDutyFragment = null, namePOD = null;
    private DatabaseReference mDataBaseGroup;

    private int posledvoet = 0, indexToday = 0, addDays = 0;
    private String  shedulePosle = "";
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<>();
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_additional_duty, null);

        BFragmentAdditionalDutyONE = (Button) v.findViewById(R.id.BFragmentAdditionalDutyONE);
        BFragmentAdditionalDutyTHREE = (Button) v.findViewById(R.id.BFragmentAdditionalDutyTHREE);
        BFragmentAdditionalDutyFIVE = (Button) v.findViewById(R.id.BFragmentAdditionalDutyFIVE);
        BFragmentAdditionalDutyBack = (Button) v.findViewById(R.id.BFragmentAdditionalDutyBack);
        BFragmentAdditionalDutyADD = (Button) v.findViewById(R.id.BFragmentAdditionalDutyADD);
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);

        BFragmentAdditionalDutyONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDays = 1;
                BFragmentAdditionalDutyONE.setBackgroundResource(R.drawable.buttoncall);
                BFragmentAdditionalDutyTHREE.setBackgroundResource(R.drawable.button);
                BFragmentAdditionalDutyFIVE.setBackgroundResource(R.drawable.button);
            }
        });
        BFragmentAdditionalDutyTHREE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDays = 3;
                BFragmentAdditionalDutyTHREE.setBackgroundResource(R.drawable.buttoncall);
                BFragmentAdditionalDutyFIVE.setBackgroundResource(R.drawable.button);
                BFragmentAdditionalDutyONE.setBackgroundResource(R.drawable.button);
            }
        });
        BFragmentAdditionalDutyFIVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDays = 5;
                BFragmentAdditionalDutyFIVE.setBackgroundResource(R.drawable.buttoncall);
                BFragmentAdditionalDutyTHREE.setBackgroundResource(R.drawable.button);
                BFragmentAdditionalDutyONE.setBackgroundResource(R.drawable.button);
            }
        });
        BFragmentAdditionalDutyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowActivity.notShowFragmentAdditionalDuty();
            }
        });
        BFragmentAdditionalDutyADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Group group = ds.getValue(Group.class);
                            assert group != null;
                            if(group.idGroup.equals(IDGroupUserAddDutyFragment)) {
                                uri = "https://dnevalnie.firebaseio.com/Group/"+ ds.getKey() +"/schedule";
                                String schedule = group.getSchedule();
                                StringBuilder nameBr = new StringBuilder();
                                StringBuilder dateBr = new StringBuilder();
                                Calendar today = new GregorianCalendar();
                                String sStr = today.get(Calendar.DAY_OF_MONTH) + "." + String.valueOf(Integer.valueOf(today.get(Calendar.DAY_OF_WEEK) - 1)) +
                                        "." + today.get(Calendar.MONTH);

                                for (int i = 0; i < schedule.length(); i++) {
                                    if (schedule.charAt(i) == ':') {
                                        posledvoet = 1;
                                    } else if (schedule.charAt(i) == ';') {
                                        nameList.add(String.valueOf(nameBr));
                                        dateList.add(String.valueOf(dateBr));
                                        nameBr.delete(0, nameBr.length());
                                        dateBr.delete(0, dateBr.length());
                                        posledvoet = 0;
                                    } else {
                                        if (posledvoet == 0) {
                                            nameBr.append(schedule.charAt(i));
                                        } else {
                                            dateBr.append(schedule.charAt(i));
                                        }
                                    }
                                }
                                for(int i = 0; i<dateList.size(); i++){
                                    if(dateList.get(i).equals(sStr)){
                                        indexToday = i;
                                        break;
                                    }
                                }
                                for(int i = 0; i<addDays; i++){
                                    if(nameList.get(indexToday).equals(namePOD)){
                                        indexToday++;
                                    }
                                    nameList.add(indexToday, namePOD);
                                    indexToday++;
                                }

                                for(int i = 0; i<dateList.size(); i++){
                                    shedulePosle += nameList.get(i) + ":" + dateList.get(i) + ";";
                                }
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference reference = db.getReferenceFromUrl(uri);
                                reference.setValue(shedulePosle);
                                ShowActivity.notShowFragmentAdditionalDuty();
                                BFragmentAdditionalDutyTHREE.setBackgroundResource(R.drawable.button);
                                BFragmentAdditionalDutyFIVE.setBackgroundResource(R.drawable.button);
                                BFragmentAdditionalDutyONE.setBackgroundResource(R.drawable.button);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
            }
        });


        return v;
    }
}