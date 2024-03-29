package com.api_mongo.api_mongodb_query_money.views;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/")
public class Views_models {

    @GetMapping("login")
    public ModelAndView Login() {
        ModelAndView login = new ModelAndView("login");
        return login;
    }

    @GetMapping("forgot")
    public ModelAndView forgot() {
        ModelAndView forgot = new ModelAndView("forgot");
        return forgot;
    }

    @GetMapping("account")
    public ModelAndView Account() {
        ModelAndView account = new ModelAndView("account");
        // System.out.println("This is account "+account.);
        // account.setHeader(String, String);
        return account;
    }

    @GetMapping("account2")
    public ModelAndView Account2() {
        ModelAndView account = new ModelAndView("account2");
        // System.out.println("This is account "+account.);
        // account.setHeader(String, String);
        return account;
    }

    @GetMapping("register")
    public ModelAndView register() {
        ModelAndView register = new ModelAndView("register");
        return register;
    }

    @GetMapping("")
    public ModelAndView Home() {
        ModelAndView home = new ModelAndView("home");
        return home;
    }
}
