package com.api_mongo.api_mongodb_query_money.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class ModelViews {

    @GetMapping("login")
    public ModelAndView Login() {
        ModelAndView login = new ModelAndView("login");
        return login;
    }
    @GetMapping("account")
    public ModelAndView Account() {
        ModelAndView account = new ModelAndView("account");
        return account;
    }
    @GetMapping("home")
    public ModelAndView Home() {
        ModelAndView home = new ModelAndView("home");
        return home;
    }
}
