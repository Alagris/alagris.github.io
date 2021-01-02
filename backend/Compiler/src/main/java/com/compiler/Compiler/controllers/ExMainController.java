//package com.compiler.Compiler.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.http.HttpSession;
//import java.util.Map;
//
//@Controller
//public class ExMainController {
//
//
//    @RequestMapping("/rest")
//    public String read(Map<String, Object> model, HttpSession httpSession){
//        final String text = (String)httpSession.getAttribute("storedInSession");
//        System.out.println("Saved in session "+text);
//        model.put("textSavedInSession", text);
//        return "rest";
//    }
//}
