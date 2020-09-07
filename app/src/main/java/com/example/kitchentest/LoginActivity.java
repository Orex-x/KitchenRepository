package com.example.kitchentest;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText ETWelcomeLogin, ETWelcomePassword, ETRLogin, ETRName, ETRPhone, ETRPassword, ETRidGroup;
    Button BWOpen, BWRegistr, BRPersonOD, BRWatcher, BRRegistr, BRBack;
    TextView TVWelcome, TVRegistr;
    private FirebaseAuth mAuth;
    private DatabaseReference PersonOnDutyDataBase, WatcherDataBase, StatusDataBase, GroupDataBase;
    private int isWatcher = 0, repeatIdGroup = 0, repeatNameInGroup = 0, groupexists = 0;
    private String phoneStr, idWatcher, name, idGroupStr, idPersonOD = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        showWelcome();
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            String userText = "Вы вошли как: " + currentUser.getEmail();
            HomeActivity.UserID =  currentUser.getUid();
            ThreadCheckStatus.UserIDforThread2 = currentUser.getUid();
           // Toast.makeText(LoginActivity.this, userText, Toast.LENGTH_SHORT).show();
        }
    }


    public void registrB(View view) {
        if(isWatcher == -1){
            createPODinBD();
        }else if (isWatcher == 1){
            createWatcherInBD();
        }else Toast.makeText(LoginActivity.this, "Выберите сввою роль", Toast.LENGTH_SHORT).show();
    }




    public void logInto(View view) {
        String log = ETWelcomeLogin.getText().toString();
        String pass = ETWelcomePassword.getText().toString();
        log.trim();
        pass.trim();
        if(!pass.isEmpty() && !log.isEmpty()) {
            mAuth.signInWithEmailAndPassword(log, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        HomeActivity.UserID =  currentUser.getUid();
                        ThreadCheckStatus.UserIDforThread2 =currentUser.getUid();

                    }else{
                        Toast.makeText(LoginActivity.this, "Неправельные логин или пароль", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else  Toast.makeText(LoginActivity.this, "Логин занят или написан некорректно", Toast.LENGTH_SHORT).show();
    }



    private void createPODinBD() {
         name = ETRName.getText().toString();
         phoneStr = ETRPhone.getText().toString();
         idGroupStr = ETRidGroup.getText().toString();
         name.trim();
         idGroupStr.trim();
         phoneStr.trim();
         repeatNameInGroup=0;
         groupexists = 0;

        if (!name.isEmpty() && !phoneStr.isEmpty() && !idGroupStr.isEmpty()){
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        PersonOnDuty personOnDuty = ds.getValue(PersonOnDuty.class);
                        assert personOnDuty != null;
                        if(personOnDuty.getIdGroup().equals(idGroupStr)){
                            if(personOnDuty.getName().equals(name)){
                                repeatNameInGroup = 1;
                            }
                        }
                    }
                    if(repeatNameInGroup == 0){
                        checkGroupId();
                    }else  Toast.makeText(LoginActivity.this, "Такое имя уже есть в группе", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            PersonOnDutyDataBase.addListenerForSingleValueEvent(valueEventListener);
        }else{
            Toast.makeText(LoginActivity.this, "Пустые поля", Toast.LENGTH_SHORT).show();
        }
    }

    public void registrCPOD() {
        String log = ETRLogin.getText().toString();
        String pass = ETRPassword.getText().toString();
        log.trim();
        pass.trim();
        if(!pass.isEmpty() && !log.isEmpty()){
            mAuth.createUserWithEmailAndPassword(log, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            idPersonOD = currentUser.getUid();
                        }
                        PersonOnDuty personOnDuty = new PersonOnDuty(name, Constant.PERSON_ON_DUTY_ROLE, idPersonOD , phoneStr, idGroupStr,
                                "0", "0" , "-");

                        PersonOnDutyDataBase.push().setValue(personOnDuty);
                        FirebaseUser currentUser2 = mAuth.getCurrentUser();
                        HomeActivity.UserID =  currentUser2.getUid();
                        ThreadCheckStatus.UserIDforThread2 = currentUser2.getUid();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "Готово", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "Логин занят или написан некорректно", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else Toast.makeText(this, "Пустые поля", Toast.LENGTH_SHORT).show();
    }
    private void createWatcherInBD() {
        name = ETRName.getText().toString();
        phoneStr = ETRPhone.getText().toString();
        idWatcher = null;
        name.trim();
        phoneStr.trim();
        repeatIdGroup = 0;
        if (!name.isEmpty() && !phoneStr.isEmpty()){

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Group group = ds.getValue(Group.class);
                        assert group != null;
                        if(group.getIdGroup().equals(phoneStr)){
                            repeatIdGroup = 1;
                        }
                    }
                    if(repeatIdGroup == 0){
                        registrCW();
                    }else Toast.makeText(LoginActivity.this, "Номер занят", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            GroupDataBase.addListenerForSingleValueEvent(valueEventListener);
        }else{
            Toast.makeText(LoginActivity.this, "Пустые поля", Toast.LENGTH_SHORT).show();
        }
    }

    public void registrCW() {
        String log = ETRLogin.getText().toString();
        String pass = ETRPassword.getText().toString();
        log.trim();
        pass.trim();
        if(!pass.isEmpty() && !log.isEmpty()){
            mAuth.createUserWithEmailAndPassword(log, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        HomeActivity.checkreplaceDay = "-";
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            idWatcher = currentUser.getUid();
                        }

                        Watcher watcher = new Watcher(name, idWatcher, phoneStr, Constant.WATCHER_ROLE);
                        WatcherDataBase.push().setValue(watcher);
                        createGruop(idWatcher, phoneStr);

                        FirebaseUser currentUser2 = mAuth.getCurrentUser();
                        HomeActivity.UserID =  currentUser2.getUid();
                        ThreadCheckStatus.UserIDforThread2 = currentUser2.getUid();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "Готово", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "Логин занят или написан некорректно", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else Toast.makeText(this, "Пустые поля", Toast.LENGTH_SHORT).show();
    }



    public void checkGroupId(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Group group = ds.getValue(Group.class);
                    assert group != null;
                    if(group.getIdGroup().equals(idGroupStr)){
                        groupexists = 1;
                    }
                }
                if(groupexists == 1) {
                    registrCPOD();
                }else{
                    Toast.makeText(LoginActivity.this, "Такой группы не существует", Toast.LENGTH_SHORT).show();
                }
                repeatNameInGroup=0;
                groupexists = 0;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        GroupDataBase.addListenerForSingleValueEvent(valueEventListener);
    }



    private void createGruop(String idWatcher, String idGroup) {
        Group group = new Group(idWatcher, idGroup, "-");
        GroupDataBase.push().setValue(group);
    }

    private void showWelcome() {
        TVWelcome.setVisibility(View.VISIBLE);
        ETWelcomeLogin.setVisibility(View.VISIBLE);
        ETWelcomePassword.setVisibility(View.VISIBLE);
        BWOpen.setVisibility(View.VISIBLE);
        BWRegistr.setVisibility(View.VISIBLE);
        TVRegistr.setVisibility(View.GONE);
        ETRLogin.setVisibility(View.GONE);
        ETRName.setVisibility(View.GONE);
        ETRPhone.setVisibility(View.GONE);
        ETRPassword.setVisibility(View.GONE);
        BRPersonOD.setVisibility(View.GONE);
        BRWatcher.setVisibility(View.GONE);
        BRRegistr.setVisibility(View.GONE);
        ETRidGroup.setVisibility(View.GONE);
        BRBack.setVisibility(View.GONE);
    }
    public void showRegistr(View view) {
        TVWelcome.setVisibility(View.GONE);
        ETWelcomeLogin.setVisibility(View.GONE);
        ETWelcomePassword.setVisibility(View.GONE);
        BWOpen.setVisibility(View.GONE);
        BWRegistr.setVisibility(View.GONE);
        ETRidGroup.setVisibility(View.GONE);
        TVRegistr.setVisibility(View.VISIBLE);
        ETRLogin.setVisibility(View.VISIBLE);
        ETRName.setVisibility(View.VISIBLE);
        ETRPhone.setVisibility(View.VISIBLE);
        ETRPassword.setVisibility(View.VISIBLE);
        BRPersonOD.setVisibility(View.VISIBLE);
        BRWatcher.setVisibility(View.VISIBLE);
        BRRegistr.setVisibility(View.VISIBLE);
        BRBack.setVisibility(View.VISIBLE);

    }
    public void showWelcomeWithView(View view) {
        TVWelcome.setVisibility(View.VISIBLE);
        ETWelcomeLogin.setVisibility(View.VISIBLE);
        ETWelcomePassword.setVisibility(View.VISIBLE);
        BWOpen.setVisibility(View.VISIBLE);
        BWRegistr.setVisibility(View.VISIBLE);
        TVRegistr.setVisibility(View.GONE);
        ETRLogin.setVisibility(View.GONE);
        ETRName.setVisibility(View.GONE);
        ETRPhone.setVisibility(View.GONE);
        ETRPassword.setVisibility(View.GONE);
        BRPersonOD.setVisibility(View.GONE);
        BRWatcher.setVisibility(View.GONE);
        BRRegistr.setVisibility(View.GONE);
        ETRidGroup.setVisibility(View.GONE);
        BRBack.setVisibility(View.GONE);

    }


    private void init(){

        ETWelcomeLogin = (EditText) findViewById(R.id.ETWelcomeLogin);
        ETWelcomePassword = (EditText) findViewById(R.id.ETWelcomePassword);
        ETRName = (EditText) findViewById(R.id.ETRName);
        ETRPhone = (EditText) findViewById(R.id.ETRPhone);
        ETRPassword = (EditText) findViewById(R.id.ETRPassword);
        ETRLogin = (EditText) findViewById(R.id.ETRLogin);
        ETRidGroup = (EditText) findViewById(R.id.ETRidGroup);

        BWOpen = (Button) findViewById(R.id.BWOpen);
        BWRegistr = (Button) findViewById(R.id.BWRegistr);
        BRPersonOD = (Button) findViewById(R.id.BRPersonOD);
        BRWatcher = (Button) findViewById(R.id.BRWatcher);
        BRRegistr = (Button) findViewById(R.id.BRRegistr);
        BRBack = (Button) findViewById(R.id.BRBack);

        TVWelcome = (TextView) findViewById(R.id.TVWelcome);
        TVRegistr = (TextView) findViewById(R.id.TVRegistr);

        mAuth = FirebaseAuth.getInstance();
        PersonOnDutyDataBase = FirebaseDatabase.getInstance().getReference(Constant.PERSON_ON_DUTY_KEY);
        WatcherDataBase = FirebaseDatabase.getInstance().getReference(Constant.WATCHER_KEY);
        StatusDataBase = FirebaseDatabase.getInstance().getReference(Constant.STATUS_KEY);
        GroupDataBase = FirebaseDatabase.getInstance().getReference(Constant.GROUP_KEY);
    }
    public void createWatcher(View view) {
        ETRidGroup.setVisibility(View.GONE);
        isWatcher = 1;
    }
    public void createPersonOD(View view) {
        ETRidGroup.setVisibility(View.VISIBLE);
        isWatcher = -1;
    }
}
