package com.xiaoma.autotracker.factory;

import com.xiaoma.autotracker.handle.BaseHandle;
import com.xiaoma.autotracker.handle.OnClickHandle;
import com.xiaoma.autotracker.handle.OnItemClickBQRvHandle;
import com.xiaoma.autotracker.handle.OnItemClickLvGvHandle;
import com.xiaoma.autotracker.handle.OnItemClickRvHandle;
import com.xiaoma.autotracker.handle.OnRadioGroupClickHandle;
import com.xiaoma.autotracker.handle.OnTabSelectedListenerHandle;

/**
 * Created by Thomas on 2018/12/6 0006
 * AOP point cut factory
 */

public class AspectHandleFactory {

   private static AspectHandleFactory aspectHandleFactory = new AspectHandleFactory();
   private OnClickHandle onClickHandle;
    private OnItemClickRvHandle onItemClickRvHandle;
    private OnItemClickLvGvHandle onItemClickLvGvHandle;
    private OnItemClickBQRvHandle onItemClickBQRvHandle;
    private OnTabSelectedListenerHandle mOnTabSelectedListenerHandle;
    private OnRadioGroupClickHandle mOnRadioGroupClickHandle;

    public static AspectHandleFactory getInstance() {
       return aspectHandleFactory;
   }

   private AspectHandleFactory() {
       init();
   }

    private void init() {
        onClickHandle = new OnClickHandle();
        onItemClickRvHandle = new OnItemClickRvHandle();
        onItemClickLvGvHandle = new OnItemClickLvGvHandle();
        onItemClickBQRvHandle = new OnItemClickBQRvHandle();
        mOnTabSelectedListenerHandle = new OnTabSelectedListenerHandle();
        mOnRadioGroupClickHandle = new OnRadioGroupClickHandle();
    }

    public BaseHandle getOnClickHandle() {
        return onClickHandle;
    }

    public BaseHandle getOnItemClickRvHandle() {
        return onItemClickRvHandle;
    }

    public OnItemClickLvGvHandle getOnItemClickLvGvHandle() {
        return onItemClickLvGvHandle;
    }

    public OnItemClickBQRvHandle getOnItemClickBQRvHandle() {
        return onItemClickBQRvHandle;
    }

    public OnTabSelectedListenerHandle getOnTabSelectedListenerHandle() {
        return mOnTabSelectedListenerHandle;
    }

    public OnRadioGroupClickHandle getOnRadioGroupClickHandle() {
        return mOnRadioGroupClickHandle;
    }
}
