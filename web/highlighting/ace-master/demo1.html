function highlightSyntax(text) {
    var res = [];

    var Tokenizer = ace.require('ace/tokenizer').Tokenizer;
    var Rules = ace.require('ace/mode/sql_highlight_rules').SqlHighlightRules;
    var Text = ace.require('ace/layer/text').Text;

    var tok = new Tokenizer(new Rules().getRules());
    var lines = text.split('\n');

    lines.forEach(function(line) {
      var renderedTokens = [];
      var tokens = tok.getLineTokens(line);

      if (tokens && tokens.tokens.length) {
        new Text(document.createElement('div')).$renderSimpleLine(renderedTokens, tokens.tokens);
      }

      res.push('<div class="ace_line">' + renderedTokens.join('') + '</div>');
    });

    return '<div class="ace_editor ace-tomorrow"><div class="ace_layer" style="position: static;">' + res.join('') + '</div></div>';
}