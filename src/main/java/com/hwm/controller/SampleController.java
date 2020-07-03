package com.hwm.controller;

import com.hwm.domain.User;
import com.hwm.rabbitmq.MQSender;
import com.hwm.rabbitmq.MQreceiver;
import com.hwm.redis.RedisService;
import com.hwm.result.Result;
import com.hwm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    @Autowired
    MQreceiver mQreceiver;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getUserById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbtx() {
        Boolean tx = userService.tx();
        return Result.success(tx);
    }



//    //direct方式
//    @RequestMapping("/rabbit")
//    @ResponseBody
//    public Result testRabbitMQ() {
//        mqSender.send("hello rabbit");
//        return Result.success("test success");
//    }
//
//
//    //direct方式
//    @RequestMapping("/mqTopic")
//    @ResponseBody
//    public Result testRabbitMQTopic() {
//        mqSender.sendTopic("hello topic");
//        return Result.success("test success");
//    }
}
