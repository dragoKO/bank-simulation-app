package com.cydeo.controller;

import com.cydeo.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Index {

    private final AccountService accountService;

    public Index(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping("/index")
    public String getIndexPage(Model model) {

        model.addAttribute("accountList", accountService.listAllAccount());

        return "account/index";
    }


}
