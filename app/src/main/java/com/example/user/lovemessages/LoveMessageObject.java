package com.example.user.lovemessages;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by minh.nt on 5/30/2017.
 */

public class LoveMessageObject extends RealmObject {
    @PrimaryKey
    private String id;

    private String content;
    private String image;
    private String music;
    private int days;

    public LoveMessageObject(String content, String id, String image, String music) {
        this.id = id;
        this.content = content;
        this.image = image;
        this.music = music;
        this.days = Utility.countDays(Utility.convertToDate(id));
    }

    public LoveMessageObject() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void saveOrUpdate() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(LoveMessageObject.this);
                }
            });
        } finally {
            if (realm != null && !realm.isClosed()) {
                realm.close();
            }
        }
    }
}
