package com.xiaoma.club.common.repo.impl;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.model.Friendship;
import com.xiaoma.club.common.repo.ModelRepo;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LKF on 2019-1-25 0025.
 * 好友关系缓存
 */
@Dao
public abstract class FriendshipRepo extends ModelRepo<Friendship> {
    @Query("SELECT * FROM Friendship WHERE hxAccount=:hxAccount")
    abstract protected Friendship[] getByHxAccount(String hxAccount);

    @Query("SELECT * FROM Friendship WHERE hxAccount=:myHxAccount AND otherHxAccount=:otherHxAccount")
    abstract protected Friendship getByOtherHxAccount(String myHxAccount, String otherHxAccount);

    @Query("DELETE FROM Friendship WHERE hxAccount=:hxAccount")
    abstract protected void clearByHxAccount(String hxAccount);

    public void append(String friendHxAccount) {
        final String myHxAccount = EMClient.getInstance().getCurrentUser();
        insert(new Friendship(myHxAccount, friendHxAccount));
    }

    public void delete(String friendHxAccount) {
        final String myHxAccount = EMClient.getInstance().getCurrentUser();
        delete(new Friendship(myHxAccount, friendHxAccount));
    }

    public Friendship getFriendship(String otherHxAccount) {
        String myHxAccount = EMClient.getInstance().getCurrentUser();
        return getByOtherHxAccount(myHxAccount, otherHxAccount);
    }

    public void fetchFriendship() {
        final String myHxAccount = EMClient.getInstance().getCurrentUser();
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                final Set<String> contacts = IMUtils.getHxContacts();
                if (contacts == null) {
                    Log.e(TAG, "fetchFriendship -> contacts is null");
                    return;
                }
                StringBuilder dump = new StringBuilder("[ ");
                clearByHxAccount(myHxAccount);
                final List<Friendship> friendships = new ArrayList<>(contacts.size());
                for (final String contact : contacts) {
                    friendships.add(new Friendship(myHxAccount, contact));
                    dump.append(contact).append("  ");
                }
                dump.append("]");
                insertAll(friendships);
                Log.i(TAG, "fetchFriendship -> " + dump);
            }
        });
    }

    public Set<String> getFriendHxAccounts() {
        final Friendship[] friendships = getByHxAccount(EMClient.getInstance().getCurrentUser());
        if (friendships == null || friendships.length <= 0)
            return null;
        final Set<String> friends = new HashSet<>(friendships.length);
        for (final Friendship friendship : friendships) {
            friends.add(friendship.getOtherHxAccount());
        }
        return friends;
    }

    /**
     * 记录联系人的环信id
     *
     * @param hxAccounts 联系人的环信id
     */
    public void putAllContacts(Collection<String> hxAccounts) {
        if (hxAccounts == null)
            return;
        final String myHxAccount = EMClient.getInstance().getCurrentUser();
        clearByHxAccount(myHxAccount);
        if (hxAccounts.isEmpty())
            return;
        final List<Friendship> friendships = new ArrayList<>(hxAccounts.size());
        for (final String account : hxAccounts) {
            friendships.add(new Friendship(myHxAccount, account));
        }
        insertAll(friendships);
    }

    @Override
    protected String getTableName() {
        return Friendship.class.getSimpleName();
    }
}