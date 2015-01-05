package com.jdapplications.gcgaming.models;

/**
 * Created by danielhartwich on 1/5/15.
 */
public class User {
    public User(int id, String password, String email, String accessToken) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.accessToken = accessToken;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int id;
    public String username;
    public String password;
    public String email;
    public String accessToken;
    //TODO: Add character to a user
}
