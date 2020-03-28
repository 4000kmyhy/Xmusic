package com.xu.toolbar;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TextDrawableUtils {

    /**
     * TextView四周drawable的序号。
     * 0 left,  1 top, 2 right, 3 bottom
     */
    private final int LEFT = 0;
    private final int RIGHT = 2;

    private OnDrawableListener mListener;
    private View mView;
    private Drawable drawableLeft, drawableRight;

    public TextDrawableUtils(View view, OnDrawableListener listener) {
        mView = view;
        mView.setOnTouchListener(mOnTouchListener);
        mListener = listener;

        if (mView instanceof TextView) {
            drawableLeft = ((TextView) mView).getCompoundDrawables()[LEFT];
            drawableRight = ((TextView) mView).getCompoundDrawables()[RIGHT];
        } else if (mView instanceof EditText) {
            drawableLeft = ((EditText) mView).getCompoundDrawables()[LEFT];
            drawableRight = ((EditText) mView).getCompoundDrawables()[RIGHT];
        }
    }

    public interface OnDrawableListener {
        void onLeft(View v, Drawable left);

        void onRight(View v, Drawable right);
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (mListener != null) {
                        if (drawableLeft != null && event.getRawX() <= (mView.getLeft() + drawableLeft.getBounds().width())) {
                            mListener.onLeft(v, drawableLeft);
                            return true;
                        }

                        if (drawableRight != null && event.getRawX() >= (mView.getRight() - drawableRight.getBounds().width())) {
                            mListener.onRight(v, drawableRight);
                            return true;
                        }
                    }
                    break;
            }

            return false;
        }

    };
}
