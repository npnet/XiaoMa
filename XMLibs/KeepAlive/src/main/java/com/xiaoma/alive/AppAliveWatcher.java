package com.xiaoma.alive;

import android.content.Context;

import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.SourceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2019/3/28
 */
public class AppAliveWatcher {
    private static AppAliveWatcher instance;
    private Context appContext;
    private List<Watcher> watcherList = new ArrayList<>();

    private AppAliveWatcher() {
    }

    public static AppAliveWatcher getInstance() {
        if (instance == null) {
            synchronized (AppAliveWatcher.class) {
                if (instance == null) {
                    instance = new AppAliveWatcher();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        appContext = context.getApplicationContext();
        Center.getInstance().init(appContext);
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                    @Override
                    public void onClientIn(SourceInfo source) {
                        int port = source.getPort();
                        if (AliveClient.CLIENT_PORT != port) {
                            return;
                        }
                        notifyAppIn(source.getLocation());
                    }

                    @Override
                    public void onClientOut(SourceInfo source) {
                        super.onClientOut(source);
                        int port = source.getPort();
                        if (AliveClient.CLIENT_PORT != port) {
                            return;
                        }
                        notifyAppOut(source.getLocation());
                    }
                });
            }
        });
    }

    private void notifyAppIn(String packageName) {
        for (int i = watcherList.size() - 1; i >= 0; i--) {
            Watcher watcher = watcherList.get(i);
            try {
                watcher.onAppIn(packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyAppOut(String packageName) {
        for (int i = watcherList.size() - 1; i >= 0; i--) {
            Watcher watcher = watcherList.get(i);
            try {
                watcher.onAppOut(packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isAppAlive(String packageName) {
        if (!Center.getInstance().isConnected()) {
            return false;
        }
        SourceInfo remote = new SourceInfo(packageName, AliveClient.CLIENT_PORT);
        return Center.getInstance().isClientAlive(remote);
    }

    public boolean addWatcher(Watcher watcher) {
        if (watcher == null) {
            return false;
        }
        if (watcherList.contains(watcher)) {
            return true;
        }
        return watcherList.add(watcher);
    }

    public boolean removeWatcher(Watcher watcher) {
        if (watcher == null) {
            return false;
        }
        return watcherList.remove(watcher);
    }

    public interface Watcher {
        void onAppIn(String packageName);

        void onAppOut(String packageName);
    }
}
