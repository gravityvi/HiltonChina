package com.example.ravi.hiltonadmin;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Order  {
    String orderId;



    String userId;
    String userName;
    String address;
    String paid;
    String Phone;
    String PaymentType;
    String Progress;
    String registrationToken;
    ArrayList<Items> ItemList;





    public Order(String orderId,String userId, String userName, String address, String paid, String phone, String paymentType, String progress,String registrationToken,ArrayList<Items> ItemList) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.address = address;
        this.paid = paid;
        Phone = phone;
        PaymentType = paymentType;
        Progress = progress;
        this.registrationToken = registrationToken;
        this.ItemList = ItemList;
    }




    public ArrayList<Items> getItemList() {
        return ItemList;
    }

    public void setItemList(ArrayList<Items> itemList) {
        ItemList = itemList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public void setProgress(String progress) {
        Progress = progress;
    }

    public String getPhone() {
        return Phone;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public String getProgress() {
        return Progress;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAddress() {
        return address;
    }

    public String getPaid() {
        return paid;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }


}
