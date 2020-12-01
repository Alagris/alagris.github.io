package com.compiler.Compiler.controllers;


import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EchoPostController {

    @RequestMapping(value = "/examples/echo", method = RequestMethod.POST)
    @ResponseBody
    public String makePostEcho(@RequestBody String data) {
        return data;
    }
}