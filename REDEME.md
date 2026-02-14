# SimpleSpring
## 简介
这个项目是个人开发的一个模仿Spring的简单IOC容器  
其中包括IOC和AOP两大块功能  
注意: 本项目未经过大量测试 **可能存在大量BUG!!!**

## 如何使用
### 1. 容器启动
使用方式和Spring大同小异  
我们仍然需要一个容器 不过一个项目中一般只用得到一个容器所以提供了工具类
```
SimpleSpringHelper.initStaticApplicationContext(App.class);
```
这样就启动了一个容器  
如果你想getBean
```
ApplicationContext context = SimpleSpringHelper.getStaticContext();
BlogService blogService = (BlogService) context.getSingleBean(BlogService.class);
```
请在initStaticApplicationContext()后再get 否则会有异常

### 2. Bean的声明
```
@Component
public class UserService {

    @Autowired
    private BlogService blogService;

    @PostConstruct
    public void init(){
        System.out.println("UserService init");
    }

}
```
使用方式和Spring如出一辙 只不过缺失某些功能  
1. 不提供@PreDestroy
2. 只有@Component 没有@Service @Controller
3. 每个bean只支持无参构造 没有Spring复杂的推断构造方法
4. @Autowired仅限字段注入 不支持setter注入

### 2. 配置类
当然是有配置类的
```
@Configurable
public class Config {

    @Bean
    public Random random(){
        return new Random();
    }

    @Bean
    public UserService userService(){
        return new UserService();
    }
}
```
但是也是缺少一些功能
1. 不支持full配置类
2. 不存在一些Spring中的冷门用法(比如内部类 接口默认实现等加@Bean)
3. 不会解析父类中的@Bean
4. 没有@Import注解
5. 支持传参但是不可以用@Autowired指定beanName(以后可能会开发)

## 3. BeanPostProcessor&BeanDefinitionPostProcessor
跟Spring的使用方式一样 实现接口并@Component 也支持@Order设置顺序



### 4. 关于AOP
没有spring那样灵活但是也可以进行aop
```
@Component
public class TestAfterAdvice extends AfterAdvice {
    @Override
    protected void invoke(JoinPoint point) throws Exception {
        System.out.println(2);
        System.out.println(point.getTarget().getClass());
    }

    @Override
    public PointCut getPointCut() {
        return PointCutHelper.createAnnotationPointCut(T1.class);
    }
}
```
这个例子是一个后置通知 需要在getPointCut里面指定切点 当前只支持注解的方式
```
@Component
public class BlogService {

    @Autowired
    private UserService userService;

    @T1
    public void t1(){
        System.out.println(userService);
    }
}
```

## 写在最后
(待补充)