package com.compiler.Compiler.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
public class MainC {

    @Autowired
    public NewRestController rest;

    @Value("${welcome.message:test}")
    private String message = "Hello World";

    @RequestMapping("/")
    public String index(Map<String, Object> model) {
        model.put("message", this.message);
        return "start";
    }


    @RequestMapping("/compiler")
    public String read(Map<String, Object> model, HttpSession httpSession) {
        model.put("automata",rest.listAutomata(httpSession));
        model.put("repl_history",httpSession.getAttribute("repl_history"));
        return "compiler2";
    }


    @GetMapping("/DocPage")
    public String DocPage(Model model) {
        model.addAttribute("title", "Solomonoff - Documentation");
        return "DocPage";
    }
    @GetMapping("/Contact")
    public String contact(Model model) {
        model.addAttribute("title", "Solomonoff - Documentation");
        return "contactPage";
    }
    @GetMapping("/Download")
    public String download(Model model) {
        model.addAttribute("title", "Solomonoff - Documentation");
        return "DownloadPage";

    }



}