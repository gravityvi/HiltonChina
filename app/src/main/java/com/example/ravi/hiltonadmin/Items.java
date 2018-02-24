package com.example.ravi.hiltonadmin;

import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ravi on 29-12-2017.
 */

public class Items implements Parcelable{

    private byte[] Image;
    private String ItemName;
    private String ItemCategory;
    private String ItemNumber;
    private String ItemDescription;
    private String ItemPrice;
    private String ItemId;

    /**getters and setters***/


    public String getItemNumber() {
        return ItemNumber;
    }

    public void setItemNumber(String itemNumber) {
        ItemNumber = itemNumber;
    }

    public String getItemCategory() {
        return ItemCategory;
    }

    public void setItemCategory(String itemCategory) {
        ItemCategory = itemCategory;
    }

    public String getItemId() {
        return ItemId;
    }

    public byte[] getImage() {
        return Image;
    }

    public String getItemName() {
        return ItemName;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public String getItemPrice() {
        return ItemPrice;
    }


    /**********************************************************************/

    Items(String ItemId,byte[] Image, String ItemName,String ItemCategory,String ItemNumber, String ItemDescription, String ItemPrice)
    {
        this.ItemId=ItemId;
        this.Image=Image;
        this.ItemName=ItemName;
        this.ItemCategory=ItemCategory;
        this.ItemDescription=ItemDescription;
        this.ItemNumber=ItemNumber;

        this.ItemPrice= ItemPrice;

    }

    public  Items(Parcel in)
    {

        this.ItemId=in.readString();
        this.ItemName=in.readString();
        this.ItemCategory=in.readString();
        this.ItemNumber=in.readString();
        this.ItemPrice=in.readString();
        this.ItemDescription=in.readString();
        Image=new byte[in.readInt()];
        in.readByteArray(Image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ItemId);
        parcel.writeString(ItemName);
        parcel.writeString(ItemCategory);
        parcel.writeString(ItemNumber);
        parcel.writeString(ItemPrice);
        parcel.writeString(ItemDescription);
        parcel.writeInt(Image.length);
        parcel.writeInt(Image.length);
        parcel.writeByteArray(Image);
    }

   public final Parcelable.Creator<Items> CREATOR=new Parcelable.Creator<Items>()
    {

        @Override
        public Items createFromParcel(Parcel parcel) {
            return new Items(parcel);
        }

        @Override
        public Items[] newArray(int i) {
            return new Items[i];
        }
    };



}