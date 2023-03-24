package com.android.launcher3.bean;

/**
 * @author tomchen
 * @date 6/14/22
 */
public class HotSeatInfo {
    public String container;
    public String screen;
    public String x;
    public String y;
    public String packageName;
    public String className;

    public HotSeatInfo(String container, String screen, String x, String y) {
        this.container = container;
        this.screen = screen;
        this.x = x;
        this.y = y;
    }
}
