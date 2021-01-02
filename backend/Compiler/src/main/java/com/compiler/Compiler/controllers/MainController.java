//package com.compiler.Compiler.controllers;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.http.HttpSession;
//import java.util.Map;
//
//
//@Controller
//public class MainController {
//
////    @RequestMapping("/rest")
////    public String read(Map<String, Object> model, HttpSession httpSession){
////        final String text = (String)httpSession.getAttribute("storedInSession");
////        System.out.println("Saved in session "+text);
////        model.put("textSavedInSession", text);
////        return "rest";
////    }
//
//    @GetMapping("/")
//    public String home(Model model) {
//        model.addAttribute("title", "Solomonoff");
//        return "start";
//
//    }
//
//    @GetMapping("/infpage")
//    public String infpage(Model model) {
//        model.addAttribute("title", "Solomonoff - InfoPage");
//        return "ttt";
//    }
//
//    @GetMapping("/DocPage")
//    public String DocPage(Model model) {
//        model.addAttribute("title", "Solomonoff - Documentation");
//        return "DocPage";
//    }
//
//
//}
//
//
