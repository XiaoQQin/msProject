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

#   秒杀商品功能开发

##  1. 数据库设计

- 商品表

   ```sql
  create table `goods`(
   `id` bigint(20) not null auto_increment comment '商品id',
   `goods_name` varchar(16) default null comment '商品名称',
   `goods_title` varchar(64) default null comment '商品标题',
   `goods_img` varchar(64) default null comment '商品图片',
   `goods_detail` longtext default null comment '商品详情介绍',
   `goods_price` decimal(10,2) default '0.00' comment '商品价格',
   `goods_stock` int(11) default '0' comment '商品库存，-1表示无限制',
   primary key(`id`)
  )engine=innodb default charset=utf8
   ```

  

- 秒杀商品表

  ```sql
  create table `ms_goods`(
   `id` bigint(20) not null auto_increment comment '商品id',
   `goods_id` bigint(20) default null comment '商品id',
   `ms_price` decimal(10,2) default '0.00' comment '秒杀价',
   `stock_count` int(11) default '0' comment '库存数量',
   `start_time` datetime default null comment '秒杀开始时间',
   `end_time` datetime default null comment '秒杀结束时间',
   primary key(`id`)
  )engine=innodb default charset=utf8
  ```

  

- 订单表

  ```sql
  create table `order_info`(
   `id` bigint(20) not null auto_increment,
   `user_id` bigint(20) default null comment '用户id',
   `goods_id` bigint(20) default null comment '商品id',
   `delivery_addr_id` bigint(20) default null comment '收货地址id',
   `goods_name` varchar(16) default null comment '商品名称',
   `goods_count`int(11) default '0' comment '商品数量',
   `goods_price` decimal(10,2) default '0.00' comment '商品单价',
   `order_channel` tinyint(4) default '0' comment '1:pc,2:android,3:ios',
   `status`tinyint(4) default '0' comment '订单状态: 0,新建未支付，1：一支付，2：已发货，3:已收货，4：已退款',
   `create_time` datetime default null comment '订单创建时间',
   `pay_time` datetime default null comment '订单支付时间',
   primary key(`id`)
  )engine=innodb auto_increment=12 default charset=utf8
  ```

  

-  秒杀订单

       ```sql
create table `ms_order`(
 `id` bigint(20) not null auto_increment,
 `user_id` bigint(20) default null comment '用户id',
 `goods_id` bigint(20) default null comment '商品id',
 `order_id` bigint(20) default null comment '订单id',
 primary key(`id`)
)engine=innodb auto_increment=3 default charset=utf8
       ```

