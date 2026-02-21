package io.github.qy.core.ioc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleSpringHelper {

    // 容器
    private static ApplicationContext applicationContext;

    // 注册一个静态容器
    public static void initStaticApplicationContext(Class<?> rootPackageOneClass){
        if (applicationContext != null) return;
        synchronized (SimpleSpringHelper.class) {
            if (applicationContext != null) return;
            applicationContext = new ApplicationContext(rootPackageOneClass);
            log.info("静态SimpleSpring容器已启动 将从:{} 开始进行包扫描", rootPackageOneClass.getPackage().getName());
        }
    }

    // 拿到静态容器
    public static ApplicationContext getStaticContext(){
        if (applicationContext == null) {
            throw new RuntimeException("请先调用initStaticApplicationContext() 初始化");
        }

        return applicationContext;
    }

}
