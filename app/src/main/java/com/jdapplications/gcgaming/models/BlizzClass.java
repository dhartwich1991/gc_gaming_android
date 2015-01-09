package com.jdapplications.gcgaming.models;

/**
 * Created by danielhartwich on 1/8/15.
 */
public class BlizzClass {

    public BlizzClass(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int id;
    public String name;
}
