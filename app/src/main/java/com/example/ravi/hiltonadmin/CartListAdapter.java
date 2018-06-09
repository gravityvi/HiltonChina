package com.example.ravi.hiltonadmin;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by ravi on 02-01-2018.
 */

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder>  {

    private Context context;
    private List<Items> CartList;
    private LayoutInflater inflater;
    private static final String TAG="PhoneAuthActivity";
    private static final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public CartListAdapter(Context context, List<Items> CartList)
    {
        this.context=context;
        this.CartList=CartList;//Cart Items
        inflater= LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.cart_row,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final  ViewHolder holder, final int position) {

        final Items item=CartList.get(position);

        holder.tItemNumber1.setText(item.getItemNumber());//Setting ItemNumber
        holder.tItemName1.setText(item.getItemName());//Setting ItemNumber
        holder.tItemPrice1.setText("PRICE: "+item.getItemPrice());//Setting ItemPrice
        holder.tDesc1.setText(item.getItemDescription());//Setting Item Description
        //Setting Item Image
        if(item.getImageUrl()==null)
        {
            holder.iItemImage1.setImageResource(R.drawable.ravi);
        }
        else {
            Picasso.with(context).load(item.getImageUrl()).placeholder(R.drawable.ravi).into(holder.iItemImage1);
        }




       //functionality to increase ItemNumber
        holder.bIncrease1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int a=Integer.parseInt(holder.tItemNumber1.getText().toString()); // getting ItemNumber
                a++;
                item.setItemNumber(Integer.toString(a));
                FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart/Items/"+item.getItemId()+"/ItemNumber").setValue(Integer.toString(a));



                FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       int TotalItems = Integer.parseInt(dataSnapshot.child("TotalItems").getValue(String.class));
                       TotalItems += 1;

                       int CheckoutSum = Integer.parseInt(dataSnapshot.child("CheckoutSum").getValue(String.class));
                       CheckoutSum += Integer.parseInt(item.getItemPrice());
                        FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart").child("TotalItems").setValue(Integer.toString(TotalItems));
                        FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart").child("CheckoutSum").setValue(Integer.toString(CheckoutSum));
                        holder.tItemNumber1.setText((item.getItemNumber()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });






        //functionality to decrease Item Number
        holder.bDecrease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a=Integer.parseInt(holder.tItemNumber1.getText().toString()); //getting ItemNumber

                //Implying condition ItemNumber cannot be less than zero
                if(a<=1)
                    Toast.makeText(context,"can't do that",Toast.LENGTH_SHORT).show();
                else
                {
                    a--;
                    item.setItemNumber(Integer.toString(a));
                    FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart/Items/"+item.getItemId()+"/ItemNumber").setValue(Integer.toString(a));



                    FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int TotalItems = Integer.parseInt(dataSnapshot.child("TotalItems").getValue(String.class));
                            TotalItems -= 1;

                            int CheckoutSum = Integer.parseInt(dataSnapshot.child("CheckoutSum").getValue(String.class));
                            CheckoutSum -= Integer.parseInt(item.getItemPrice());
                            FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart").child("TotalItems").setValue(Integer.toString(TotalItems));
                            FirebaseDatabase.getInstance().getReference("UserData/"+user.getUid()+"/Cart").child("CheckoutSum").setValue(Integer.toString(CheckoutSum));
                            holder.tItemNumber1.setText((item.getItemNumber()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }


            }
        });






    }



    @Override
    public int getItemCount() {

        return CartList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        Button bIncrease1;
        Button bDecrease1;
        TextView tItemNumber1;
        LinearLayout lCartRow;
        ImageView iItemImage1;
        TextView tDesc1;
        TextView tItemName1;
        TextView tItemPrice1;
        Button bRemove1;

        public ViewHolder(View itemView) {
            super(itemView);

            bDecrease1=itemView.findViewById(R.id.bDecrease1);
            bIncrease1=itemView.findViewById(R.id.bIncrease1);
            tItemNumber1=itemView.findViewById(R.id.tItemNumber1);
            bRemove1=itemView.findViewById(R.id.bRemove1);
            lCartRow=itemView.findViewById(R.id.lCartRow);
            iItemImage1=itemView.findViewById(R.id.iItemImage1);
            tDesc1=itemView.findViewById(R.id.tItemDescription1);
            tItemName1=itemView.findViewById(R.id.tItemName1);
            tItemPrice1=itemView.findViewById(R.id.tItemPrice1);
            bRemove1.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            final int position=getAdapterPosition();
            final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("UserData/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Cart/Items/"+CartList.get(position).getItemId());
            databaseReference.removeValue();
            FirebaseDatabase.getInstance().getReference("UserData/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int totalItems = Integer.parseInt(dataSnapshot.child("TotalItems").getValue(String.class));
                    totalItems -= Integer.parseInt(CartList.get(position).getItemNumber());
                    int checkoutSum = Integer.parseInt(dataSnapshot.child("CheckoutSum").getValue(String.class));
                    checkoutSum -= (Integer.parseInt(CartList.get(position).getItemPrice()) * Integer.parseInt(CartList.get(position).getItemNumber()));
                    FirebaseDatabase.getInstance().getReference("UserData/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Cart").child("TotalItems").setValue(Integer.toString(totalItems));
                    FirebaseDatabase.getInstance().getReference("UserData/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Cart").child("CheckoutSum").setValue(Integer.toString(checkoutSum));
                    CartList.remove(position);
                    notifyItemRemoved(position);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }
    }
}
