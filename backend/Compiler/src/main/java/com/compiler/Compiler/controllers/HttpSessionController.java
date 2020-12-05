package com.compiler.Compiler.controllers;

import lombok.Data;
import net.alagris.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpSessionController {




    private final HttpSessionBean httpSessionBean;

    @Autowired
    public HttpSessionController(HttpSessionBean httpSessionBean) {
        this.httpSessionBean = httpSessionBean;
    }

    //Test me with:
    // http://localhost:8080/parse?code=f%3D%27re%27%3A%27tr%27
    // in javascript:
    // "http://localhost:8080/parse?code=" + encodeURIComponent("f='re':'tr'")
    // It encode the following regex:
    // f='re':'tr'
    @GetMapping(path = "/parse")
    public String parseCode(String code) {
        //take a look at https://stackoverflow.com/questions/46766075/how-to-pass-ajax-into-a-spring-controller/46783132
        try {
            //This creates new compiler instance.
            CLI.OptimisedHashLexTransducer compiler = new CLI.OptimisedHashLexTransducer(code,0,Integer.MAX_VALUE,true);
            //Every user should have their own instance!
            httpSessionBean.setCompiler(compiler);
        } catch (CompilationError compilationError) {
            //in case of error, say what went wrong
            return compilationError.getMessage();
        }
        // in case of success
        return "Success!";
    }
    @GetMapping(path = "/list")
    public String listDefinedTransducers() {
        final CLI.OptimisedHashLexTransducer compiler = httpSessionBean.getCompiler();
        return compiler.specs.variableAssignments.keySet().toString();
    }
    // Test me with:
    // http://localhost:8080/run?transducerName=f&transducerInput=yy
    // http://localhost:8080/run?transducerName=f&transducerInput=re
    @GetMapping(path = "/run")
    public String runTransducer(String transducerName,String transducerInput) {
        if(transducerInput==null)return "TRANSDUCER INPUT IS MISSING";
        final CLI.OptimisedHashLexTransducer compiler = httpSessionBean.getCompiler();
        //run the transducer
        final String output = compiler.run(transducerName,transducerInput);
        return output==null?"INPUT REJECTED":"OUTPUT RETURNED:"+output;
    }
    @GetMapping(path = "/controller")
    public String example(String name) {
        if(!StringUtils.isEmpty(name)){
            httpSessionBean.setName(name);
            return "New name have been received - " + name;
        }else if (!StringUtils.isEmpty(httpSessionBean.getName())){
            return "Current name: " + httpSessionBean.getName();
        }else {
            return "There is no saved name";
        }
    }
}
