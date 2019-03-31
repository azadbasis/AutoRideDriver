package org.autoride.driver.utils.reference;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by msc10 on 16/02/2017.
 */
public class ReferenceItem implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ReferenceItem> CREATOR = new Parcelable.Creator<ReferenceItem>() {
        @Override
        public ReferenceItem createFromParcel(Parcel in) {
            return new ReferenceItem(in);
        }

        @Override
        public ReferenceItem[] newArray(int size) {
            return new ReferenceItem[size];
        }
    };

    public String name;
    public String status;
    public String address;
    public String accountNumber;
    public String registeredLocation;
    public String createdDate;
    public String imageUrl;

    public ReferenceItem() {
    }


    public ReferenceItem(String name, String status, String address, String accountNumber, String registeredLocation, String createdDate, String imageUrl) {
        this.name = name;
        this.status = status;
        this.address = address;
        this.accountNumber = accountNumber;
        this.registeredLocation = registeredLocation;
        this.createdDate = createdDate;
        this.imageUrl = imageUrl;
    }

    protected ReferenceItem(Parcel in) {
        name = in.readString();
        status = in.readString();
        address = in.readString();
        accountNumber = in.readString();
        registeredLocation = in.readString();
        createdDate = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(status);
        dest.writeString(address);
        dest.writeString(accountNumber);
        dest.writeString(registeredLocation);
        dest.writeString(createdDate);
        dest.writeString(imageUrl);
    }
}