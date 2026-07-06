package com.puppet.proxy;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) throws IOException {
        MyInterface myInterface = MyInterfaceFactory.createProxyObject(new PrintFunctionName());
        myInterface.func1();
        myInterface.func2();
        myInterface.func3();

        System.out.println("------------------------");

        MyInterface myInterface1 = MyInterfaceFactory.createProxyObject(new LogHandler(myInterface));
        myInterface1.func1();
        myInterface1.func2();
        myInterface1.func3();

        // jdk 自带的动态代理
        PrintFunctionName printFunctionName = new PrintFunctionName();
        MyHandler jdkMyHandler = (MyHandler)Proxy.newProxyInstance(
                printFunctionName.getClass().getClassLoader(),
                printFunctionName.getClass().getInterfaces(),
                new JdkProxy(printFunctionName)
        );

        jdkMyHandler.functionBody("jdk");
    }

    static class PrintFunctionName implements MyHandler{
        @Override
        public String functionBody(String methodName) {
            return """
                    System.out.println(1);
                    System.out.println("%s");
                    """.formatted(methodName);
        }
    }

    static class LogHandler implements MyHandler{

        MyInterface myInterface;

        public LogHandler(MyInterface myInterface) {
            this.myInterface = myInterface;
        }

        @Override
        public String functionBody(String methodName) {
            String context = "System.out.println(\"before\");\n" +
                    "        myInterface." + methodName + "();\n" +
                    "        System.out.println(\"after\");";
            return context;
        }

        @Override
        public void setProxy(MyInterface proxy) {
            Class<? extends MyInterface> aClass = proxy.getClass();
            Field field = null;
            try {
                field = aClass.getDeclaredField("myInterface");
                field.setAccessible(true);
                field.set(proxy, myInterface);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
