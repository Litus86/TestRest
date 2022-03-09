package com.restAPI;

import java.io.Serializable;

public class Configuration implements Serializable, Comparable<Configuration> {
    public Configuration() {}

    static final long serialVersionUID = 1L;
    private String presetTitle;
    private int temperature;
    private String time;
    private int id;

    public void setPresetTitle (final String presetTitle){ this.presetTitle = presetTitle;}
    public String getPresetTitle(){return this.presetTitle;}
    public void setTemperature(final int temperature){this.temperature = temperature;}
    public int getTemperature(){return this.temperature;}
    public void setTime(final String time){this.time = time;}
    public String getTime(){return this.time;}
    public void setId(final int id){this.id = id;}
    public int getId(){return this.id;}


    public int compareTo(final Configuration o) {
        return this.id - o.id;
    }
}
