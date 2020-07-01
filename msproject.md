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

# 学到的知识点



-   GET和POST的区别

     GET具有幂等性，也就是GET方法调用多次返回的数据是一致的，在不改动数据的前提下。通俗的来讲就是  GET方法不改变数据。

     POST则当改变服务端的数据时使用。

-   如何保证买超问题

     利用数据库，在sql语句中设置当前商品库存大于0，才让商品库存减一

    ```sql
  update ms_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count>0
  ```

  SQL加库存数量判断：防止库存变为负数

-   如何解决一个用户秒杀两个商品问题

    利用数据库，生成商品id和用户id的唯一索引

    ```sql
  alter table ms_order add unique index u_uid_gid(user_id,goods_id)
  ```

  

#  静态资源优化

-  js/css压缩，减少流量
- 多个js/css组合，减少连接数
- CDN就近访问



# 秒杀接口优化

-  redis预减库存减少数据库访问
  -   系统初始化，把商品库存数量加载到redis
  -   收到请求，redis预减库存，库存不足，直接返回 
  -  库存还有，请求入队，返回排队中 
  
- 内存标记减少redis访问

  设置一个map,来存储当前商品是否已经结束秒杀。若已经结束，则直接返回，不访问redis

- 请求先入队缓存，异步下单

- 请求出队，生成订单，减少库存

-  客户端轮询，是否秒杀成功



#  集成RabbitMQ

## 安装依赖

```shell
添加依赖 spring-boot-starter-amqp
```

## 创建消息接受者

## 创建消息发送者

