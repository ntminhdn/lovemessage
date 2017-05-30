package com.example.user.lovemessages;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by User on 23/02/2017.
 */
@IgnoreExtraProperties
public class LoveMessage {
    private String content;
    private String id;
    private String image;
    private String music;

    public LoveMessage(String content, String id, String image, String music) {
        this.id = id;
        this.content = content;
        this.image = image;
        this.music = music;
    }

    public LoveMessage() {

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

}
