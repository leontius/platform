package cn.elvea.controller;

import cn.elvea.domain.User;
import cn.elvea.service.UserService;
import cn.elvea.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("index")
    public String index() {
        return "user/index";
    }

    @RequestMapping("detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id) {
        model.addAttribute("entiry", userService.findById(id));
        return "user/detail";
    }

    @RequestMapping("create")
    public String create() {
        return "user/index";
    }

    @RequestMapping(method = RequestMethod.GET, value = "update/{id}")
    public String update(@PathVariable("id") Long id) {
        return "user/update";
    }

    @RequestMapping(method = RequestMethod.POST, value = "update/{id}")
    public String update(@Valid @ModelAttribute("user") User user, BindingResult result, @PathVariable("id") Long id) {
        if (result.hasErrors()) {
            return "user/update";
        }
        return "user/update";
    }

    @RequestMapping("list/json")
    @ResponseBody
    public Page<User> listJson(Page<User> page) {
        userService.findByPage(page);
        return page;
    }

    @RequestMapping()
    public String delete() {
        return "user/index";
    }
}
