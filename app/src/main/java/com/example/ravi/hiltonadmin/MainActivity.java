package com.example.ravi.hiltonadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.nfc.Tag;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.EntityReference;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {



    private FirebaseDatabase database;
    private DatabaseReference myRef;

    
    private Button bVerify;
    private Button bResend;
    private EditText eVerifyCode;
    private TextView tRegisterStatus;
    private EditText ephone;
    private String mverificationid;
    private Button bsignup;
    private PhoneAuthProvider.ForceResendingToken mresendtoken;
    private boolean mverificationinprogress=false; //phone verification in progress.
    private FirebaseAuth firebaseAuth;
    private static final String KEY_VERIFY_IN_PROGRESS="key_verify_in_progress";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallsbacks;
    private static final String TAG="PhoneAuthActivity";


    private static final int STATE_INITIALIZED =1;
    private static final int STATE_CODE_SENT=2;
    private static final int STATE_VERIFY_FAILED =3;
    private static final int STATE_VERIFY_SUCCESS =4;
    private static final int STATE_SIGNIN_FAILED =5;
    private static final int STATE_SIGNIN_SUCCESS =6;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Restore instance state
        if(savedInstanceState!=null)
        {
            onRestoreInstanceState(savedInstanceState);

        }


        database =FirebaseDatabase.getInstance();
        myRef = database.getReference("UserData");
         bResend=(Button)findViewById(R.id.bResend);
        bVerify=(Button)findViewById(R.id.bVerify);
        eVerifyCode=(EditText)findViewById(R.id.eVerifycode);
        ephone=(EditText)findViewById(R.id.ephone);

        bsignup=(Button) findViewById(R.id.bsignup);


        firebaseAuth= FirebaseAuth.getInstance();
        tRegisterStatus=(TextView)findViewById(R.id.tRegisterStatus);






        mcallsbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                //this callback will invoke in two sotuations:
                //1- Instant verification. In some cases the phone number can instantly verified without needing to send or enter a verfication code
                //2- Auto retrieval

                //user action
                Log.d(TAG,"onVerificationCompleted:"+phoneAuthCredential);
                mverificationinprogress=false;
                UpdateUI(STATE_VERIFY_SUCCESS,phoneAuthCredential);

                SignInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                //this callback is invoked in a invalid request for verification is made
                //for instance if the phone number format is wrong.
                Log.w(TAG,"onVerificationfailed: ",e);
                mverificationinprogress=false;



                if(e instanceof FirebaseAuthInvalidCredentialsException)
                {
                    //ephone.setError("Invalid Phone number.");
                    UpdateUI(STATE_VERIFY_FAILED);
                    ephone.setError("Invalid phone number");
                }

                else if(e instanceof FirebaseTooManyRequestsException)
                {
                    // The sms quota for the project has beeen exceeded
                    tRegisterStatus.setText("sms quota for the project has beeen exceeded");
                    //

                }
                else
                {
                    tRegisterStatus.setText("oops! , Please Try Again");
                    UpdateUI(STATE_VERIFY_FAILED);
                }
            }


            @Override
            public void onCodeSent(String verificationid, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationid, forceResendingToken);
                //the sms verification code has been sent to the provided phone number, we
                //now need to ask the user to enter the code and then construct a credential
                //by combinig the code with a verification id
                Log.d(TAG,"onCodeSent: "+verificationid);

                //save verification id and resending token so we can use them later
                mverificationid=verificationid;
                mresendtoken=forceResendingToken;

                UpdateUI(STATE_CODE_SENT);
            }
        };


    }


    @Override
    protected void onStart() {
        super.onStart();
        //check if user is signed in (non - null update UI accordingly)
        FirebaseUser CurrentUser=firebaseAuth.getCurrentUser();

        UpdateUI(CurrentUser);

        //

        if(mverificationinprogress && validatephonenumber() )
        {

            startphonenumberverification(ephone.getText().toString().trim());
        }



    }



    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS,mverificationinprogress);
    }





    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mverificationinprogress=savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }




    public void startphonenumberverification(String phonenumber)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber,120, TimeUnit.SECONDS,this,mcallsbacks);
        mverificationinprogress=true;
    }



    private void verifyPhoneNumberWithCode(String verificationid,String code)
    {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationid,code);
        SignInWithPhoneAuthCredential(credential);
    }

    private void resendVerifiacationCode(String PhoneNumber,PhoneAuthProvider.ForceResendingToken token)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(PhoneNumber,60,TimeUnit.SECONDS,this,mcallsbacks,token);
    }



    public void SignInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //Sign in success, update UI with the signed-in user's information
                    Log.d(TAG,"signInWithCredential:Success");

                    FirebaseUser user= task.getResult().getUser();



                    UpdateUI(STATE_SIGNIN_SUCCESS,user);
                    //UpdateUi(STATE_SIGNIN_SUCCESS,credentials);
                }

                else
                {
                    //Sign In failed display a message and update UI
                    Log.w(TAG,"signInWithCredential:failure",task.getException());
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        //the verification code entered was invalid

                        eVerifyCode.setError("Invalid Code");

                        //Update UI

                        UpdateUI((STATE_SIGNIN_FAILED));
                    }
                }
            }
        });
    }








    public boolean validatephonenumber()
   {
       String phoneNumber = ephone.getText().toString().trim();

       if(TextUtils.isEmpty(ephone.getText()))
       {
           //ephone.setError("Invalid phone number");
           return false;

       }
       else
           return true;
   }







    private void UpdateUI(int uistate)
    {
        UpdateUI(uistate,firebaseAuth.getCurrentUser(),null);
    }

    private void UpdateUI(FirebaseUser user)
    {
        if(user!=null)
            UpdateUI(STATE_SIGNIN_SUCCESS,user);
        else
        {
            UpdateUI(STATE_INITIALIZED);
        }
    }


    private void UpdateUI(int uistate,FirebaseUser user)
    {
        UpdateUI(uistate,user,null);
    }

    private void UpdateUI(int uistate, PhoneAuthCredential cred)
    {
        UpdateUI(uistate,null,cred);
    }


    private void UpdateUI(int uistate, final FirebaseUser user, PhoneAuthCredential cred)
    {
        switch (uistate){

            case STATE_INITIALIZED:
                //Intialized state, show only the phone number field and the start button
                //enable views
                //disable views
                enableViews(bsignup,ephone);
                disableViews(bVerify,bResend,eVerifyCode);


                //Intent i=new Intent(MainActivity.this,);

                break;
            case STATE_CODE_SENT:
                //code sent state , show only the verification field,
                //setContentView(R.layout.code_verify);
                tRegisterStatus.setText("Code has been sent please enter the received code under the space provided\n In Case of you have entered wrong phone number change it and press resend");
                enableViews(bVerify,bResend,ephone,eVerifyCode);
                disableViews(bsignup);


                break;

            case STATE_VERIFY_FAILED:
                //Code sent state, show the verifiacation field
                enableViews(ephone,bsignup);
                disableViews(eVerifyCode,bVerify,bResend);
                setContentView(R.layout.activity_main);


                break;

            case STATE_VERIFY_SUCCESS:
                //verification has suceeded, proceed to firebase sign in
                disableViews(eVerifyCode,ephone,bResend,bVerify,bsignup);
                if(cred!=null) {
                    if (cred.getSmsCode() != null) {
                        //codefield set text to sms code
                        eVerifyCode.setText(cred.getSmsCode());


                    } else {
                        // set the code to instan verification
                        //

                    }
                }


                break;

            case STATE_SIGNIN_FAILED:


                UpdateUI(STATE_INITIALIZED);
                break;

            case STATE_SIGNIN_SUCCESS:

                Log.d(TAG,"state signin success");
                disableViews(ephone,bVerify,bsignup,eVerifyCode,bResend);
                Query UserIdPresent=myRef.orderByKey().equalTo(user.getUid());
                UserIdPresent.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists())
                        {
                            Log.d(TAG,"UserId does not exists");
                            EnterUserInfo(user);
                        }

                        else
                        {
                            Log.d(TAG,"UserId exist");
                            EnterHome();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                break;
        }


    }




    private void enableViews(View... views )
    {
        for(View v : views)
        {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views )
    {
        for(View v : views)
        {
            v.setEnabled(false);
        }
    }


    public  void lsignout()
    {
        firebaseAuth.signOut();
        UpdateUI(STATE_INITIALIZED);
    }



    public void signup(View view)
    {


        if(!validatephonenumber())
        {
            ephone.setError("cannot be empty");
        }
        else
        {

            startphonenumberverification(ephone.getText().toString().trim());
        }
    }


    public void Verify(View view)
    {
        String code=eVerifyCode.getText().toString();
        if(TextUtils.isEmpty(code))
        {
            eVerifyCode.setError("Cannot be Empty");
        }
        else
        {
            verifyPhoneNumberWithCode(mverificationid,code);
        }
    }

    public void Resend(View view)
    {
        resendVerifiacationCode(ephone.getText().toString(),mresendtoken);

    }

    public void EnterUserInfo(FirebaseUser user)
    {
        Log.d(TAG,"Entering UserInfo");
        Intent i=new Intent(this,UserInfo.class);
        Bundle extras=new Bundle();
        extras.putString("PhoneNumber",user.getPhoneNumber());
        extras.putString("UserId",user.getUid());
        i.putExtras(extras);
        startActivity(i);
        finish();//to destroy the this activity
    }


    public void EnterHome()
    {
        Log.d(TAG,"Entering Home");
        Intent i=new Intent(this,Home.class);
        startActivity(i);
        finish(); //to destroy this activity

    }






















}


