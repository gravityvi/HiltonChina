package com.example.ravi.hiltonadmin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {
    private ArrayList<Order> orders;
    private LayoutInflater inflater;
    private DatabaseReference databaseReference;

    public OrdersListAdapter(Context context,ArrayList<Order> orders)
    {
        this.orders = orders;
        inflater = LayoutInflater.from(context);
        /** using sample id **/
//        databaseReference = FirebaseDatabase.getInstance("UserData/"+ FirebaseAuth.getInstance().getUid()+"/Orders").getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.order_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Order o = orders.get(position);

        holder.tOrderId.setText("#"+o.orderId);
        holder.tAmount.setText("#"+o.paid);

        if(o.getProgress().equals("Accepted"))
        {
            holder.iProgressStatus.setImageResource(R.drawable.accepted_status);

        }
        else if(o.getProgress().equals("Delivered"))
        {
            holder.iProgressStatus.setImageResource(R.drawable.delivered_status);

        }
        else if(o.getProgress().equals("Canceled"))
        {
            holder.iProgressStatus.setImageResource(R.drawable.dispute_status);

        }

    }



    @Override
    public int getItemCount() {
        return orders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tOrderId;
        ImageView iProgressStatus;
        MaterialButton bViewItems;
        TextView tAmount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tOrderId = itemView.findViewById(R.id.tOrderId);
            iProgressStatus=itemView.findViewById(R.id.iProgressStatus);
            bViewItems = itemView.findViewById(R.id.bViewItems);
            tAmount = itemView.findViewById(R.id.tAmount);
        }
    }
}
