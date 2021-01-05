//package com.compiler.Compiler.controllers;
//
//        import net.alagris.CompilationError;
//        import net.alagris.OptimisedLexTransducer;
//        import org.springframework.beans.factory.annotation.Autowired;
//        import org.springframework.ui.Model;
//        import org.springframework.web.bind.annotation.*;
//
//        import javax.servlet.http.HttpSession;
//        import java.util.Map;
//
//
//@RestController
//public class SempleRestC {
//
//    @PostMapping("/compile")
//    public String compile(HttpSession httpSession, @RequestBody String text){
//        httpSession.setAttribute("code",text);
//        try {
//            final OptimisedLexTransducer.Repl repl = new OptimisedLexTransducer.Repl(new OptimisedLexTransducer.OptimisedHashLexTransducer(text,0,Integer.MAX_VALUE,true));
//            httpSession.setAttribute("repl",repl);
//        } catch (Exception compilationError) {
//            return compilationError.getMessage();
//        }
//        return "";
//    }
//
//    @PostMapping("/repl")
//    public String repl(HttpSession httpSession, @RequestBody String line){
//        OptimisedLexTransducer.Repl repl = (OptimisedLexTransducer.Repl) httpSession.getAttribute("repl");
//        if(repl==null){
//            try {
//                repl = new OptimisedLexTransducer.Repl(new OptimisedLexTransducer.OptimisedHashLexTransducer(0,Integer.MAX_VALUE,true));
//            } catch (Exception compilationError) {
//                return compilationError.getMessage();
//            }
//            httpSession.setAttribute("repl",repl);
//        }
//
//        try {
//            final StringBuilder out = new StringBuilder();
//            final String result = repl.run(line, s -> out.append(s).append('\n'));
//            out.append(result);
//            return out.toString();
//        }catch (Exception e){
//            return e.toString();
//        }
//
//    }
//
//}