package com.example.customlights.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Mode {
    private String mode;
    private int speed;
    private int brightness;
    private int red;
    private int green;
    private int blue;

    public Mode(String mode, int speed, int brightness, int red, int green, int blue) {
        this.mode = mode;
        this.speed = speed;
        this.brightness = brightness;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public String getMode() {
        return mode;
    }

    public int getSpeed() {
        return speed;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

//    toJson
    public String toJSON(){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("mode", mode);
            jsonObject.put("brightness", brightness);
            jsonObject.put("speed", speed);
            jsonObject.put("red", red);
            jsonObject.put("green", green);
            jsonObject.put("blue", blue);
            return jsonObject.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
