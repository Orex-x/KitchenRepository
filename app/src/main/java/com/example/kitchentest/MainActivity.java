package com.example.kitchentest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private ListView lv;
    private AdapterCastom customeAdapter;
    public ArrayList<EditModel> editModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        lv = (ListView) findViewById(R.id.listView);
//
//        editModelArrayList = populateList();
//        customeAdapter = new AdapterCastom(this,editModelArrayList);
//        lv.setAdapter(customeAdapter);



    }

//    private ArrayList<EditModel> populateList(){
//
//        ArrayList<EditModel> list = new ArrayList<>();
//
//        for(int i = 0; i < 8; i++){
//            EditModel editModel = new EditModel();
//            editModel.setEditTextValue(String.valueOf(i));
//            list.add(editModel);
//        }
//
//        return list;
//    }
}
