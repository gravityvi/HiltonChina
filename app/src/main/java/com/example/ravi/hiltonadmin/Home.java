package com.example.ravi.hiltonadmin;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity {

    private BottomNavigationView BottomNavigation;
    private FrameLayout lFragmentContent;
    private static FragmentManager fragmentManager;

    private static final int NUM_PAGES = 5;

/******** For The Navigation Bar***********************/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_settings:
                    boolean exist0 = fragmentExist(SettingsFragment.class.getName());
                    if(exist0)
                    {
                        fragmentManager.popBackStackImmediate(SettingsFragment.class.getName(),0);
                    }
                    else {
                        if (BottomNavigation.getSelectedItemId() != R.id.navigation_settings) {
                            transaction.replace(R.id.lFragmentContent, new SettingsFragment(), "0").addToBackStack(SettingsFragment.class.getName()).commit();

                        }
                    }



                    return true;

                case R.id.navigation_history:

                    boolean exist1 = fragmentExist(HistoryFragment.class.getName());

                    if(exist1)
                    {
                        fragmentManager.popBackStackImmediate(HistoryFragment.class.getName(),0);
                    }
                    else{
                        if(BottomNavigation.getSelectedItemId() != R.id.navigation_history)
                        {
                            transaction.replace(R.id.lFragmentContent, new HistoryFragment(),"2").addToBackStack(HistoryFragment.class.getName()).commit();
                        }
                    }
                    return true;

                case R.id.navigation_home:
                    boolean exist2 = fragmentExist(HomeFragment.class.getName());

                    if(exist2)
                    {
                        fragmentManager.popBackStackImmediate(HomeFragment.class.getName(),0);
                    }
                    else{
                        if(BottomNavigation.getSelectedItemId() != R.id.navigation_home)
                        {
                            transaction.replace(R.id.lFragmentContent, new HomeFragment(),"2").addToBackStack(HomeFragment.class.getName()).commit();
                        }
                    }
                    return true;

                case R.id.navigation_cart:
                    boolean exist3 = fragmentExist(CartFragment.class.getName());
                    if(exist3)
                    {
                        fragmentManager.popBackStackImmediate(CartFragment.class.getName(),0);
                    }
                    else {
                        if(BottomNavigation.getSelectedItemId() != R.id.navigation_cart)
                        {
                            transaction.replace(R.id.lFragmentContent,new CartFragment(),"3").addToBackStack(CartFragment.class.getName()).commit();
                        }
                    }

                    return true;
                case R.id.navigation_order:
                    boolean exist4 = fragmentExist(OrderFragment.class.getName());
                    if(exist4)
                    {
                        fragmentManager.popBackStackImmediate(OrderFragment.class.getName(),0);
                    }
                    else{
                        if(BottomNavigation.getSelectedItemId() != R.id.navigation_order) {
                            transaction.replace(R.id.lFragmentContent, new OrderFragment(),"4");
                            transaction.addToBackStack(OrderFragment.class.getName());
                            transaction.commit();
                        }
                    }
                    return true;
            }
            return false;
        }
    };
    /********************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lFragmentContent = findViewById(R.id.lFragmentContent);
         BottomNavigation = (BottomNavigationView) findViewById(R.id.BottomNavigation);
        BottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigation.setSelectedItemId(R.id.navigation_home);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                Fragment currentFragement = getSupportFragmentManager().findFragmentById(R.id.lFragmentContent);
                BottomNavigation.getMenu().getItem(Integer.parseInt(currentFragement.getTag())%5).setChecked(true);
                
            }
        });

    }

    public static boolean fragmentExist(String tagName)
    {
        for(int i=0; i< fragmentManager.getBackStackEntryCount() ; i++)
        {
            if(tagName.equals(fragmentManager.getBackStackEntryAt(i).getName()))
            {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onSupportNavigateUp() {
         onBackPressed();
         return true;
    }
}
