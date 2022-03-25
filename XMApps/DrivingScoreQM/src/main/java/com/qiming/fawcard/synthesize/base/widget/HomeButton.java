package com.qiming.fawcard.synthesize.base.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qiming.fawcard.synthesize.R;

public class HomeButton extends FrameLayout {
    public HomeButton(Context context) {
        super(context);
        init(context);
    }

    public HomeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // 加载布局
        View view = LayoutInflater.from(context).inflate(R.layout.back_home, null);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ImageView imageView = view.findViewById(R.id.common);
        imageView.setImageResource(R.drawable.home);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                (((Activity) v.getContext()).getApplication()).startActivity(intent);
            }
        });
        this.addView(view);
    }
}
