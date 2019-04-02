package com.example.ravi.hiltonadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int NUM_PAGES= 5;
    private android.support.v4.view.ViewPager ViewPager;
    private LinearLayout lSliderdots;
    private int dotcount;
    private ImageView[] dots;
    private Button bSignout;

    private static final String TAG="PhoneAuthActivity";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        Log.d(TAG,"Home Fragment On Create View called");
         view=inflater.inflate(R.layout.fragment_home, container, false);



        ViewPager=(ViewPager)view.findViewById(R.id.vImageSlider);//image slider viewpager
        lSliderdots=(LinearLayout)view.findViewById(R.id.lSliderDots); //dots below it
        bSignout=(Button)view.findViewById(R.id.bSignout);



        /******************For Image Slider Present at the Home Page***********/

        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getContext());
        ViewPager.setAdapter(viewPagerAdapter);


        Timer timer =new Timer(); //making swiping automatic
        timer.scheduleAtFixedRate(new MyTimerTask() ,2000, 4000);

        dotcount=viewPagerAdapter.getCount();
        dots=new ImageView[dotcount];

        for(int i=0 ;i<dotcount;i++) //adding new dots for the images
        {
            dots[i]=new ImageView(getContext());
            dots[i].setImageResource(R.drawable.nonactive_dot);

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(6,0,6,0);
            lSliderdots.addView(dots[i],params);
        }


        dots[0].setImageResource(R.drawable.active_dot);



        ViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


            @Override
            public void onPageSelected(int position) {

                for(int i=0 ;i<dotcount;i++)
                {
                    dots[i].setImageResource(R.drawable.nonactive_dot);

                }
                dots[position].setImageResource(R.drawable.active_dot);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /***********************************************************/



        bSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i=new Intent(getActivity(),MainActivity.class);
                startActivity(i);
                getActivity().finish(); //to finish this activity
            }
        });

        return view;




    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) //LEARN ABOUT THIS
    {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) //LEARN ABOUT THIS
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           Log.d(TAG,"Home Fragment onAttach called");

        }
    }

    @Override
    public void onDetach() //LEARN ABOUT THIS
    {
        super.onDetach();
        Log.d(TAG,"Home Fragment onDetach called");
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
    public interface OnFragmentInteractionListener //LEARN ABOUT THIS
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    /****for the automatic image slider********/
    public class MyTimerTask extends TimerTask
    {

        @Override
        public void run() {

            if(getActivity()!=null)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if(ViewPager.getCurrentItem() ==0)
                            ViewPager.setCurrentItem(1);
                        else if(ViewPager.getCurrentItem()==1)
                            ViewPager.setCurrentItem(2);
                        else if(ViewPager.getCurrentItem()==2)
                            ViewPager.setCurrentItem(0);

                    }
                });
            }

        }
    }

    /*******************************************************/


}



