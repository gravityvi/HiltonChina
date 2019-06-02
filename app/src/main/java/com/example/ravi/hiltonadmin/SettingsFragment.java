package com.example.ravi.hiltonadmin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView tUsername_Settings;
    private TextView tEmail_Settings;
    private TextView tPhone_Settings;

    private EditText eUsername_Settings;
    private EditText eEmail_Settings;
    private EditText ePhone_Settings;

    private final String PHONE="Phone";
    private final String USERNAME="UserName";
    private final String EMAIL="Email";

    private CardView cdChangeUserDetails;
    private CardView cdSignout;
    private CardView cdAboutUs;
    private CardView cdOutlets;

    private Button bChange;

    private static final String TAG="PhoneAuthActivity";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"Settings Fragment onCreate called");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG,"Settings Fragment onCreateView called");

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_settings, container, false);

        tUsername_Settings=(TextView)view.findViewById(R.id.tUsername_Settings);
        tEmail_Settings=(TextView)view.findViewById(R.id.tEmail_Settings);
        tPhone_Settings=(TextView)view.findViewById(R.id.tPhone_Settings);
        cdChangeUserDetails=view.findViewById(R.id.cdChangeUserDetails);
        cdSignout = view.findViewById(R.id.cdSignout);
        cdAboutUs = view.findViewById(R.id.cdAboutUs);
        cdOutlets=view.findViewById(R.id.cdOutlets);

        /*********Reading Database to set the the info header********/
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String UserId=user.getUid();
        DatabaseReference UserReference=FirebaseDatabase.getInstance().getReference("UserData");

        UserReference.child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String Username=(String) dataSnapshot.child(USERNAME).getValue(String.class);
                String Email=(String)dataSnapshot.child(EMAIL).getValue(String.class);
                String Phone= (String)dataSnapshot.child(PHONE).getValue(String.class);

                tUsername_Settings.setText("USERNAME: "+Username);
                tEmail_Settings.setText("EMAIL: "+Email);
                tPhone_Settings.setText("PHONE: "+Phone);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        cdChangeUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),new ChangeUserDetails().getClass());
                startActivity(i);

            }
        });

        cdSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(),new MainActivity().getClass());
                startActivity(i);
                getActivity().finish();
            }
        });

        cdAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),new AboutUs().getClass());
                startActivity(i);
            }
        });

        cdOutlets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),new Outlets().getClass());
                startActivity(i);
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
            Log.d(TAG,"Settings Fragment  onAttach called");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG,"Settings Fragment onDetach called");
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
