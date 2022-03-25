package com.xiaoma.xting.assistant;

import com.xiaoma.xting.sdk.bean.XMRadio;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/26
 */
public class AssistantRepository {

    private List<XMRadio> mXMRadios;

    private AssistantRepository() {
        mXMRadios = new ArrayList<>();
    }

    public static AssistantRepository newSingleton() {
        return Holder.sINSTANCE;
    }

    public void updateRadios(List<XMRadio> list) {
        mXMRadios.clear();
        mXMRadios.addAll(list);
    }

    public List<XMRadio> getRadios() {
        return mXMRadios;
    }

    interface Holder {
        AssistantRepository sINSTANCE = new AssistantRepository();
    }
}
