//package com.compiler.Compiler.controllers;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//@Controller
//public class StudentController {
//    @RequestMapping("/student_form")
//    public String showStudentForm( Model m) {
//        Student student = new Student();
//        m.addAttribute("student", student);
//        return "studentform" ;
//    }
//    @RequestMapping("/studentregis")
//    public String showStudentData(@ModelAttribute("student") Student student) {
//        System.out.println("student:" + student.getFname() +" "+ student.getLname());
//        return "student-data" ;
//    }
//}