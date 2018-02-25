package com.example.ravi.hiltonadmin;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfo extends AppCompatActivity {


    private final String PHONE="Phone";
    private final String USERNAME="UserName";
    private final String EMAIL="Email";
    private Button bSignIn;
    private EditText eUsername;
    private EditText eEmail;
    private FirebaseDatabase database;
    private DatabaseReference UserdataRef;
    private static final String TAG="PhoneAuthActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        eUsername=(EditText)findViewById(R.id.eUsername);
        eEmail=(EditText)findViewById(R.id.eEmail);
        bSignIn=(Button)findViewById(R.id.bSignin);
        database=FirebaseDatabase.getInstance();
        UserdataRef=database.getReference("UserData");



    }

    public void SignIn(View view)
    {
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        String String_UserId=extras.getString("UserId");
        String String_Phone=extras.getString("PhoneNumber");
        String String_Username=eUsername.getText().toString().trim();
        String String_Email=eEmail.getText().toString().trim();

        if(TextUtils.isEmpty(String_Username))
        {
            eUsername.setError("Username cannot be Empty");
        }

        else if(TextUtils.isEmpty(String_Email))
        {
            eEmail.setError("Email cannot be Empty");
        }

        else
        {

            UserdataRef.child(String_UserId).child(USERNAME).setValue(String_Username);
            UserdataRef.child(String_UserId).child(EMAIL).setValue(String_Email);
            UserdataRef.child(String_UserId).child(PHONE).setValue(String_Phone);

            Intent i=new Intent(this,Home.class);
            startActivity(i);//starting home activity
            finish();



        }
    }

}
