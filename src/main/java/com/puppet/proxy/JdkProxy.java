package com.puppet.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JdkProxy implements InvocationHandler {

    /**
     * 持有被代理的真实对象
     * */
    private final Object target;

    public JdkProxy(Object target) {
        this.target = target;
    }

    /**
     * @param proxy  代理对象本身（一般不用）
     * @param method 被调用的方法
     * @param args   方法参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // ---------- 前置增强 ----------
        System.out.println("🔔 [LOG] 调用方法: " + method.getName()
                + ", 参数: " + java.util.Arrays.toString(args));
        long start = System.currentTimeMillis();

        // ---------- 反射调用真实方法 ----------
        Object result = method.invoke(target, args);

        // ---------- 后置增强 ----------
        long cost = System.currentTimeMillis() - start;
        System.out.println("🔔 [LOG] 方法返回: " + result + ", 耗时: " + cost + "ms");
        System.out.println("-----------------------------------");

        return result;
    }
}
