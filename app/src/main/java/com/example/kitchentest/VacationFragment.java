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
import android.widget.FrameLayout;
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


public class VacationFragment extends Fragment {
    private TextView TVFragmentVacation;
    private ListView LVFragmentVacation;
    private Button BFragmentVacationBack, BFragmentVacationOpen;
    private int pointToday = 0, posledvoet2 = 0, posledvoet = 0, permissionToShowLVRDay = 0,
            posledvoetV = 0;
    private String todayStr, stringVacation, schedule, uri;
    public static List<String> listDataVacationFragment;
    private ArrayList<String> buf, listVacationDays, nameList, dateList, nameListV, dateListV,
            indexRemove;

    public static String IDGroupUserVacationFragment = null, userNameFragmentVacation = null;
    private ArrayAdapter<String> ArrayAdapterForVacationFragment;

    private int dayIsBe = 0;

    private DatabaseReference mDataBaseGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vacation, null);

        TVFragmentVacation = (TextView) v.findViewById(R.id.TVFragmentVacation);
        BFragmentVacationBack = (Button) v.findViewById(R.id.BFragmentVacationBack);
        BFragmentVacationOpen = (Button) v.findViewById(R.id.BFragmentVacationOpen);
        LVFragmentVacation = (ListView) v.findViewById(R.id.LVFragmentVacation);
        listDataVacationFragment = new ArrayList<>();
        listVacationDays = new ArrayList<>();
        nameList = new ArrayList<>();
        dateList = new ArrayList<>();
        nameListV = new ArrayList<>();
        dateListV = new ArrayList<>();
        indexRemove = new ArrayList<>();

        ArrayAdapterForVacationFragment = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listDataVacationFragment);
        LVFragmentVacation.setAdapter(ArrayAdapterForVacationFragment);
        buf = new ArrayList<>();

        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
        BFragmentVacationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ShowActivity.notShowFragmentVacation();
            }
        });
        BFragmentVacationOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!listVacationDays.isEmpty()){
                    openOtpuskPartOne();
                    ShowActivity.notShowFragmentVacation();
                }else{
                    TVFragmentVacation.setText("Вы не выбрали ни одного дня");
                }
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
                if(listDataVacationFragment.size()>0){
                    listDataVacationFragment.clear();
                }
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.idGroup.equals(IDGroupUserVacationFragment)) {
                        uri = "https://dnevalnie.firebaseio.com/Group/"+ ds.getKey() +"/schedule";
                        String scheduleFull = group.getSchedule();
                        try {
                            StringBuilder scheduleBuilder = new StringBuilder();
                            StringBuilder scheduleBuilderName = new StringBuilder();
                            StringBuilder builderDate = new StringBuilder();

                            for (int i = 0; i < scheduleFull.length(); i++) {

                                if (scheduleFull.charAt(i) == ':') {
                                    posledvoet2 = 1;
                                }
                                if (posledvoet2 == 1) {
                                    builderDate.append(scheduleFull.charAt(i));
                                }

                                if (scheduleFull.charAt(i) == ':'){
                                    posledvoet = 1;
                                    if (String.valueOf(scheduleBuilderName).equals(userNameFragmentVacation)) {
                                        permissionToShowLVRDay = 1;
                                    }
                                    scheduleBuilderName.delete(0, scheduleBuilderName.length());
                                } else if (posledvoet == 0) {
                                    scheduleBuilderName.append(scheduleFull.charAt(i));
                                }
                                if (scheduleFull.charAt(i) == ';') {
                                    posledvoet = 0;
                                    posledvoet2 = 0;
                                    if (todayStr.equals(String.valueOf(builderDate))) {
                                        pointToday = 1;
                                    }
                                    if (permissionToShowLVRDay == 1 && pointToday == 1) {
                                        permissionToShowLVRDay = 0;
                                        listDataVacationFragment.add(createFormate(String.valueOf(scheduleBuilder)));
                                        buf.add(String.valueOf(scheduleBuilder));
                                    }
                                    scheduleBuilder.delete(0, scheduleBuilder.length());
                                    builderDate.delete(0, builderDate.length());
                                } else scheduleBuilder.append(scheduleFull.charAt(i));
                            }
                        }catch(NullPointerException e){
                        }
                    }

                }
                try {
                    //удаляет первый элемет если он не нужен (имя другое)
                    StringBuilder firstName = new StringBuilder();
                    for(int i = 0; i<listDataVacationFragment.get(0).length(); i++){
                        if(listDataVacationFragment.get(0).charAt(i) == ':'){
                            if(!String.valueOf(firstName).equals(userNameFragmentVacation)){
                                listDataVacationFragment.remove(0);
                                buf.remove(0);
                            }
                        }else firstName.append(listDataVacationFragment.get(0).charAt(i));
                    }
                    ArrayAdapterForVacationFragment.notifyDataSetChanged();
                }catch (IndexOutOfBoundsException e){
//график устарел походу..
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
        setOnClickItemReplaceDay();
        return v;
    }

    private void openOtpuskPartOne() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(listDataVacationFragment.size()>0){
                    listDataVacationFragment.clear();
                }
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.idGroup.equals(IDGroupUserVacationFragment)) {
                        schedule = group.getSchedule();
                    }

                }
                posledvoetV = 0;
                posledvoet = 0;
                StringBuilder nameBr = new StringBuilder();
                StringBuilder dateBr = new StringBuilder();
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

                String vacationDaysStr = "";

                for(String str : listVacationDays){
                    vacationDaysStr += str +";";
                }

                StringBuilder nameBrV = new StringBuilder();
                StringBuilder dateBrV = new StringBuilder();
                for (int i = 0; i < vacationDaysStr.length(); i++) {
                    if (vacationDaysStr.charAt(i) == ':') {
                        posledvoetV = 1;
                    } else if (vacationDaysStr.charAt(i) == ';') {
                        nameListV.add(String.valueOf(nameBrV));
                        dateListV.add(String.valueOf(dateBrV));
                        nameBrV.delete(0, nameBrV.length());
                        dateBrV.delete(0, dateBrV.length());
                        posledvoetV = 0;
                    } else {
                        if (posledvoetV == 0) {
                            nameBrV.append(vacationDaysStr.charAt(i));
                        } else {
                            dateBrV.append(vacationDaysStr.charAt(i));
                        }
                    }

                }
                for(int i = 0; i<dateListV.size(); i++){
                    for(int y = 0; y<dateList.size(); y++){
                        if(dateList.get(y).equals(dateListV.get(i))){
                          indexRemove.add(String.valueOf(y));
                     }
                    }
                }

                //y нужна что бы удалять индекс сдвигнутого обьекта в массиве, и поэтому сама за обьектом бегает
                int y = 0;

                for (String indexR : indexRemove) {
                    nameList.remove(Integer.parseInt(indexR) - y);
                    y++;
                }
                //System.out.println("на отработку " + indexRemove.size() + " дней");

                String scheduleItog = "";
                for(int i = 0; i<nameList.size(); i++){
                    scheduleItog += nameList.get(i) + ":" + dateList.get(i) + ";";
                }
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference reference = db.getReferenceFromUrl(uri);
                reference.setValue(scheduleItog);
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
        LVFragmentVacation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stringVacation = buf.get(position);
                dayIsBe = 0;
                for(int i = 0; i<listVacationDays.size(); i++){
                    if(listVacationDays.get(i).equals(stringVacation)){
                        dayIsBe = 1;
                        listVacationDays.remove(i);
                    }
                }
                if(dayIsBe == 0) {
                    listVacationDays.add(buf.get(position));
                }

                String str = "";
                for(String listDays : listVacationDays){
                    str += listDays;
                    TVFragmentVacation.setText(createFormate(listDays) + " ");
                }
                if(listVacationDays.isEmpty()){
                    TVFragmentVacation.setText("Выбирите дни для отпуска");
                }

            }
        });
    }
}