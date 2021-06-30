package com.example.smarthome.Model;

import java.io.Serializable;

public class HomeTypeModel implements Serializable {
    public int image;
    public String nameRoom;
    public String idDevice;

    public HomeTypeModel( String nameRoom, String idDevice) {

        this.nameRoom = nameRoom;
        this.idDevice = idDevice;
    }

    public HomeTypeModel(int image, String nameRoom, String idDevice) {
        this.image = image;
        this.nameRoom = nameRoom;
        this.idDevice = idDevice;
    }
    public HomeTypeModel( int image, String nameRoom) {

        this.image = image;
        this.nameRoom = nameRoom;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }
}
