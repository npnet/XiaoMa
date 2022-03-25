package com.xiaoma.vr.dispatch;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.RegexUtils;

/**
 * @author youthyJ
 * @date 2019/3/14
 */
public final class Dispatch {
    private Dispatch() throws Exception {
        throw new Exception();
    }

    public static Middleware with(@NonNull Class cls) {
        return with(cls.getName());
    }

    public static Middleware with(@NonNull String detail) {
        return new Middleware(detail);
    }

    public static void remove(String command) {
        DispatchManager.getInstance().removeOnDispatchImpl(command);
    }

    public static void remove(Middleware middleware) {
        if (middleware == null) {
            return;
        }
        middleware.removeOnDispatchImpl();
    }

    public interface Event {
        void onEvent();
    }

    public static class Middleware {
        private final String detail;
        private String regex;
        private String follow;
        private Event event;
        private DispatchManager.OnDispatch onDispatchImpl = new DispatchManager.OnDispatch() {

            @Override
            public boolean hit(String in, boolean forEvent) {
                if (TextUtils.isEmpty(in) || in.trim().isEmpty()) {
                    return false;
                }
                if (!isRegexEmpty() && (RegexUtils.isMatch(regex, in) || TextUtils.equals(regex, in))) {
                    if (forEvent) {
                        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                            @Override
                            public void run() {
                                if (event != null) {
                                    event.onEvent();
                                }
                            }
                        });
                    }
                    return true;
                }
                return false;
            }

            @Override
            public String getHandleDetail() {
                return detail;
            }

            @Override
            public String getFollowVoice() {
                return follow;
            }
        };

        Middleware(String detail) {
            this.detail = detail;
        }

        public Middleware match(String regex) {
            this.regex = regex;
            return this;
        }

        public Middleware follow(String follow) {
            this.follow = follow;
            return this;
        }

        public Middleware event(Event event) {
            this.event = event;
            return this;
        }

        public void done() {
            if (isRegexEmpty()) {
                throw new IllegalArgumentException("command is empty");
            }
            if (event == null) {
                return;
            }
            DispatchManager.getInstance().addOnDispatchImpl(onDispatchImpl);
        }

        private void removeOnDispatchImpl() {
            DispatchManager.getInstance().removeOnDispatchImpl(onDispatchImpl);
        }

        private boolean isRegexEmpty() {
            return TextUtils.isEmpty(regex);
        }

    }
}
