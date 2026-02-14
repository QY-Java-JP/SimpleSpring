package qy;

import qy.core.ioc.ApplicationContext;
import qy.core.ioc.SimpleSpringHelper;
import qy.test.BlogService;

public class main {
    public static void main(String[] args) {
        SimpleSpringHelper.initStaticApplicationContext(main.class);
        ApplicationContext context = SimpleSpringHelper.getStaticContext();

        BlogService blogService = (BlogService) context.getSingleBean(BlogService.class);
        blogService.t1();
    }
}
