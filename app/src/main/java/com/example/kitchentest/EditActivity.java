package com.example.kitchentest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {
    EditText ETAEName, ETAEPhone;
    Button BAEEdit;
    public static DatabaseReference mDataBaseW, mDataBasePOD, mDataBaseGroup;
    boolean nameUnique = true, phoneUnique = true;
    private String  newName, newPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ETAEName = (EditText) findViewById(R.id.ETAEName);
        ETAEPhone = (EditText) findViewById(R.id.ETAEPhone);
        BAEEdit = (Button) findViewById(R.id.BAEEdit);

        ETAEName.setText(HomeActivity.userName);
        ETAEPhone.setText(HomeActivity.userPhone);

        mDataBasePOD = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        mDataBaseW = FirebaseDatabase.getInstance().getReference(Constant.WATCHER_KEY);
        mDataBaseGroup = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);



    }




    public void backHome(View view) {
        onBackPressed();
    }

    public void edit(View view) {
        if(HomeActivity.userStatus.equals("PersonOnDuty")) {
            newName = ETAEName.getText().toString();
            newPhone = ETAEPhone.getText().toString();
            //изменялось ли имя
            if(!newName.equals(HomeActivity.userName)){
                if(newName.length() >= 3) {
                    nameUnique = true;
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                                    assert personOnDuty != null;
                                    if(personOnDuty.getIdGroup().equals(HomeActivity.IDGroupUser)){
                                        if(personOnDuty.getName().equals(newName)){
                                            nameUnique = false;
                                        }
                                    }
                                }
                                if(nameUnique){
                                    editName(newName);
                                }else Toast.makeText(EditActivity.this, "Имя занято", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        };
                        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
                }else Toast.makeText(EditActivity.this, "Короткое имя", Toast.LENGTH_SHORT).show();
            }

            //изменялся ли телеон
            if(!newPhone.equals(HomeActivity.userPhone)){
                if(newPhone.length() >= 11) {
                    editPhone(newPhone);
                }else Toast.makeText(EditActivity.this, "Короткий номер", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void editName(final String newName) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if(personOnDuty.getId().equals(HomeActivity.UserID)){
                        String uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/"+  ds.getKey() +"/name";
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl(uri);
                        reference.setValue(newName);
                        HomeActivity.userName = newName;
                        Toast.makeText(EditActivity.this, "Имя успешно изменёно", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDataBasePOD.addListenerForSingleValueEvent(valueEventListener);
    }

    private void editPhone(final String newPhone) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                    assert personOnDuty != null;
                    if(personOnDuty.getId().equals(HomeActivity.UserID)){
                        String uri = "https://dnevalnie.firebaseio.com/Person_On_Duty/"+  ds.getKey() +"/phone";
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReferenceFromUrl(uri);
                        reference.setValue(newPhone);
                        HomeActivity.userPhone = newPhone;
                        Toast.makeText(EditActivity.this, "Номер успешно изменён", Toast.LENGTH_SHORT).show();
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