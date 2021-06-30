package com.example.smarthome.Model;

public class DeviceModel {
    String nameDevice;
    String id;

    public DeviceModel(String nameDevice, String id) {
        this.nameDevice = nameDevice;
        this.id = id;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
