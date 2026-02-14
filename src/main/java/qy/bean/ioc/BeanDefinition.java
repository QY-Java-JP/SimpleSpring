package qy.bean.ioc;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Data
public class BeanDefinition {
    // bean名称
    private String beanName;
    // class类型
    private Class<?> beanType;
    // 是否是正常类 (非枚举 注解 接口)
    private boolean isIndependent;

    // 初始化方法名
    @Nullable
    private String initMethodName;
    // 销毁方法名
    @Nullable
    private String destroyMethodName;

    // 属性以及相关值
    private Map<String, Object> propValueMap = new HashMap<>();

    public BeanDefinition(String beanName, Class<?> beanType) {
        this.beanName = beanName;
        this.beanType = beanType;

        comIsIndependent();
    }

    // 判断是不是正常类
    private void comIsIndependent(){
        this.isIndependent = !beanType.isInterface() && !beanType.isEnum() && !beanType.isAnnotation();
    }
}
