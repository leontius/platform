package cn.elvea.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @RequestMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @RequestMapping("home")
    public String home() {
        return "home";
    }

    @RequestMapping("dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
