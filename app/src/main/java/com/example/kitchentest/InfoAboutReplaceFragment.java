package com.example.kitchentest;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static java.lang.Thread.sleep;


public class InfoAboutReplaceFragment extends Fragment {
    public static int ODCinFragmentReplaceOne = 0, permissionToShowLVRDay = 0, ODCinFragmentReplaceTwo = 0;
    private int posledvoet = 0, posledvoet2 = 0,indexForDay = 0, indexDay = 0, onlyOneStopper = 0, pointToday = 0;

    private DatabaseReference mDataBaseW, mDataBaseGroup, mDataBasePOD;

    private TextView TVFragmentReplaceDay;


    public static String uriUserStatusFragmentReplace = null, uriReplaceFragmentReplace = null, IDGroupUserFragmentReplace = null,
            userNameFragmentReplace = null, replaceDay = null, UriUserInFragReplace = null;



    private String replaceForDay = null, schedule = null,  nameOne = null, nameTwo  = null,
            uri, todayStr, scheduleFull;



    public static List<String> listDataForReplaseFinishFragmentReplace, listDataForReplaseSchedule;
    private ArrayList<String> buf;
    private ArrayAdapter<String> ArrayAdapterForReplaceFinishFragmentReplace;


    private Button BFragmentNotReplaceDay, BFragmentReplaceDay;
    private ListView LVFragmentReplaysForDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_about_replace, null);
        BFragmentNotReplaceDay = (Button) v.findViewById(R.id.BFragmentNotReplaceDay);
        BFragmentReplaceDay = (Button) v.findViewById(R.id.BFragmentReplaceDay);
        BFragmentReplaceDay.setVisibility(View.GONE);
        TVFragmentReplaceDay = (TextView) v.findViewById(R.id.TVFragmentReplaceDay);
        LVFragmentReplaysForDay = (ListView) v.findViewById(R.id.LVFragmentReplaysForDay);


        listDataForReplaseFinishFragmentReplace = new ArrayList<>();

        listDataForReplaseSchedule = new ArrayList<>();

        ArrayAdapterForReplaceFinishFragmentReplace = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listDataForReplaseFinishFragmentReplace);
        LVFragmentReplaysForDay.setAdapter(ArrayAdapterForReplaceFinishFragmentReplace);
        buf = new ArrayList();
        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        mDataBaseW = FirebaseDatabase.getInstance().getReference(Constant.WATCHER_KEY);
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
        setOnClickItemReplaceDay();
        TVFragmentReplaceDay.setText(IDGroupUserFragmentReplace + uriUserStatusFragmentReplace + uriReplaceFragmentReplace
                + userNameFragmentReplace + replaceDay + UriUserInFragReplace);

        BFragmentNotReplaceDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.NotshowFragmentInfoAboutReplace();
                ODCinFragmentReplaceOne = 1;
                HomeActivity.statusOne = 0;
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference reference = db.getReferenceFromUrl(uriUserStatusFragmentReplace);
                reference.setValue("0");
                DatabaseReference reference2 = db.getReferenceFromUrl(uriReplaceFragmentReplace);
                reference2.setValue("-");
            }
        });


        // метод меняющий грфик
        BFragmentReplaceDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.NotshowFragmentInfoAboutReplace();
                //вызывается по кнопке поменять, меняет график
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                Group group = ds.getValue(Group.class);
                                assert group != null;
                                if(group.idGroup.equals(IDGroupUserFragmentReplace)) {
                                    schedule = group.getSchedule();
                                    StringBuilder builder = new StringBuilder();
                                    for(int i = 0; i<schedule.length(); i++) {
                                        if (schedule.charAt(i) == ';') {
                                            listDataForReplaseSchedule.add(String.valueOf(builder));
                                            if(String.valueOf(builder).equals(replaceForDay)){
                                                indexForDay = listDataForReplaseSchedule.size();
                                            }
                                            if(String.valueOf(builder).equals(replaceDay)){
                                                indexDay = listDataForReplaseSchedule.size();
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
                            listDataForReplaseSchedule.set(indexDay - 1, listDataForReplaseSchedule.get(indexDay - 1).replaceAll(nameTwo , nameOne));
                            listDataForReplaseSchedule.set(indexForDay - 1, listDataForReplaseSchedule.get(indexForDay - 1).replaceAll(nameOne , nameTwo));
                            String itog = "";
                            for(String str: listDataForReplaseSchedule){
                                itog = itog + str + ";";
                            }
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference reference = db.getReferenceFromUrl(UriUserInFragReplace);
                            reference.setValue(itog);
                            itog = "";
                            HomeActivity.NotshowFragmentInfoAboutReplace();

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    };
                    mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);

                //вторая часть метода меняющего грфик просто надо

                    ODCinFragmentReplaceTwo = 1;
                    HomeActivity.statusOne = 0;
                    ValueEventListener valueEventListener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (ODCinFragmentReplaceTwo == 1) {
                                uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    ThreadCheckStatus.ODCinThreadChecStatone = 0;
                                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                                    assert personOnDuty != null;
                                    if (personOnDuty.getIdGroup().equals(IDGroupUserFragmentReplace)) {
                                        if(onlyOneStopper == 0){
                                            onlyOneStopper = 1;
                                        }
                                        uri = uri + ds.getKey() + "/status";
                                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = db.getReferenceFromUrl(uri);
                                        reference.setValue("0");
                                        uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                                        uri = uri + ds.getKey() + "/replaceDay";
                                        DatabaseReference reference2 = db.getReferenceFromUrl(uri);
                                        reference2.setValue("-");
                                        uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";

                                    }
                                }
                                ODCinFragmentReplaceTwo = 0;
                                onlyOneStopper = 0;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    };
                    mDataBasePOD.addListenerForSingleValueEvent(valueEventListener2);
            }
        });


        pointToday = 0;
        //отображает лист всех доступных дней пользователя
        Calendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();
        todayStr = ":" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +
                "." + date.getDay() + "." + date.getMonth() + ";";

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(listDataForReplaseFinishFragmentReplace.size()>0){
                        listDataForReplaseFinishFragmentReplace.clear();
                    }
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Group group = ds.getValue(Group.class);
                        assert group != null;
