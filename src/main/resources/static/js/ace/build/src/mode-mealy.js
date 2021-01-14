define("ace/mode/mealy_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module) {
"use strict";
var oop = require("../lib/oop");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;
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
oop.inherits(MealyHighlightRules, TextHighlightRules);
exports.MealyHighlightRules = MealyHighlightRules;
});

define("ace/mode/folding/cstyle",["require","exports","module","ace/lib/oop","ace/range","ace/mode/folding/fold_mode"], function(require, exports, module) {
"use strict";

var oop = require("../../lib/oop");
var Range = require("../../range").Range;
var BaseFoldMode = require("./fold_mode").FoldMode;

var FoldMode = exports.FoldMode = function(commentRegex) {
    if (commentRegex) {
        this.foldingStartMarker = new RegExp(
            this.foldingStartMarker.source.replace(/\|[^|]*?$/, "|" + commentRegex.start)
        );
        this.foldingStopMarker = new RegExp(
            this.foldingStopMarker.source.replace(/\|[^|]*?$/, "|" + commentRegex.end)
        );
    }
};
oop.inherits(FoldMode, BaseFoldMode);

(function() {
    
    this.foldingStartMarker = /([\{\[\(])[^\}\]\)]*$|^\s*(\/\*)/;
    this.foldingStopMarker = /^[^\[\{\(]*([\}\]\)])|^[\s\*]*(\*\/)/;
    this.singleLineBlockCommentRe= /^\s*(\/\*).*\*\/\s*$/;
    this.tripleStarBlockCommentRe = /^\s*(\/\*\*\*).*\*\/\s*$/;
    this.startRegionRe = /^\s*(\/\*|\/\/)#?region\b/;
    this._getFoldWidgetBase = this.getFoldWidget;
    this.getFoldWidget = function(session, foldStyle, row) {
        var line = session.getLine(row);
    
        if (this.singleLineBlockCommentRe.test(line)) {
            if (!this.startRegionRe.test(line) && !this.tripleStarBlockCommentRe.test(line))
                return "";
        }
    
        var fw = this._getFoldWidgetBase(session, foldStyle, row);
    
        if (!fw && this.startRegionRe.test(line))
            return "start"; // lineCommentRegionStart
    
        return fw;
    };

    this.getFoldWidgetRange = function(session, foldStyle, row, forceMultiline) {
        var line = session.getLine(row);
        
        if (this.startRegionRe.test(line))
            return this.getCommentRegionBlock(session, line, row);
        
        var match = line.match(this.foldingStartMarker);
        if (match) {
            var i = match.index;

            if (match[1])
                return this.openingBracketBlock(session, match[1], row, i);
                
            var range = session.getCommentFoldRange(row, i + match[0].length, 1);
            
            if (range && !range.isMultiLine()) {
                if (forceMultiline) {
                    range = this.getSectionRange(session, row);
                } else if (foldStyle != "all")
                    range = null;
            }
            
            return range;
        }

        if (foldStyle === "markbegin")
            return;

        var match = line.match(this.foldingStopMarker);
        if (match) {
            var i = match.index + match[0].length;

            if (match[1])
                return this.closingBracketBlock(session, match[1], row, i);

            return session.getCommentFoldRange(row, i, -1);
        }
    };
    
    this.getSectionRange = function(session, row) {
        var line = session.getLine(row);
        var startIndent = line.search(/\S/);
        var startRow = row;
        var startColumn = line.length;
        row = row + 1;
        var endRow = row;
        var maxRow = session.getLength();
        while (++row < maxRow) {
            line = session.getLine(row);
            var indent = line.search(/\S/);
            if (indent === -1)
                continue;
            if  (startIndent > indent)
                break;
            var subRange = this.getFoldWidgetRange(session, "all", row);
            
            if (subRange) {
                if (subRange.start.row <= startRow) {
                    break;
                } else if (subRange.isMultiLine()) {
                    row = subRange.end.row;
                } else if (startIndent == indent) {
                    break;
                }
            }
            endRow = row;
        }
        
        return new Range(startRow, startColumn, endRow, session.getLine(endRow).length);
    };
    this.getCommentRegionBlock = function(session, line, row) {
        var startColumn = line.search(/\s*$/);
        var maxRow = session.getLength();
        var startRow = row;
        
        var re = /^\s*(?:\/\*|\/\/|--)#?(end)?region\b/;
        var depth = 1;
        while (++row < maxRow) {
            line = session.getLine(row);
            var m = re.exec(line);
            if (!m) continue;
            if (m[1]) depth--;
            else depth++;

            if (!depth) break;
        }

        var endRow = row;
        if (endRow > startRow) {
            return new Range(startRow, startColumn, endRow, line.length);
        }
    };

}).call(FoldMode.prototype);

});

define("ace/mode/mealy",["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/mealy_highlight_rules","ace/mode/folding/cstyle"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var MealyHighlightRules = require("./mealy_highlight_rules").MealyHighlightRules; // use your $NAME_highlight_rules.js instead of "mealy_highlight_rules"
var FoldMode = require("./folding/cstyle").FoldMode;

var Mode = function() {
    this.HighlightRules = MealyHighlightRules;
    this.foldingRules = new FoldMode();
    this.$behaviour = this.$defaultBehaviour;
};
oop.inherits(Mode, TextMode);

(function() {
    this.lineCommentStart = "//";
    this.$id = "ace/mode/mealy"; //rename this to match your language
    this.snippetFileId = "ace/snippets/mealy"; //rename this to match your language
}).call(Mode.prototype);

exports.Mode = Mode;
});                (function() {
                    window.require(["ace/mode/mealy"], function(m) {
                        if (typeof module == "object" && typeof exports == "object" && module) {
                            module.exports = m;
                        }
                    });
                })();
            