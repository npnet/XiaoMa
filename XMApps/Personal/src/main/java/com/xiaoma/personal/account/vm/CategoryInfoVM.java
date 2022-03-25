package com.xiaoma.personal.account.vm;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.personal.R;
import com.xiaoma.personal.account.model.CategoryInfo;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gillben
 * date: 2018/12/03
 */
public class CategoryInfoVM extends BaseViewModel {

    private int[] icons; // 保证三者同步修改

    private int[] titleResId; // 保证三者同步修改

    private int[] itemFlag; // 保证三者同步修改

    public CategoryInfoVM(@NonNull Application application) {
        super(application);
    }

    public List<CategoryInfo> getHomeList(Context context) {
//        String[] titles = getApplication().getResources().getStringArray(R.array.OperateItem);
        setIconsAndTitleResId();
        List<CategoryInfo> infoArrayList = new ArrayList<>();
        for (int i = 0; i < titleResId.length; i++) {
            infoArrayList.add(new CategoryInfo(icons[i], context.getString(titleResId[i]), itemFlag[i]));
        }
        return infoArrayList;
    }

    private void setIconsAndTitleResId() {
        if (icons == null) {
            icons = getIcons();
        }
        if (titleResId == null) {
            titleResId = getTitleResId();
        }
        if (itemFlag == null) {
            itemFlag = getItemFlag();
        }
    }

    /**
     * 如果是游客模式屏蔽新手模式入口
     *
     * @return
     */
    private int[] getIcons() {
        if (LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.BEGINNER_S_GUIDE)) {
            return new int[]{R.drawable.icon_personal_my_coin, R.drawable.icon_personal_account_manager,
                    R.drawable.icon_personal_task_center, R.drawable.icon_personal_feedback,
                    R.drawable.icon_personal_my_order, R.drawable.icon_personal_new_gay,
                    R.drawable.icon_personal_car_info, R.drawable.icon_personal_qr_code};
        }
        return new int[]{R.drawable.icon_personal_my_coin, R.drawable.icon_personal_account_manager,
                R.drawable.icon_personal_task_center, R.drawable.icon_personal_feedback,
                R.drawable.icon_personal_my_order, R.drawable.icon_personal_car_info,
                R.drawable.icon_personal_qr_code};
    }

    /**
     * 如果是游客模式屏蔽新手模式入口
     *
     * @return
     */
    private int[] getTitleResId() {
        if (LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.BEGINNER_S_GUIDE)) {
            return new int[]{R.string.car_coin_title, R.string.account_setup_title,
                    R.string.task_center_title, R.string.feedback_title, R.string.mine_order_title,
                    R.string.new_mode_title, R.string.car_info_title, R.string.qr_code_manager_title};
        }
        return new int[]{R.string.car_coin_title, R.string.account_setup_title,
                R.string.task_center_title, R.string.feedback_title, R.string.mine_order_title,
                R.string.car_info_title, R.string.qr_code_manager_title};
    }

    private int[] getItemFlag() {
        if (LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.BEGINNER_S_GUIDE)) {
            return new int[]{
                    ItemFlag.CAR_COIN,
                    ItemFlag.ACCOUNT,
                    ItemFlag.TASK_CENTER,
                    ItemFlag.FEED_BACK,
                    ItemFlag.ORDER,
                    ItemFlag.NEW_GUIDE,
                    ItemFlag.CAR_INFO,
                    ItemFlag.CODE_MANAGER};
        }
        return new int[]{
                ItemFlag.CAR_COIN,
                ItemFlag.ACCOUNT,
                ItemFlag.TASK_CENTER,
                ItemFlag.FEED_BACK,
                ItemFlag.ORDER,
                ItemFlag.CAR_INFO,
                ItemFlag.CODE_MANAGER};
    }

}
