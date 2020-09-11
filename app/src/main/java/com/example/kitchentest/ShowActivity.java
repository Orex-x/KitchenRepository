package com.example.kitchentest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {
    private TextView tvName, tvId;
    private DatabaseReference mDataBasePOD, mDataBaseGroup;
    public static FrameLayout fragmentVacation, fragmentAdditionalDuty;
    private Button BActivityShowOpenVacation, BActivityShowAddDay, BSActivityRemovePOD;
    private String userName, userIdGroup, scheduleFull, schedulePosle = "";
    private int posledvoetV, posledvoet, EdinstvennoyeNameInSchedule = 0;

    private ArrayList<String>  nameList, dateList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show);
        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
        tvName = (TextView) findViewById(R.id.ShowName);
        tvId = (TextView) findViewById(R.id.ShowId);

        nameList = new ArrayList<>();
        dateList = new ArrayList<>();
        BActivityShowOpenVacation = (Button) findViewById(R.id.BActivityShowOpenVacation);
        BActivityShowAddDay = (Button) findViewById(R.id.BActivityShowAddDay);
        BSActivityRemovePOD = (Button) findViewById(R.id.BSActivityRemovePOD);
        fragmentVacation = (FrameLayout) findViewById(R.id.fragment_Vacation);
        fragmentAdditionalDuty = (FrameLayout) findViewById(R.id.fragment_AdditionalDuty);
        fragmentVacation.setVisibility(View.GONE);
        fragmentAdditionalDuty.setVisibility(View.GONE);
        getIntentMain();
    }
    private void getIntentMain(){
        Intent intent = getIntent();
        if(intent != null){
            VacationFragment.userNameFragmentVacation = intent.getStringExtra(Constant.USER_NAME);
            AdditionalDutyFragment.namePOD = intent.getStringExtra(Constant.USER_NAME);
            tvName.setText(intent.getStringExtra(Constant.USER_NAME));
           // tvId.setText(intent.getStringExtra(Constant.USER_ID));
            userName = intent.getStringExtra(Constant.USER_NAME);
            userIdGroup = intent.getStringExtra(Constant.USER_IDGROUP);
        }
    }
    public void RemovePOD(View view) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                Intent intent = getIntent();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if(personOnDuty.getIdGroup().equals(userIdGroup) && personOnDuty.getName().equals(userName)){
                        if(personOnDuty.getId().equals("IDID")){
                            uri += ds.getKey();
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference reference = db.getReferenceFromUrl(uri);
                            reference.removeValue();
                            BActivityShowOpenVacation.setVisibility(View.GONE);
                            BActivityShowAddDay.setVisibility(View.GONE);
                            BSActivityRemovePOD.setVisibility(View.GONE);
                            fragmentVacation.setVisibility(View.GONE);
                            fragmentAdditionalDuty.setVisibility(View.GONE);

                            updateSchedule();
                            uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                        }else {
                            uri += ds.getKey() + "/idGroup";
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference reference = db.getReferenceFromUrl(uri);
                            reference.setValue("-");
                            BActivityShowOpenVacation.setVisibility(View.GONE);
                            BActivityShowAddDay.setVisibility(View.GONE);
                            BSActivityRemovePOD.setVisibility(View.GONE);
                            fragmentVacation.setVisibility(View.GONE);
                            fragmentAdditionalDuty.setVisibility(View.GONE);
                            updateSchedule();
                            uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                        }
                        tvId.setText("больше нет c нами");

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }

    private void updateSchedule() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uri = "https://dnevalnie.firebaseio.com/Group/";
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if (group.getIdGroup().equals(userIdGroup)) {
                        uri += ds.getKey() + "/schedule";
                        scheduleFull = group.getSchedule();
                        if (scheduleFull != null){
                            posledvoetV = 0;
                            posledvoet = 0;
                            StringBuilder nameBr = new StringBuilder();
                            StringBuilder dateBr = new StringBuilder();
                            for (int i = 0; i < scheduleFull.length(); i++) {
                                if (scheduleFull.charAt(i) == ':') {
                                posledvoet = 1;
                            } else if (scheduleFull.charAt(i) == ';') {
                                nameList.add(String.valueOf(nameBr));
                                dateList.add(String.valueOf(dateBr));
                                nameBr.delete(0, nameBr.length());
                                dateBr.delete(0, dateBr.length());
                                posledvoet = 0;
                            } else {
                                if (posledvoet == 0) {
                                    nameBr.append(scheduleFull.charAt(i));
                                } else {
                                    dateBr.append(scheduleFull.charAt(i));
                                }
                            }
                        }
                        for (int i = 0; i < nameList.size(); i++) {
                            if (nameList.get(i).equals(userName)) {
                                nameList.remove(i);
                            }
                        }
                        for (String name : nameList) {
                            if (name.equals(userName)) {
                                EdinstvennoyeNameInSchedule = 1;
                            } else {
                                EdinstvennoyeNameInSchedule = 0;
                                break;
                            }
                        }
                        if (EdinstvennoyeNameInSchedule == 0) {
                            for (int i = 0; i < nameList.size(); i++) {
                                schedulePosle += nameList.get(i) + ":" + dateList.get(i) + ";";
                            }
                        } else {
                            schedulePosle = null;
                        }
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl(uri);
                        reference.setValue(schedulePosle);
                        schedulePosle = "";
                        Toast.makeText(ShowActivity.this, "график обновлен", Toast.LENGTH_SHORT).show();

                    }
                }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
    }

    public void openVacation(View view) {
        fragmentVacation.setVisibility(View.VISIBLE);
        fragmentAdditionalDuty.setVisibility(View.GONE);
    }
    public void openAdditionalDuty(View view) {
        fragmentAdditionalDuty.setVisibility(View.VISIBLE);
        fragmentVacation.setVisibility(View.GONE);
    }
    public static void notShowFragmentVacation() {
        fragmentVacation.setVisibility(View.GONE);
    }
    public static void notShowFragmentAdditionalDuty() {
        fragmentAdditionalDuty.setVisibility(View.GONE);
    }
    public void backListGroup(View view) {
        onBackPressed();
    }
}
