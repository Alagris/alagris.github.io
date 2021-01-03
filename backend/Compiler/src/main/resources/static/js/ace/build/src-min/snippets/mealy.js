define("ace/snippets/mealy",["require","exports","module"],function(e,t,n){"use strict";t.snippetText="snippet >\ndescription function\nscope mealy\n	-> ${1}() = ${2};\n\nsnippet >\ndescription if\nscope mealy\n	-> struct ${1} \\{ ${2:**} \\n \\}\n",t.scope="mealy"});                (function() {
                    window.require(["ace/snippets/mealy"], function(m) {
                        if (typeof module == "object" && typeof exports == "object" && module) {
                            module.exports = m;
                        }
                    });
                })();
            