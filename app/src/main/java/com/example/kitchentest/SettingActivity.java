package com.example.kitchentest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import java.util.Set;

public class SettingActivity extends AppCompatActivity {
   private static ArrayList<ItemSetting> listSettings = new ArrayList<>();;
   private static ListView LVAS;
    public static DatabaseReference mDataBaseGroup;

    private static AdapterCastom customeAdapter;
    public ArrayList<EditModel> editModelArrayList;


   public static String deadLine, quantityDayInSchedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();


    }

    private void init() {
        LVAS = (ListView) findViewById(R.id.LVAS);
        listSettings.clear();
        customeAdapter = new AdapterCastom(this,listSettings);
        LVAS.setAdapter(customeAdapter);
        populateList();
    }

    public static void populateList(){
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.getIdGroup().equals(HomeActivity.IDGroupUser)){
                       deadLine = group.getSDeadline();
                        quantityDayInSchedule = group.getSquantityDayInSchedule();
                        listSettings.add(new ItemSetting("Крайний срок сдачи дежурства (ч)", deadLine));
                        listSettings.add(new ItemSetting("На сколько дней делать график (дн)", quantityDayInSchedule));
                    }
                }
                try {
                    customeAdapter.notifyDataSetChanged();
                }catch (Exception e){

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
    }

    public void onDestroy() {
        String valueEdit;
        int proverka;
        for (int i = 0; i < AdapterCastom.editModelArrayList.size(); i++){
             valueEdit =  AdapterCastom.editModelArrayList.get(i).getEditTextStr();
             try{
                 proverka = Integer.parseInt(valueEdit);
                 if(proverka > 0){
                     switch (i){
                         case 0: {
                             if(!valueEdit.equals(deadLine)){
                                 upgradeSettingOne(valueEdit, proverka);
                             }
                             break;
                         }
                         case 1: {
                             if(!valueEdit.equals(quantityDayInSchedule)){
                                 upgradeSettingTwo(valueEdit, proverka);
                             }
                             break;
                         }
                     }
                 }
             }catch (Exception e){

             }
        }
        listSettings.clear();
        super.onDestroy();
    }

    private void upgradeSettingOne(final String valueEdit, int pr) {
        if(pr <= 23) {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Group group = ds.getValue(Group.class);
                        assert group != null;
                        if(group.getIdGroup().equals(HomeActivity.IDGroupUser)
                                && HomeActivity.userStatus.equals("Watcher")){
                            String uri = "https://dnevalnie.firebaseio.com/Group/" + ds.getKey() + "/sdeadline";
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference reference = db.getReferenceFromUrl(uri);
                            reference.setValue(valueEdit);
                        }
                    }
                    customeAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    private void upgradeSettingTwo(final String valueEdit, int pr) {
        if(pr <= 60 && pr > 10) {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Group group = ds.getValue(Group.class);
                        assert group != null;
                        if(group.getIdGroup().equals(HomeActivity.IDGroupUser)
                                && HomeActivity.userStatus.equals("Watcher")){
                            String uri = "https://dnevalnie.firebaseio.com/Group/" + ds.getKey() + "/squantityDayInSchedule";
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference reference = db.getReferenceFromUrl(uri);
                            reference.setValue(valueEdit);
                        }
                    }
                    customeAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mDataBaseGroup.addListenerForSingleValueEvent(valueEventListener);
        }
    }


    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(intent);
        HomeActivity.UserID = null;
        HomeActivity.IDGroupUser = null;
        HomeActivity.userName = null;
        HomeActivity.stopChekWhoIsUser = 0;
        //HomeActivity.statusOne = 0;
    }
    public void goToHome(View view) {
        onBackPressed();
    }

    public void goToDutiesAct(View view) {
        Intent intent = new Intent(SettingActivity.this, DutiesActivity.class);
        startActivity(intent);
    }
}



