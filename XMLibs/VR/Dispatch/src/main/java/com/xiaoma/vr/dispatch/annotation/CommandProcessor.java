package com.xiaoma.vr.dispatch.annotation;

import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.vr.dispatch.Dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 简化语音指令注册行为
 *
 * @author youthyJ
 * @date 2019/3/19
 */
public final class CommandProcessor {
    private CommandProcessor() throws Exception {
        throw new Exception();
    }

    public static void register(final Object handler) {
        if (handler == null) {
            return;
        }
        ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
            @Override
            public void run() {
                findMethod(handler, new OnMethodFound() {
                    @Override
                    public void onFound(Object handler, Method method, String command) {
                        registerInner(handler, method, command);
                    }
                });
            }
        }, Priority.LOW);
    }

    public static void remove(final Object handler) {
        if (handler == null) {
            return;
        }
        ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
            @Override
            public void run() {
                findMethod(handler, new OnMethodFound() {
                    @Override
                    public void onFound(Object handler, Method method, String command) {
                        removeInner(command);
                    }
                });
            }
        }, Priority.LOW);
    }

    private static void findMethod(Object handler, OnMethodFound onFound) {
        Class<?> handlerClass = handler.getClass();
        Method[] methods = handlerClass.getDeclaredMethods();
        if (methods == null || methods.length <= 0) {
            return;
        }
        for (final Method method : methods) {
            if (method == null) {
                continue;
            }
            Annotation[] methodAnnotations = method.getDeclaredAnnotations();
            if (methodAnnotations == null || methodAnnotations.length <= 0) {
                continue;
            }
            for (Annotation annotation : methodAnnotations) {
                if (annotation == null) {
                    continue;
                }
                if (!Command.class.equals(annotation.annotationType())) {
                    continue;
                }
                String command = method.getAnnotation(Command.class).value();
                onFound.onFound(handler, method, command);
            }
        }
    }

    private static void registerInner(final Object handler, final Method method, final String command) {
        Dispatch.with(handler.getClass())
                .match(command)
                .event(new Dispatch.Event() {
                    @Override
                    public void onEvent() {
                        int modifiers = method.getModifiers();
                        if (modifiers != Modifier.PUBLIC) {
                            return;
                        }
                        if (!void.class.equals(method.getReturnType())) {
                            return;
                        }
                        Class<?>[] paramClasses = method.getParameterTypes();
                        if (paramClasses.length == 0) {
                            try {
                                method.invoke(handler);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else if (paramClasses.length == 1 && String.class.equals(paramClasses[0])) {
                            try {
                                method.invoke(handler, command);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .done();
    }

    private static void removeInner(String command) {
        Dispatch.remove(command);
    }

    private interface OnMethodFound {
        void onFound(Object handler, Method method, String command);
    }
}
