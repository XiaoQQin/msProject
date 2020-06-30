# msProject
SpringBoot秒杀系统  
-  数据库框架: mybatis  
-  缓存数据库: redis
## 登录模块
-   明文密码两次MD5验证
-   JSR303参数检验,全局异常处理器
-   分布式动态session

## 优化

-  **页面静态化**:  将html页面缓存到redis中，使用springboot来进行渲染，将渲染好的html存入redis,缓存时间较短设置为30s
-  **对象缓存**:  使用redis来对对象进行缓存，不从数据库直接查询获取对象  
    
