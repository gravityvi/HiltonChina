package com.example.ravi.hiltonadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class OrderItems extends AppCompatActivity {

    private RecyclerView rOrderItems;
    private ArrayList<Items> ItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);

        Intent i = getIntent();
        ItemList = i.getParcelableArrayListExtra("OrderItems");

        rOrderItems = findViewById(R.id.rOrderItems);
        OrderItemsAdapter orderItemsAdapter = new OrderItemsAdapter(this,ItemList);
        rOrderItems.setAdapter(orderItemsAdapter);
        rOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rOrderItems.addItemDecoration(new DividerItemDecoration(rOrderItems.getContext(),DividerItemDecoration.VERTICAL));
    }
}
