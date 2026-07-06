package com.puppet.proxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MyInterfaceFactory {

    private static final AtomicInteger atomicInteger = new AtomicInteger();

    private static File createJavaFile(String className,MyHandler handler) throws IOException {
        String fun1 = handler.functionBody("func1");
        String fun2 = handler.functionBody("func1");
        String fun3 = handler.functionBody("func1");
        String context = """
                package com.puppet.proxy;
                
                public class %s implements MyInterface{
                    
                    MyInterface myInterface;
                    
                    @Override
                    public void func1() {
                        %s
                    }
                
                    @Override
                    public void func2() {
                        %s
                    }
                
                    @Override
                    public void func3() {
                        %s
                    }
                }
                """.formatted(className,fun1,fun2,fun3);

        File javaFile = new File(className + ".java");

        Files.writeString(javaFile.toPath(),context);
        return javaFile;
    }

    /**
     * 获取 类名
     */
    private static String getClassName(){
        return "MyInterface$Proxy" + atomicInteger.incrementAndGet();
    }

    /**
     * 获取 方法
     */
    private static String getMethodBody(String methodName){
        return "System.out.println(\"%s\");".formatted(methodName);
    }

    private static MyInterface newInstance(String className,MyHandler handler){
        try {
            Class<?> aClass = MyInterfaceFactory.class.getClassLoader().loadClass(className);
            MyInterface MyInterface = (MyInterface)aClass.getConstructor().newInstance();
            handler.setProxy(MyInterface);
            return MyInterface;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MyInterface createProxyObject (MyHandler myHandler) throws IOException {
        String className = getClassName();
        File javaFile = createJavaFile(className, myHandler);
        Compiler.compile(javaFile);
        return newInstance("com.puppet.proxy." + className, myHandler);
    }
}
