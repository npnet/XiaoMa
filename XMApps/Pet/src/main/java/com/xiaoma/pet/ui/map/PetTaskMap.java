package com.xiaoma.pet.ui.map;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.common.utils.ConvertMapTimeCoordinate;
import com.xiaoma.pet.common.utils.SavePetInfoUtils;
import com.xiaoma.pet.model.PetInfo;
import com.xiaoma.pet.model.PetMapInfo;
import com.xiaoma.pet.ui.view.MapTrackView;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Gillben on 2019/1/2 0002
 * <p>
 * desc: 宠物任务地图
 */
public class PetTaskMap extends PopupWindow {

    private static final String TAG = PetTaskMap.class.getSimpleName();
    private static final int DEFAULT_WIDTH = 1396;
    private Context mContext;

    //闯关
    private ConstraintLayout mPassChapterLayout;
    private TextView mPassChapterTitle;
    private ImageView mPetIcon;
    private Button mNewTrip;

    //地图
    private ConstraintLayout mapContentLayout;
    private TextView mCityNameText;
    private TextView mPromptText;
    private MapTrackView mapTrackView;
    private TextView driveDistanceText;
    private ImageView mCarView;
    private IPassChapterListener mPassChapterListener;

    public PetTaskMap(Context context, boolean complete, PetInfo petInfo, PetMapInfo petMapInfo) {
        this.mContext = context;
        initView(context);
        initAttrs(complete);
        initData(complete, petInfo, petMapInfo);
    }


    private void initAttrs(boolean complete) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width = complete ? dm.widthPixels : DEFAULT_WIDTH;
        int height = dm.heightPixels;
        setWidth(width);
        setHeight(height);
        setFocusable(true);
        setTouchable(true);
        setClippingEnabled(false);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.PetMapPopupAnimation);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.pet_task_map, null);
        bindView(view);
        setContentView(view);
    }

    private void bindView(View view) {
        mapContentLayout = view.findViewById(R.id.map_content_layout);
        mPassChapterLayout = view.findViewById(R.id.title_prompt_layout);
        mPassChapterTitle = view.findViewById(R.id.tv_prompt_title);
        driveDistanceText = view.findViewById(R.id.tv_map_travel_text);
        mCarView = view.findViewById(R.id.iv_map_car_icon);
        mPetIcon = view.findViewById(R.id.iv_prompt_pet_icon);
        mNewTrip = view.findViewById(R.id.prompt_bt);
        mPromptText = view.findViewById(R.id.tv_map_prompt);
        mCityNameText = view.findViewById(R.id.tv_city_name);

        mNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PetTaskMap.this.dismiss();
                if (mPassChapterListener != null) {
                    mPassChapterListener.startNewTrip();
                }
            }
        });

        //退出
        view.findViewById(R.id.map_navigation_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


    private void initData(boolean complete, PetInfo petInfo, PetMapInfo petMapInfo) {
        if (complete) {
            mapContentLayout.setVisibility(View.GONE);
            mPassChapterLayout.setVisibility(View.VISIBLE);
            mPassChapterTitle.setText(mContext.getString(R.string.pass_chapter_success_prompt, petMapInfo.getChapterName()));
            mNewTrip.setText(R.string.start_new_travel);
            passChapterSuccess(petInfo, petMapInfo.getChapterNextId());
        } else {
            mPassChapterLayout.setVisibility(View.GONE);
            mapContentLayout.setVisibility(View.VISIBLE);

            int totalKm = Integer.parseInt(petMapInfo.getMileage());
            int surplusKM = ConvertMapTimeCoordinate.getSurplusKM(totalKm, petInfo.getTimeAccumulation());
            int driveDistance = ConvertMapTimeCoordinate.driverDistance(petInfo.getTimeAccumulation());
            mCityNameText.setText(petMapInfo.getChapterName());
            mPromptText.setText(mContext.getString(R.string.pet_map_prompt_format, surplusKM));
            driveDistanceText.setText(mContext.getString(R.string.drive_distance_format, driveDistance));

            float driverPercent = driveDistance > 0 ? driveDistance / (float) totalKm : 0;
            updateCarLocation(driverPercent);
        }
    }


    private void updateCarLocation(@FloatRange(from = 0.0, to = 1.0) float ratio) {
        int[] coordinate = ConvertMapTimeCoordinate.convert(ratio);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mCarView.getLayoutParams();
        layoutParams.leftMargin = coordinate[0];
        layoutParams.topMargin = coordinate[1];
        mCarView.setLayoutParams(layoutParams);
        mCarView.setImageLevel(coordinate[2]);
        update();
    }


    private void passChapterSuccess(final PetInfo petInfo, final long newChapterId) {
        RequestManager.postMapCompleteTime(0, newChapterId, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                KLog.w(TAG, "通关累积计数器绑定成功");
                petInfo.setChapterId(newChapterId);
                SavePetInfoUtils.save(petInfo);
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.w(TAG, "通关累积计数器绑定失败");
            }
        });
    }


    public void setPassChapterListener(IPassChapterListener listener) {
        this.mPassChapterListener = listener;
    }


    public interface IPassChapterListener {
        void startNewTrip();
    }

}
