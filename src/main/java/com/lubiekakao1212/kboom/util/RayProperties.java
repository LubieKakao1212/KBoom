package com.lubiekakao1212.kboom.util;

import com.google.gson.annotations.SerializedName;

public class RayProperties {

    @SerializedName("penetration")
    public float penetration = 1f;

    @SerializedName("penetration-noise")
    public float penertationNoise = 0f;

    @SerializedName("natural-decay")
    public float naturalDecay = 1f / 16f;

    public double maxDistanceForPower(double power) {
        return power / naturalDecay;
    }

}
