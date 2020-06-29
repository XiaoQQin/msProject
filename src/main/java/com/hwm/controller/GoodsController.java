package com.hwm.controller;

import com.hwm.domain.MsUser;
import com.hwm.service.MsUserService;
import com.hwm.service.MsUserServiceImpl;
import com.hwm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/goods")
public class GoodsController {


    @Autowired
    MsUserService msUserService;

    @RequestMapping("/to_list")
    public String to_list(Model model,MsUser msuser){
        model.addAttribute("user",msuser);
        System.out.println(msuser);
        return "goods_list";
    }
}
