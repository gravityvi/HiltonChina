package com.example.ravi.hiltonadmin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private DatabaseReference ItemData;
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ArrayList<Items> CartItems;
    private static final String TAG="PhoneAuthActivity";
    private CartListAdapter cartListAdapter;
    private ValueEventListener value;
    private ValueEventListener value1;


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

        recyclerView=view.findViewById(R.id.rCartView);
        CartItems=new ArrayList<>();//creating Arraylist of type Items to add Cart Items in database into this array
        cartListAdapter=new CartListAdapter(getContext(),CartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();//getting the current User
        databaseReference= FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid());//going in User profile to read Cart data
        value1=new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final long NumberOfItems=dataSnapshot.getChildrenCount();
                for(DataSnapshot data: dataSnapshot.child("Cart").getChildren())
                {
                    final String ItemId= data.getKey();//getting Item Key
                    final String ItemCategory=data.child("ItemCategory").getValue(String.class);//getting Item Category
                    final String ItemNumber=data.child("ItemNumber").getValue(String.class);//getting ItemNumber or number of order





                    ItemData=FirebaseDatabase.getInstance().getReference("ItemData").child(ItemCategory).child(ItemId);//going directly to Item to retrive ItemData

                    value=new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String ItemName=dataSnapshot.child("Name").getValue(String.class);//getting ItemName
                            final String ItemDescription=dataSnapshot.child("Desc").getValue(String.class);//getting ItemDescription
                            final String ItemPrice=dataSnapshot.child("Price").getValue(String.class);//getting ItemPrice


                            //getting Image File From url of Database--
                            String ImageUrl= dataSnapshot.child("Image").getValue(String.class);
                            StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(ImageUrl);
                            storageReference.getBytes(1024*1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Count++;
                                    CartItems.add(new Items(ItemId,bytes,ItemName,ItemCategory,ItemNumber,ItemDescription,ItemPrice));

                                    if(Count==NumberOfItems)//Things to be Performed only once
                                        recyclerView.setAdapter(cartListAdapter);




                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    ItemData.addValueEventListener(value);







                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(value1);



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

        //removing value event listner so that they does not create problem
        ItemData.removeEventListener(value);
        databaseReference.removeEventListener(value1);

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
