#  springboot 集成redis

## 安装设置redis

在linux上安装redis,安装目录在```/user/local/bin```下  

修改redis中的配置文件

```shell
band 0.0.0.0

```

##  springboot集成

- 加入依赖

- 自己编写RedisSerialize序列化类和RedisConfig

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
  ```

#  实现登录功能

 ## 1. 数据库设计

```sql
create table `user`(
`id` bigint(20) not null comment '用户id,手机号码',
`nickname` varchar(255) not null,
`password` varchar(32) default null comment 'MD5(MD5(pass明文+salt)+salt)',
`salt` varchar(10) default null,
`head` varchar(128) default null comment '头像，云存储的id',
`register_date` datetime default null comment '注册时间',
`last_login_date` datetime default null comment '上次登陆时间',
`login_count` int(11) default '0' comment '登陆次数',
primary key (`id`)
)engine=innodb default charset=utf8
```



##  2. 明文密码两次MD5验证

   1. 用户端：pass=md5(明文+固定Salt)

        防止用户密码泄露

   2. 服务端：pass=md5(用户输入+随机Salt)

-  导入依赖

   ```xml
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.14</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>
   ```

-  

## 3. JSR303参数检验+全局异常处理器

