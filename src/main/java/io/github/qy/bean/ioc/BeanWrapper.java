package io.github.qy.bean.ioc;

import org.jetbrains.annotations.Nullable;
import io.github.qy.exception.bean.BeanCreateException;
import io.github.qy.util.RefUtil;

public class BeanWrapper {
    // 对象本体
    private final Object bean;

    public BeanWrapper(Object bean) {
        this.bean = bean;
    }

    // 注入普通属性
    public void setProp(String propName, Object value){
        if (RefUtil.setProp(bean, propName, value)) {
            throw new BeanCreateException(propName + "set失败");
        }
    }

    // 获取普通属性
    @Nullable
    public Object getProp(String propName){
        return RefUtil.getProp(bean, propName);
    }

    // 获取对象本体
    public Object getBean(){
        return bean;
    }
}
