package de.vitapublic.essAuthenticationService.controller;


import de.vitapublic.essAuthenticationService.controller.exception.LogicalException;
import de.vitapublic.essAuthenticationService.model.User;
import de.vitapublic.essAuthenticationService.service.UserService;
import de.vitapublic.essAuthenticationService.service.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


@Controller
public class UserController {

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/registration", method = RequestMethod.GET , produces = "text/plain;charset=UTF-8")
    public String registration(Model model, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        model.addAttribute("user", user);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies)
                    .forEach(c -> System.out.println(c.getName() + "=" + c.getValue()));
        }

        Cookie newCookie = new Cookie("testCookie", "testCookieValue");
        newCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(newCookie);
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute User user, BindingResult bindingResult, Model model, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies)
                    .forEach(c -> System.out.println(c.getName() + "=" + c.getValue()));
        }

        userValidator.validate(user, bindingResult);
        model.addAttribute("user", user);
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        try {
            userService.registerUser(user);
        } catch (LogicalException e) {
            e.printStackTrace();
            bindingResult.reject(e.getMessage());
            return "registration";
        }

        return "redirect:/welcome";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @RequestMapping(value = {"/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        return "welcome";
    }

    @RequestMapping("/")
    public String index() {
        return "login";
    }
}
