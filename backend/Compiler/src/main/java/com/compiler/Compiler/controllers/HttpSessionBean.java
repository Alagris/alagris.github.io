package com.compiler.Compiler.controllers;

import lombok.Data;

import net.alagris.CLI;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Data
@SessionScope
@Component
public class HttpSessionBean {
    private String name;
    private CLI.OptimisedHashLexTransducer compiler;
}

