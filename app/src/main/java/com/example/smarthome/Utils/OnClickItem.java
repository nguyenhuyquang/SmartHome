package com.example.smarthome.Utils;

import com.example.smarthome.Model.HomeTypeModel;

public class OnClickItem {
    public HomeTypeModel homeTypeModel;
    public int pos;
    public String idDevice;

    public OnClickItem(HomeTypeModel homeTypeModel, int pos, String idDevice) {
        this.homeTypeModel = homeTypeModel;
        this.pos = pos;
        this.idDevice=idDevice;
    }

    public HomeTypeModel getHomeTypeModel() {
        return homeTypeModel;
    }

    public void setHomeTypeModel(HomeTypeModel homeTypeModel) {
        this.homeTypeModel = homeTypeModel;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getId() {
        return idDevice;
    }

    public void setId(String idDevice) {
        this.idDevice = idDevice;
    }

    public OnClickItem(HomeTypeModel homeTypeModel) {
    }
}
