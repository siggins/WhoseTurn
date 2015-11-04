package com.timsiggins.whoseturn.data;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tim on 10/31/15.
 */
public class Group extends StoredObject implements Iterable<Person>, Parcelable{

    private final String name;
    private final ArrayList<Person> people = new ArrayList<>();
    private Date lastUsed = new Date();
    private String picture = null;

    public Group(int id, String name) {
        super(id);
        this.name = name;
    }

    public Group(String name) {
        super(-1);
        this.name = name;
    }

    public void addPerson(Person p){
        people.add(p);
    }

    public Person getPerson(int num){
        return people.get(num);
    }

    @Override
    public Iterator<Person> iterator() {
        return people.iterator();
    }

    public Person getNextPayer(){
        Person payer = null;
        for (Person p: people){
            if (payer == null || p.getLastPaid().before(payer.getLastPaid())){
                payer = p;
            }
        }
        return payer;
    }

    public String getName() {
        return name;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public void addPeople(List<Person> newPeople) {
        people.addAll(newPeople);
    }

    public int size() {
        return people.size();
    }
    public ArrayList<Person> getPeople() {
        return people;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(getId());
        parcel.writeString(name);
        parcel.writeLong(lastUsed.getTime());
        parcel.writeTypedList(people);
        parcel.writeString(picture);
    }
    public Group(Parcel in){
        super(in.readInt());
        name = in.readString();
        lastUsed = new Date(in.readLong());
        people.clear();
        in.readTypedList(people, Person.CREATOR);
        picture = in.readString();
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };


}
