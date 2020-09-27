package com.example.kitchentest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class FragmentReplaceDay extends Fragment {

    private Button BFRDReplace, BFRDNot;
    private DatabaseReference mDataBasePOD, mDataBaseGroup;
    private ListView LVFRD;
    private Adapter AdapterFragmentReplaceDay;
    private List<String> listData;
    private ArrayList<String> buf;
    private int pointToday, posledvoet, posledvoet2, permissionToShowLVRDay;
    private String todayStr, uri, scheduleFull;

    public static String uriUserStatusFragmentReplace = null, uriReplaceFragmentReplace = null,
            IDGroupUserFragmentReplace, userNameFragmentReplace;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_replace_day, null);

        LVFRD = (ListView) v.findViewById(R.id.LVFRD);
        BFRDReplace = (Button) v.findViewById(R.id.BFRDReplace);
        BFRDNot = (Button) v.findViewById(R.id.BFRDNot);
        TextView textView2 = (TextView) v.findViewById(R.id.textView2);
        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        mDataBaseGroup  = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
        listData = new ArrayList<>();

        AdapterFragmentReplaceDay = new Adapter(getActivity(), listData);
        LVFRD.setAdapter(AdapterFragmentReplaceDay);


        pointToday = 0;
        //отображает лист всех доступных дней пользователя
        Calendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();
        todayStr = ":" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +
                "." + date.getDay() + "." + date.getMonth() + ";";

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(listData.size()>0){
                    listData.clear();
                }
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.idGroup.equals(IDGroupUserFragmentReplace)) {
                        try{
                            scheduleFull = group.getSchedule();
                            StringBuilder scheduleBuilder = new StringBuilder();
                            StringBuilder builderDate = new StringBuilder();
                            StringBuilder scheduleBuilderName = new StringBuilder();

                            for (int i = 0; i < scheduleFull.length(); i++) {
                                if (scheduleFull.charAt(i) == ':') {
                                    posledvoet = 1;
                                    if (String.valueOf(scheduleBuilderName).equals(userNameFragmentReplace)) {
                                        permissionToShowLVRDay = 1;
                                    }
                                } else if (posledvoet == 0) {
                                    scheduleBuilderName.append(scheduleFull.charAt(i));
                                }
                                if (posledvoet == 1) {
                                    builderDate.append(scheduleFull.charAt(i));
                                }

                                if (scheduleFull.charAt(i) == ';') {
                                    posledvoet = 0;

                                    if (todayStr.equals(String.valueOf(builderDate))) {
                                        pointToday = 1;
                                    }
                                    if (pointToday == 1 && permissionToShowLVRDay == 1) {
                                        permissionToShowLVRDay = 0;
                                        listData.add(Constant.createFormate(String.valueOf(scheduleBuilder)));
                                        buf.add(String.valueOf(scheduleBuilder));
                                    }

                                    scheduleBuilder.delete(0, scheduleBuilder.length());
                                    builderDate.delete(0, builderDate.length());
                                    scheduleBuilderName.delete(0, scheduleBuilderName.length());

                                } else {
                                    scheduleBuilder.append(scheduleFull.charAt(i));
                                }
                            }
                        }catch (Exception e){

                        }
                    }
                }
                try {
                    //удаляет первый элемет если он не нужен (имя другое)
                    StringBuilder firstName = new StringBuilder();
                    for(int i = 0; i<listData.get(0).length(); i++){
                        if(listData.get(0).charAt(i) == ':'){
                            if(!String.valueOf(firstName).equals(userNameFragmentReplace)){
                                listData.remove(0);
                                buf.remove(0);
                            }
                        }else firstName.append(listData.get(0).charAt(i));
                    }
                    AdapterFragmentReplaceDay.notifyDataSetChanged();
                }catch (IndexOutOfBoundsException e){
                    //график устарел походу..
                    // TVFragmentReplaceDay.setText(scheduleFull);
                }
                AdapterFragmentReplaceDay.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);








        BFRDNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HomeActivity.statusOne = 0;
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference reference = db.getReferenceFromUrl(uriUserStatusFragmentReplace);
                reference.setValue("0");
                DatabaseReference reference2 = db.getReferenceFromUrl(uriReplaceFragmentReplace);
                reference2.setValue("-");
            }
        });


        BFRDReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return v;
    }
}