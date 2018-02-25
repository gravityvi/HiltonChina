package com.example.ravi.hiltonadmin;

import android.content.Context;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
            Picasso.with(context).load(item.getImageUrl()).into(holder.iItemImage1);
        }
       //functionality to increase ItemNumber
        holder.bIncrease1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int a=Integer.parseInt(holder.tItemNumber1.getText().toString()); // getting ItemNumber
                a++;
                CartFragment.UpdateCostView( 1,Integer.parseInt(item.getItemPrice()),true);
                holder.tItemNumber1.setText(Integer.toString(a));
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
                    CartFragment.UpdateCostView( 1,Integer.parseInt(item.getItemPrice()),false);
                    holder.tItemNumber1.setText(Integer.toString(a));
                }


            }
        });






    }

    public  void  RemoveItem(Items item)
    {


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
            int position=getAdapterPosition();
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("UserData/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Cart/"+CartList.get(position).getItemId());
            databaseReference.removeValue();
            int ItemNumber=Integer.parseInt(tItemNumber1.getText().toString());
            CartFragment.UpdateCostView(ItemNumber,ItemNumber*Integer.parseInt(CartList.get(position).getItemPrice()),false);
            CartList.remove(position);
            notifyItemRemoved(position);


        }
    }
}
