package com.xiaoma.login.business.ui.infoview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.xiaoma.login.R;
import com.xiaoma.ui.view.WheelPickView;

import java.util.ArrayList;
import java.util.List;

public class PersonalRelationDialog extends BasePersonalInfoDialog implements View.OnClickListener {

    public static final int WIDTH = 650;
    public static final int HEIGHT = 350;
    private int index;
    private WheelPickView mRelation;

    @Override
    protected boolean isCancelableOutside() {
        return false;
    }

    @Override
    protected int contentLayoutId() {
        return R.layout.dialog_personal_relation;
    }


    @Override
    protected WindowManager.LayoutParams changeWindowParams(WindowManager.LayoutParams lp) {
        lp.width = WIDTH;
        lp.height = HEIGHT;
        return lp;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.personal_relation);
    }

    @Override
    public void onBindView(View view) {
        mRelation = view.findViewById(R.id.relation);
        List<String> relations = new ArrayList<>(4);
        relations.add(getString(R.string.self));
        relations.add(getString(R.string.family));
        relations.add(getString(R.string.friends));
        relations.add(getString(R.string.loan));
        mRelation.setItems(relations);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mRelation.setSelection(index);
    }

    @Override
    protected void onSure() {
        onSuccessResult(mRelation.getSeletedItem());
        index = mRelation.getSeletedIndex();
    }
}
