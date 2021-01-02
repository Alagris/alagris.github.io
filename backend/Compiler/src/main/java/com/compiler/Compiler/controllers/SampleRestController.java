//package com.compiler.Compiler.controllers;
//
//
//import net.alagris.CLI;
//import net.alagris.CompilationError;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpSession;
//
//@RestController
//public class SampleRestController {
//    private final HttpSessionBean httpSessionBean;
//
//    @Autowired
//    public SampleRestController(HttpSessionBean httpSessionBean) {
//        this.httpSessionBean = httpSessionBean;
//    }
//
//    @PostMapping("/reverse")
//    public String reverse(HttpSession httpSession, @RequestBody String text) {
//        System.out.println(text);
//        httpSession.setAttribute("storedInSession", text);
//        //return new StringBuilder(text).reverse().toString();
//        try {
//            //This creates new compiler instance.
//            CLI.OptimisedHashLexTransducer compiler = new CLI.OptimisedHashLexTransducer(text, 0, Integer.MAX_VALUE, true);
//            //Every user should have their own instance!
//            httpSessionBean.setCompiler(compiler);
//        } catch (CompilationError compilationError) {
//            //in case of error, say what went wrong
//            return compilationError.getMessage();
//        }
//        // in case of success
//        return "Success!" + text;
//
//    }
//
//    @GetMapping(path = "/list")
//    public String listDefinedTransducers() {
//        final CLI.OptimisedHashLexTransducer compiler = httpSessionBean.getCompiler();
//        return compiler.specs.variableAssignments.keySet().toString();
//    }
//
//    // Test me with:
//    // http://localhost:8080/run?transducerName=f&transducerInput=yy
//    // http://localhost:8080/run?transducerName=f&transducerInput=re
//    @PostMapping("/run")
//    public String reverse1(HttpSession httpSession, @RequestBody String transducerInput) {
//         String transducerName = "f";
//      //  public String runTransducer (String transducerName, String transducerInput){
//            if (transducerInput == null) return "TRANSDUCER INPUT IS MISSING";
//            final CLI.OptimisedHashLexTransducer compiler = httpSessionBean.getCompiler();
//            //run the transducer
//            final String output = compiler.run(transducerName, transducerInput);
//            return output == null ? "INPUT REJECTED" : "OUTPUT RETURNED:" + output;
//       // }
//    }
//}