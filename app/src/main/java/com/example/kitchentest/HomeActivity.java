package com.example.kitchentest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
import java.lang.Thread;

class ThreadCheckStatus extends Thread{
    public static String UserIDforThread2 = null;
    private DatabaseReference mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
    public static int ODCinThreadChecStatone = 0;
    @Override
    public void run(){
        ODCinThreadChecStatone = 1;

        while(HomeActivity.chekStatus){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
//                    if (ODCinThreadChecStatone == 1){
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                            assert personOnDuty != null;
                            if (personOnDuty.getId().equals(UserIDforThread2) && ODCinThreadChecStatone == 1) {
                                if (personOnDuty.getStatus().equals("1")) {
                                    ODCinThreadChecStatone = 0;
                                   // HomeActivity.statusOne = 1;
                                    HomeActivity.chekStatus = false;
                                    HomeActivity.showQuestionAboutAcceptReplace();
                                }

                            }
                        }
                    }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
 

class ThreadCheckStatusHOWork extends Thread{
    private DatabaseReference mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
    @Override
    public void run(){
        while (HomeActivity.chekStatusHOWork){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                        assert personOnDuty != null;
                        if(personOnDuty.getIdGroup().equals(HomeActivity.IDGroupUser)){
                            if(HomeActivity.userName.equals(HomeActivity.tomorrowPODstr)){
                                checkStatusHOWork();
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
    }
    void checkStatusHOWork(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if(personOnDuty.getIdGroup().equals(HomeActivity.IDGroupUser)) {
                        if (personOnDuty.getName().equals(HomeActivity.todayPODStr)) {
                            if (personOnDuty.getStatusHandOverWork().equals("1")) {
                                HomeActivity.showFragmentInfoAboutAcceptWork();
                            }
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
}





public class HomeActivity extends AppCompatActivity {

        public static DatabaseReference mDataBaseW, mDataBasePOD, mDataBaseGroup;
        private TextView TVActHomeGlav, TodayPOD,YesterdaayPOD, TomorrowPOD,
                TVHActivitySecundu, GroupIsNull, TVHActivityReplaceName,
                TV_replace_day_p2, TVAHReplacetext,
                TVAHAcceptToday, TVAHHandOverToday, TVAHAcceptTommorow, TVAHHandOverTommorow, TVAHAcceptYesterday, TVAHHandOverYesterday;
        public static TextView TVaboutAcceptReplace,TVaboutAcceptWork;

        private EditText ETHActivityGroupID, editTextTextPersonName;


        public static String UserID = null, UriUserInHome, userName = null, userStatus = null,userPhone = null,
                uriUserStatus, checkreplaceDay, IDGroupUser = null, replaceDay, todayPODStr, tomorrowPODstr = "";
        private static String itogNotAcceptWork = "";
        private String  todayStr = "", yesterdayPODstr = "",
                uriReplace, replaceForDay = null, nameOne = null, nameTwo  = null,  uriFirst,
                uriSecond, schedule, editTextIDGroup, editTextName,
                scheduleFullReplaceDay;




        private int promptToEnterIDGroupIsWas = 0, groupIsExist = 1, scheduleExistTPODnotExist = 1;;
        private static int methodOn = 0;
        public static int  stopChekWhoIsUser = 0, ODCinHomeActtwo = 0,
                ODCinHomeActthree = 0,  posledvoet = 0, posledvoet2 = 0,
                permissionToShowLVRDay = 0, indexDay = 0, indexForDay = 0,
                scheduleIsLagbehind = 0, NameinGroupBe = 0, pointToday = 0,
                ODCinFragmentReplaceTwo = 0, onlyOneStopper = 0;


    private Button listGroupForWatcher, listGroupForPOD, scheduleForPOD, BHActivityAddIdGroup,
            BHActivityReplaceName,CallYesterdaayPOD, CallTodayPOD, CallTomorrowPOD,
            BexitFromApp, BSettings, BAHLogOut, BAHNotReplace, BAHReplace, BAHsetHandOverWorkOne, BAHEdit;
    public static Button BHAcceptReplace, BHNotAcceptReplace, showUndoneDuties;


    public static List<String> OKlistDataInHome;
    private List<String> listDataForReplaseFinishFragmentReplace, listDataForReplaseSchedule = new ArrayList<>();
    private  ArrayList<String> buf, listName, listNamePOD;
    private ArrayList<Watcher> listForImportWatchers = new ArrayList<>();
    private static ArrayList<String>
            listSchedule = new ArrayList<>(),
            buflistScheduleDate = new ArrayList<>(),
            buflistScheduleDateClear = new ArrayList<>(),
            buflistScheduleName = new ArrayList<>(),
            newlistSchedule = new ArrayList<>();
    private ListView LVAHReplaceDay;



        private Intent callTodayPODI, callTomorrowPODI, callYesterdayPODI;
        private Calendar today;
        public static FrameLayout fragmentAccrptWork, infoAboutUndoneDuties;
        public static TextView TVQestionReplace;
        private Adapter ArrayAdapterForReplaceFinishFragmentReplace;

        public static boolean chekStatus = true, chekStatusHOWork = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        checkGroup();
    }

    private void checkGroup() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if(personOnDuty.getId().equals(UserID)){
                        userName = personOnDuty.getName();
                        editTextTextPersonName.setText(userName);
                    }
                    if(personOnDuty.getIdGroup().equals("-") && personOnDuty.getId().equals(UserID)){
                        TVHActivitySecundu.setVisibility(View.GONE);
                        promptToEnterIDGroup();
                        promptToEnterIDGroupIsWas = 1;
                    }
                }
                if(promptToEnterIDGroupIsWas == 0){
                    ToEnterIDGroup();
                    TVHActivitySecundu.setVisibility(View.GONE);
                    TVActHomeGlav.setVisibility(View.VISIBLE);
                    startMainCode();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }

    private void startMainCode() {
        // checkReplaceDayInBD();

        scheduleForPOD.setVisibility(View.VISIBLE);

        fragmentAccrptWork.setVisibility(View.VISIBLE);
        YesterdaayPOD.setVisibility(View.VISIBLE);
        TodayPOD.setVisibility(View.VISIBLE);
        TomorrowPOD.setVisibility(View.VISIBLE);
        BSettings.setVisibility(View.VISIBLE);
        BAHEdit.setVisibility(View.VISIBLE);
        BexitFromApp.setVisibility(View.VISIBLE);
        NotshowTextInfoAboutReplace();
        NotshowFragmentInfoAboutAcceptWork();
        notshowQuestionAboutAcceptReplace();
        //ПОДУМАТЬ КАК МОЖНО ОПТИМИЗИРОВАТЬ
        getDataFromTableW();
        getDataFromTablePOD();
        checkStatus();
        checkReplaceDay();
        SettingActivity.populateList();
    }

    private void promptToEnterIDGroup() {
        GroupIsNull.setVisibility(View.VISIBLE);
        ETHActivityGroupID.setVisibility(View.VISIBLE);
        BHActivityAddIdGroup.setVisibility(View.VISIBLE);
        TVHActivityReplaceName.setVisibility(View.VISIBLE);
        editTextTextPersonName.setVisibility(View.VISIBLE);
        BAHLogOut.setVisibility(View.VISIBLE);
        BHActivityReplaceName.setVisibility(View.VISIBLE);
    }

    private void ToEnterIDGroup() {
        GroupIsNull.setVisibility(View.GONE);
        ETHActivityGroupID.setVisibility(View.GONE);
        BHActivityAddIdGroup.setVisibility(View.GONE);
        BAHLogOut.setVisibility(View.GONE);
        TVHActivityReplaceName.setVisibility(View.GONE);
        editTextTextPersonName.setVisibility(View.GONE);
        BHActivityReplaceName.setVisibility(View.GONE);
        TVActHomeGlav.setVisibility(View.VISIBLE);
    }


    public void addIDGroupFromHActivity(View view) {
        editTextIDGroup = ETHActivityGroupID.getText().toString();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.getIdGroup().equals(editTextIDGroup)){
                        addIDGroupFromHActivityCont();
                        groupIsExist = 0;
                    }
                }
                if(groupIsExist == 1){
                    Toast.makeText(HomeActivity.this, "Группы не существует", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
    }

    private void addIDGroupFromHActivityCont() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uriAddIDGroup = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                NameinGroupBe = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if(personOnDuty.getIdGroup().equals(editTextIDGroup) && personOnDuty.getName().equals(userName)){
                        NameinGroupBe = 1;
                    }
                    if(personOnDuty.getId().equals(UserID)){
                        uriAddIDGroup += ds.getKey() + "/idGroup";
                    }
                }
                if(NameinGroupBe == 0) {
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReferenceFromUrl(uriAddIDGroup);
                    reference.setValue(editTextIDGroup);
                    ToEnterIDGroup();
                    startMainCode();
                }else {
                    Toast.makeText(HomeActivity.this, "Имя занято", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }

    public void replaceName(View view) {
        editTextName = editTextTextPersonName.getText().toString();
        editTextName.trim();
        editTextIDGroup = ETHActivityGroupID.getText().toString();
        editTextIDGroup.trim();
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uri= "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if(personOnDuty.getId().equals(UserID)){
                        uri = uri + ds.getKey() + "/name";
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl(uri);
                        editTextName.replace(" ", "");
                        reference.setValue(editTextName);
                        uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                        userName = editTextName;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
        Toast.makeText(HomeActivity.this, "Готово", Toast.LENGTH_SHORT).show();
    }


    private void makeStatusNull(){
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           String uriPODReplaceDay = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
            for(DataSnapshot ds : dataSnapshot.getChildren()){
                PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                assert personOnDuty != null;
                if(personOnDuty.getIdGroup().equals(IDGroupUser)){
                    uriPODReplaceDay = uriPODReplaceDay + ds.getKey() + "/status";
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReferenceFromUrl(uriPODReplaceDay);
                    reference.setValue("0");
                    uriPODReplaceDay = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                    uriPODReplaceDay = uriPODReplaceDay + ds.getKey() + "/replaceDay";
                    DatabaseReference reference2 = db.getReferenceFromUrl(uriPODReplaceDay);
                    reference2.setValue("-");
                    uriPODReplaceDay = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };
    mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
}
    private void checkReplaceDay(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder checkreplaceDayBuilder = new StringBuilder();
                int stop = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if (personOnDuty.getIdGroup().equals(IDGroupUser)) {
                        checkreplaceDay = personOnDuty.replaceDay;
                        if (!checkreplaceDay.equals("-") && stop == 0) {
                            stop = 1;
                            checkreplaceDayBuilder.append(checkreplaceDay);
                            for (int i = 0; i < checkreplaceDayBuilder.length(); i++) {
                                if (checkreplaceDayBuilder.charAt(i) == ':') {
                                    checkreplaceDayBuilder.delete(0, i + 1);
                                }
                            }
                        }
                    }
                }
                try {
                    if (stop == 1) {
                        if (checkreplaceDayBuilder.charAt(1) == '.') {

                            int replaceDayInt = Integer.parseInt(String.valueOf(checkreplaceDayBuilder.charAt(0)));
                            int replaceMonthInt = Integer.parseInt(checkreplaceDayBuilder.substring(4,
                                    checkreplaceDayBuilder.length()));


                            Date dateToday = today.getTime();
                            int todayDayInt = today.get(Calendar.DAY_OF_MONTH);
                            int todayMonthInt = dateToday.getMonth();


                            if (todayMonthInt > replaceMonthInt) {
                               makeStatusNull();
                            } else if (todayMonthInt == replaceMonthInt) {
                                if (todayDayInt >= replaceDayInt) {
                                    makeStatusNull();
                                }
                            }

                        } else {

                            int replaceDayInt = Integer.parseInt(checkreplaceDayBuilder.substring(0, 2));
                            int replaceMonthInt = Integer.parseInt(checkreplaceDayBuilder.substring(5,
                                    checkreplaceDayBuilder.length()));

                            Date dateToday = today.getTime();
                            int todayDayInt = today.get(Calendar.DAY_OF_MONTH);
                            int todayMonthInt = dateToday.getMonth();

                            if (todayMonthInt > replaceMonthInt) {
                                makeStatusNull();
                            } else if (todayMonthInt == replaceMonthInt) {
                                if (todayDayInt >= replaceDayInt) {
                                    makeStatusNull();
                                }
                            }

                        }
                    }
                }catch (Exception e){
                    Toast.makeText(HomeActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }

    public static void checkStatus() {
        ThreadCheckStatus threadCheckStatus = new ThreadCheckStatus();
        threadCheckStatus.start();
    }

    public static void chekStatutHOWork() {
        ThreadCheckStatusHOWork threadCheckStatusHOWork = new ThreadCheckStatusHOWork();
        threadCheckStatusHOWork.start();

    }


    private void getDataFromTableW(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Watcher watcher = ds.getValue(Watcher.class);
                    assert watcher != null;
                    if(watcher.getId().equals(UserID)){
                        listForImportWatchers.add(watcher);
                        userName = watcher.getName();
                        userStatus = watcher.getRole();
                        userPhone = watcher.getPhone();
                        Toast.makeText(HomeActivity.this, "Добро пожаловать " + userName, Toast.LENGTH_SHORT).show();
                        scheduleIsLagbehind++;
                        listGroupForWatcher.setVisibility(View.VISIBLE);
                        listGroupForPOD.setVisibility(View.GONE);
                        IDGroupUser = watcher.getPhone();
                        VacationFragment.IDGroupUserVacationFragment = IDGroupUser;
                        AdditionalDutyFragment.IDGroupUserAddDutyFragment = IDGroupUser;
                        getUriUserForWatcher();
                    }
                }
                importListWatchers(listForImportWatchers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBaseW.addListenerForSingleValueEvent(valueEventListener);

           }
    private void importListWatchers(ArrayList<Watcher> listForImport) {
        ListOfFirebase.fullListWatcher = listForImport;
    }
    private void getUriUserForWatcher()  {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.getIdGroup().equals(IDGroupUser)){
                        UriUserInHome = uriFirst + ds.getKey() + "/" + "schedule";
                        Schedule_ActivityForWatcher.UriUser = UriUserInHome;
                        ListOfFirebase.scheduleStr = group.getSchedule();
                        if(ListOfFirebase.scheduleStr != null){
                            showTodayPOD();
                        }else{
                            CallYesterdaayPOD.setVisibility(View.GONE);
                            CallTomorrowPOD.setVisibility(View.GONE);
                            CallTodayPOD.setVisibility(View.GONE);
                            TodayPOD.setText("Сегодня никто не дежурит");
                            YesterdaayPOD.setText("Вчера никто не дежурил");
                            TomorrowPOD.setText("Завтра никто не дежурит");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent( valueEventListener);
    }



    private void getDataFromTablePOD(){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);

                    assert personOnDuty != null;

                    if (personOnDuty.getId().equals(UserID)) {
                        replaceDay = personOnDuty.getReplaceDay();
                        userName = personOnDuty.getName();
                        userStatus = personOnDuty.getRole();
                        IDGroupUser = personOnDuty.getIdGroup();
                        userPhone = personOnDuty.getPhone();
                        if(!personOnDuty.getUndoneDuties().equals("-")){
                            showUndoneDuties.setVisibility(View.VISIBLE);
                        }
                        uriUserStatus = "https://dnevalnie.firebaseio.com/Person_On_Duty/"
                                + ds.getKey() + "/status";
                        uriReplace = "https://dnevalnie.firebaseio.com/Person_On_Duty/"
                                + ds.getKey() + "/replaceDay";

                        Toast.makeText(HomeActivity.this, "Добро пожаловать " + userName, Toast.LENGTH_SHORT).show();
                        Schedule_ActivityForPOD.userNameForScheduleActForPOD = userName;
                        scheduleIsLagbehind++;
                        listGroupForPOD.setVisibility(View.VISIBLE);
                        listGroupForWatcher.setVisibility(View.GONE);
                        scheduleForPOD.setVisibility(View.VISIBLE);
                        getUriUserForPOD();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }
    private void getUriUserForPOD()  {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.getIdGroup().equals(IDGroupUser)){
                        UriUserInHome = uriFirst + ds.getKey() + "/" + "schedule";
                        Schedule_ActivityForPOD.uriUserForPOD = uriFirst + uriSecond + "/" + "schedule";
                        Schedule_ActivityForPOD.scheduleForPOD = group.getSchedule();
                        if(group.getSchedule() != null){
                            showTodayPOD();
                        }else{
                            CallYesterdaayPOD.setVisibility(View.GONE);
                            CallTomorrowPOD.setVisibility(View.GONE);
                            CallTodayPOD.setVisibility(View.GONE);
                            TodayPOD.setText("Сегодня никто не дежурит");
                            YesterdaayPOD.setText("Вчера никто не дежурил");
                            TomorrowPOD.setText("Завтра никто не дежурит");
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
    //находит сегодняшнего дежурного и обновляет график при необходимости
    private void showTodayPOD() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Date dateToday = today.getTime();
                todayStr = ":" + String.valueOf(today.get(Calendar.DAY_OF_MONTH)) +
                        "." + dateToday.getDay() + "." + dateToday.getMonth();
                today.add(Calendar.DAY_OF_YEAR, 1);
                boolean stat = false;
                final int hour = dateToday.getHours();
                if (OKlistDataInHome.size() > 0) {
                    OKlistDataInHome.clear();
                }
                if (listName.size() > 0) {
                    listName.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if (group.idGroup.equals(IDGroupUser)) {
                        schedule = group.getSchedule();
                        if (schedule != null){
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < schedule.length(); i++) {
                                if (schedule.charAt(i) == ':') {
                                    listName.add(String.valueOf(builder));
                                    builder.delete(0, i);
                                }

                                if (schedule.charAt(i) == ',') {
                                    stat = true;
                                    OKlistDataInHome.add(String.valueOf(builder));
                                    builder.delete(0, builder.length());
                                } else if(!stat){
                                    builder.append(schedule.charAt(i));
                                }
                                if(schedule.charAt(i) == ';'){
                                    stat = false;
                                }
                            }
                    }
                } //Распределяет имена и дату по листам

                //находит сегодняшнего дежурного и обновляет график при необходимости
                for (int i = 0; i < OKlistDataInHome.size(); i++) {
                    if (todayStr.equals(OKlistDataInHome.get(i))) {

                        todayPODStr = listName.get(i);
                        CallYesterdaayPOD.setVisibility(View.VISIBLE);
                        CallTomorrowPOD.setVisibility(View.VISIBLE);
                        CallTodayPOD.setVisibility(View.VISIBLE);
                        try {
                            tomorrowPODstr = listName.get(i + 1);
                            yesterdayPODstr = listName.get(i - 1);
                            createIntentForCall();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            CallYesterdaayPOD.setVisibility(View.GONE);
                            try {
                                tomorrowPODstr = listName.get(i + 1);
                                createIntentForCall();
                            } catch (ArrayIndexOutOfBoundsException ee) {
                                CallTomorrowPOD.setVisibility(View.GONE);
                                createIntentForCall();
                            }
                        }


//                        //приемм дежурства осуществяется в зависимости от натроек
//                        if (todayPODStr.equals(userName) && hour < Integer.parseInt(SettingActivity.deadLine)) {
//                            checkStatusAWork();
//                        } else if (!todayPODStr.equals(userName)) {
//                            setStatusAworkNol();
//                        }


                        if (!todayPODStr.equals(userName)) {
                            setStatusAworkNol();
                            BAHsetHandOverWorkOne.setVisibility(View.GONE);
                        }

                        if (todayPODStr.equals(userName)) {
                            TodayPOD.setText("Сегодня дежурите вы");
                            //checkStatusAWork();
                            CallTodayPOD.setVisibility(View.GONE);
                            BAHsetHandOverWorkOne.setVisibility(View.VISIBLE);
                        } else {
                            TodayPOD.setText("Сегодня дежурит: " + listName.get(i));
                            setStatusAworkNol();
                        }

                        try {
                            TomorrowPOD.setText("Завтра дежурит:  " + listName.get(i + 1));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            TomorrowPOD.setText("Завтра никто не дежурит");
                        }
                        try {
                            YesterdaayPOD.setText("Вчера дежурил(-а): " + listName.get(i - 1));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            YesterdaayPOD.setText("Вчера никто не дежурил");
                        }

                     if (tomorrowPODstr.equals(userName)) {
                        TomorrowPOD.setText("Завтра дежурите вы");
                        chekStatutHOWork();
                     } else if (yesterdayPODstr.equals(userName)) {
                         YesterdaayPOD.setText("Вчера дежурили вы");
                     }

                        scheduleExistTPODnotExist = 0;
                        //8 это за сколько дней делать график
                        if (i >= OKlistDataInHome.size() - 8) {
                            CreateNewScedule();
                        }
                    } else if (scheduleExistTPODnotExist == 1) {
                        CallYesterdaayPOD.setVisibility(View.GONE);
                        CallTomorrowPOD.setVisibility(View.GONE);
                        CallTodayPOD.setVisibility(View.GONE);
                        TodayPOD.setText("Сегодня никто не дежурит");
                        YesterdaayPOD.setText("Вчера никто не дежурил");
                        TomorrowPOD.setText("Завтра никто не дежурит");
                    }
                }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
        checkStatusInSchedule();
    }


    private void createIntentForCall() {
        callYesterdayPODI = new Intent(Intent.ACTION_CALL);
        callTomorrowPODI = new Intent(Intent.ACTION_CALL);
        callTodayPODI = new Intent(Intent.ACTION_CALL);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if(personOnDuty.getIdGroup().equals(IDGroupUser)){
                        if(personOnDuty.getName().equals(todayPODStr)){
                            callTodayPODI.setData(Uri.parse("tel:" + personOnDuty.getPhone()));
                        }
                        if(personOnDuty.getName().equals(tomorrowPODstr)){
                            callTomorrowPODI.setData(Uri.parse("tel:" + personOnDuty.getPhone()));
                        }
                        if(personOnDuty.getName().equals(yesterdayPODstr)){
                            callYesterdayPODI.setData(Uri.parse("tel:" +personOnDuty.getPhone()));
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


    private void setStatusAworkNol() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if (!personOnDuty.getName().equals(todayPODStr) && personOnDuty.getIdGroup().equals(IDGroupUser)) {
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl("https://dnevalnie.firebaseio.com/Person_On_Duty/"+ ds.getKey() +"/statusAcceptWork");
                        reference.setValue("0");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }




    public static void setStatusAworkOne() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if (personOnDuty.name.equals(userName) && personOnDuty.getIdGroup().equals(IDGroupUser)) {
                        String uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/"
                                + ds.getKey() +"/statusAcceptWork";
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl(uri);
                        reference.setValue("1");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }
    public static void setStatusHOworkOne(View view) {
        editScheduleStatus("?1");
        showUndoneDuties.setVisibility(View.GONE);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if (personOnDuty.name.equals(userName) && personOnDuty.getIdGroup().equals(IDGroupUser)) {
                        String uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/"
                                + ds.getKey() +"/statusHandOverWork";
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl(uri);
                        reference.setValue("1");
                        uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/"
                                + ds.getKey() + "/undoneDuties";
                       reference = db.getReferenceFromUrl(uri);
                       reference.setValue("-");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }




    //ПРинял дежурство статус = 1 (1?)
    //СДал дежурство статус = 1 (?1)
    //не сдал дежуство статус = 0 (?0)
    public static void editScheduleStatus(final String status){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> listShedule = new ArrayList<>();
                ArrayList<String> listDate = new ArrayList<>();
                String scheduleDoEditSchedule = "", schedulePosleEditSchedule = "", uri = "";
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.getIdGroup().equals(IDGroupUser)){
                        uri = "https://dnevalnie.firebaseio.com/Group/" + ds.getKey() + "/schedule";
                        scheduleDoEditSchedule = group.getSchedule();
                        StringBuilder builderFull = new StringBuilder();
                        StringBuilder builderDate = new StringBuilder();

                        boolean startDate = false;
                        for(int i = 0; i<scheduleDoEditSchedule.length(); i++){

                            if(scheduleDoEditSchedule.charAt(i) == ','){
                                startDate = false;
                                listDate.add(String.valueOf(builderDate));
                                builderDate.delete(0, builderDate.length());
                            }

                            if(startDate){
                                builderDate.append(scheduleDoEditSchedule.charAt(i));
                            }


                            if(scheduleDoEditSchedule.charAt(i) == ':'){
                                startDate = true;
                            }


                            if(scheduleDoEditSchedule.charAt(i) == ';'){
                                startDate = false;
                                listShedule.add(String.valueOf(builderFull));
                                builderFull.delete(0, builderFull.length());
                                builderDate.delete(0, builderDate.length());
                            }else builderFull.append(scheduleDoEditSchedule.charAt(i));

                        }

                        Calendar calendar = new GregorianCalendar();
                        String today = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.DAY_OF_WEEK)-1) + "." + calendar.get(Calendar.MONTH);
                            int indextodayDay = -1;
                            for(int i = 0; i<listDate.size(); i++){
                                if(listDate.get(i).equals(today)){
                                    indextodayDay = i;
                                }
                            }

                            String edit, newStatus;

                            switch (status){
                                case "?1":{
                                    edit = listShedule.get(indextodayDay);
                                    newStatus = edit.substring(0,edit.length()-1);
                                    newStatus = newStatus + "1";
                                    listShedule.set(indextodayDay, newStatus);
                                    break;
                                }
                                case "?0":{
                                    edit = listShedule.get(indextodayDay);
                                    newStatus = edit.substring(0,edit.length()-1);
                                    newStatus = newStatus + "0";
                                    listShedule.set(indextodayDay, newStatus);
                                    break;
                                }
                                case "1?":{
                                    edit = listShedule.get(indextodayDay + 1);
                                    char buf = edit.charAt(edit.length()-1);
                                    newStatus = edit.substring(0, edit.length()-2);
                                    newStatus += "1" + buf;
                                    listShedule.set(indextodayDay + 1, newStatus);
                                    break;
                                }

                            }

                            for(String str: listShedule){
                                schedulePosleEditSchedule += str + ";";
                            }
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference reference = db.getReferenceFromUrl(uri);
                            reference.setValue(schedulePosleEditSchedule);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
    }


    public  void checkStatusInSchedule(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> listShedule = new ArrayList<>();
                ArrayList<String> listDate = new ArrayList<>();
                ArrayList<String> listStatus = new ArrayList<>();
                ArrayList<String> listName = new ArrayList<>();
                String scheduleDoEditSchedule = "", uri = "";
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.getIdGroup().equals(IDGroupUser)){
                        uri = "https://dnevalnie.firebaseio.com/Group/"+ ds.getKey() +"/schedule";
                        scheduleDoEditSchedule = group.getSchedule();
                        StringBuilder builderFull = new StringBuilder();
                        StringBuilder builderDate = new StringBuilder();
                        StringBuilder builderStatus = new StringBuilder();
                        StringBuilder builderName = new StringBuilder();

                        boolean startDate = false, startStatus = false, startName = true;

                        for(int i = 0; i<scheduleDoEditSchedule.length(); i++){



                            if(startStatus){
                                builderStatus.append(scheduleDoEditSchedule.charAt(i));
                            }

                            if(scheduleDoEditSchedule.charAt(i) == ','){
                                startDate = false;
                                startStatus = true;
                                listDate.add(String.valueOf(builderDate));
                                builderDate.delete(0, builderDate.length());
                            }

                            if(startDate){
                                builderDate.append(scheduleDoEditSchedule.charAt(i));
                            }


                            if(scheduleDoEditSchedule.charAt(i) == ':'){
                                startDate = true;
                                startName = false;
                            }

                            if(startName){
                                builderName.append(scheduleDoEditSchedule.charAt(i));
                            }

                            if(scheduleDoEditSchedule.charAt(i) == ';'){
                                startDate = false;
                                startStatus = false;
                                startName = true;
                                listName.add(String.valueOf(builderName));
                                listShedule.add(String.valueOf(builderFull));
                                listStatus.add(String.valueOf(builderStatus));
                                builderName.delete(0, builderName.length());
                                builderFull.delete(0, builderFull.length());
                                builderDate.delete(0, builderDate.length());
                                builderStatus.delete(0, builderStatus.length());
                            }else builderFull.append(scheduleDoEditSchedule.charAt(i));

                        }

                        Calendar calendar = new GregorianCalendar();
                        String today = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.DAY_OF_WEEK)-1) + "." + calendar.get(Calendar.MONTH);
                        int indextodayDay = -1;
                        for(int i = 0; i<listDate.size(); i++){
                            if(listDate.get(i).equals(today)){
                                indextodayDay = i;
                            }
                        }


                        String statusTodayPOD = listStatus.get(indextodayDay);
                        String statusYesterdayPOD = listStatus.get(indextodayDay-1);
                        String statusTommorowPOD = listStatus.get(indextodayDay+1);

                        setTextViewAcceptAndHandover(statusTodayPOD, TVAHAcceptToday, TVAHHandOverToday);
                        setTextViewAcceptAndHandover(statusTommorowPOD, TVAHAcceptTommorow, TVAHHandOverTommorow);
                        setTextViewAcceptAndHandover(statusYesterdayPOD, TVAHAcceptYesterday, TVAHHandOverYesterday);


                        int indexDayWhenStatusHadOverIsOne = indextodayDay;
                        if(statusTodayPOD.charAt(0) == '1'){
                        }else{
                            if(statusYesterdayPOD.charAt(1) == '1'){
                               // showtost("degurstvo prognorenno vi degurite");
                            }else{
                              while(listStatus.get(indexDayWhenStatusHadOverIsOne).charAt(1) == '0'){
                                  indexDayWhenStatusHadOverIsOne--;
                              }
                                indexDayWhenStatusHadOverIsOne++;
                                showtost(String.valueOf(indexDayWhenStatusHadOverIsOne));
                                showtost(String.valueOf(indextodayDay));
                              //degurit echeika index + 1
                                String itog = "";
                                int raznizza = indextodayDay - indexDayWhenStatusHadOverIsOne;
                                boolean startEdit = false;
                                boolean endEdit = false;
                                for(int i = 0; i<listName.size(); i++){
                                    if(i == indexDayWhenStatusHadOverIsOne){
                                        startEdit = true;
                                    }
                                    if(!startEdit && !endEdit){
                                        itog += listName.get(i) + ":" + listDate.get(i) + "," + listStatus.get(i);
                                    }
                                    if(startEdit && !endEdit){
                                        itog += listName.get(indexDayWhenStatusHadOverIsOne) + ":" + listDate.get(i) + "," + listStatus.get(i);
                                    }
                                    if(endEdit){
                                        itog += listName.get(i - raznizza) + ":" + listDate.get(i) + "," + listStatus.get(i);

                                    }
                                    if(i == indextodayDay){
                                        endEdit = true;
                                        startEdit = false;
                                    }
                                }

                                showtost(itog);
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference reference = db.getReferenceFromUrl(uri);
                                reference.setValue(itog);


                            }
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


    public static void setTextViewAcceptAndHandover(String str, TextView textViewAccept, TextView textViewHO){
        if(str.charAt(0) == '1')
            textViewAccept.setText("Дежурство принял");
        else
            textViewAccept.setText("Дежурство не принял");

        if(str.charAt(1) == '1')
            textViewHO.setText("Дежурство сдал");
        else
            textViewHO.setText("Дежурство не сдал");
    }



    private void CreateNewScedule() {
        ODCinHomeActthree = 1;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(ODCinHomeActthree == 1) {
                    if (listNamePOD.size() > 0) {
                        listNamePOD.clear();
                    }
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                        assert personOnDuty != null;
                        if (personOnDuty.idGroup.equals(IDGroupUser)) {
                            listNamePOD.add(personOnDuty.getName());
                        }
                    }
                    String lol = "";
                    int counter = 0;
                    int i = listNamePOD.indexOf(todayPODStr);
                    Calendar calendar = new GregorianCalendar();
                    Date date;
                    while (counter < Integer.parseInt(SettingActivity.quantityDayInSchedule)) {
                        counter++;
                        date = calendar.getTime();
                        String dayOfTheWeek = String.valueOf(date.getDay());
                        String dayOfTheMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                        String month = String.valueOf(date.getMonth());
                        if (i == listNamePOD.size()) {
                            i = 0;
                        }
                        lol = lol + listNamePOD.get(i) + ":" + dayOfTheMonth + "." +
                                dayOfTheWeek + "." + month + "," + "00" + ";";
                        i++;
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReferenceFromUrl(UriUserInHome);
                    reference.setValue(lol);
                    Toast.makeText(HomeActivity.this, "График обновлен", Toast.LENGTH_SHORT).show();

                }
                ODCinHomeActthree = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }




    public void GoToSchedule(View view) {
        try{
            if(userStatus.equals("PersonOnDuty")){
                GoToScheduleForPOD();
            }else{
                GoToScheduleForWatcher();
            }
        }catch (NullPointerException e){
            Toast.makeText(HomeActivity.this,"секунду", Toast.LENGTH_SHORT).show();
        }
    }

    public void GoToScheduleForWatcher() {
        Intent intent = new Intent(HomeActivity.this, Schedule_ActivityForWatcher.class);
        startActivity(intent);
        Schedule_ActivityForWatcher.IDGroup = IDGroupUser;
    }
    public void GoToScheduleForPOD() {
        Intent intent = new Intent(HomeActivity.this, Schedule_ActivityForPOD.class);
        startActivity(intent);
        Schedule_ActivityForPOD.iDGroup = IDGroupUser;
        Schedule_ActivityForPOD.idPOD = UserID;
    }



    public void ShowGroupForWatcher(View view) {
        ShowGroupActivity.isPOD = -1;
        Intent intent = new Intent(HomeActivity.this, ShowGroupActivity.class);
        startActivity(intent);
        ShowGroupActivity.IDGroup = IDGroupUser;
        ShowGroupActivity.userIdShowGAct = UserID;
    }
    public void ShowGroupForPOD(View view) {
        ShowGroupActivity.isPOD = 1;
        Intent intent = new Intent(HomeActivity.this, ShowGroupActivity.class);
        startActivity(intent);
        ShowGroupActivity.IDGroup = IDGroupUser;
        ShowGroupActivity.userIdShowGAct = UserID;
    }






    //надо что то делать с хвостом
    public static void AcceptWorkIsFalse() {
        methodOn = 0;
        if(listSchedule.size()>0){
            listSchedule.clear();
        }
        if(buflistScheduleDate.size()>0){
            buflistScheduleDate.clear();
        }
        if(buflistScheduleName.size()>0){
            buflistScheduleName.clear();
        }
        if(newlistSchedule.size()>0){
            newlistSchedule.clear();
        }
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.idGroup.equals(IDGroupUser)) {
                        if(methodOn == 0) {
                            methodOn = 1;
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
                                    buflistScheduleName.add(String.valueOf(nameBr));
                                    //удаляю хвост
                                    buflistScheduleDate.add(String.valueOf(dateBr));
                                    dateBr.delete(dateBr.length()-3, dateBr.length());
                                    buflistScheduleDateClear.add(String.valueOf(dateBr));
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
                            //узнаем какой индекс у сегодняшнего дня
                            int index = 0;
                            for(String date : buflistScheduleDateClear){
                                if(date.equals(sStr)){
                                    index = buflistScheduleDateClear.indexOf(date);
                                }
                            }

                            for(int i = 0; i<index; i++){
                                itogNotAcceptWork += buflistScheduleName.get(i) + ":" + buflistScheduleDate.get(i) + ";";
                            }

                            for(int i = index-1; i<buflistScheduleName.size()-1; i++){
                                itogNotAcceptWork += buflistScheduleName.get(i) + ":" + buflistScheduleDate.get(i+1) + ";";
                            }
                            AcceptWorkIsFalseCont(itogNotAcceptWork);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
        NotshowFragmentInfoAboutAcceptWork();
    }
    private static void AcceptWorkIsFalseCont(String str){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReferenceFromUrl(UriUserInHome);
        reference.setValue(str);
    }





    //Вызывается вначале всей проги и когда показывается больше инфы о обмнене
    public static void notshowQuestionAboutAcceptReplace(){
        TVaboutAcceptReplace.setVisibility(View.GONE);
        BHNotAcceptReplace.setVisibility(View.GONE);
        BHAcceptReplace.setVisibility(View.GONE);
        TVQestionReplace.setVisibility(View.GONE);
    }
    //Вызывается когда статус обмена равен 1
    public static void showQuestionAboutAcceptReplace(){

        TVaboutAcceptReplace.setVisibility(View.VISIBLE);
        BHNotAcceptReplace.setVisibility(View.VISIBLE);
        BHAcceptReplace.setVisibility(View.VISIBLE);
        TVQestionReplace.setVisibility(View.VISIBLE);
    }

    //вызывается когда пользователь нажимат подробнее
    public void AcceptReplaceIsLearnMore(View view) {
        notshowQuestionAboutAcceptReplace();
        showTextInfoAboutReplace();
    }

    //вызывается когда пользовательь нажимает нет
    public void AcceptReplaceIsFalse(View view) {
        ODCinHomeActtwo = 1;
        TVAHReplacetext.setText("Выберите день который хотите поменять");
        NotshowTextInfoAboutReplace();
        notshowQuestionAboutAcceptReplace();
        //HomeActivity.statusOne = 0;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReferenceFromUrl(uriUserStatus);
        reference.setValue("0");
        DatabaseReference reference2 = db.getReferenceFromUrl(uriReplace);
        reference2.setValue("-");
        Toast.makeText(HomeActivity.this, "Хорошо", Toast.LENGTH_SHORT).show();
        notshowQuestionAboutAcceptReplace();
        chekStatus = true;
        checkStatus();

    }

    public void AcceptReplaceIsTrue(View view){
        //вызывается по кнопке поменять, меняет график
        indexForDay = 0;
        indexDay = 0;
        ODCinFragmentReplaceTwo = 0;
        onlyOneStopper = 0;
        nameOne = "";
        nameTwo = "";
        schedule = "";
        NotshowTextInfoAboutReplace();
        TVAHReplacetext.setText("Выберите день который хотите поменять");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.idGroup.equals(IDGroupUser)) {
                        if(listDataForReplaseSchedule.size()>0){
                            listDataForReplaseSchedule.clear();
                        }
                        schedule = group.getSchedule();
                        StringBuilder builder = new StringBuilder();
                        for(int i = 0; i<schedule.length(); i++) {
                            if (schedule.charAt(i) == ';') {
                                listDataForReplaseSchedule.add(String.valueOf(builder));
                                builder.delete(builder.length()-3, builder.length());
                                if(String.valueOf(builder).equals(replaceForDay)){
                                    indexForDay = listDataForReplaseSchedule.size()-1;
                                }
                                if(String.valueOf(builder).equals(replaceDay)){
                                    indexDay = listDataForReplaseSchedule.size()-1;
                                }
                                builder.delete(0, builder.length());
                            } else builder.append(schedule.charAt(i));
                        }
                    }
                }
                StringBuilder buildername1 = new StringBuilder();
                StringBuilder buildername2 = new StringBuilder();
                for(int i = 0; i<replaceForDay.length(); i++){
                    if (replaceForDay.charAt(i) == ':') {
                        nameOne = String.valueOf(buildername1);//da
                    }else buildername1.append(replaceForDay.charAt(i));
                }
                for(int i = 0; i<replaceDay.length(); i++){
                    if (replaceDay.charAt(i) == ':') {
                        nameTwo = String.valueOf(buildername2);//ra
                    }else buildername2.append(replaceDay.charAt(i));
                }
                try {
                    listDataForReplaseSchedule.set(indexDay, listDataForReplaseSchedule.get(indexDay).replaceAll(nameTwo, nameOne));
                    listDataForReplaseSchedule.set(indexForDay, listDataForReplaseSchedule.get(indexForDay).replaceAll(nameOne, nameTwo));
                    String itog = "";
                    for(int i = 0; i<listDataForReplaseSchedule.size(); i++){
                        itog += listDataForReplaseSchedule.get(i) + ";";
                    }
                   // Toast.makeText(HomeActivity.this,String.valueOf(itog.substring(0, 50)), Toast.LENGTH_SHORT).show();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReferenceFromUrl(UriUserInHome);
                    reference.setValue(itog);

                    ODCinFragmentReplaceTwo = 1;
                   // HomeActivity.statusOne = 0;
                    ValueEventListener valueEventListener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (ODCinFragmentReplaceTwo == 1) {
                                String uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    ThreadCheckStatus.ODCinThreadChecStatone = 0;
                                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                                    assert personOnDuty != null;
                                    if (personOnDuty.getIdGroup().equals(IDGroupUser)) {
                                        if(onlyOneStopper == 0){
                                            onlyOneStopper = 1;
                                        }
                                        uri = uri + ds.getKey() + "/status";
                                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = db.getReferenceFromUrl(uri);
                                       // Toast.makeText(HomeActivity.this,"поменял на 0", Toast.LENGTH_SHORT).show();
                                         reference.setValue("0");
                                        uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                                        uri = uri + ds.getKey() + "/replaceDay";
                                        DatabaseReference reference2 = db.getReferenceFromUrl(uri);
                                       // Toast.makeText(HomeActivity.this,"поменял на -", Toast.LENGTH_SHORT).show();
                                         reference2.setValue("-");
                                        uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";

                                    }
                                }
                                NotshowTextInfoAboutReplace();
                                chekStatus = true;
                                checkStatus();
                                ODCinFragmentReplaceTwo = 0;
                                onlyOneStopper = 0;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    };

                    mDataBasePOD.addListenerForSingleValueEvent(valueEventListener2);

                }catch (Exception e){
                    Toast.makeText(HomeActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomeActivity.this, listDataForReplaseSchedule.get(indexDay), Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomeActivity.this, listDataForReplaseSchedule.get(indexForDay), Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomeActivity.this,nameOne , Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomeActivity.this, nameTwo, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);

        //вторая часть метода меняющего грфик просто надо
    }

    public void showTextInfoAboutReplace(){
        TV_replace_day_p2.setVisibility(View.VISIBLE);
        TVAHReplacetext.setVisibility(View.VISIBLE);
        BAHNotReplace.setVisibility(View.VISIBLE);
        LVAHReplaceDay.setVisibility(View.VISIBLE);
        BAHReplace.setVisibility(View.VISIBLE);
        updateReplaceDay();
        setOnClickItemReplaceDay();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(listDataForReplaseFinishFragmentReplace.size()>0){
                    listDataForReplaseFinishFragmentReplace.clear();
                }
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.idGroup.equals(IDGroupUser)) {
                        try {
                            posledvoet = 0;
                            posledvoet2 = 0;
                            scheduleFullReplaceDay = group.getSchedule();

                            StringBuilder scheduleBuilder = new StringBuilder();
                            StringBuilder scheduleBuilderName = new StringBuilder();
                            StringBuilder builderDate = new StringBuilder();
                            for (int i = 0; i < scheduleFullReplaceDay.length(); i++) {

                                if (scheduleFullReplaceDay.charAt(i) == ':') {
                                    posledvoet2 = 1;
                                }
                                if (posledvoet2 == 1) {
                                    builderDate.append(scheduleFullReplaceDay.charAt(i));
                                }

                                if (scheduleFullReplaceDay.charAt(i) == ':'){
                                    posledvoet = 1;
                                    if (String.valueOf(scheduleBuilderName).equals(userName)) {
                                        permissionToShowLVRDay = 1;
                                    }
                                    scheduleBuilderName.delete(0, scheduleBuilderName.length());
                                } else if (posledvoet == 0) {
                                    scheduleBuilderName.append(scheduleFullReplaceDay.charAt(i));
                                }
                                if (scheduleFullReplaceDay.charAt(i) == ';') {
                                    posledvoet = 0;
                                    posledvoet2 = 0;
                                    builderDate.delete(builderDate.length()-4, builderDate.length());
                                    if (todayStr.equals(String.valueOf(builderDate))) {
                                        pointToday = 1;
                                    }
                                    if (permissionToShowLVRDay == 1 && pointToday == 1) {
                                        permissionToShowLVRDay = 0;
                                        scheduleBuilder.delete(scheduleBuilder.length()-3, scheduleBuilder.length());
                                        listDataForReplaseFinishFragmentReplace.add(Constant.createFormate(String.valueOf(scheduleBuilder)));
                                        buf.add(String.valueOf(scheduleBuilder));
                                    }
                                    scheduleBuilder.delete(0, scheduleBuilder.length());
                                    builderDate.delete(0, builderDate.length());
                                } else scheduleBuilder.append(scheduleFullReplaceDay.charAt(i));
                            }
                        }catch(NullPointerException e){
                        }
                    }
                }
                try {
                    //удаляет первый элемет если он не нужен (имя другое)
                    StringBuilder firstName = new StringBuilder();
                    String firstEl = listDataForReplaseFinishFragmentReplace.get(0);
                    for(int i = 0; i<firstEl.length(); i++){
                        if(firstEl.charAt(i) == ':'){
                            if(!String.valueOf(firstName).equals(userName)){
                                listDataForReplaseFinishFragmentReplace.remove(0);
                                buf.remove(0);
                            }
                        }else firstName.append(firstEl.charAt(i));
                    }
                    ArrayAdapterForReplaceFinishFragmentReplace.notifyDataSetChanged();
                }catch (IndexOutOfBoundsException e){
                }
                ArrayAdapterForReplaceFinishFragmentReplace.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);

    }

    //вызывается сразу после нажатия кнопки подробнее, нужен что бы отобразить кнопку поменять
    private void setOnClickItemReplaceDay(){
        LVAHReplaceDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                replaceForDay = buf.get(position);
                TVAHReplacetext.setText("Вы будете дежурить " +
                        Constant.createFormateForReplaceDay(replaceDay) +
                        " но не дежурить: " +
                        Constant.createFormateForReplaceDay(replaceForDay));

            }
        });
    }


    public static void showFragmentInfoAboutAcceptWork(){
        fragmentAccrptWork.setVisibility(View.VISIBLE);
       // NotshowFragmentInfoAboutReplace();
    }

    public void NotshowTextInfoAboutReplace(){
        TV_replace_day_p2.setVisibility(View.GONE);
        TVAHReplacetext.setVisibility(View.GONE);
        BAHNotReplace.setVisibility(View.GONE);
        LVAHReplaceDay.setVisibility(View.GONE);
        BAHReplace.setVisibility(View.GONE);
       // HomeActivity.statusOne = 0;
        //checkStatus();
    }
    public static void NotshowFragmentInfoAboutAcceptWork(){
        fragmentAccrptWork.setVisibility(View.GONE);
    }



    private void init() {
        TVActHomeGlav = (TextView) findViewById(R.id.TVActHomeGlav);
        TodayPOD = (TextView) findViewById(R.id.TodayPOD);
        GroupIsNull = (TextView) findViewById(R.id.GroupIsNull);
        TVHActivitySecundu = (TextView) findViewById(R.id.TVHActivitySecundu);
        YesterdaayPOD = (TextView) findViewById(R.id.YesterdaayPOD);
        TomorrowPOD = (TextView) findViewById(R.id.TomorrowPOD);
        TVHActivityReplaceName =(TextView) findViewById(R.id.TVHActivityReplaceName);
        TVQestionReplace = (TextView) findViewById(R.id.TVQestionReplace);
        TVaboutAcceptWork = (TextView) findViewById(R.id.TVaboutAcceptWork);
        TVaboutAcceptReplace = (TextView) findViewById(R.id.TVaboutAcceptReplace);
        TV_replace_day_p2 = (TextView) findViewById(R.id.TV_replace_day_p2);
        TVAHReplacetext = (TextView) findViewById(R.id.TVAHReplacetext);

        TVAHAcceptToday = (TextView) findViewById(R.id.TVAHAcceptToday);
        TVAHHandOverToday = (TextView) findViewById(R.id.TVAHHandOverToday);
        TVAHAcceptTommorow  = (TextView) findViewById(R.id.TVAHAcceptTommorow);
        TVAHHandOverTommorow   = (TextView) findViewById(R.id.TVAHHandOverTommorow);
        TVAHAcceptYesterday  = (TextView) findViewById(R.id.TVAHAcceptYesterday);
        TVAHHandOverYesterday = (TextView) findViewById(R.id.TVAHHandOverYesterday);

        ETHActivityGroupID = (EditText) findViewById(R.id.ETHActivityGroupID);
        editTextTextPersonName = (EditText) findViewById(R.id.editTextTextPersonName);

        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        mDataBaseW = FirebaseDatabase.getInstance().getReference(Constant.WATCHER_KEY);
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);

        BAHLogOut = (Button) findViewById(R.id.BAHLogOut);
        BSettings = (Button) findViewById(R.id.BSettings);
        BexitFromApp = (Button) findViewById(R.id.BexitFromApp);
        BHActivityReplaceName = (Button) findViewById(R.id.BHActivityReplaceName);
        BHActivityAddIdGroup = (Button) findViewById(R.id.BHActivityAddIdGroup);
        listGroupForWatcher = (Button) findViewById(R.id.listGroupForWatcher);
        listGroupForPOD = (Button) findViewById(R.id.listGroupForPOD);
        scheduleForPOD = (Button) findViewById(R.id.BHScheduleForPOD);
        BHAcceptReplace = (Button) findViewById(R.id.BHAcceptReplace);
        BHNotAcceptReplace = (Button) findViewById(R.id.BHNotAcceptReplace);
        CallYesterdaayPOD = (Button) findViewById(R.id.CallYesterdaayPOD);
        CallTodayPOD = (Button) findViewById(R.id.CallTodayPOD);
        CallTomorrowPOD = (Button) findViewById(R.id.CallTomorrowPOD);
        BAHNotReplace = (Button) findViewById(R.id.BAHNotReplace);
        BAHReplace = (Button) findViewById(R.id.BAHReplace);
        BAHsetHandOverWorkOne = (Button) findViewById(R.id.BAHsetHandOverWorkOne);
        showUndoneDuties = (Button) findViewById(R.id.showUndoneDuties);
        BAHEdit = (Button) findViewById(R.id.BAHEdit);

        LVAHReplaceDay = (ListView) findViewById(R.id.LVAHReplaceDay);
        listDataForReplaseFinishFragmentReplace = new ArrayList<>();
        buf = new ArrayList<>();
        ArrayAdapterForReplaceFinishFragmentReplace = new Adapter(this, listDataForReplaseFinishFragmentReplace);
        LVAHReplaceDay.setAdapter(ArrayAdapterForReplaceFinishFragmentReplace);

        fragmentAccrptWork = (FrameLayout)  findViewById(R.id.fragment_info_about_acceptWork);
        infoAboutUndoneDuties = (FrameLayout) findViewById(R.id.fargment_info_undoneDuties);
        fragmentAccrptWork.setVisibility(View.GONE);
        infoAboutUndoneDuties.setVisibility(View.GONE);



        OKlistDataInHome = new ArrayList<>();
        listName = new ArrayList<>();
        listNamePOD = new ArrayList<>();

        today = new GregorianCalendar();



        listNamePOD = new ArrayList<>();

        uriReplace = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
        uriUserStatus = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
        uriFirst = "https://dnevalnie.firebaseio.com/Group/";
        uriSecond = null;
    }


    public void callTodayPOD(View view) {
        try{
        startActivity(callTodayPODI);
            }catch (SecurityException e){
            Toast.makeText(HomeActivity.this, "Вы не разрешили приложению осуществять звонки", Toast.LENGTH_SHORT).show();
        }
    }

    public void callTomorrowPOD(View view) {
        try {
            startActivity(callTomorrowPODI);
        }catch (SecurityException e){
            Toast.makeText(HomeActivity.this, "Вы не разрешили приложению осуществять звонки", Toast.LENGTH_SHORT).show();
        }

    }
    public void callYesterdaayPOD(View view) {
        try{
        startActivity(callYesterdayPODI);
        }catch (SecurityException e){
            Toast.makeText(HomeActivity.this, "Вы не разрешили приложению осуществять звонки", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    public void exitTheApp(View view) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }


    public void logOutH(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        UserID = null;
        IDGroupUser = null;
        userName = null;
        stopChekWhoIsUser = 0;
       // statusOne = 0;
    }


    public void updateReplaceDay(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if (personOnDuty.getId().equals(UserID)) {
                        replaceDay = personOnDuty.getReplaceDay();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBasePOD.addValueEventListener(valueEventListener);
    }

    public void goToEditor(View view) {
        Intent intent = new Intent(HomeActivity.this, EditActivity.class);
        startActivity(intent);
    }

    public  void showtost(String str){
        Toast.makeText(HomeActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    public void showUndoneDuties(View view) {
        infoAboutUndoneDuties.setVisibility(View.VISIBLE);
    }
}

