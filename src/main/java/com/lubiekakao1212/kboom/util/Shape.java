package com.lubiekakao1212.kboom.util;

import com.google.gson.annotations.SerializedName;

public enum Shape {
    @SerializedName("sphere")
    SPHERE(IDistanceFunction::sphere),
    @SerializedName("cube")
    CUBE(IDistanceFunction::cube),
    @SerializedName("diamond")
    DIAMOND(IDistanceFunction::manhattan);

    private final IDistanceFunction distanceFunction;

    Shape(IDistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    public IDistanceFunction getDistanceFunction() {
        return distanceFunction;
    }
}
