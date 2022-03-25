package com.xiaoma.instruction.ui.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.instruction.R;
import com.xiaoma.instruction.common.constant.InstructionConstants;
import com.xiaoma.instruction.view.ObservableScrollView;
import com.xiaoma.instruction.view.VerticalScrollBar;
import com.xiaoma.utils.StringUtil;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/10
 *     desc   :
 * </pre>
 */
@SuppressLint("ValidFragment")
public class GeneralFragment extends BaseFragment {


    private ImageView mManualImgView;
    private TextView mManualTextView;
    private VerticalScrollBar mScrollBar;
    private ObservableScrollView mScrollView;

    public static GeneralFragment newInstance(String manualText, String imgUrl) {
        GeneralFragment orderFragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putString(InstructionConstants.MANUAL_TEXT, manualText);
        args.putString(InstructionConstants.MANUAL_URL, imgUrl);
        orderFragment.setArguments(args);

        return orderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_general, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mManualImgView = view.findViewById(R.id.iv_content);
        mManualTextView = view.findViewById(R.id.manual_text);
        mScrollBar = view.findViewById(R.id.sisclamer_scrollbar);
        mScrollView = view.findViewById(R.id.sisclamer_scrollview);
        initData();
    }

    private void initData() {
        String manualText = getArguments().getString(InstructionConstants.MANUAL_TEXT);
        String manualImg = getArguments().getString(InstructionConstants.MANUAL_URL);
        if (StringUtil.isNotEmpty(manualImg)) {
            ImageLoader.with(getContext()).load(manualImg).into(mManualImgView);
        }
        if (StringUtil.isNotEmpty(manualText)) {
            manualText = manualText.replace("\\u3000","\u3000").replace("\\r","\r").replace("\\n","\n");
            mManualTextView.setText(manualText);
        }
        mScrollBar.setScrollView(mScrollView);
    }


}
