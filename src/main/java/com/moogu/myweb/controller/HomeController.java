package com.moogu.myweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @RequestMapping("/helloController")
    @ResponseBody
    public String greeting() {
        return "Hello, World";
    }
}
