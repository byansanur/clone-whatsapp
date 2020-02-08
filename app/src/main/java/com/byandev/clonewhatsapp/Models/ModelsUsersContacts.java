package com.byandev.clonewhatsapp.Models;

public class ModelsUsersContacts {

    public String aname, image, status;

    public ModelsUsersContacts() {

    }

    public ModelsUsersContacts(String aname, String image, String status) {
        this.aname = aname;
        this.image = image;
        this.status = status;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
