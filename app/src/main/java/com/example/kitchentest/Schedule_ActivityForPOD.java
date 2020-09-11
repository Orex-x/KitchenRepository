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
    private ArrayAdapter<String> OKarrayAdapter;
    private List<String> OKlistData;
    private List<Group> OKlistItem;
    private DatabaseReference mDataBaseGroup,mDataBasePOD;
    public static String iDGroup = null, idPOD = null, uriUserForPOD = null, scheduleForPOD = null, schedule = null,
            userNameForScheduleActForPOD = null;
    public static int threadIntFromScheduleActForPOD = 0, stop = 0, ODCinScheduleActForPODone = 0, ODCinScheduleActForPODtwo = 0;
    private TextView TVNullList;
    private String uri,replaceDay = null, todayStr = null;
    private int stopCheckStatus = 0, posledvoet = 0, permissionToShowLVRDay = 0, pointToday =1, replaseOneTap = 0;

    private Button replacePODinSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                        "." + date.getDay() + "." + date.getMonth() + ";";

                pointToday = 0;

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
//                    if(group.idGroup.equals(iDGroup)) {
//                        String scheduleFull = group.getSchedule();
//                        StringBuilder scheduleBuilder = new StringBuilder();
//                        StringBuilder scheduleBuilderName = new StringBuilder();
//
//                        for(int i = 0; i<scheduleFull.length(); i++){
//
//                            if(scheduleFull.charAt(i) == ':'){
//                                posledvoet = 1;
//                                if(String.valueOf(scheduleBuilderName).equals(userNameForScheduleActForPOD)){
//                                    permissionToShowLVRDay = 1;
//                                }
//                                scheduleBuilderName.delete(0, scheduleBuilderName.length());
//                            }else if(posledvoet == 0){ scheduleBuilderName.append(scheduleFull.charAt(i)); }
//
//
//                            if(scheduleFull.charAt(i) == ';'){
//                                posledvoet = 0;
//                                if(permissionToShowLVRDay == 1) {
//                                    permissionToShowLVRDay = 0;
//                                    OKlistData.add(createFormate(String.valueOf(scheduleBuilder)));
//                                    buf.add(String.valueOf(scheduleBuilder));
//                                }
//                                scheduleBuilder.delete(0, scheduleBuilder.length());
//                            }else scheduleBuilder.append(scheduleFull.charAt(i));
//                        }
//                    }
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

                                if(todayStr.equals(String.valueOf(builderDate))){
                                    pointToday = 1;
                                }
                                if(pointToday == 1 && permissionToShowLVRDay==1){
                                    permissionToShowLVRDay = 0;
                                    OKlistData.add(createFormate(String.valueOf(scheduleBuilder)));
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
                    //график устарел походу..
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
    private String createFormate(String str){
        String dayOfWeek = " ", month = " ";
        StringBuilder itog = new StringBuilder();
        itog.append(str);
//        char c = '0';
//        int l = 0;
//
//        try {
//            c = str.charAt(str.length() - 2);
//            l = str.length();
//        }catch(StringIndexOutOfBoundsException e){
//
//        }
//
//        if(c== '.'){
//            dayOfWeek = String.valueOf(str.charAt(l - 3));
//            month  = String.valueOf(str.charAt(l-1));
//            itog.delete(l-4, l);

//        }else{
//            dayOfWeek = String.valueOf(str.charAt(l - 3));
//            month  = String.valueOf(str.charAt(l-1));
//            itog.delete(l-4, l);
//        }
        try{
            if(itog.charAt(itog.length()-2) == '.'){
                dayOfWeek = String.valueOf(itog.charAt(itog.length() - 3));
                month = itog.substring(itog.length()-1, itog.length());
                itog.delete(itog.length()-4, itog.length());
            }else{
                dayOfWeek = String.valueOf(itog.charAt(itog.length() - 4));
                month = itog.substring(itog.length()-1, itog.length());
                itog.delete(itog.length()-5, itog.length());
            }
        }catch (Exception e){
            Toast.makeText(Schedule_ActivityForPOD.this, "жжопе", Toast.LENGTH_SHORT).show();
        }

        switch (dayOfWeek){
            case("0"): dayOfWeek = " воскресенье"; break;
            case("1"): dayOfWeek = " понедельник"; break;
            case("2"): dayOfWeek = " вторник"; break;
            case("3"): dayOfWeek = " среда"; break;
            case("4"): dayOfWeek = " четерг"; break;
            case("5"): dayOfWeek = " пятница"; break;
            case("6"): dayOfWeek = " суббота"; break;
        }
        switch (month){
            case("0"): month = " января"; break;
            case("1"): month = " февраля"; break;
            case("2"): month = " марта"; break;
            case("3"): month = " апреля"; break;
            case("4"): month = " мая"; break;
            case("5"): month = " июня"; break;
            case("6"): month= " июля"; break;
            case("7"): month= " августа"; break;
            case("8"): month= " сентября"; break;
            case("9"): month= " октября"; break;
            case("10"): month = " ноября"; break;
            case("11"): month = " дерабря"; break;
        }

        itog.append(month);
        itog.append(dayOfWeek);
        return String.valueOf(itog);
    }





    private void setOnClickItemReplaceDay(){

        OKlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                replacePODinSchedule.setBackgroundResource(R.drawable.button_ready_replace);
                replaceDay = buf.get(position);

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
                                replaceDay = null;
                                Toast.makeText(Schedule_ActivityForPOD.this, "Прошлый опрос еще не закончился", Toast.LENGTH_SHORT).show();
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
                                && !personOnDuty.getPhone().equals("0000")) {
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
        replaceDay = null;
        Toast.makeText(Schedule_ActivityForPOD.this,"ведутся работы", Toast.LENGTH_SHORT).show();
    }




    public void replacePODinScheduleButton(View view) {
        replaseOneTap++;
        if(replaseOneTap == 1) {
            replacePODinSchedule.setBackgroundResource(R.drawable.buttoncall);
            Toast.makeText(Schedule_ActivityForPOD.this, "Выберите день который хотели бы поменять", Toast.LENGTH_SHORT).show();
            setOnClickItemReplaceDay();
        }else{
            replacePODinSchedule.setBackgroundResource(R.drawable.button);
            if (replaceDay != null) {
                replacePODinSchedule();
            }
            replaseOneTap = 0;
        }
    }


    public void backHomeActivityFromASFPOD(View view) {
        onBackPressed();
    }



    private void init() {
        OKlistView = (ListView) findViewById(R.id.OKListViewInSchedule_ActivityForPOD);
        OKlistData = new ArrayList<>();
        OKarrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, OKlistData);
        OKlistView.setAdapter(OKarrayAdapter);

        OKlistItem = new ArrayList<>();
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);

        TVNullList = (TextView) findViewById(R.id.TVNullList);
        replacePODinSchedule = (Button) findViewById(R.id.replacePODinSchedule);

        buf = new ArrayList<>();

        uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
    }
}
