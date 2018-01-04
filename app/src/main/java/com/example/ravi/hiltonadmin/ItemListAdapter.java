package com.example.ravi.hiltonadmin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by ravi on 29-12-2017.
 */

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private static final String TAG="PhoneAuthActivity";
    private LayoutInflater inflator;
    private DatabaseReference databaseReference;
    private ArrayList<Items> arrayList;
    private Context context;
    private FirebaseAuth firebaseAuth;


    public  ItemListAdapter(Context context,ArrayList<Items> arrayList)
    {
        this.context=context;
        this.arrayList=arrayList;
        inflator= LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflator.inflate(R.layout.item_row,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("UserData");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Items item= arrayList.get(position);//Item with the index position in arrayList





        holder.tItemName.setText(item.getItemName());//Setting ItemName
        holder.tItemPrice.setText("PRICE: "+item.getItemPrice());//Setting ItemPrice
        holder.tDesc.setText(item.getItemDescription());//Setting ItemDescription
        //Setting ItemImage
        Bitmap bmp= BitmapFactory.decodeByteArray(item.getImage(),0,item.getImage().length);
        holder.iItemImage.setImageBitmap(bmp);



        //functionality to increase Item Number
        holder.bIncrease.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int a=Integer.parseInt(holder.tItemNumber.getText().toString());
                a++;
                holder.tItemNumber.setText(Integer.toString(a));
            }
        });

        //functionality to decrease Item Number
        holder.bDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a=Integer.parseInt(holder.tItemNumber.getText().toString());
                if(a<0)
                    Toast.makeText(context,"can't do that",Toast.LENGTH_SHORT).show();
                else
                {
                    a--;
                    holder.tItemNumber.setText(Integer.toString(a));
                }


            }
        });


        //aading onclick for add to cart for eACH button for each row
        holder.bAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user=firebaseAuth.getCurrentUser();
                String userUid=user.getUid();

                //adding Selected Item Into database
                databaseReference.child(userUid).child("Cart").child(item.getItemId()).child("ItemNumber").setValue(holder.tItemNumber.getText().toString());
                databaseReference.child(userUid).child("Cart").child(item.getItemId()).child("ItemCategory").setValue(item.getItemCategory());
                



            }
        });

    }

    @Override
    public int getItemCount() {


        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        Button bIncrease;
        Button bDecrease;
        TextView tItemNumber;
        LinearLayout lItem;
        ImageView iItemImage;
        TextView tDesc;
        TextView tItemName;
        TextView tItemPrice;
        Button bAddToCart;

        public ViewHolder(View itemView) {
            super(itemView);

            bDecrease=itemView.findViewById(R.id.bDecrease);
            bIncrease=itemView.findViewById(R.id.bIncrease);
            tItemNumber=itemView.findViewById(R.id.tItemNumber);
            bAddToCart=itemView.findViewById(R.id.bAddToCart);
            lItem=itemView.findViewById(R.id.lItemRow);
            iItemImage=itemView.findViewById(R.id.iItemImage);
            tDesc=itemView.findViewById(R.id.tItemDescription);
            tItemName=itemView.findViewById(R.id.tItemName);
            tItemPrice=itemView.findViewById(R.id.tItemPrice);
        }
    }
}
