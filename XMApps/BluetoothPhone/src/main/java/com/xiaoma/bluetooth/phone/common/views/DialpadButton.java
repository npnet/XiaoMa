/*
 * Copyright (c) 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaoma.bluetooth.phone.common.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.bluetooth.phone.R;

/**
 * A View that represents a single button on the dialpad. This View display a number above letters
 * or an image.
 */
public class DialpadButton extends FrameLayout {
    private static final int INVALID_IMAGE_RES = -1;

    private String mNumberText;
    private String mLetterText;
    private int mImageRes = INVALID_IMAGE_RES;
    private int mTextColor;
    private float mTextSize;

    public DialpadButton(Context context) {
        this(context, null);
    }

    public DialpadButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialpadButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.dialpad_button, this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DialpadButton);
        Resources resources = getResources();
        try {
            mNumberText = ta.getString(R.styleable.DialpadButton_numberText);
            mLetterText = ta.getString(R.styleable.DialpadButton_letterText);
            mImageRes = ta.getResourceId(R.styleable.DialpadButton_image, INVALID_IMAGE_RES);
            mTextColor = ta.getColor(R.styleable.DialpadButton_textColor, context.getColor(R.color.dialpad_number_default_text_color));
            mTextSize = ta.getDimension(R.styleable.DialpadButton_textSize, resources.getDimensionPixelSize(R.dimen.dialpad_number_default_text_size));
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        if (mNumberText != null) {
            TextView numberTextView = findViewById(R.id.dialpad_number);
            numberTextView.setText(mNumberText);
            numberTextView.setTextSize(mTextSize);
            numberTextView.setTextColor(mTextColor);
            numberTextView.setVisibility(VISIBLE);
        }

        if (mLetterText != null) {
            TextView letterTextView = findViewById(R.id.dialpad_letters);
            letterTextView.setText(mLetterText);
            letterTextView.setVisibility(VISIBLE);
        }

        if (mImageRes != INVALID_IMAGE_RES) {
            ImageView imageView = findViewById(R.id.dialpad_image);
            imageView.setImageResource(mImageRes);
            imageView.setVisibility(VISIBLE);
        }
    }
}
