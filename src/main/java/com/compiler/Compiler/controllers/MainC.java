package com.compiler.Compiler.controllers;

import net.alagris.OptimisedLexTransducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        NewRestController.Repl repl = (NewRestController.Repl) httpSession.getAttribute("repl");
        if (repl == null) {
            try {
                repl = new NewRestController.Repl(new OptimisedLexTransducer.OptimisedHashLexTransducer(0, Integer.MAX_VALUE, true));
            } catch (Exception compilationError) {
                return compilationError.getMessage();
            }
            httpSession.setAttribute("repl", repl);
        }
        model.put("automata",repl.compiler.specs.variableAssignments.keySet());
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
        model.addAttribute("title", "Solomonoff - Contact");
        return "contactPage";
    }
    @GetMapping("/Download")
    public String download(Model model) {
        model.addAttribute("title", "Solomonoff - Download");
        return "DownloadPage";

    }
    final String filename = "solomonoff.jar";
    final File file = new File(filename);
    final long fileLength =  file.length();
    @RequestMapping(path = "/solomonoff.jar", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {
        HttpHeaders headers = new HttpHeaders(); headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


}