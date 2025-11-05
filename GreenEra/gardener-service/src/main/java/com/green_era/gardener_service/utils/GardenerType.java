package com.green_era.gardener_service.utils;

public enum GardenerType {
    REGULAR("Regular"),
    URGENT("Urgent"),
    BOTH("Both");

    String type;

    GardenerType(String type) {this.type = type;}

    public String getType() {return type;}
}
