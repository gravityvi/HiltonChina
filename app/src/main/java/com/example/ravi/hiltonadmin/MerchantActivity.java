package com.example.ravi.hiltonadmin;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class MerchantActivity extends AppCompatActivity implements PaymentResultListener {
    private int amount;
    private String userId;
    private String orderId;
    private TextInputEditText eAddress;
    private ArrayList<Items> CartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        Intent i = getIntent();
        amount = i.getIntExtra("amount",0);
        CartItems = i.getParcelableArrayListExtra("CartItems");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        eAddress = findViewById(R.id.eAddress);
        Checkout.preload(getApplicationContext());


    }

    @Override
    public void onPaymentSuccess(String s) {
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
        generateOrderId();
        startPayment();
    }

    public void COD(View view)
    {
        generateOrderId();
        pushItems(0,"COD");
        ClearCart();
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
            FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Orders/"+orderId).child("Items").child("ItemId").setValue(CartItems.get(i).getItemId());
            FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Orders/"+orderId).child("Items").child("ItemNumber").setValue(CartItems.get(i).getItemNumber());
        }

        //pushing Items to merchant data
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("UserId").setValue(userId);
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("paymentType").setValue(paymentType);
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("Progress").setValue("InProcess");
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("Amount").setValue(amount);
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).child("Paid").setValue(Integer.toString(paid));
        //pushing cart Items
        for(int i=0 ;i <CartItems.size();i++)
        {
            FirebaseDatabase.getInstance().getReference("Orders/"+orderId).child("Items").child("ItemId").setValue(CartItems.get(i).getItemId());
            FirebaseDatabase.getInstance().getReference("Orders/"+orderId).child("Items").child("ItemNumber").setValue(CartItems.get(i).getItemNumber());
        }

    }

    public void ClearCart()
    {
        FirebaseDatabase.getInstance().getReference("UserData/"+userId).child("Cart").removeValue();
    }



}





