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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowGroupActivity extends AppCompatActivity {
    private DatabaseReference mDataBasePOD;
    private ListView listView;
    private  Adapter arrayAdapter;
    private List<String> listData;
    private List<PersonOnDuty> listItem;
    private ArrayList<PersonOnDuty> listForImportPOD = new ArrayList<>();
    private EditText EDASGNamePPOD, EDASGPhonePOD;

    public static int isPOD = 0, repeatNameInGroup = 0;
    private int firstTap;
    public static String userIdShowGAct = null, IDGroup = null;
    private String namePPOD, phonePPOD;

    private Button BASGAddPOD, BSActivityLeaveGroup, BASGnotAddPOD;
    private TextView addPODtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show_group);
       if(isPOD == 1) {
           init();
           BASGAddPOD.setVisibility(View.GONE);
           BASGnotAddPOD.setVisibility(View.GONE);
           BSActivityLeaveGroup.setVisibility(View.VISIBLE);
           getDataFromDb();
       }else if(isPOD == -1){
           init();
           BASGAddPOD.setVisibility(View.VISIBLE);
           BASGnotAddPOD.setVisibility(View.GONE);
           BSActivityLeaveGroup.setVisibility(View.GONE);
           getDataFromDb();
           setOnClickItem();
       }
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
                        listForImportPOD.add(personOnDuty);
                        listData.add(personOnDuty.getName());
                        listItem.add(personOnDuty);
                    }
                }
                  arrayAdapter.notifyDataSetChanged();
                importListPODs(listForImportPOD);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBasePOD.addValueEventListener(valueEventListener);
    }
    private void importListPODs(ArrayList<PersonOnDuty> listForImportPOD) {
        ListOfFirebase.fullListPOD = listForImportPOD;
    }

    private void setOnClickItem(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonOnDuty personOnDuty = listItem.get(position);
                Intent intent = new Intent(ShowGroupActivity.this, ShowActivity.class);
                intent.putExtra(Constant.USER_NAME, personOnDuty.getName());
                intent.putExtra(Constant.USER_ID, personOnDuty.getId());
                intent.putExtra(Constant.USER_IDGROUP, personOnDuty.getIdGroup());
                startActivity(intent);
            }
        });
    }

    private void init() {
        listView = (ListView) findViewById(R.id.ListViewGroup);
        listData = new ArrayList<>();
        arrayAdapter = new Adapter(this, listData);
        listView.setAdapter(arrayAdapter);
        listItem = new ArrayList<>();
        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);

        addPODtext = (TextView) findViewById(R.id.addPODtext);
        BSActivityLeaveGroup = (Button) findViewById(R.id.BSActivityLeaveGroup);
        BASGnotAddPOD = (Button) findViewById(R.id.BASGnotAddPOD);
        BASGAddPOD = (Button) findViewById(R.id.BASGAddPOD);
        EDASGPhonePOD = (EditText) findViewById(R.id.EDASGPhonePOD);
        EDASGNamePPOD = (EditText) findViewById(R.id.EDASGNamePPOD);
        EDASGNamePPOD.setVisibility(View.GONE);
        EDASGPhonePOD.setVisibility(View.GONE);
        firstTap = 0;
        namePPOD = null;
        phonePPOD = null;

    }

    public void createPassivePOD(View view) {
        if(firstTap == 0){
            firstTap++;
            BASGAddPOD.setBackgroundResource(R.drawable.button_vacation);
            listView.setVisibility(View.GONE);
            addPODtext.setVisibility(View.VISIBLE);
            EDASGNamePPOD.setVisibility(View.VISIBLE);
            EDASGPhonePOD.setVisibility(View.VISIBLE);

            BASGnotAddPOD.setVisibility(View.VISIBLE);
        }else{
            BASGAddPOD.setBackgroundResource(R.drawable.button);
            try {
                namePPOD = EDASGNamePPOD.getText().toString();
                namePPOD.trim();

            }catch (NullPointerException e){
                Toast.makeText(ShowGroupActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
                BASGAddPOD.setBackgroundResource(R.drawable.button_vacation);
            }

            try{
                phonePPOD = EDASGPhonePOD.getText().toString();
                phonePPOD.trim();
            }catch (NullPointerException e){
                Toast.makeText(ShowGroupActivity.this, "phone is empty", Toast.LENGTH_SHORT).show();
                BASGAddPOD.setBackgroundResource(R.drawable.button_vacation);
            }

            if(namePPOD.length()<3){
                Toast.makeText(ShowGroupActivity.this, "name is short", Toast.LENGTH_SHORT).show();
                BASGAddPOD.setBackgroundResource(R.drawable.button_vacation);
            }else if(phonePPOD.length()<11) {
                Toast.makeText(ShowGroupActivity.this, "phone is short", Toast.LENGTH_SHORT).show();
                BASGAddPOD.setBackgroundResource(R.drawable.button_vacation);
            }else{
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                            assert personOnDuty != null;
                            if (personOnDuty.getIdGroup().equals(IDGroup)) {
                                if (personOnDuty.getName().equals(namePPOD)) {
                                    repeatNameInGroup = 1;
                                }
                            }
                        }
                        if (repeatNameInGroup == 0) {
                            createPPOD();
                        } else {
                            repeatNameInGroup = 0;
                            Toast.makeText(ShowGroupActivity.this, "Такое имя уже есть в группе", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
            }
        }
    }




    private void createPPOD() {
        
        PersonOnDuty personOnDuty = new PersonOnDuty(namePPOD, Constant.PERSON_ON_DUTY_ROLE, "IDID" , phonePPOD, IDGroup,
                "0", "0" , "-");

        mDataBasePOD.push().setValue(personOnDuty);
        listView.setVisibility(View.VISIBLE);
        addPODtext.setVisibility(View.GONE);
        EDASGNamePPOD.setVisibility(View.GONE);
        EDASGPhonePOD.setVisibility(View.GONE);
        EDASGNamePPOD.setText("");
        EDASGPhonePOD.setText("");
        BASGnotAddPOD.setVisibility(View.GONE);
        firstTap = 0;
        repeatNameInGroup = 0;
        Toast.makeText(ShowGroupActivity.this, "Готово", Toast.LENGTH_SHORT).show();
    }

    public void leaveTheGroup(View view) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if(personOnDuty.getId().equals(userIdShowGAct)){

                        uri +=  ds.getKey() + "/idGroup";
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl(uri);
                        reference.setValue("-");
                        uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/";
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(ShowGroupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        HomeActivity.UserID = null;
                        HomeActivity.stopChekWhoIsUser = 0;
                        HomeActivity.statusOne = 0;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }

    public void notAddPOD(View view) {
        BASGAddPOD.setBackgroundResource(R.drawable.button);
        listView.setVisibility(View.VISIBLE);
        addPODtext.setVisibility(View.GONE);
        BASGnotAddPOD.setVisibility(View.GONE);
        EDASGNamePPOD.setVisibility(View.GONE);
        EDASGPhonePOD.setVisibility(View.GONE);
        firstTap = 0;
    }

    public void backHomeActivity(View view) {
        onBackPressed();
    }
}
