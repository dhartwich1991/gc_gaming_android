package com.jdapplications.gcgaming.models;

import java.util.Date;

/**
 * Created by danielhartwich on 12/19/14.
 */
public class Raid {
    public String name;
    public String description;
    public String leader;
    public String startsAt;
    public String endsAt;

    public Raid(String name, String description, String leader, String startsAt, String endsAt) {
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }
}
