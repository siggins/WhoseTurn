package com.timsiggins.whoseturn.data;

import java.util.Date;

/**
 * Created by tim on 10/31/15.
 */
public class Person extends StoredObject{
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
}
