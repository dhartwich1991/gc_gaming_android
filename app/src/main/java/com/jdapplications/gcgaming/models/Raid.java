package com.jdapplications.gcgaming.models;

/**
 * Created by danielhartwich on 12/19/14.
 */
public class Raid {
    public int id;
    public String name;
    public String description;
    public String leader;
    public String startsAt;
    public String endsAt;

    public Raid(int id, String name, String description, String leader, String startsAt, String endsAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }
}
