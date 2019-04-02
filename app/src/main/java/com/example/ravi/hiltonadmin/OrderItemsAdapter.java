package com.example.ravi.hiltonadmin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {


    private LayoutInflater layoutInflater;
    private ArrayList<Items> ItemList;
    private Context context;

    public OrderItemsAdapter(Context context, ArrayList<Items> ItemList) {
        layoutInflater = LayoutInflater.from(context);
        this.ItemList = ItemList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            /** hiding not needed views**/
            holder.tItemNumber.setVisibility(View.GONE);
            holder.bAddtocart.setVisibility(View.GONE);
            holder.bIncrease.setVisibility(View.GONE);
            holder.bDecrease.setVisibility(View.GONE);
            /*******************************************/

            Items i = ItemList.get(position);
            holder.tItemName.setText(i.getItemName());
            holder.tItemPrice.setText("Price: "+i.getItemPrice());
            holder.tItemDesc.setText(i.getItemDescription());
            Picasso.with(context).load(i.getImageUrl()).placeholder(R.drawable.ravi).into(holder.iItemImage);



    }

    @Override
    public int getItemCount() {
        return ItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tItemName;
        TextView tItemDesc;
        ImageView iItemImage;
        TextView tItemPrice;
        TextView tItemNumber;
        Button bAddtocart;
        Button bIncrease;
        Button bDecrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tItemName = itemView.findViewById(R.id.tItemName);
            tItemDesc = itemView.findViewById(R.id.tItemDescription);
            iItemImage = itemView.findViewById(R.id.iItemImage);
            tItemPrice=itemView.findViewById(R.id.tItemPrice);
            tItemNumber=itemView.findViewById(R.id.tItemNumber);
            bAddtocart=itemView.findViewById(R.id.bAddToCart);
            bIncrease = itemView.findViewById(R.id.bIncrease);
            bDecrease = itemView.findViewById(R.id.bDecrease);
        }
    }
}
