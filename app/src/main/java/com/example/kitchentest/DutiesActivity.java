package com.example.kitchentest;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class DutiesActivity extends AppCompatActivity {
    private ListView LVAD;
    private Adapter adapter;
    private ArrayList<String> list, trashbox;
    private Button BADaddDutie, BADdeleteDutie;
    private EditText ETADDuties;
    private TextView TVADemptyList;
    private String uri, dutiesStr = "";  // Pomit Poli; Vinesti Musor; Hui pOSOSAT;
    private DatabaseReference PersonOnDutyDataBase, WatcherDataBase, GroupDataBase;
    private boolean edit, firstTap, firstTap2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duties);
        init();
        getDataFromBd();
    }

    private void fillList() {
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i<dutiesStr.length(); i++){
            if(dutiesStr.charAt(i) == ';'){
                list.add(String.valueOf(buf));
                buf.delete(0, buf.length());
            }else buf.append(dutiesStr.charAt(i));
        }
    }

    private void getDataFromBd() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(list.size()>0){
                    list.clear();
                }
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.getIdGroup().equals(HomeActivity.userPhone)){
                        uri = "https://dnevalnie.firebaseio.com/Group/" + ds.getKey()+"/duties";
                        dutiesStr = group.getDuties();
                        if(!dutiesStr.equals("-")){
                            fillList();
                        }else{
                            TVADemptyList.setVisibility(View.VISIBLE);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        GroupDataBase.addValueEventListener(valueEventListener);
    }

    private void init() {
        LVAD = (ListView) findViewById(R.id.LVAD);
        BADaddDutie = (Button) findViewById(R.id.BADaddDutie);
        BADdeleteDutie= (Button) findViewById(R.id.BADdeleteDutie);
        ETADDuties = (EditText) findViewById(R.id.ETADDuties);
        TVADemptyList = (TextView) findViewById(R.id.TVADemptyList);
        list = new ArrayList<>();
        trashbox = new ArrayList<>();
        adapter = new Adapter(this, list);
        LVAD.setAdapter(adapter);
        PersonOnDutyDataBase = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        WatcherDataBase = FirebaseDatabase.getInstance().getReference(Constant.WATCHER_KEY);
        GroupDataBase = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
        edit = false;
        firstTap = true;
        firstTap2 = true;
    }

    public void backSettings(View view) {
        onBackPressed();
    }

    public void addDuties(View view) {
        edit = true;
        if(firstTap){
            firstTap = false;
            BADaddDutie.setBackgroundResource(R.drawable.button_vacation);
            ETADDuties.setVisibility(View.VISIBLE);
            LVAD.setVisibility(View.GONE);
            TVADemptyList.setVisibility(View.GONE);

            firstTap2 = true;
            BADdeleteDutie.setBackgroundResource(R.drawable.button);
            trashbox.clear();
        }else{
            firstTap = true;
            BADaddDutie.setBackgroundResource(R.drawable.button);
            ETADDuties.setVisibility(View.GONE);
            LVAD.setVisibility(View.VISIBLE);
            if(!ETADDuties.getText().toString().isEmpty()) {
                list.add(ETADDuties.getText().toString());
                ETADDuties.setText("");
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void deleteDuties(View view) {
        edit = true;
        if(firstTap2){
            firstTap2 = false;
            BADdeleteDutie.setBackgroundResource(R.drawable.button_add_days);
            setOnClickItem();
            Toast.makeText(DutiesActivity.this, "Выбирите в списке обязанность", Toast.LENGTH_SHORT).show();

            firstTap = true;
            BADaddDutie.setBackgroundResource(R.drawable.button);
            ETADDuties.setVisibility(View.GONE);
            LVAD.setVisibility(View.VISIBLE);
        }else{
            setOnClickItemEmpty();
            firstTap2 = true;
            BADdeleteDutie.setBackgroundResource(R.drawable.button);
            for(int i = 0; i<trashbox.size(); i++){
                list.remove(trashbox.get(i));
            }
            adapter.notifyDataSetChanged();
            trashbox.clear();
        }
    }


    public void onDestroy() {
        if(edit){
            String newDutiesStr = "";
            for(String str : list){
                newDutiesStr += str + ";";
            }
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference reference = db.getReferenceFromUrl(uri);
            reference.setValue(newDutiesStr);
        }
        super.onDestroy();
    }

    private void setOnClickItem(){
        LVAD.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                trashbox.add(list.get(position));
                Toast.makeText(DutiesActivity.this, "ok", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setOnClickItemEmpty(){
        LVAD.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

}