package com.xu.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchToolbar extends FrameLayout {

    private ImageView mLeftBtn;
    private TextView mRightBtn;
    private EditText mInput;

    public interface OnSearchListener {
        void afterTextChanged(String s);
    }

    private OnSearchListener onSearchListener;

    public SearchToolbar(Context context) {
        this(context, null);
    }

    public SearchToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
        initAttr(attrs);
        initEvent();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.search_toolbar, this);
        mLeftBtn = view.findViewById(R.id.toolbar_left_btn);
        mRightBtn = view.findViewById(R.id.toolbar_right_btn);
        mInput = view.findViewById(R.id.toolbar_input);
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SearchToolbar);
        int leftBtnRes = typedArray.getResourceId(R.styleable.SearchToolbar_search_left_btn, 0);
        boolean isShowRight = typedArray.getBoolean(R.styleable.SearchToolbar_is_show_right, false);
        String inputHint = typedArray.getString(R.styleable.SearchToolbar_input_hint);
        typedArray.recycle();

        if (leftBtnRes != 0) {
            mLeftBtn.setVisibility(VISIBLE);
            mLeftBtn.setImageResource(leftBtnRes);
        }
        if (isShowRight) {
            mRightBtn.setVisibility(VISIBLE);
        }
        if (!TextUtils.isEmpty(inputHint)) {
            mInput.setHint(inputHint);
        }
    }

    private void initEvent() {
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    setClearIconVisible(true);
                } else {
                    setClearIconVisible(false);
                }
                if (onSearchListener != null) {
                    onSearchListener.afterTextChanged(s.toString());
                }
            }
        });
    }

    private void setClearIconVisible(boolean visible) {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_toolbar_del);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

        if (visible) {
            mInput.setCompoundDrawables(mInput.getCompoundDrawables()[0], null, drawable, null);

            new TextDrawableUtils(mInput, new TextDrawableUtils.OnDrawableListener() {
                @Override
                public void onLeft(View v, Drawable left) {

                }

                @Override
                public void onRight(View v, Drawable right) {
                    String s = mInput.getText().toString();
                    if (!TextUtils.isEmpty(s)) {
                        mInput.setText("");
                    }
                }
            });
        } else {
            mInput.setCompoundDrawables(mInput.getCompoundDrawables()[0], null, null, null);
        }
    }

    public SearchToolbar setPaddingTop(int top) {
        setPadding(0, top, 0, 0);
        return this;
    }

    public SearchToolbar setHint(String hint) {
        mInput.setHint(hint);
        return this;
    }

    public SearchToolbar setText(String text) {
        mInput.setText(text);
        return this;
    }

    public String getText() {
        return mInput.getText().toString();
    }

    public SearchToolbar setLeftBtn(int res) {
        mLeftBtn.setVisibility(VISIBLE);
        mLeftBtn.setImageResource(res);
        return this;
    }

    public SearchToolbar setIsShowRight(boolean isShow) {
        mRightBtn.setVisibility(isShow ? VISIBLE : GONE);
        return this;
    }

    public SearchToolbar setLeftBtnOnClickListener(OnClickListener onClickListener) {
        mLeftBtn.setOnClickListener(onClickListener);
        return this;
    }

    public SearchToolbar setRightBtnOnClickListener(OnClickListener onClickListener) {
        mRightBtn.setOnClickListener(onClickListener);
        return this;
    }

    public SearchToolbar setEditorSearchListener(TextView.OnEditorActionListener listener) {
        mInput.setOnEditorActionListener(listener);
        return this;
    }

    public SearchToolbar setOnSearchListener(OnSearchListener listener) {
        onSearchListener = listener;
        return this;
    }
}
