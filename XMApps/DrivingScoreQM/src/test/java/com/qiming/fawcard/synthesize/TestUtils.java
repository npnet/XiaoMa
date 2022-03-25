package com.qiming.fawcard.synthesize;

import android.app.Activity;

import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDao;

import org.robolectric.Robolectric;

import java.lang.reflect.Field;

public class TestUtils {
    public static void resetSingleton(Class clazz, String fieldName) {
        Field instance;
        try {
            instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static void creatHistoryEntity(Long startTime, Double score, Long turnNum, Long decNum, Long accNum) {
        DriveScoreHistoryEntity driveScoreHistoryEntity = setDriveScoreHistoryEntity(startTime, score, turnNum, decNum, accNum);
        Activity activity = Robolectric.setupActivity(Activity.class);
        DriveScoreHistoryDao mDriveScoreHistoryDao = new DriveScoreHistoryDao(activity);
        mDriveScoreHistoryDao.create(driveScoreHistoryEntity);
    }

    private static DriveScoreHistoryEntity setDriveScoreHistoryEntity(Long startTime, Double score, Long turnNum, Long decNum, Long accNum) {
        DriveScoreHistoryEntity data = new DriveScoreHistoryEntity();
        data.startTime = startTime;
        data.score = score;
        data.turnNum = turnNum;
        data.decNum = decNum;
        data.accNum = accNum;
        return data;
    }

}
