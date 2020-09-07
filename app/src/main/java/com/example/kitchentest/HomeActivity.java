package com.example.kitchentest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    private String todayDayStr, todayMonthStr;
    private int replaceDay, replaceMonth;
    private StringBuilder replaceDayBuilder;
    @Override
    public void run(){
        ODCinThreadChecStatone = 1;

        while(HomeActivity.statusOne == 0){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                    if (ODCinThreadChecStatone == 1){
                        Calendar calendar = new GregorianCalendar();
                        Date date = calendar.getTime();
                        todayDayStr = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                        todayMonthStr = String.valueOf(date.getMonth());

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                            assert personOnDuty != null;
                            if (personOnDuty.getId().equals(UserIDforThread2) && ODCinThreadChecStatone == 1) {
                                if (personOnDuty.getStatus().equals("1")) {
                                    ODCinThreadChecStatone = 0;
                                    HomeActivity.statusOne = 1;
                                    HomeActivity.showQuestionAboutAcceptReplace();

                                }
//                                if (personOnDuty.getStatusAcceptWork().equals("1")) {
//                                    ODCinThreadChecStatone = 0;
//                                    Calendar calendar1 = new GregorianCalendar();
//                                    Date date1 = calendar1.getTime();
//                                    int hour = date1.getHours();
//                                    if(hour<10) {
//                                        HomeActivity.showFragmentInfoAboutAcceptWork();
//                                    }
//
//                                }
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mDataBasePOD.addValueEventListener(valueEventListener);
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


public class HomeActivity extends AppCompatActivity {

        private DatabaseReference mDataBaseW;
        private static DatabaseReference mDataBaseGroup;
        private DatabaseReference mDataBasePOD;


        private TextView TodayPOD,YesterdaayPOD, TomorrowPOD, TVReplaseFinish, TVHActivitySecundu, GroupIsNull, TVHActivityReplaceName;
        public static TextView TVaboutAcceptWork;
        public static TextView TVaboutAcceptReplace;

        private EditText ETHActivityGroupID, editTextTextPersonName;


        public static String UserID = null, UriUserInHome, userName = null, userStatus = null, uriUserStatus, checkreplaceDay, IDGroupUser = null;
        private String  todayStr, todayPODStr, tomorrowStr, NamePODyesterdayStr, uri, uriReplace,
                replaceForDay = null, nameOne = null, nameTwo  = null, nameBuf  = null, uriChengeSchedule = null;
        private String uriFirst, uriSecond, schedule, LMBuf;
        private static String itogNotAcceptWork = "";
        private String editTextIDGroup, editTextName, uriGroup;


        private Button listGroupForWatcher, listGroupForPOD, scheduleForPOD, BHNotReplaceFinish, BHReplaceFinish, BHActivityAddIdGroup,
                BHExit, BHActivityReplaceName;
        public static Button BHNotAcceptWork, BHAcceptWork;
        public static Button BHAcceptReplace, BHNotAcceptReplace;

        public static List<String> OKlistDataInHome;
        private ListView ListViewDayForReplace;
        private ArrayList<String> listName, listNamePOD;
        ArrayList<Watcher> listForImportWatchers = new ArrayList<>();
        static ArrayList<String> listSchedule = new ArrayList<>();
        static ArrayList<String> buflistScheduleDate = new ArrayList<>();
        static ArrayList<String> buflistScheduleName = new ArrayList<>();
        static ArrayList<String> newlistSchedule = new ArrayList<>();
        private ArrayList<String> bufForSchedule;


        Calendar today;


        private int newScheduleStart,promptToEnterIDGroupIsWas = 0, groupIsExist = 1;
        private static int nomberListScheduleWhenWorkisNotAccept;
        private static int methodOn = 0, updateScheduleIsWas;
        private int scheduleExistTPODnotExist = 1;
        public static int statusOne = 0, sleep = 0, stopChekWhoIsUser = 0,
                ODCinHomeActone = 0, ODCinHomeActtwo = 0,ODCinHomeActthree = 0,  posledvoet = 0,
                permissionToShowLVRDay = 0, indexDay = 0, indexForDay = 0, scheduleIsLagbehind = 0, NameinGroupBe = 0;



        private Fragment fragmentInfoAbReplace = null;
        public static FrameLayout fragmentReplaceDay, fragmentAccrptWork;
        ArrayList<Watcher> listW;

        public static ImageView IVBackGQuestionAboutReplaceDay;


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
                        BHExit.setVisibility(View.VISIBLE);
                        promptToEnterIDGroup();
                        promptToEnterIDGroupIsWas = 1;
                    }
                }
                if(promptToEnterIDGroupIsWas == 0){
                    ToEnterIDGroup();
                    TVHActivitySecundu.setVisibility(View.GONE);
                    BHExit.setVisibility(View.VISIBLE);
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
        fragmentReplaceDay.setVisibility(View.VISIBLE);
        fragmentAccrptWork.setVisibility(View.VISIBLE);
        YesterdaayPOD.setVisibility(View.VISIBLE);
        TodayPOD.setVisibility(View.VISIBLE);
        TomorrowPOD.setVisibility(View.VISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_info_about_replace, fragmentInfoAbReplace);
        NotshowFragmentInfoAboutReplace();
        NotshowFragmentInfoAboutAcceptWork();
        notshowQuestionAboutAcceptReplace();
        getDataFromTableW();
        checkStatus();
        checkReplaceDay();
    }

    private void promptToEnterIDGroup() {
        GroupIsNull.setVisibility(View.VISIBLE);
        ETHActivityGroupID.setVisibility(View.VISIBLE);
        BHActivityAddIdGroup.setVisibility(View.VISIBLE);

        TVHActivityReplaceName.setVisibility(View.VISIBLE);
        editTextTextPersonName.setVisibility(View.VISIBLE);
        BHActivityReplaceName.setVisibility(View.VISIBLE);
    }

    private void ToEnterIDGroup() {
        GroupIsNull.setVisibility(View.GONE);
        ETHActivityGroupID.setVisibility(View.GONE);
        BHActivityAddIdGroup.setVisibility(View.GONE);

        TVHActivityReplaceName.setVisibility(View.GONE);
        editTextTextPersonName.setVisibility(View.GONE);
        BHActivityReplaceName.setVisibility(View.GONE);
    }


    public void addIDGroupFromHActivity(View view) {
        editTextIDGroup = ETHActivityGroupID.getText().toString();
        editTextIDGroup.trim();
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
                        uriAddIDGroup = uriAddIDGroup + ds.getKey() + "/idGroup";
                    }
                }
                if(NameinGroupBe == 0) {
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReferenceFromUrl(uriAddIDGroup);
                    reference.setValue(editTextIDGroup);
                    uriAddIDGroup = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
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



    private void getDataFromTableW(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Watcher watcher = ds.getValue(Watcher.class);
                    assert watcher != null;
//                    Toast.makeText(HomeActivity.this, UserID + userName, Toast.LENGTH_SHORT).show();
                    if(watcher.getId().equals(UserID)){
                        listForImportWatchers.add(watcher);
                        userName = watcher.getName();
                        userStatus = watcher.getRole();
                        Toast.makeText(HomeActivity.this, "Добро пожаловать " + userName, Toast.LENGTH_SHORT).show();
                        scheduleIsLagbehind++;
                        listGroupForWatcher.setVisibility(View.VISIBLE);
                        listGroupForPOD.setVisibility(View.GONE);
//                       scheduleForPOD.setVisibility(View.GONE);
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
        //ПОДУМАТЬ КАК МОЖНО ОПТИМИЗИРОВАТЬ
        getDataFromTablePOD();
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
                        InfoAboutReplaceFragment.UriUserInFragReplace = UriUserInHome;
                        Schedule_ActivityForWatcher.UriUser = UriUserInHome;
                        ListOfFirebase.scheduleStr = group.getSchedule();
                        if(ListOfFirebase.scheduleStr != null){
                            showTodayPOD();
                        }else{
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
        mDataBaseGroup.addValueEventListener( valueEventListener);
    }



    private void getDataFromTablePOD(){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);

                    assert personOnDuty != null;

                    if (personOnDuty.getId().equals(UserID)) {
                        InfoAboutReplaceFragment.replaceDay = personOnDuty.getReplaceDay();
                        userName = personOnDuty.getName();
                        userStatus = personOnDuty.getRole();
                        IDGroupUser = personOnDuty.getIdGroup();

                        uriUserStatus = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                        uriUserStatus = uriUserStatus + ds.getKey() + "/status";
                        InfoAboutReplaceFragment.uriUserStatusFragmentReplace = uriUserStatus;
                        uriReplace = "https://dnevalnie.firebaseio.com/Person_On_Duty/" + ds.getKey() + "/replaceDay";
                        InfoAboutReplaceFragment.uriReplaceFragmentReplace = uriReplace;

                        Toast.makeText(HomeActivity.this, "Добро пожаловать " + userName, Toast.LENGTH_SHORT).show();
                        Schedule_ActivityForPOD.userNameForScheduleActForPOD = userName;
                        InfoAboutReplaceFragment.IDGroupUserFragmentReplace = IDGroupUser;
                        InfoAboutReplaceFragment.userNameFragmentReplace = userName;
                        Toast.makeText(HomeActivity.this, InfoAboutReplaceFragment.IDGroupUserFragmentReplace, Toast.LENGTH_SHORT).show();
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
                        InfoAboutReplaceFragment.UriUserInFragReplace = UriUserInHome;
                        Schedule_ActivityForPOD.uriUserForPOD = uriFirst + uriSecond + "/" + "schedule";
                        Schedule_ActivityForPOD.scheduleForPOD = group.getSchedule();
                        if(group.getSchedule() != null){
                            showTodayPOD();
                        }else{
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
        mDataBaseGroup.addValueEventListener(valueEventListener);
    }
    //находит сегодняшнего дежурного и обновляет график при необходимости
    private void showTodayPOD() {
        Date dateToday = today.getTime();
        todayStr = ":" + String.valueOf(today.get(Calendar.DAY_OF_MONTH)) +
                "." + dateToday.getDay() + "." + dateToday.getMonth();
        today.add(Calendar.DAY_OF_YEAR, 1);
        tomorrowStr = ":" + String.valueOf(today.get(Calendar.DAY_OF_MONTH)) +
                "." + dateToday.getDay() + "." + dateToday.getMonth();

        final int hour = dateToday.getHours();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

                                if (schedule.charAt(i) == ';') {
                                    OKlistDataInHome.add(String.valueOf(builder));
                                    builder.delete(0, builder.length());
                                } else builder.append(schedule.charAt(i));
                            }
                    }
                } //Распределяет имена и дату по листам

                //находит сегодняшнего дежурного и обновляет график при необходимости
                for (int i = 0; i < OKlistDataInHome.size(); i++) {
                    if (todayStr.equals(OKlistDataInHome.get(i))) {

                        todayPODStr = listName.get(i);
                        //до 10 надо приннимать
                        if (todayPODStr.equals(userName) && hour < 10) {
                            checkStatusAWork();
                        } else if (!todayPODStr.equals(userName)) {
                            setStatusAworkNol();
                        }


                        TodayPOD.setText("Сегодня дежурит: " + listName.get(i));
                        try {
                            TomorrowPOD.setText("Завтра дежурит:  " + listName.get(i + 1));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            TomorrowPOD.setText("---");
                        }
                        try {
                            YesterdaayPOD.setText("Вчера дежурил(-а): " + listName.get(i - 1));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            YesterdaayPOD.setText("---");
                        }
                        scheduleExistTPODnotExist = 0;
                        //8 это за сколько дней делать график
                        if (i >= OKlistDataInHome.size() - 8) {
                            CreateNewScedule();
                        }
                    } else if (scheduleExistTPODnotExist == 1) {
                        TodayPOD.setText("Сегодня никто не деурит");
                    }
                }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBaseGroup.addValueEventListener(valueEventListener);
    }

    private void checkStatusAWork() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if (personOnDuty.statusAcceptWork.equals("0") && personOnDuty.getName().equals(todayPODStr)
                    && personOnDuty.getIdGroup().equals(IDGroupUser)) {
                        InfoAboutAcceptWork.userNameFragmentAcceptWork = userName;
                        InfoAboutAcceptWork.idGroupUserFragmentAcceptWork = IDGroupUser;
                        showFragmentInfoAboutAcceptWork();
                        setStatusAwork();
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
    private void setStatusAwork() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if (personOnDuty.name.equals(userName) && personOnDuty.getIdGroup().equals(IDGroupUser)) {
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl("https://dnevalnie.firebaseio.com/Person_On_Duty/"+ ds.getKey() +"/statusAcceptWork");
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
                    while (counter < 28) {
                        counter++;
                        date = calendar.getTime();
                        String dayOfTheWeek = String.valueOf(date.getDay());
                        String dayOfTheMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                        String month = String.valueOf(date.getMonth());
                        if (i == listNamePOD.size()) {
                            i = 0;
                        }
                        lol = lol + listNamePOD.get(i) + ":" + dayOfTheMonth + "." + dayOfTheWeek + "." + month + ";";
                        i++;
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReferenceFromUrl(UriUserInHome);
                    reference.setValue(lol);
                    lol = "";
                    counter = 0;
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
                        String scheduleFull = group.getSchedule();
                        StringBuilder scheduleBuilder = new StringBuilder();

                        for(int i = 0; i<scheduleFull.length(); i++){
                            if(scheduleFull.charAt(i) == ';'){
                                scheduleBuilder.append(";");
                                listSchedule.add(String.valueOf(scheduleBuilder));   //name and date list
                                scheduleBuilder.delete(0, scheduleBuilder.length());
                            }else{
                                scheduleBuilder.append(scheduleFull.charAt(i));
                            }
                        }
                    }
                }
                if(methodOn == 0) {
                    itogNotAcceptWork = "";
                    methodOn = 1;
                    StringBuilder buildername = new StringBuilder();
                    StringBuilder builderdata = new StringBuilder();
                    int dvoetochWas= 0;

                    for(String str: listSchedule){
                        for(int i = 0; i<str.length(); i++){
                            if(str.charAt(i) == ':'){
                                dvoetochWas = 1;
                            }
                            if(str.charAt(i) != ':'){
                                if(dvoetochWas == 0){
                                    buildername.append(str.charAt(i));
                                }else{
                                    builderdata.append(str.charAt(i));
                                }
                            }
                            if(str.charAt(i) == ';'){
                                buflistScheduleName.add(String.valueOf(buildername));
                                buflistScheduleDate.add(String.valueOf(builderdata));
                                buildername.delete(0, buildername.length());
                                builderdata.delete(0, builderdata.length());
                                dvoetochWas = 0;
                            }
                        }
                    }
                    Calendar today1 = new GregorianCalendar();
                    Date dateToday = today1.getTime();
                    String todayStr =  String.valueOf(today1.get(Calendar.DAY_OF_MONTH)) +
                            "." + dateToday.getDay() + "." + dateToday.getMonth() + ";"; // 12.2.8;
                    int index = 0;
                    for(String date : buflistScheduleDate){
                        if(date.equals(todayStr)){
                            index = buflistScheduleDate.indexOf(date);
                        }
                    }


                    for(int i = 0; i<index-1; i++){
                        itogNotAcceptWork += buflistScheduleName.get(i) + ":" + buflistScheduleDate.get(i);
                    }
                    for(int i = index-1; i<listSchedule.size(); i++){
                        itogNotAcceptWork += buflistScheduleName.get(i-1) + ":" + buflistScheduleDate.get(i);
                    }
                     AcceptWorkIsFalseCont(itogNotAcceptWork);
                    itogNotAcceptWork = "";
                    newlistSchedule.clear();
                    buflistScheduleName.clear();
                    buflistScheduleDate.clear();
                    listSchedule.clear();

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


//    public static void notshowQuestionAboutAcceptWork(){
//        TVaboutAcceptWork.setVisibility(View.GONE);
//        BHNotAcceptWork.setVisibility(View.GONE);
//        BHAcceptWork.setVisibility(View.GONE);
//    }
//    private void showQuestionAboutAcceptWork(){
//        TVaboutAcceptWork.setVisibility(View.VISIBLE);
//        BHNotAcceptWork.setVisibility(View.VISIBLE);
//        BHAcceptWork.setVisibility(View.VISIBLE);
//        notshowQuestionAboutAcceptReplace();
//    }


    //Вызывается вначале всей проги и когда показывается больше инфы о обмнене
    public static void notshowQuestionAboutAcceptReplace(){
        TVaboutAcceptReplace.setVisibility(View.GONE);
        BHNotAcceptReplace.setVisibility(View.GONE);
        BHAcceptReplace.setVisibility(View.GONE);
        IVBackGQuestionAboutReplaceDay.setVisibility(View.GONE);
    }
    //Вызывается когда статус обмена равен 1
    public static void showQuestionAboutAcceptReplace(){
        TVaboutAcceptReplace.setVisibility(View.VISIBLE);
        BHNotAcceptReplace.setVisibility(View.VISIBLE);
        BHAcceptReplace.setVisibility(View.VISIBLE);
        IVBackGQuestionAboutReplaceDay.setVisibility(View.VISIBLE);
    }


    //вызывается когда пользователь нажимат подробнее
    public void AcceptReplaceIsLearnMore(View view) {
        Toast.makeText(HomeActivity.this, InfoAboutReplaceFragment.IDGroupUserFragmentReplace, Toast.LENGTH_SHORT).show();
        notshowQuestionAboutAcceptReplace();
        showFragmentInfoAboutReplace();
    }
    //вызывается когда пользовательь нажимает нет
    public void AcceptReplaceIsFalse(View view) {
        ODCinHomeActtwo = 1;
        HomeActivity.statusOne = 0;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReferenceFromUrl(uriUserStatus);
        reference.setValue("0");
        DatabaseReference reference2 = db.getReferenceFromUrl(uriReplace);
        reference2.setValue("-");
        Toast.makeText(HomeActivity.this, "Хорошо", Toast.LENGTH_SHORT).show();
        notshowQuestionAboutAcceptReplace();
        checkStatus();
    }

    public void showFragmentInfoAboutReplace(){
        fragmentReplaceDay.setVisibility(View.VISIBLE);
    }
    public static void showFragmentInfoAboutAcceptWork(){
        fragmentAccrptWork.setVisibility(View.VISIBLE);
        NotshowFragmentInfoAboutReplace();
    }
    public static void NotshowFragmentInfoAboutReplace(){
        fragmentReplaceDay.setVisibility(View.GONE);
        checkStatus();
    }
    public static void NotshowFragmentInfoAboutAcceptWork(){
        fragmentAccrptWork.setVisibility(View.GONE);
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
            Toast.makeText(HomeActivity.this, "жжопе", Toast.LENGTH_SHORT).show();
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
    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        UserID = null;
        IDGroupUser = null;
        userName = null;

        stopChekWhoIsUser = 0;
        HomeActivity.statusOne = 0;
    }
    private void init() {
        TodayPOD = (TextView) findViewById(R.id.TodayPOD);
        GroupIsNull = (TextView) findViewById(R.id.GroupIsNull);
        TVHActivitySecundu = (TextView) findViewById(R.id.TVHActivitySecundu);
        YesterdaayPOD = (TextView) findViewById(R.id.YesterdaayPOD);
        TomorrowPOD = (TextView) findViewById(R.id.TomorrowPOD);
        TVHActivityReplaceName =(TextView) findViewById(R.id.TVHActivityReplaceName);

        TVaboutAcceptWork = (TextView) findViewById(R.id.TVaboutAcceptWork);
        TVaboutAcceptReplace = (TextView) findViewById(R.id.TVaboutAcceptReplace);

        ETHActivityGroupID = (EditText) findViewById(R.id.ETHActivityGroupID);
        editTextTextPersonName = (EditText) findViewById(R.id.editTextTextPersonName);

        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        mDataBaseW = FirebaseDatabase.getInstance().getReference(Constant.WATCHER_KEY);
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);



        BHActivityReplaceName = (Button) findViewById(R.id.BHActivityReplaceName);
        BHActivityAddIdGroup = (Button) findViewById(R.id.BHActivityAddIdGroup);
        listGroupForWatcher = (Button) findViewById(R.id.listGroupForWatcher);
        listGroupForPOD = (Button) findViewById(R.id.listGroupForPOD);
        scheduleForPOD = (Button) findViewById(R.id.BHScheduleForPOD);
        BHAcceptReplace = (Button) findViewById(R.id.BHAcceptReplace);
        BHNotAcceptReplace = (Button) findViewById(R.id.BHNotAcceptReplace);
        BHExit = (Button) findViewById(R.id.BHExit);

        fragmentReplaceDay = (FrameLayout) findViewById(R.id.fragment_info_about_replace);
        fragmentAccrptWork = (FrameLayout)  findViewById(R.id.fragment_info_about_acceptWork);
        fragmentReplaceDay.setVisibility(View.GONE);
        fragmentAccrptWork.setVisibility(View.GONE);


        IVBackGQuestionAboutReplaceDay = (ImageView) findViewById(R.id.IVBackGQuestionAboutReplaceDay);


        OKlistDataInHome = new ArrayList<>();
        listName = new ArrayList<>();
        listNamePOD = new ArrayList<>();


        newScheduleStart = 0;
        updateScheduleIsWas = 0;
        today = new GregorianCalendar();

        bufForSchedule = new ArrayList<>();
        listW = new ArrayList<>();
        listNamePOD = new ArrayList<>();

        uriReplace = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
        uriUserStatus = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
        uriFirst = "https://dnevalnie.firebaseio.com/Group/";
        uriGroup = "https://dnevalnie.firebaseio.com/Group/";
        uriSecond = null;

        fragmentInfoAbReplace = new InfoAboutReplaceFragment();
    }



}



//    private void checkReplaceDayInBD(){
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    Group group = ds.getValue(Group.class);
//                    assert group != null;
//                    if(group.getIdGroup().equals(IDGroupUser)){
//                        Toast.makeText(HomeActivity.this, "1",  Toast.LENGTH_SHORT).show();
//                        CheckreplaceDay = group.replaceDayGroup;
//                        uriGroup =  uriGroup + ds.getKey() + "/replaceDayGroup";
//                        if(!CheckreplaceDay.equals("-")) {
//                            Date dateToday = today.getTime();
//                            todayStr = ":" + String.valueOf(today.get(Calendar.DAY_OF_MONTH)) +
//                                    "." + dateToday.getDay() + "." + dateToday.getMonth();
//                            Toast.makeText(HomeActivity.this, CheckreplaceDay,  Toast.LENGTH_SHORT).show();
////                            Toast.makeText(HomeActivity.this, todayStr, Toast.LENGTH_SHORT).show();
////                            Toast.makeText(HomeActivity.this, uriGroup, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        };
//        uriGroup = "https://dnevalnie.firebaseio.com/Group/";
//        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
//    }

//    public static void sbrosSchedule() {
//
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            String uri = "https://dnevalnie.firebaseio.com/Group/";
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        Group group = ds.getValue(Group.class);
//                        assert group != null;
//                        if(group.idGroup.equals(IDGroupUser)){
//                            uri += ds.getKey() + "/schedule";
//                        }
//                    }
//                    FirebaseDatabase db = FirebaseDatabase.getInstance();
//                    DatabaseReference reference = db.getReferenceFromUrl(uri);
//                    reference.setValue("");
//                }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        };
//        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
//    }



