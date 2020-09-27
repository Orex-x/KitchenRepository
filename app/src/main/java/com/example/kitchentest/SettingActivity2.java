package com.example.kitchentest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SettingActivity2.this, LoginActivity.class);
        startActivity(intent);
        HomeActivity.UserID = null;
        HomeActivity.IDGroupUser = null;
        HomeActivity.userName = null;
        HomeActivity.stopChekWhoIsUser = 0;
        HomeActivity.statusOne = 0;
    }



    public void goToHome(View view) {
        onBackPressed();
    }
}