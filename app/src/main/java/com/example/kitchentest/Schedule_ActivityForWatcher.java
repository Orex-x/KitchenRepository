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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Schedule_ActivityForWatcher extends AppCompatActivity {
    private ListView listView, OKlistView;
    private Adapter arrayAdapter, OKarrayAdapter;
    private List<String> listData;
    public static List<String> OKlistData;
    private List<PersonOnDuty> listItem;
    private DatabaseReference mDataBasePOD, mDataBaseGroup;
    public static String IDGroup = null, UriUser = null;
    private TextView TVinSchedule;
    private String lol, todayStr;
    private int  counterForNewSchedule, counterForShowSchedule, posledvoet = 0, pointToday = 0;
    private Button replaceOnNewSchedule, notReplace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_schedule_for_watcher);

        init();

        if(ListOfFirebase.scheduleStr == null){
            getDataFromDb();
            setOnClickItem();
            showNewList();
            replaceOnNewSchedule.setVisibility(View.GONE);
        }else {
            showListSchedule();
            getScheduleFromDb();
            notReplace.setVisibility(View.GONE);}
    }




    private void getDataFromDb(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(listData.size()>0){
                    listData.clear();
                }
                if(listItem.size()>0){
                    listItem.clear();
                }
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;

                    if(personOnDuty.idGroup.equals(IDGroup)) {
                        listData.add(personOnDuty.getName());
                        listItem.add(personOnDuty);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBasePOD.addValueEventListener(valueEventListener);
    }
    private void setOnClickItem(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i = position;
                if(UriUser != null) {
                    counterForNewSchedule = 0;
                    Calendar calendar = new GregorianCalendar();
                    Date date2 = calendar.getTime();


                    if(i != -1) {
                        while (counterForNewSchedule < Integer.parseInt(SettingActivity.quantityDayInSchedule)) {
                            counterForNewSchedule++;
                            date2 = calendar.getTime();
                            String dayOfTheWeek = String.valueOf(date2.getDay());
                            String dayOfTheMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                            String month = String.valueOf(date2.getMonth());
                            if (i == listItem.size()) {
                                i = 0;
                            }
                            lol = lol +  listItem.get(i).getName() + ":" + dayOfTheMonth +
                                    "." + dayOfTheWeek + "." + month + "," + "00" + ";" ;
                            i++;
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                        }

                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl(UriUser);
                        reference.setValue(lol);
                        lol = "";

                        Toast.makeText(Schedule_ActivityForWatcher.this, "Готово", Toast.LENGTH_SHORT).show();
                        showListSchedule();
                        getScheduleFromDb();
                        notReplace.setVisibility(View.GONE);
                        replaceOnNewSchedule.setVisibility(View.VISIBLE);

                    }else Toast.makeText(Schedule_ActivityForWatcher.this, "Выберете дежурного", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(Schedule_ActivityForWatcher.this, "Секунду", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getScheduleFromDb(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(OKlistData.size()>0){
                    OKlistData.clear();
                }
                pointToday = 0;
                counterForShowSchedule = 0;

                Calendar calendar = new GregorianCalendar();
                Date date = calendar.getTime();
                todayStr = ":" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +
                        "." + date.getDay() + "." + date.getMonth() + ",";

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if (group.idGroup.equals(IDGroup)) {
                        String scheduleFull = group.getSchedule();
                        if (scheduleFull != null){
                            StringBuilder scheduleBuilder = new StringBuilder();
                            StringBuilder builderDate = new StringBuilder();

                            for (int i = 0; i < scheduleFull.length(); i++) {
                                if (scheduleFull.charAt(i) == ':') {
                                posledvoet = 1;
                                }
                                if (posledvoet == 1) {
                                builderDate.append(scheduleFull.charAt(i));
                                }


                            if (scheduleFull.charAt(i) == ';') {
                                posledvoet = 0;
                                builderDate.delete(builderDate.length()-3, builderDate.length());

                                if (todayStr.equals(String.valueOf(builderDate))) {
                                    pointToday = 1;
                                }
                                if (pointToday == 1 && counterForShowSchedule < 8) {
                                    counterForShowSchedule++;
                                    scheduleBuilder.delete(scheduleBuilder.length()-3, scheduleBuilder.length());
                                    OKlistData.add(Constant.createFormate(String.valueOf(scheduleBuilder)));
                                }

                                scheduleBuilder.delete(0, scheduleBuilder.length());
                                builderDate.delete(0, builderDate.length());

                            } else {
                                scheduleBuilder.append(scheduleFull.charAt(i));
                            }
                        }
                    }
                }
                }
                pointToday = 0;
                counterForShowSchedule = 0;
                OKarrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBaseGroup.addValueEventListener(valueEventListener);
    }



    private void showListSchedule(){
        OKlistView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        TVinSchedule.setVisibility(View.GONE);
    }
    private void showNewList(){
        OKlistView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        TVinSchedule.setVisibility(View.VISIBLE);
    }
    private void init() {
        listView = (ListView) findViewById(R.id.ListViewInSchedule_Activity);
        OKlistView = (ListView) findViewById(R.id.OKListViewInSchedule_Activity);
        OKlistData = new ArrayList<>();
        listData = new ArrayList<>();

        OKarrayAdapter = new Adapter(this, OKlistData);
        arrayAdapter = new Adapter(this, listData);

        OKlistView.setAdapter(OKarrayAdapter);
        listView.setAdapter(arrayAdapter);

        listItem = new ArrayList<>();

        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);

        TVinSchedule = (TextView) findViewById(R.id.TVinSchedule);

        replaceOnNewSchedule = (Button) findViewById(R.id.replaceSchedule);
        notReplace = (Button) findViewById(R.id.notReplace);



        lol = "";
        counterForNewSchedule = 0;
        counterForShowSchedule = 0;
    }
    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols(){

        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }

    };




    public void replaceOnNewSchedule(View view) {
        getDataFromDb();
        setOnClickItem();
        showNewList();
        TVinSchedule.setText("Кто сегодня дежурит?");
        replaceOnNewSchedule.setVisibility(View.GONE);
        notReplace.setVisibility(View.VISIBLE);
    }
    public void notreplaceOnNewSchedule(View view) {
        replaceOnNewSchedule.setVisibility(View.VISIBLE);
        notReplace.setVisibility(View.GONE);
        showListSchedule();

    }

    public void backHomeActivityFromASFW(View view) {
        onBackPressed();
    }
}
