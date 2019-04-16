package com.example.ravi.hiltonadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    private static final String Tag = "HistoryFragment";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView rOrders;
    private View layout;
    private ArrayList<Order> orderList;
    private long TotalOrders=0;
    private long OrderCount=0;


    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        layout =  inflater.inflate(R.layout.fragment_history, container, false);
        orderList = new ArrayList<Order>();

        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Cooking your stuff");
        progressDialog.setCancelable(false);
        progressDialog.show();



        FirebaseDatabase.getInstance().getReference("UserData/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+"/Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                TotalOrders=dataSnapshot.getChildrenCount();
                for(DataSnapshot orderSnapshot : dataSnapshot.getChildren())
                {
                    final String orderId = orderSnapshot.getKey();

                    final String UserId =FirebaseAuth.getInstance().getCurrentUser().getUid().toString() ;
                    final String Paid = orderSnapshot.child("Paid").getValue(String.class);
                    final String PaymentType = orderSnapshot.child("PaymentType").getValue(String.class);
                    final String Progress = orderSnapshot.child("Progress").getValue(String.class);
                    final long TotalItems = orderSnapshot.child("Items").getChildrenCount();
                    final long countItems[] = new long[1];
                    countItems[0]=0;
                    final ArrayList<Items> itemList = new ArrayList<>();
                    Log.d(Tag,"Total Items "+TotalItems);
                    for(DataSnapshot  itemSnapshot : orderSnapshot.child("Items").getChildren())
                    {
                        final String Itemid = itemSnapshot.getKey();
                        final String itemCategory = itemSnapshot.child("ItemCategory").getValue(String.class);
                        final String itemNumber = itemSnapshot.child("ItemCategory").getValue(String.class);
                        FirebaseDatabase.getInstance().getReference("ItemData/"+itemCategory+"/"+Itemid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot Itemdata) {
                                String ItemDescription = Itemdata.child("Desc").getValue(String.class);
                                String ItemPrice = Itemdata.child("Price").getValue(String.class);
                                String ItemName = Itemdata.child("Name").getValue(String.class);
                                String ItemUrl = Itemdata.child("Image").getValue(String.class);
                                Items i = new Items(Itemid,ItemName,itemCategory,itemNumber,ItemDescription,ItemPrice,ItemUrl);
                                itemList.add(i);
                                countItems[0]++;
                                Log.d(Tag,"Item count"+countItems[0]);
                                if(countItems[0] == TotalItems)
                                {
                                    Log.d(Tag,"Item Count Total Count "+countItems[0]+" "+TotalItems);
                                    final Order o = new Order(orderId,UserId,null,null,Paid,null,PaymentType,Progress,null,itemList);
                                    orderList.add(o);
                                    countItems[0]=0;
                                    OrderCount++;


                                    if(OrderCount == TotalOrders)
                                    {
                                        //set adapter and linear layout manager
                                        rOrders = layout.findViewById(R.id.rOrders);
                                        OrdersListAdapter ordersListAdapter = new OrdersListAdapter(getContext(),orderList);
                                        rOrders.setAdapter(ordersListAdapter);
                                        rOrders.addItemDecoration(new DividerItemDecoration(rOrders.getContext(),DividerItemDecoration.VERTICAL));
                                        rOrders.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        progressDialog.cancel();

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

        return layout;
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

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
