package com.jdapplications.gcgaming.models;

/**
 * Created by danielhartwich on 1/7/15.
 */
public class Character {
    public Character(int lastModified, String name, String realm, String battlegroup, int charClass, int race, int gender, int level, int achievementPoints, String thumbNailUrl, int itemLevelTotal, int itemLevelEquipped, int userid) {
        this.lastModified = lastModified;
        this.name = name;
        this.realm = realm;
        this.battlegroup = battlegroup;
        this.charClass = charClass;
        this.race = race;
        this.gender = gender;
        this.level = level;
        this.achievementPoints = achievementPoints;
        this.thumbNailUrl = thumbNailUrl;
        this.itemLevelTotal = itemLevelTotal;
        this.itemLevelEquipped = itemLevelEquipped;
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", lastModified=" + lastModified +
                ", name='" + name + '\'' +
                ", realm='" + realm + '\'' +
                ", battlegroup='" + battlegroup + '\'' +
                ", charClass=" + charClass +
                ", race=" + race +
                ", gender=" + gender +
                ", level=" + level +
                ", achievementPoints=" + achievementPoints +
                ", thumbNailUrl='" + thumbNailUrl + '\'' +
                ", itemLevelTotal=" + itemLevelTotal +
                ", itemLevelEquipped=" + itemLevelEquipped +
                ", userid=" + userid +
                '}';
    }

    public int id;

    public Character(int id, int lastModified, String name, String realm, String battlegroup, int charClass, int race, int gender, int level, int achievementPoints, String thumbNailUrl, int itemLevelTotal, int itemLevelEquipped, int userid) {
        this.id = id;
        this.lastModified = lastModified;
        this.name = name;
        this.realm = realm;
        this.battlegroup = battlegroup;
        this.charClass = charClass;
        this.race = race;
        this.gender = gender;
        this.level = level;
        this.achievementPoints = achievementPoints;
        this.thumbNailUrl = thumbNailUrl;
        this.itemLevelTotal = itemLevelTotal;
        this.itemLevelEquipped = itemLevelEquipped;
        this.userid = userid;
    }

    public Character(int id, String name, String realm, int race, int charClass, String thumbNailUrl, int level, int itemLevelEquipped, int itemLevelTotal, String role) {
        this.id = id;
        this.name = name;
        this.realm = realm;
        this.race = race;
        this.charClass = charClass;
        this.thumbNailUrl = thumbNailUrl;
        this.level = level;
        this.itemLevelEquipped = itemLevelEquipped;
        this.itemLevelTotal = itemLevelTotal;
        this.role = role;
    }

    public int lastModified;public String name;
    public String realm;
    public String battlegroup;
    public int charClass;
    public int race;
    public int gender;
    public int level;
    public int achievementPoints;
    public String thumbNailUrl;
    public int itemLevelTotal;
    public int itemLevelEquipped;
    public int userid;  //The user this character belongs to
    public String role;
}
