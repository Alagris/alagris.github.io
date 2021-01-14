/*
* To try in Ace editor, copy and paste into the mode creator
* here : http://ace.c9.io/tool/mode_creator.html
*/

define(function(require, exports, module) {
    "use strict";
    var oop = require("../lib/oop");
    var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;
    /* --------------------- START ----------------------------- */
    var MealyHighlightRules = function() {
    this.$rules = {
    "start" : [
       {
          "token" : ["keyword.operator", "punctuation"],
          "regex" : "([#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_][#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_0-9]*)(\\s*\\[)"
       },
       {
          "token" : ["keyword.operator", "punctuation.definition", "keyword.operator"],
          "regex" : "(!!\\s*)?([#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_][#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_0-9]*)(\\s*=)"
       },
       {
          "token" : ["punctuation.definition", "punctuation.definition", "keyword.operator"],
          "regex" : "(@\\s*)([#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_][#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_0-9]*)(\\s*=)"
       },
       {
          "token" : ["keyword.operator", "variable", "keyword.operator"],
          "regex" : "(!!\\s*)?([#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_][#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_0-9]*)(!)"
       },
       {
          "token" : ["variable.other", "variable.parameter"],
          "regex" : "(^:eval\\s+)(.*$)"
       },
       {
          "token" : "variable.other",
          "regex" : "([#.\u2205\u03a3])"
       },
       {
          "token" : ["keyword.operator", "variable"],
          "regex" : "(!!\\s*)?([#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_][#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_0-9]*)"
       },
       {
          "token" : ["punctuation.definition", "variable"],
          "regex" : "(@\\s*)([#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_][#.a-zA-Z\u03b1-\u03c9\u0391-\u03a9\u2205_0-9]*)"
       },
       {
          "token" : "constant.character",
          "regex" : "(<[0-9]+-[0-9]+>|\\[\\\\?.-\\\\?.\\])"
       },
       {
          "token" : "entity.name.type",
          "regex" : "(&&|->|<:|[;{}])"
       },
       {
          "token" : "keyword.operator",
          "regex" : "([|?*+:])"
       },
       {
          "token" : "punctuation",
          "regex" : "([\\](),])"
       },
       {
          "token" : "string punctuation",
          "regex" : "(')",
          "push" : "main__1"
       },
       {
          "token" : "constant.numeric",
          "regex" : "(-?\\d+)"
       },
       {
          "token" : "string",
          "regex" : "(<)",
          "push" : "main__2"
       },
       {
          "token" : "comment",
          "regex" : "(/\\*)",
          "push" : "main__3"
       },
       {
          "token" : "comment",
          "regex" : "(//.*)"
       },
       {
          "token" : "invalid",
          "regex" : "([^\\s])"
       },
       {
          defaultToken : "text",
       }
    ], 
    "main__1" : [
       {
          "token" : "string punctuation",
          "regex" : "(')",
          "next" : "pop"
       },
       {
          "token" : "text",
          "regex" : "(\\\\.)"
       },
       {
          "token" : "string punctuation",
          "regex" : "([^\\\\']+)"
       },
       {
          defaultToken : "text",
       }
    ], 
    "main__2" : [
       {
          "token" : "string",
          "regex" : "(>)",
          "next" : "pop"
       },
       {
          defaultToken : "string",
       }
    ], 
    "main__3" : [
       {
          "token" : "comment",
          "regex" : "(\\*/)",
          "next" : "pop"
       },
       {
          defaultToken : "comment",
       }
    ]
    };
    this.normalizeRules();
    };
    /* ------------------------ END ------------------------------ */
    oop.inherits(MealyHighlightRules, TextHighlightRules);
    exports.MealyHighlightRules = MealyHighlightRules;
    });
    