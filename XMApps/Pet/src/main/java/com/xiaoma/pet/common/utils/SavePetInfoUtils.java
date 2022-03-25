package com.xiaoma.pet.common.utils;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.pet.model.PetInfo;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.encrypt.AESUtils;

/**
 * Created by Gillben
 * date: 2019/2/18 0018
 */
public final class SavePetInfoUtils {

    private static final String PET_INFO_ENCRYPT_AES_KEY = "xiaomalixing0001";

    private SavePetInfoUtils() {
        throw new RuntimeException("Can't create instance.");
    }

    public static synchronized void save(PetInfo petInfo) {
        if (petInfo == null) {
            return;
        }
        String infoJson = GsonHelper.toJson(petInfo);
        String encrypt = AESUtils.encryp(infoJson, PET_INFO_ENCRYPT_AES_KEY);
        FileUtils.write(encrypt, ConfigManager.FileConfig.getPetInfoFile(), false);
    }


    public static PetInfo readPetInfo() {
        String infoJson = FileUtils.read(ConfigManager.FileConfig.getPetInfoFile());
        String decrypt = AESUtils.decrypt(infoJson, PET_INFO_ENCRYPT_AES_KEY);
        return GsonHelper.fromJson(decrypt, PetInfo.class);
    }

}
