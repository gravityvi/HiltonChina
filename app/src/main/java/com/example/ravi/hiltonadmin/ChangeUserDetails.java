package com.example.ravi.hiltonadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeUserDetails extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private EditText eUsername;
    private EditText eEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_details);
        databaseReference = FirebaseDatabase.getInstance().getReference("UserData").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        eUsername = findViewById(R.id.eUsername_Settings);
        eEmail = findViewById(R.id.eEmail_Settings);
    }


    public void change(View view)
    {
        if(eEmail.getText().toString().trim().isEmpty())
        {
            eEmail.setError("Cannot be empty");
            return;
        }
        if(eUsername.getText().toString().trim().isEmpty())
        {
            eUsername.setError("cannot be Empty");
        }

        databaseReference.child("UserName").setValue(eUsername.getText().toString());
        databaseReference.child("Email").setValue(eEmail.getText().toString());
        Toast.makeText(this,"Username and Email updated",Toast.LENGTH_LONG);
        finish();

    }
}
