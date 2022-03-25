package com.limpidj;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.TypeElement;

/**
 */
public class AllBuilderData {

    private HashMap<Object, BuilderData> map = new HashMap<>();

    // 该缓存为了尝试直接命中以提高执行效率
    private Object keyHitCache = null;
    private BuilderData valueHitCache = null;

    private AllBuilderData() {
    }

    public static AllBuilderData getOrCreateAllBuilderData() {
        ContextUtils.Context context = ContextUtils.getContext();
        if (context.allBuilderData == null) {
            context.allBuilderData = new AllBuilderData();
        }
        return context.allBuilderData;
    }

    public BuilderData getOrCreate(Object k) {
        if (k.equals(keyHitCache)) {
            return valueHitCache;
        }
        BuilderData v = map.get(k);
        if (v == null) {
            v = new BuilderData();
            if (k instanceof TypeElement) {
                v.add(TypeElement.class.getName(), k);
            }

            map.put(k, v);
        }
        keyHitCache = k;
        return valueHitCache = v;
    }

    public Set<Map.Entry<Object, BuilderData>> entrySet() {
        return map.entrySet();
    }
}
