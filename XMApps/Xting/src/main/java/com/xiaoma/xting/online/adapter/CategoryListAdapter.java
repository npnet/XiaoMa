package com.xiaoma.xting.online.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.xiaoma.xting.online.model.INamed;
import com.xiaoma.xting.online.model.ProvinceBean;
import com.xiaoma.xting.online.model.TagBean;
import com.xiaoma.xting.online.ui.CategoryAlbumChildFragment;
import com.xiaoma.xting.online.ui.CategoryRadioChildFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
public class CategoryListAdapter<T extends INamed> extends FragmentStatePagerAdapter {
    private List<T> childCategoryBeans;
    private long categoryId;
    private Fragment mCurrentFragment;

    public CategoryListAdapter(ArrayList<T> childCategoryBeans, FragmentManager fm) {
        super(fm);
        this.childCategoryBeans = childCategoryBeans;
    }

    public void setChildCategories(long categoryId, List<T> childCategoryBeans) {
        this.childCategoryBeans = childCategoryBeans;
        this.categoryId = categoryId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return childCategoryBeans.size();
    }

    @Override
    public Fragment getItem(int position) {
        T t = childCategoryBeans.get(position);
        if (t instanceof TagBean) {
            return CategoryAlbumChildFragment.newInstance(categoryId, (TagBean) t);
        } else if (t instanceof ProvinceBean) {
            return CategoryRadioChildFragment.newInstance((ProvinceBean) t);
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return childCategoryBeans.get(position).getName();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentFragment = (Fragment) object;
        super.setPrimaryItem(container, position, object);
    }


    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
