package com.xiaoma.skin.inflater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.skin.views.XmSkinRecyclerView;

import skin.support.app.SkinLayoutInflater;
import skin.support.widget.SkinCompatAutoCompleteTextView;
import skin.support.widget.SkinCompatButton;
import skin.support.widget.SkinCompatCheckBox;
import skin.support.widget.SkinCompatCheckedTextView;
import skin.support.widget.SkinCompatEditText;
import skin.support.widget.SkinCompatImageButton;
import skin.support.widget.SkinCompatImageView;
import skin.support.widget.SkinCompatMultiAutoCompleteTextView;
import skin.support.widget.SkinCompatRadioButton;
import skin.support.widget.SkinCompatRatingBar;
import skin.support.widget.SkinCompatSeekBar;
import skin.support.widget.SkinCompatSpinner;
import skin.support.widget.SkinCompatTextView;
import skin.support.widget.SkinCompatToolbar;

/**
 * Created by Thomas on 2018/12/19 0019
 */

public class XmSkinAndroidSupportV7ViewInflater implements SkinLayoutInflater {

    @Override
    public View createView(@NonNull Context context, final String name, @NonNull AttributeSet attrs) {
        View view = null;
        switch (name) {
            case "android.support.v7.widget.AppCompatTextView":
                view = new SkinCompatTextView(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatAutoCompleteTextView":
                view = new SkinCompatAutoCompleteTextView(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatButton":
                view = new SkinCompatButton(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatCheckBox":
                view = new SkinCompatCheckBox(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatCheckedTextView":
                view = new SkinCompatCheckedTextView(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatEditText":
                view = new SkinCompatEditText(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatImageButton":
                view = new SkinCompatImageButton(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatImageView":
                view = new SkinCompatImageView(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatMultiAutoCompleteTextView":
                view = new SkinCompatMultiAutoCompleteTextView(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatRadioButton":
                view = new SkinCompatRadioButton(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatRatingBar":
                view = new SkinCompatRatingBar(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatSeekBar":
                view = new SkinCompatSeekBar(context, attrs);
                break;
            case "android.support.v7.widget.AppCompatSpinner":
                view = new SkinCompatSpinner(context, attrs);
                break;
            case "android.support.v7.widget.Toolbar":
                view = new SkinCompatToolbar(context, attrs);
                break;
            case "android.support.v7.widget.RecyclerView":
                view = new XmSkinRecyclerView(context, attrs);
                break;
        }
        return view;
    }

}
