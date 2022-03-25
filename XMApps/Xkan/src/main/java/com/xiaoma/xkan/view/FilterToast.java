package com.xiaoma.xkan.view;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.xkan.view
 *  @file_name:      FilterToast
 *  @author:         Rookie
 *  @create_time:    2018/12/18 15:20
 *  @description：   筛选toast             */

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.xkan.R;

public class FilterToast {


    private static Toast sToast;

    public static void showFilterToast(Context context, String filterStr) {
        context = context.getApplicationContext();
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
        sToast = new Toast(context);
        //设置Toast显示位置，居左上角，X Y轴偏移量为HMI
        sToast.setGravity(Gravity.TOP | Gravity.START, 1035, 311);
        //获取自定义视图
        View view = LayoutInflater.from(context).inflate(R.layout.view_filter_toast, null);
        TextView tvStart = view.findViewById(R.id.tv_filter_start);
        TextView tvEnd = view.findViewById(R.id.tv_filter_end);
        //设置文本
        String[] split = filterStr.split("->");
        tvStart.setText(split[0]);
        tvEnd.setText(split[1]);
        //设置视图
        sToast.setView(view);
        //设置显示时长
        sToast.setDuration(Toast.LENGTH_SHORT);
        //显示
        sToast.show();
    }
}
