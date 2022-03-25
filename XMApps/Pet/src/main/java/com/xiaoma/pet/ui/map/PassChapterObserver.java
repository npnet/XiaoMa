package com.xiaoma.pet.ui.map;

import com.xiaoma.pet.model.PetInfo;
import com.xiaoma.pet.model.PetMapInfo;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/22 0022 16:07
 *   desc:
 * </pre>
 */
public interface PassChapterObserver {

    void passChapter(boolean complete, PetInfo petInfo, PetMapInfo mapInfo);
}
