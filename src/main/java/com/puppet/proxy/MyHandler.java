package com.puppet.proxy;

public interface MyHandler {
    String functionBody(String methodName);

    default void setProxy(MyInterface proxy) {

    }
}
