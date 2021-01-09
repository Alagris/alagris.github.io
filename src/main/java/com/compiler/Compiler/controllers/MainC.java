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


    @RequestMapping("/compiler1")
    public String read(Map<String, Object> model, HttpSession httpSession) {
        final String text = (String)httpSession.getAttribute("code");
        model.put("code",text);
        return "compiler2";
    }

//    @GetMapping("/compiler1")
//    public String compiler1(Model model) {
//        model.addAttribute("title", "Solomonoff - InfoPage");
//        return "compiler2";
   //}
    @GetMapping("/infpage")
    public String infpage(Model model) {
        model.addAttribute("title", "Solomonoff - InfoPage");
        return "ttt";
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
    @GetMapping("/Download2")
    public String download2(Model model) {
        model.addAttribute("title", "Solomonoff - Documentation");
        return "DownloadPage2";

    }


}