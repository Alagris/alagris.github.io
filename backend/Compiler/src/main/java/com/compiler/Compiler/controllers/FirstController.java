package com.compiler.Compiler.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FirstController {

    @GetMapping("/compiler")
    public String compiler(Model model){
        return "compiler";
    }

}
