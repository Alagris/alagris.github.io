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

    // inject via application.properties
    @Value("${welcome.message:test}")
    private String message = "Hello World";
    // /pdfjs/web/viewer.html
    @RequestMapping("/")
    public String index(Map<String, Object> model) {
        model.put("message", this.message);
//        System.out.println(message);
        return "start";
    }


    @RequestMapping("/rest")
    public String read(Map<String, Object> model, HttpSession httpSession) {
        final String text = (String)httpSession.getAttribute("code");
        model.put("code",text);
        return "rest1";
    }

}