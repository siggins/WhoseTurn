package com.timsiggins.whoseturn.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by tim on 10/31/15.
 */
public class Person extends StoredObject implements Parcelable{
    private String name;
    private Date lastpaid;
    private int ahead;

    public Person(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastPaid() {
        return lastpaid;
    }

    public void setLastpaid(Date lastpaid) {
        this.lastpaid = lastpaid;
    }

    public int getAhead() {
        return ahead;
    }

    public void setAhead(int ahead) {
        this.ahead = ahead;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(getId());
        parcel.writeString(name);
        parcel.writeLong(lastpaid.getTime());
        parcel.writeInt(ahead);
    }

    public Person(Parcel in){
        super(in.readInt());
        name = in.readString();
        lastpaid = new Date(in.readLong());
        ahead = in.readInt();
    }
    public static final Parcelable.Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel parcel) {
            return new Person(parcel);
        }
        @Override
        public Person[] newArray(int i) {
            return new Person[i];
        }
    };
}
