package com.leon.myblog.controller;

import com.leon.myblog.enity.Category;
import com.leon.myblog.enity.User;
import com.leon.myblog.service.CategoryService;
import com.leon.myblog.service.SendMailService;
import com.leon.myblog.service.UserService;
import com.leon.myblog.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：leon
 * @date ：Created in 2019-12-11 14:40
 * @description：${description}
 * @modified By：
 * @version: $version$
 */

@RestController
@RequestMapping("/front")
public class TestController {

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    UserService userService;

    @Autowired
    RedisUtils redisUtil;

    @GetMapping("/test")
    public void test(){

        User userInfo = userService.findByUserName("leon1");
        System.out.println("1111111111111111111111111"+userInfo.getId());
        System.out.println("2222222222222222222222222"+userService.getRoleByUserId(userInfo.getId()));
        System.out.println("3333333333333333333333333"+userService.getRoleByRileId(1).getRolename());
        System.out.println("4444444444444444444444444"+userService.getPermisonsByUserId(1));
        System.out.println("5555555555555555555555555"+userService.getPermisonByPermisionId(1).getPermisionname());


    }

    @GetMapping("/tt")
    public List<Category> tt(){

        String key="mytestkey";
        redisUtil.set(key,50);
        System.out.println(redisUtil.get(key));


        return  categoryService.getall();
    }
}
