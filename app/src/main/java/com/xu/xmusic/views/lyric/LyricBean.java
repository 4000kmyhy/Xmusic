package com.xu.xmusic.views.lyric;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class LyricBean implements Comparable<LyricBean> {
    private long time;
    private String text;
    private StaticLayout staticLayout;
    /**
     * 歌词距离视图顶部的距离
     */
    private float offset = Float.MIN_VALUE;
    public static final int GRAVITY_CENTER = 0;
    public static final int GRAVITY_LEFT = 1;
    public static final int GRAVITY_RIGHT = 2;

    public LyricBean(long time, String text) {
        this.time = time;
        this.text = text;
    }

    public void init(TextPaint paint, int width, int gravity) {
        Layout.Alignment align;
        switch (gravity) {
            case GRAVITY_LEFT:
                align = Layout.Alignment.ALIGN_NORMAL;
                break;

            default:
            case GRAVITY_CENTER:
                align = Layout.Alignment.ALIGN_CENTER;
                break;

            case GRAVITY_RIGHT:
                align = Layout.Alignment.ALIGN_OPPOSITE;
                break;
        }
        staticLayout = new StaticLayout(getShowText(), paint, width, align, 1f, 0f, false);

        offset = Float.MIN_VALUE;
    }

    public long getTime() {
        return time;
    }

    public StaticLayout getStaticLayout() {
        return staticLayout;
    }

    public int getHeight() {
        if (staticLayout == null) {
            return 0;
        }
        return staticLayout.getHeight();
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public String getText() {
        return text;
    }

    private String getShowText() {
        return text;
    }

    @Override
    public int compareTo(LyricBean entry) {
        if (entry == null) {
            return -1;
        }
        return (int) (time - entry.getTime());
    }
}
