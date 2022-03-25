package com.xiaoma.scene;

/**
 * @author youthyJ
 * @date 2018/11/21
 */
public class SceneManager {
    private static SceneManager instance;

    public static SceneManager getInstance() {
        if (instance == null) {
            synchronized (SceneManager.class) {
                if (instance == null) {
                    instance = new SceneManager();
                }
            }
        }
        return instance;
    }

    public void init() {

    }

}
