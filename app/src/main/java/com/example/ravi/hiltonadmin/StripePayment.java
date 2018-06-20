package com.example.ravi.hiltonadmin;

import android.app.VoiceInteractor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.util.HashMap;
import java.util.Map;

public class StripePayment extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);
        final CardInputWidget cardInputWidget = findViewById(R.id.cardInputwidget);
        Button bpay = findViewById(R.id.bPay);
        final TextView textView = findViewById(R.id.tText);
        final WebView webView = findViewById(R.id.webView);
        final int amount = getIntent().getIntExtra("amount",0);
        bpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Card cardToSave = cardInputWidget.getCard();
                if(cardToSave == null)
                {

                    Toast.makeText(StripePayment.this,"Fill in all the card details",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Stripe stripe = new Stripe(getApplicationContext(),"pk_test_NEFk4ehkEwgTSO2wBClYsXk4");
                    stripe.createToken(cardToSave, new TokenCallback() {
                        @Override
                        public void onError(Exception error) {
                            Toast.makeText(StripePayment.this,error.toString(),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(final Token token) {
                            System.out.println("helooooooooo " + token);

                            RequestQueue queue = Volley.newRequestQueue(StripePayment.this);
                            String url = " http://0.0.0.0:5000";

                            //Request a string response from provided url
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    textView.setText("Done");
                                    webView.getSettings().setJavaScriptEnabled(true);
                                    webView.loadDataWithBaseURL("", response, "text/html", "UTF-8", "");

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(StripePayment.this, "oops! error", Toast.LENGTH_LONG);
                                }
                            })
                            {

                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {

                                    Map<String,String> params = new HashMap<String,String>();
                                    params.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                    params.put("amount",Integer.toString(amount));
                                    params.put("token",token.toString());
                                    return params;
                                }


                            };


                            queue.add(stringRequest);



                        }
                    });
                }
            }
        });

    }
}
