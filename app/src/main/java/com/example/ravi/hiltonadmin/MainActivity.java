package com.example.ravi.hiltonadmin;

import android.app.DownloadManager;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

    private int everify_status=0;
    private int ephone_status=0;
    private Button bVerify;
    private TextView tResend;
    private EditText eVerifyCode;
    private TextView tRegisterStatus;
    private EditText ephone;
    private String mverificationid;
    private ValueEventListener valueEventListener;
    private Button bsignup;
    private PhoneAuthProvider.ForceResendingToken mresendtoken; //used when to resend the code on the phonenumber
    private boolean mverificationinprogress=false; //phone verification in progress.
    private FirebaseAuth firebaseAuth;
    private Query UserIdPresent;
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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.sign_in);



        //Restore instance state if the user is already logged in
        if(savedInstanceState!=null)
        {
            onRestoreInstanceState(savedInstanceState);

        }


        database =FirebaseDatabase.getInstance();
        myRef = database.getReference("UserData");
         tResend=(TextView) findViewById(R.id.tResend);
        bVerify=(Button)findViewById(R.id.bVerify);
        eVerifyCode=(EditText)findViewById(R.id.eVerifycode);
        ephone=(EditText)findViewById(R.id.ephone);

        bsignup=(Button) findViewById(R.id.bsignup);


        firebaseAuth= FirebaseAuth.getInstance();
        tRegisterStatus=(TextView)findViewById(R.id.tRegisterStatus);

        ephone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                activeEphone();
                if(everify_status==1)
                    enableEverify();

                return false;
            }
        });


        eVerifyCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                activeEverify();
                if(ephone_status==1)
                    enableEphone();

                return false;
            }
        });




        mcallsbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                //this callback will invoke in two sotuations:
                //1- Instant verification. In some cases the phone number can instantly verified without needing to send or enter a verfication code
                //2- Auto retrieval

                //user action
                Log.d(TAG,"onVerificationCompleted:"+phoneAuthCredential);
                mverificationinprogress=false;
                UpdateUI(STATE_VERIFY_SUCCESS,phoneAuthCredential);  //Verified and User is provided in the PhoneAuth credentials

                SignInWithPhoneAuthCredential(phoneAuthCredential);//after verification sign in the user
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
                    tRegisterStatus.setText("oops! , Please Try Again "+e.toString());
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

        Log.d(TAG,"on Start Called");
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

        Log.d(TAG,"on Save Instance Called");

        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS,mverificationinprogress);
    }





    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        Log.d(TAG,"onRestore Instance Called");
        super.onRestoreInstanceState(savedInstanceState);
        mverificationinprogress=savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }




    public void startphonenumberverification(String phonenumber)
    {
        Log.d(TAG,"No Current User, starting phone Verification");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber,120, TimeUnit.SECONDS,this,mcallsbacks);
        mverificationinprogress=true;
    }



    private void verifyPhoneNumberWithCode(String verificationid,String code)
    {
        Log.d(TAG,"Code entered verifying code started");

        //code has been entered by user check for the code if it is correct
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationid,code);
        SignInWithPhoneAuthCredential(credential);
    }

    private void resendVerifiacationCode(String PhoneNumber,PhoneAuthProvider.ForceResendingToken token)
    {
        Log.d(TAG,"Resend Verification called");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(PhoneNumber,60,TimeUnit.SECONDS,this,mcallsbacks,token);
        tRegisterStatus.setText("Code has been Resend wait for few minutes to appear");
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
                Log.d(TAG,"State Initialized");
                tRegisterStatus.setText("Welcome User");
                enableViews(bsignup,ephone);
                enableEphone();

                disableViews(bVerify,tResend,eVerifyCode);
                disableEverify();
                disableResend();

                //Intent i=new Intent(MainActivity.this,);

                break;
            case STATE_CODE_SENT:
                //code sent state , show only the verification field,
                //setContentView(R.layout.code_verify);

                Log.d(TAG,"State Code Sent");

                tRegisterStatus.setText("Code has been sent please enter the received code under the space provided\n In Case of you have entered wrong phone number change it and press resend");
                enableViews(bVerify,tResend,ephone,eVerifyCode);
                enableEphone();
                enableEverify();
                enableResend();
                disableViews(bsignup);




                break;

            case STATE_VERIFY_FAILED:
                Log.d(TAG,"State Verify failed");

                //verification failed
                //tRegisterStatus.setText("oops something went wrong");
                enableViews(ephone,bsignup);
                enableEphone();
                disableViews(eVerifyCode,bVerify,tResend);
                disableResend();
                disableEverify();



                break;

            case STATE_VERIFY_SUCCESS:

                Log.d(TAG,"State Verify Success");

                tRegisterStatus.setText("Verification Complete");
                //verification has suceeded, proceed to firebase sign in
                disableViews(eVerifyCode,ephone,tResend,bVerify,bsignup);
                disableEverify();
                disableResend();
                disableEphone();
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
                Log.d(TAG,"State SignIn Failed");



                UpdateUI(STATE_INITIALIZED);
                tRegisterStatus.setText(" Retry SignIn Process");
                break;

            case STATE_SIGNIN_SUCCESS:

                Log.d(TAG,"state signin success");

                tRegisterStatus.setText("Signed In Succesfully");
                disableViews(ephone,bVerify,bsignup,eVerifyCode,tResend);
                disableResend();
                disableEverify();
                disableEphone();
                UserIdPresent=myRef.orderByKey().equalTo(user.getUid());
                valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists())
                        {
                            Log.d(TAG,"UserId does not exists");
                            EnterUserInfo(user);
                        }

                        else
                        {
                            Log.d(TAG,"UserId exist"+dataSnapshot.getKey());
                            EnterHome();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG,"user cancelled signin process");
                        tRegisterStatus.setText("Sign In Cancelled");
                    }
                };
                UserIdPresent.addListenerForSingleValueEvent(valueEventListener);



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

    public void disableEphone()
    {
        ephone_status=0;
        ephone.setBackgroundResource(R.drawable.ephone_disabled1);
        ephone.setHint(null);
    }

    public void enableEphone()
    {
        ephone_status=1;
        ephone.setBackgroundResource(R.drawable.ephone_notactive1);
        ephone.setHint(R.string.ephone_hint);
        ephone.setHintTextColor(getResources().getColor(R.color.enon_active_hint));
    }

    public void disableEverify()
    {
        everify_status=0;
        eVerifyCode.setBackgroundResource(R.drawable.everify_disabled1);
        eVerifyCode.setHint(null);
        eVerifyCode.setHintTextColor(getResources().getColor(R.color.edisabled_hint));
    }

    public void enableEverify()
    {
        everify_status=1;
        eVerifyCode.setBackgroundResource(R.drawable.everify_notactive1);
        eVerifyCode.setHint(R.string.everify_hint);
        eVerifyCode.setHintTextColor(getResources().getColor(R.color.enon_active_hint));
    }

    public void activeEphone()
    {
        ephone.setBackgroundResource(R.drawable.ephone_active1);
        ephone.setHintTextColor(getResources().getColor(R.color.enon_active_hint));
    }

    public void activeEverify()
    {
        eVerifyCode.setBackgroundResource(R.drawable.everify_active1);
        eVerifyCode.setHintTextColor(getResources().getColor(R.color.enon_active_hint));
    }

    public void enableResend()
    {
        tResend.setTextColor(getResources().getColor(R.color.enon_active_hint));
        tResend.setText("Resend Code ?");
    }

    public void disableResend()
    {
        tResend.setText(null);
        tResend.setTextColor(getResources().getColor(R.color.edisabled_hint));
    }
}


