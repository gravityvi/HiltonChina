package com.example.ravi.hiltonadmin;

import android.app.Activity;
import android.content.ClipData;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravi on 26-12-2017.
 */

public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.ViewHolder> {

    private static final String TAG="PhoneAuthActivity";
    private DatabaseReference databaseReference;
    private  LayoutInflater inflator;
    private ArrayList<String> arrayList;
    private Context context;
    private FirebaseStorage storage;
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
        storage=FirebaseStorage.getInstance();



        return viewHolder;

    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG,"CategoriesAdapter onBindViewHolder called");

        /************************adding different colours to rows*********************/
        if(position%2==0) {
            holder.CategoriesRowTitle.setText(arrayList.get(position));
            holder.linearLayout.setBackgroundColor(Color.parseColor("#C5CAE9"));

        }
        else
        {
            holder.CategoriesRowTitle.setText(arrayList.get(position));
            holder.linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
        }
/**************************************************************************************/

/**********Setting OnClcikListner for Every Row****************************************/
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getting values from database
               final ArrayList<Items> ItemList=new ArrayList<>();
                databaseReference.child(holder.CategoriesRowTitle.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data: dataSnapshot.getChildren())
                        {

                            final String ItemCategory=holder.CategoriesRowTitle.getText().toString();
                            final String ItemId=data.getKey();
                            final String ItemName=data.child("Name").getValue(String.class);
                            final String Desc=data.child("Desc").getValue(String.class);
                            final String ItemPrice=data.child("Price").getValue(String.class);
                            final String ItemNumber="1";


                            //getting Image File From url of Database
                            String ImgeUrl= data.child("Image").getValue(String.class);
                            StorageReference storageReference=storage.getReferenceFromUrl(ImgeUrl);
                            storageReference.getBytes(1024*1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {

                                    //Creating Fragment for selected row and adding Categories Items to the arraylist and passing to the ItemsFragment

                                    Items item=new Items(ItemId,bytes,ItemName,ItemCategory,ItemNumber,Desc,ItemPrice);
                                    ItemList.add(item);
                                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                                    fragmentTransaction.addToBackStack("Categories");//adding Categories to the backstack
                                    fragmentTransaction.replace(R.id.lFragmentContent,ItemsFragment.newInstance(ItemList)).commit();

                                }
                            });





                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d(TAG,"CategoriesAdapter getCountCalled");
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
