package com.example.ravi.hiltonadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int Count=0;
    private int amount;
    private DatabaseReference ItemData;
    private OnFragmentInteractionListener mListener;
    private Button bCheckout;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private  ArrayList<Items> CartItems;
    private static final String TAG="PhoneAuthActivity";
    private CartListAdapter cartListAdapter;
    private ValueEventListener value;
    private ValueEventListener value1;
    private static TextView  tTotalCost;
    private static int checkoutSum=0;
    private static int totalItems=0;







    public CartFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_cart, container, false);
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Cooking your stuff");
        progressDialog.setCancelable(false);
        progressDialog.show();

        tTotalCost=(TextView)view.findViewById(R.id.tTotalCost);
        recyclerView=view.findViewById(R.id.rCartView);
        bCheckout = view.findViewById(R.id.bChekout);
        CartItems=new ArrayList<>();//creating Arraylist of type Items to add Cart Items in database into this array
        CartItems.clear();


        bCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserData/"+userId+"/Coupons");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int couponAmount;
                        couponAmount=Integer.parseInt(dataSnapshot.getValue(String.class));
                        Intent i =new Intent(getContext(),MerchantActivity.class);
                        amount = Math.max(0,amount-couponAmount);
                        couponAmount = 0;


                        if(amount==0)
                        {
                            Toast.makeText(getContext(),"Cart is Empty!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            i.putExtra("amount",amount);
                            i.putExtra("CartItems",CartItems);
                            startActivity(i);
                            getActivity().onBackPressed();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        /*****Reading cart items and setting up recycler view ***/

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();//getting the current User
        databaseReference= FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid());//going in User profile to read Cart data
        checkoutSum=0;
        totalItems=0;
        value1=new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final long NumberOfItems=dataSnapshot.child("Cart").child("Items").getChildrenCount();
                if(NumberOfItems==0)
                {
                    progressDialog.cancel();
                    tTotalCost.setText("Total Cost("+totalItems+" Items) : "+checkoutSum );

                }
                for(DataSnapshot data: (dataSnapshot.child("Cart").child("Items").getChildren()))
                {
                    final String ItemId= data.getKey();//getting Item Key
                    final String ItemCategory=data.child("ItemCategory").getValue(String.class);//getting Item Category
                    final String ItemNumber=data.child("ItemNumber").getValue(String.class);//getting ItemNumber or number of order




                    System.out.println(ItemCategory+" "+ItemId);
                    ItemData=FirebaseDatabase.getInstance().getReference("ItemData").child(ItemCategory).child(ItemId);//go`

                    value=new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String ItemName=dataSnapshot.child("Name").getValue(String.class);//getting ItemName
                            final String ItemDescription=dataSnapshot.child("Desc").getValue(String.class);//getting ItemDescription
                            final String ItemPrice=dataSnapshot.child("Price").getValue(String.class);//getting ItemPrice
                            final boolean avail = dataSnapshot.child("Avail").getValue(boolean.class);


                            //getting Image File From url of Database--
                            final String ImageUrl= dataSnapshot.child("Image").getValue(String.class);



                                    Count++;
                                    CartItems.add(new Items(ItemId,ItemName,ItemCategory,ItemNumber,ItemDescription,ItemPrice,ImageUrl,avail));
                                    totalItems+=Integer.parseInt(ItemNumber);
                                    checkoutSum+=Integer.parseInt(ItemNumber)*Integer.parseInt(ItemPrice);
                                    if(Count==NumberOfItems)//Things to be Performed only once and at last

                                    {
                                        cartListAdapter=new CartListAdapter(getContext(),CartItems);
                                        recyclerView.setAdapter(cartListAdapter);
                                        FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart").child("CheckoutSum").setValue(Integer.toString(checkoutSum));
                                        FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart").child("TotalItems").setValue(Integer.toString(totalItems));
                                        Count=0;
                                        progressDialog.cancel();
                                    }






                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    ItemData.addListenerForSingleValueEvent(value);

                    /*****************************************************************************/







                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(value1);


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart");
                reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("TotalItems"))
                    {
                        int totalItems=Integer.parseInt(dataSnapshot.child("TotalItems").getValue(String.class));
                        int checkoutSum=Integer.parseInt(dataSnapshot.child("CheckoutSum").getValue(String.class));
                        amount = checkoutSum;
                        tTotalCost.setText("Total Cost("+totalItems+" Items) : "+checkoutSum );
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //on attach called
            Log.d(TAG,"Cart Fragment on Attach called ");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG,"Cart Fragment on Detach called");




    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
