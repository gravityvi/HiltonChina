package com.example.ravi.hiltonadmin;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity {



    private static final int NUM_PAGES= 5;
    private ViewPager ViewPager;
    private LinearLayout lSliderdots;
    private int dotcount;
    private ImageView[] dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewPager=(ViewPager)findViewById(R.id.vImageSlider);
        lSliderdots=(LinearLayout)findViewById(R.id.lSliderDots);


        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(this);
        ViewPager.setAdapter(viewPagerAdapter);
        Timer timer =new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask() ,2000, 4000);

        dotcount=viewPagerAdapter.getCount();
        dots=new ImageView[dotcount];

        for(int i=0 ;i<dotcount;i++)
        {
            dots[i]=new ImageView(this);
            dots[i].setImageResource(R.drawable.nonactive_dot);

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
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

    }


    public class MyTimerTask extends TimerTask
    {

        @Override
        public void run() {
            Home.this.runOnUiThread(new Runnable() {
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



    public void signout(View view)
    {

        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        finish(); //to destroy this activity

    }




}
