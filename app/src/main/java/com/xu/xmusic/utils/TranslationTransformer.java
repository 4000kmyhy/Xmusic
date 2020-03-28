package com.xu.xmusic.utils;

import android.support.v4.view.ViewPager;
import android.view.View;

public class TranslationTransformer implements ViewPager.PageTransformer {

    private static final float MIN_ALPHA = 0.0f;

    @Override
    public void transformPage(View page, float position) {
        if (position < 0) {
            page.setTranslationX(-position * page.getWidth());
            page.setTranslationZ(position);
            float alphaA = MIN_ALPHA + (1 - MIN_ALPHA) * (1 + position);
            page.setAlpha(alphaA);
        }
    }
}
