package com.example.ravi.hiltonadmin;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    private ViewPager vImaageAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vImaageAnimation= (ViewPager)findViewById(R.id.vImageAnimation);
    }


    public void signout(View view)
    {

        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        finish(); //to destroy this activity

    }
}
