package com.example.kitchentest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Schedule_ActivityForPOD extends AppCompatActivity {

    private ListView OKlistView;
    private ArrayList<String> buf;
    private Adapter OKarrayAdapter;
    private List<String> OKlistData;
    private DatabaseReference mDataBaseGroup,mDataBasePOD;
    public static String iDGroup = null, idPOD = null, uriUserForPOD = null, scheduleForPOD = null,
            userNameForScheduleActForPOD = null;
    public static int  stop = 0, ODCinScheduleActForPODone = 0, ODCinScheduleActForPODtwo = 0;
    private TextView TVNullList, TVaboutReplaceDay;
    private String uri,replaceDay = null, todayStr = null;
    private int stopCheckStatus = 0, posledvoet = 0, permissionToShowLVRDay = 0, pointToday =1,
            replaseOneTap = 0, setOnItemRobit = 0;

    private Button replacePODinSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_schedule__for_pod);
        init();
        if(scheduleForPOD == null){
            TVNullList.setVisibility(View.VISIBLE);
            replacePODinSchedule.setVisibility(View.GONE);
        }else {
            TVNullList.setVisibility(View.GONE);
            getScheduleFromDb();
        }
    }

    private void getScheduleFromDb(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(OKlistData.size()>0){
                    OKlistData.clear();
                }


                Calendar calendar = new GregorianCalendar();
                Date date = calendar.getTime();
                todayStr = ":" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +
                        "." + date.getDay() + "." + date.getMonth() + ",";

                pointToday = 0;

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.idGroup.equals(iDGroup)) {
                        String scheduleFull = group.getSchedule();
                        StringBuilder scheduleBuilder = new StringBuilder();
                        StringBuilder builderDate = new StringBuilder();
                        StringBuilder scheduleBuilderName = new StringBuilder();


                        for(int i = 0; i<scheduleFull.length(); i++){



                            if(scheduleFull.charAt(i) == ':'){
                                posledvoet = 1;
                                if(String.valueOf(scheduleBuilderName).equals(userNameForScheduleActForPOD)){
                                    permissionToShowLVRDay = 1;
                                }
                            }else if(posledvoet == 0){
                                scheduleBuilderName.append(scheduleFull.charAt(i));
                            }
                            if(posledvoet == 1){
                                builderDate.append(scheduleFull.charAt(i));
                            }

                            if(scheduleFull.charAt(i) == ';'){
                                posledvoet = 0;

                                builderDate.delete(builderDate.length()-3, builderDate.length());
                                if(todayStr.equals(String.valueOf(builderDate))){
                                    pointToday = 1;
                                }
                                if(pointToday == 1 && permissionToShowLVRDay==1){
                                    permissionToShowLVRDay = 0;
                                    scheduleBuilder.delete(scheduleBuilder.length()-3, scheduleBuilder.length());
                                    OKlistData.add(Constant.createFormate(String.valueOf(scheduleBuilder)));
                                    buf.add(String.valueOf(scheduleBuilder));
                                }

                                scheduleBuilder.delete(0, scheduleBuilder.length());
                                builderDate.delete(0, builderDate.length());
                                scheduleBuilderName.delete(0, scheduleBuilderName.length());

                            }else{
                                scheduleBuilder.append(scheduleFull.charAt(i));
                            }
                        }
                    }
                }
                try {
                    OKlistData.remove(0);
                    buf.remove(0);
                    OKarrayAdapter.notifyDataSetChanged();
                }catch (IndexOutOfBoundsException e){
                    TVNullList.setText("Неактуальный график");
                    TVNullList.setVisibility(View.VISIBLE);
                    replacePODinSchedule.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
    }






    private void setOnClickItemReplaceDay(){
            OKlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(setOnItemRobit == 1) {
                        replacePODinSchedule.setBackgroundResource(R.drawable.button_ready_replace);
                        replaceDay = buf.get(position);
                        TVaboutReplaceDay.setText(Constant.createFormate(replaceDay));
                    }

                }
            });

    }




    public void replacePODinSchedule() {
        ODCinScheduleActForPODone = 1;
        stopCheckStatus = 0;
        stop = 0;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(ODCinScheduleActForPODone == 1) {
                    if (stopCheckStatus == 0){
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                                assert personOnDuty != null;
                                String stat = personOnDuty.getStatus();
                                if (stat.equals("1")) {
                                    stop = 1;
                                    stopCheckStatus = 1;
                                } else stopCheckStatus = 1;
                            }
                            if (stop == 0) {
                                replacePODinSchedulePartTwo();
                            } else {
//                                replaceDay = null;
                                Toast.makeText(Schedule_ActivityForPOD.this,
                                        "Прошлый опрос еще не закончился", Toast.LENGTH_SHORT).show();
                                TVaboutReplaceDay.setText("");
                                setOnItemRobit = 0;
                            }
                        ODCinScheduleActForPODone = 0;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addValueEventListener(valueEventListener);
    }
    public void replacePODinSchedulePartTwo() {
            ODCinScheduleActForPODtwo = 1;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(ODCinScheduleActForPODtwo == 1) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                        assert personOnDuty != null;

                        if (personOnDuty.getIdGroup().equals(iDGroup) && !personOnDuty.getId().equals(idPOD)
                                && !personOnDuty.getId().equals("IDID")) {
                            uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                            uri = uri + ds.getKey() + "/status";
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference reference = db.getReferenceFromUrl(uri);
                              reference.setValue("1");
                            uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                            uri = uri + ds.getKey() + "/replaceDay";
                            DatabaseReference reference2 = db.getReferenceFromUrl(uri);
                            reference2.setValue(replaceDay);
                            uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                        }
                    }
                    ODCinScheduleActForPODtwo = 0;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addValueEventListener(valueEventListener);
        TVaboutReplaceDay.setText("");
        setOnItemRobit = 0;
        Toast.makeText(Schedule_ActivityForPOD.this,"ведутся работы", Toast.LENGTH_SHORT).show();
    }




    public void replacePODinScheduleButton(View view) {
        replaseOneTap++;
        if(replaseOneTap == 1) {
            replacePODinSchedule.setBackgroundResource(R.drawable.buttoncall);
            Toast.makeText(Schedule_ActivityForPOD.this, "Выберите день " +
                    "который хотели бы поменять", Toast.LENGTH_SHORT).show();
            setOnItemRobit = 1;
            setOnClickItemReplaceDay();
        }else{
            replacePODinSchedule.setBackgroundResource(R.drawable.button);
            if (replaceDay != null) {
                replacePODinSchedule();
                setOnItemRobit = 0;
            }
            replaseOneTap = 0;
            setOnItemRobit = 0;
        }
    }


    public void backHomeActivityFromASFPOD(View view) {
        onBackPressed();
    }



    private void init() {
        OKlistView = (ListView) findViewById(R.id.OKListViewInSchedule_ActivityForPOD);
        OKlistData = new ArrayList<>();
        OKarrayAdapter = new Adapter(this, OKlistData);
        OKlistView.setAdapter(OKarrayAdapter);

        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);

        TVaboutReplaceDay = (TextView) findViewById(R.id.TVaboutReplaceDay);
        TVNullList = (TextView) findViewById(R.id.TVNullList);
        replacePODinSchedule = (Button) findViewById(R.id.replacePODinSchedule);

        buf = new ArrayList<>();

        uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
    }
}
