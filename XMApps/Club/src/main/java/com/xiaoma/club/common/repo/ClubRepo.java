package com.xiaoma.club.common.repo;

import android.app.ActivityManager;
import android.app.Service;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.xiaoma.club.BuildConfig;
import com.xiaoma.club.common.model.Friendship;
import com.xiaoma.club.common.model.PushDisabledConversation;
import com.xiaoma.club.common.model.PushedNotification;
import com.xiaoma.club.common.repo.impl.FriendshipRepo;
import com.xiaoma.club.common.repo.impl.GroupRepo;
import com.xiaoma.club.common.repo.impl.PushDisabledConversationRepo;
import com.xiaoma.club.common.repo.impl.PushedNotificationRepo;
import com.xiaoma.club.common.repo.impl.UserRepo;
import com.xiaoma.club.contact.model.ContactGroup;
import com.xiaoma.club.contact.model.ContactUser;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.contact.repo.ContactGroupRepo;
import com.xiaoma.club.contact.repo.ContactUserRepo;
import com.xiaoma.club.discovery.model.DiscoverGroup;
import com.xiaoma.club.discovery.model.SearchHistoryInfo;
import com.xiaoma.club.discovery.repo.DiscoverRepo;
import com.xiaoma.club.discovery.repo.SearchHistoryInfoRepo;
import com.xiaoma.club.msg.chat.model.FaceQuickInfo;
import com.xiaoma.club.msg.chat.model.GroupMuteUser;
import com.xiaoma.club.msg.chat.repo.FaceRepo;
import com.xiaoma.club.msg.chat.repo.GroupMuteUserRepo;
import com.xiaoma.club.msg.conversation.model.TopConversation;
import com.xiaoma.club.msg.conversation.repo.TopConversationRepo;
import com.xiaoma.model.User;

import java.lang.ref.WeakReference;

/**
 * Created by LKF on 2019-2-22 0022.
 * 车信数据仓库,目前采用Room实现,数据仓库的管理器集中在此管理类中
 */
@Database(entities = {
        User.class, GroupCardInfo.class, Friendship.class,
        GroupMuteUser.class, TopConversation.class, FaceQuickInfo.class,
        DiscoverGroup.class, ContactGroup.class, ContactUser.class,
        PushDisabledConversation.class, PushedNotification.class, SearchHistoryInfo.class
},
        version = BuildConfig.VERSION_CODE,
        exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ClubRepo extends RoomDatabase {
    private static final String DB_NAME = "XiaoMaRoomDb.db";
    private static ClubRepo sInstance;
    private static WeakReference<Context> sContextRef;

    public static void init(Context context) {
        sContextRef = new WeakReference<>(context.getApplicationContext());
    }

    public static ClubRepo getInstance() {
        if (sInstance == null) {
            synchronized (ClubRepo.class) {
                if (sInstance == null) {
                    Context context=sContextRef.get();
                    try {
                        sInstance = Room.databaseBuilder(context, ClubRepo.class, DB_NAME)
                                .fallbackToDestructiveMigration()// 发生错误时,重建数据库,而不是抛出异常
                                .allowMainThreadQueries()// 允许在主线程读写
                                .build();
                    } catch (Throwable e) {
                        // 如果数据创建异常,则强行清除自己的数据,避免一致Crash
                        ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
                        am.clearApplicationUserData();
                    }
                }
            }
        }
        return sInstance;
    }

    abstract public UserRepo getUserRepo();

    abstract public GroupRepo getGroupRepo();

    abstract public FriendshipRepo getFriendshipRepo();

    abstract public GroupMuteUserRepo getGroupMuteUserRepo();

    abstract public TopConversationRepo getTopConversationRepo();

    abstract public FaceRepo getFaceRepo();

    abstract public DiscoverRepo getDiscoverRepo();

    abstract public ContactGroupRepo getContactGroupsRepo();

    abstract public ContactUserRepo getContactUserRepo();

    abstract public PushDisabledConversationRepo getPushDisabledConversationRepo();

    abstract public PushedNotificationRepo getPushedNotificationRepo();

    abstract public SearchHistoryInfoRepo getSearchHistoryInfoRepo();
}