//                        if(group.idGroup.equals(IDGroupUserFragmentReplace)) {
//                            String scheduleFull = group.getSchedule();
//                            try {
//                                StringBuilder scheduleBuilder = new StringBuilder();
//                                StringBuilder scheduleBuilderName = new StringBuilder();
//                                StringBuilder builderDate = new StringBuilder();
//
//                                for (int i = 0; i < scheduleFull.length(); i++) {
//
//                                    if (scheduleFull.charAt(i) == ':') {
//                                        posledvoet2 = 1;
//                                    }
//                                    if (posledvoet2 == 1) {
//                                        builderDate.append(scheduleFull.charAt(i));
//                                    }
//
//                                    if (scheduleFull.charAt(i) == ':') {
//                                        posledvoet = 1;
//                                        if (String.valueOf(scheduleBuilderName).equals(userNameFragmentReplace)) {
//                                            permissionToShowLVRDay = 1;
//                                        }
//                                        scheduleBuilderName.delete(0, scheduleBuilderName.length());
//                                    } else if (posledvoet == 0) {
//                                        scheduleBuilderName.append(scheduleFull.charAt(i));
//                                    }
//
//
//                                    if (scheduleFull.charAt(i) == ';') {
//                                        posledvoet = 0;
//                                        posledvoet2 = 0;
//                                        if (todayStr.equals(String.valueOf(builderDate))) {
//                                            pointToday = 1;
//                                        }
//                                        if (permissionToShowLVRDay == 1 && pointToday == 1) {
//                                            permissionToShowLVRDay = 0;
//                                            listDataForReplaseFinishFragmentReplace.add(createFormate(String.valueOf(scheduleBuilder)));
//                                            buf.add(String.valueOf(scheduleBuilder));
//                                        }
//                                        scheduleBuilder.delete(0, scheduleBuilder.length());
//                                        builderDate.delete(0, builderDate.length());
//                                    } else scheduleBuilder.append(scheduleFull.charAt(i));
//                                }
//                            }catch(NullPointerException e){
//                            }
//                        }
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
                                        listDataForReplaseFinishFragmentReplace.add(createFormate(String.valueOf(scheduleBuilder)));
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
                                TVFragmentReplaceDay.setText("hui");
                            }
                        }
                    }
