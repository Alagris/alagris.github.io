package com.compiler.Compiler.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Solomonoff");
        return "start";

    }

    @GetMapping("/infpage")
    public String infpage(Model model) {
        model.addAttribute("title", "Solomonoff - InfoPage");
        return "infpage";
    }

    @GetMapping("/DocPage")
    public String DocPage(Model model) {
        model.addAttribute("title", "Solomonoff - Documentation");
        return "DocPage";
    }


}


