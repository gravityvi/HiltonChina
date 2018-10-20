package com.example.ravi.hiltonadmin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places.*;
import com.google.android.gms.location.places.Places;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;
import java.util.ArrayList;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;



public class MerchantActivity extends AppCompatActivity implements PaymentResultListener,OnConnectionFailedListener  {
    private int amount;
    private String userId;
    private String orderId;
    private AutoCompleteTextView eAddress;
    private ArrayList<Items> CartItems;
    private TextView tAmount;
    private GoogleApiClient googleApiClient;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private final String TAG = "merchantActivity";
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;






    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        Intent i = getIntent();
        amount = i.getIntExtra("amount",0);
        CartItems = i.getParcelableArrayListExtra("CartItems");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        tAmount = (TextView) findViewById(R.id.tAmount);
        eAddress = findViewById(R.id.eAddress);
        Checkout.preload(getApplicationContext());
        tAmount.setText("Amount: "+amount);

        // Construct a GeoDataClient.
        geoDataClient = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        placeDetectionClient = Places.getPlaceDetectionClient(this);

        /****connecting to google api services****/
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        /**** creating adapter for auto complete texr view and setting adapter to text view***/
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this,geoDataClient,new LatLngBounds(new LatLng(22,73),new LatLng(23,74)),null);
        eAddress.setAdapter(placeAutocompleteAdapter);


    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this,"String "+s,Toast.LENGTH_LONG);
        pushItems(amount,"cardPayment");
        ClearCart();
    }

    @Override
    public void onPaymentError(int i, String s) {



    }

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.ravi);

        /**
         * Reference to current activity
         */
        final Activity activity = MerchantActivity.this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: Rentomojo || HasGeek etc.
             */
            options.put("name", "Hilton China");

            /**
             * Description can be anything
             * eg: Order #123123
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Order :"+orderId);

            options.put("currency", "INR");

            /**
             * Amount is always passed in PAISE
             */
            amount=amount*100; //converting in rupees
            options.put("amount", Integer.toString(amount));
            checkout.open(activity, options);
        } catch(Exception e) {
            System.out.println("RazorPay error :"+e.toString());
        }
    }

    public void cardPayment(View view)
    {
        if(eAddress.getText().toString().trim().equals(""))
        {
            eAddress.setError("Input required.");
        }
        else
        {
            generateOrderId();
            startPayment();
        }

    }

    public void COD(View view)
    {
        if(eAddress.getText().toString().trim().equals(""))
        {
            eAddress.setError("Input required.");
        }
        else
        {
            generateOrderId();
            pushItems(0,"COD");
            ClearCart();
            finish();
        }

    }

    public void generateOrderId()
    {
        orderId = FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Orders").push().getKey();

    }

    public void pushItems(int paid, String paymentType)
    {
        //pushing Items to personal user data
        String token=FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Orders/"+orderId).child("Amount").setValue(Integer.toString(amount));
        FirebaseDatabase.getInstance().getReference("UserData/"+userId).child("FirebaseToken").setValue(token);
        FirebaseDatabase.getInstance().getReference(("UserData/"+userId+"/Orders/"+orderId)).child("Paid").setValue(Integer.toString(paid));
        FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Orders/"+orderId).child("Progress").setValue("InProcess"); // InProcess //Accepted //Delivered //Dispute
        FirebaseDatabase.getInstance().getReference("UserData/"+userId).child("Address").setValue(eAddress.getText().toString()); //address field.
        FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Orders/"+orderId).child("PaymentType").setValue(paymentType); //COD //cardPayment
        //pushing cart Items
        for(int i=0 ;i <CartItems.size();i++)
        {

            FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Orders/"+orderId).child("Items").child(CartItems.get(i).getItemId()).child("ItemNumber").setValue(CartItems.get(i).getItemNumber());
            FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Orders/"+orderId).child("Items").child(CartItems.get(i).getItemId()).child("ItemCategory").setValue(CartItems.get(i).getItemCategory());
            FirebaseDatabase.getInstance().getReference("Orders/"+orderId).child("Items").child(CartItems.get(i).getItemId()).child("ItemNumber").setValue(CartItems.get(i).getItemNumber());
            FirebaseDatabase.getInstance().getReference("Orders/"+orderId).child("Items").child(CartItems.get(i).getItemId()).child("ItemCategory").setValue(CartItems.get(i).getItemCategory());


        }

        //pushing Items to merchant data
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("UserId").setValue(userId);
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("paymentType").setValue(paymentType);
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("Progress").setValue("InProcess");
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("Amount").setValue(amount);
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("Paid").setValue(Integer.toString(paid));

        //clearing Coupons money
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Coupons");
        int couponAmount = 0;
        databaseReference.setValue(Integer.toString(amount));



    }

    public void ClearCart()
    {
        FirebaseDatabase.getInstance().getReference("UserData/"+userId).child("Cart").removeValue();
    }

    public void currLocation(View view)
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1
                        );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            getCurrentLocation();

        }
    }

    public void getCurrentLocation()
    {


        Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
               try {
                   int count=0;
                   String address="";
                       PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                       for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                           if(count==0)
                           {
                               address = placeLikelihood.getPlace().getName().toString();
                               eAddress.setText(address);
                               count++;
                           }
                           Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                                   placeLikelihood.getPlace().getName(),
                                   placeLikelihood.getLikelihood()));
                       }
                       likelyPlaces.release();


               }catch (Exception e)
               {
                   Log.d(TAG,"Exception msg "+e.toString());
                   Toast.makeText(MerchantActivity.this,"Sorry!, cannot get exact address.",Toast.LENGTH_LONG).show();
               }


            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getCurrentLocation(); //getting current lcoatio
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}





