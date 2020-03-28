package com.xu.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleToolbar extends FrameLayout {

    private static final String TAG = "SimpleToolbar";

    private ImageView mLeftBtn, mRightBtn;
    private TextView mTitle, mSubtitle;
    private LinearLayout mTitleLayout;

    public SimpleToolbar(Context context) {
        this(context, null);
    }

    public SimpleToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
        initAttr(attrs);
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.simple_toolbar, this);
        mLeftBtn = view.findViewById(R.id.toolbar_left_btn);
        mRightBtn = view.findViewById(R.id.toolbar_right_btn);
        mTitle = view.findViewById(R.id.toolbar_title);
        mSubtitle = view.findViewById(R.id.toolbar_subtitle);
        mTitleLayout = view.findViewById(R.id.toolbar_title_layout);
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SimpleToolbar);
        String title = typedArray.getString(R.styleable.SimpleToolbar_title);
        String subtitle = typedArray.getString(R.styleable.SimpleToolbar_subtitle);
        int leftImg = typedArray.getResourceId(R.styleable.SimpleToolbar_left_btn, 0);
        int rightImg = typedArray.getResourceId(R.styleable.SimpleToolbar_right_btn, 0);
        typedArray.recycle();

        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
            mTitle.setSelected(true);
        }
        if (!TextUtils.isEmpty(subtitle)) {
            mSubtitle.setVisibility(VISIBLE);
            mSubtitle.setText(subtitle);
            mSubtitle.setSelected(true);
        }
        if (leftImg != 0) {
            mLeftBtn.setImageResource(leftImg);
        }
        if (rightImg != 0) {
            mRightBtn.setImageResource(rightImg);
        }
    }

    public TextView getTitleView(){
        return mTitle;
    }

    public SimpleToolbar setPaddingTop(int top) {
        setPadding(0, top, 0, 0);
        return this;
    }

    public SimpleToolbar setTitle(String title) {
        mTitle.setText(title);
        mTitle.setSelected(true);
        return this;
    }

    public SimpleToolbar setSubtitle(String subtitle) {
        mSubtitle.setVisibility(VISIBLE);
        mSubtitle.setText(subtitle);
        mSubtitle.setSelected(true);
        return this;
    }

    public void removeSubtitle() {
        mSubtitle.setVisibility(GONE);
    }

    public SimpleToolbar setTitleGravity(int gravity) {
        mTitleLayout.setGravity(gravity);
        return this;
    }

    public SimpleToolbar setLeftBtn(int res) {
        mLeftBtn.setImageResource(res);
        return this;
    }

    public SimpleToolbar setRightBtn(int res) {
        mRightBtn.setImageResource(res);
        return this;
    }

    public SimpleToolbar setLeftBtnOnClickListener(OnClickListener onClickListener) {
        mLeftBtn.setOnClickListener(onClickListener);
        return this;
    }

    public SimpleToolbar setRightBtnOnClickListener(OnClickListener onClickListener) {
        mRightBtn.setOnClickListener(onClickListener);
        return this;
    }
}
