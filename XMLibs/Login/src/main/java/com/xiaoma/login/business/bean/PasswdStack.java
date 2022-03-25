package com.xiaoma.login.business.bean;

import java.io.Serializable;

/**
 * 模仿栈结构进行密码的输入和回退
 *
 * @author KY
 * @date 11/20/2018
 */
public class PasswdStack implements Serializable {

    private final int capacity;
    private final String[] array;
    private int top;

    public PasswdStack(int capacity) {
        this.capacity = capacity;
        array = new String[capacity];
        for (int i = 0; i < capacity; i++) {
            array[i] = "";
        }
        top = -1;
    }

    /**
     * 判断是否为空栈
     *
     * @return 是否为空栈
     */
    public boolean isEmpty() {
        return top == -1;
    }

    public int getTop() {
        return top;
    }

    /**
     * 判断是否为满栈
     *
     * @return 是否为满栈
     */
    public boolean isFull() {
        return (top + 1) == capacity;
    }

    public String get(int index) {
        return array[index];
    }

    /**
     * 压栈操作，模拟输入
     *
     * @param passwdChar 密码字符
     */
    public void push(String passwdChar) {
        if (!isFull()) {
            top++;
            this.array[top] = passwdChar;
        }
    }


    /**
     * 出栈操作，模拟回退
     */
    public void backSpace() {
        if (!isEmpty()) {
            this.array[top] = "";
            top--;
        }
    }

    /**
     * 获取密码字符串
     *
     * @return 密码字符串
     */
    public String getPasswd() {
        StringBuilder builder = new StringBuilder();
        for (String s : array) {
            builder.append(s);
        }
        return builder.toString();
    }
}
