package com.example.ravi.hiltonadmin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by ravi on 26-12-2017.
 */

public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.ViewHolder> {

    private static final String TAG="PhoneAuthActivity";
    private DatabaseReference databaseReference;
    private  LayoutInflater inflator;
    private ArrayList<String> arrayList;
    private Context context;
    private FragmentManager fragmentManager;
    CategoriesListAdapter(Context context, ArrayList<String> arrayList, FragmentManager fragmentManager)

    {
        this.fragmentManager=fragmentManager;
        this.context=context;
        this.arrayList=arrayList;
        inflator= LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       Log.d(TAG,"CategoriesAdapter onCreateViewHolder called");
        View view= inflator.inflate(R.layout.categories_row,parent,false);
       ViewHolder viewHolder=new ViewHolder(view);
       databaseReference= FirebaseDatabase.getInstance().getReference("ItemData");



        return viewHolder;

    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG,"CategoriesFragment onBindViewHolder called");

        if(position%2==0) {
            holder.CategoriesRowTitle.setText(arrayList.get(position));
            holder.linearLayout.setBackgroundColor(Color.parseColor("#C5CAE9"));
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack("Categories");
                    fragmentTransaction.replace(R.id.lFragmentContent,new ItemsFragment()).commit();
                }
            });
        }
        else
        {
            holder.CategoriesRowTitle.setText(arrayList.get(position));
            holder.linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG,"getCountCalled");
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        TextView CategoriesRowTitle;
        LinearLayout linearLayout;


        public ViewHolder(View itemView) {

            super(itemView);

            CategoriesRowTitle=(TextView)itemView.findViewById(R.id.tCategoriesRowTitle);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.lCategoriesLayout);


        }
    }


}
