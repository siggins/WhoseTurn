package com.timsiggins.whoseturn.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tim on 10/31/15.
 */
public class Group extends StoredObject implements Iterable<Person>{

    private final String name;
    private final ArrayList<Person> people = new ArrayList<>();
    private Date lastUsed = new Date();

    public Group(int id, String name) {
        super(id);
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
}