//                try {
//                    buf.remove(0);
//                    listDataForReplaseFinishFragmentReplace.remove(0);
//                    }catch (IndexOutOfBoundsException e){
//                        //график устарел походу..
//                    TVFragmentReplaceDay.setText("hui2");
//                    }
//                    ArrayAdapterForReplaceFinishFragmentReplace.notifyDataSetChanged();
                    try {
                        //удаляет первый элемет если он не нужен (имя другое)
                        StringBuilder firstName = new StringBuilder();
                        for(int i = 0; i<listDataForReplaseFinishFragmentReplace.get(0).length(); i++){
                            if(listDataForReplaseFinishFragmentReplace.get(0).charAt(i) == ':'){
                                if(!String.valueOf(firstName).equals(userNameFragmentReplace)){
                                    listDataForReplaseFinishFragmentReplace.remove(0);
                                    buf.remove(0);
                                }
                            }else firstName.append(listDataForReplaseFinishFragmentReplace.get(0).charAt(i));
                        }
                        ArrayAdapterForReplaceFinishFragmentReplace.notifyDataSetChanged();
                    }catch (IndexOutOfBoundsException e){
//график устарел походу..
                       // TVFragmentReplaceDay.setText(scheduleFull);
                    }
                    ArrayAdapterForReplaceFinishFragmentReplace.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
        return v;
    }

    //вызывается сразу после нажатия кнопки подробнее, нужен что бы отобразить кнопку поменять
    private void setOnClickItemReplaceDay(){
        LVFragmentReplaysForDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BFragmentReplaceDay.setVisibility(View.VISIBLE);
                replaceForDay = buf.get(position);
                TVFragmentReplaceDay.setText("Вы будете дежурить " + createFormateForReplaceDay(replaceDay) + " но не дежурить: " +
                        createFormateForReplaceDay(replaceForDay));

            }
        });
    }
    private String createFormate(String str){

        String dayOfWeek = "1", month = "1";
        StringBuilder itog = new StringBuilder();
        itog.append(str);
        char c = '0';
        int l = 0;

        try {
            c = str.charAt(str.length() - 2);
            l = str.length();
        }catch(StringIndexOutOfBoundsException e){

        }

        if(c== '.'){
            dayOfWeek = String.valueOf(str.charAt(l - 3));
            month  = String.valueOf(str.charAt(l-1));
            itog.delete(l-4, l);
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
    private String createFormateForReplaceDay(String str){

        String dayOfWeek = "1", month = "1";
        StringBuilder itog = new StringBuilder();
        itog.append(str);
        for(int i =0; i<itog.length(); i++){
            if(itog.charAt(i) == ':'){
                itog.delete(0, i+1);
            }
        }
        char c = '0';
        int l = 0;

        try {
            c = itog.charAt(itog.length() - 2);
            l = itog.length();
        }catch(StringIndexOutOfBoundsException e){

        }

        if(c== '.'){
            dayOfWeek = String.valueOf(itog.charAt(l - 3));
            month  = String.valueOf(itog.charAt(l-1));
            itog.delete(l-4, l);
        }

        switch (dayOfWeek){
            case("0"): dayOfWeek = " (воскресенье)"; break;
            case("1"): dayOfWeek = " (понедельник)"; break;
            case("2"): dayOfWeek = " (вторник)"; break;
            case("3"): dayOfWeek = " (среда)"; break;
            case("4"): dayOfWeek = " (четерг)"; break;
            case("5"): dayOfWeek = " (пятница)"; break;
            case("6"): dayOfWeek = " (суббота)"; break;
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
}




//
//    //Вызывается когда пользоваель нажал на подробнее, но не выбрал день который хочет поменять
//    public void showQuestionAboutAcceptReplaceFinish(){
//        TVReplaseFinish.setVisibility(View.VISIBLE);
//        ListViewDayForReplace.setVisibility(View.VISIBLE);
//        BHNotReplaceFinish.setVisibility(View.VISIBLE);
//    }




