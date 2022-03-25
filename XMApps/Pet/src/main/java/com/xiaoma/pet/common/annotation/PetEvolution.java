package com.xiaoma.pet.common.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/31 0031 11:31
 *   desc:   宠物进化
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({PetEvolution.BABY,
        PetEvolution.CHILDHOOD,
        PetEvolution.JUVENILE,
        PetEvolution.YOUNG,
        PetEvolution.STRONG})
public @interface PetEvolution {

    String BABY = "幼儿";
    String CHILDHOOD = "童年";
    String JUVENILE = "少年";
    String YOUNG = "青年";
    String STRONG = "壮年";
}
