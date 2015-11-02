package com.timsiggins.whoseturn.data;

/**
 * Created by tim on 10/31/15.
 */
public class StoredObject {
    protected int id;

    public StoredObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
