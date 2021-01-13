function saveStaticDataToFile() {
    var val2 = outputField.value;
    var val1 = editor.getSession().getValue();
    var val4 = "Code: \n\n";
    var val5 = "\n\nConsole:  \n\n";
    var val3 = val4 + val1 + val5 + val2;
    var blob = new Blob([val3], {
        type: "text/plain;charset=utf-8"
    });
    saveAs(blob, "SavedGrammar.mealy");
}


replHistory = []
replHistoryIndex = 0
replCurrent = ""
const MAX_REPL_LENGTH = 3000
function createDropdownCallback(automaton){
    return function(){variableSelected(automaton)}
}
async function listAutomata() {
    const response = await fetch('list_automata', {
        method: 'POST'
    })
    const replResult = await response.text()
    const automata = JSON.parse(replResult)
    // var ll = "";
     while (automataHtmlList.childNodes.length > 2) {
         automataHtmlList.removeChild(automataHtmlList.lastChild);
     }
    for(var i=0;i<automata.length;i++){
        var span = document.createElement('span');
        var automaton = automata[i]
        span.appendChild(document.createTextNode(automaton));
        span.className = "border dropbtn"
        span.onclick = createDropdownCallback(automaton)
        automataHtmlList.appendChild(span);
     }
    //var tok = automataHtmlList + "";
    // automataHtmlList.toString();
    // automataHtmlList.join('; ');
    //var fok = "Define variables: " + ll;
    //tips.innerText = replResult;


    //  document.getElementsByName('inp')[0].placeholder=fok;

}

function escapeCookie(cookieText) {
    escaped = ''
    for (var i = 0; i < cookieText.length; i++) {
        if (cookieText.charAt(i) == ';') {
            escaped = escaped + '%s'
        } else if (cookieText.charAt(i) == '%') {
            escaped = escaped + '%p'
        } else if (cookieText.charAt(i) == '\n') {
            escaped = escaped + '%n'
        } else {
            escaped = escaped + cookieText.charAt(i)
        }
    }
    return escaped
}

function unescapeCookie(cookieText) {
    unescaped = ''
    for (var i = 0; i < cookieText.length; i++) {
        if (cookieText.charAt(i) == '%') {
            if (cookieText.charAt(i + 1) == 's') {
                unescaped = unescaped + ';'
            } else if (cookieText.charAt(i + 1) == 'p') {
                unescaped = unescaped + '%'
            } else if (cookieText.charAt(i + 1) == 'n') {
                unescaped = unescaped + '\n'
            }
            i++
        } else {
            unescaped = unescaped + cookieText.charAt(i)
        }
    }
    return unescaped
}

function setCookie(key, value) {
    document.cookie = key + "=" + escapeCookie(value) + ";max-age=" + (60 * 60 * 60) + ";"
}

function getCookie(key) {
    matches = document.cookie.match(';\\s*' + key + '=([^;]*)')
    if (matches == null) return null;
    return unescapeCookie(matches[1])
}




async function repl(replCommand) {
    replCommand = replCommand.trim()
    if (replCommand === "") {
        replCommand = " "
    }
    const input = replCommand.match(/\S+/g)
    const firstWord = input == null || input.length === 0 ? null : input[0]
    if (firstWord == ":clear") {
        outputField.value = ""
    } else if (firstWord == ":load") {
        const code = editor.getValue().trim()
        setCookie('code', code)
        const response = await fetch('upload_code', {
            method: 'POST',
            body: code
        })
        const errorMessage = await response.text()
    }
    const response = await fetch('repl', {
        method: 'POST',
        body: replCommand
    })
    const replResult = JSON.parse(await response.text())
    if (firstWord == ":vis" && !replResult.wasError) {
        console.log(replResult.output)
        const workerURL = 'js/full.render.js';
        let viz = new Viz({
            workerURL
        });
        viz.renderSVGElement(replResult.output).then(function(element) {
            ji.innerHTML = '';
            ji.appendChild(element);
            var modal = document.getElementById("myModal");
            modal.style.display = "block";
        }).catch(error => {
            // Create a new Viz instance (@see Caveats page for more info)
            viz = new Viz({
                workerURL
            });

            // Possibly display the error
            console.error(error);
        });
    }


    if (replCommand != null) {
        replHistory.push(replCommand)
        replHistoryIndex = replHistory.length
        toBeAppended = '> ' + replCommand + '\n' + (replResult.output).trim()
    } else {
        toBeAppended = (replResult.output).trim()
    }
    if (outputField.value.endsWith('\n') || outputField.value.length==0) {
        outputField.value += toBeAppended
    } else {
        outputField.value += '\n' + toBeAppended
    }
    outputField.scrollTop = outputField.scrollHeight
    replCurrent = inputField.value = ""

    if (replResult.wasError) {
        outputField.style.borderWidth = 'thick'
        outputField.style.borderColor = "red"
        rangeID = null
        if (replResult.col > -1) {
            range = new Range(replResult.row, replResult.col - 1, replResult.row, replResult.col + 1)
            rangeID = editor.session.addMarker(range, "red", "text");
            console.log("rangeID=" + rangeID)
        }

        setTimeout(function() {
            outputField.style.borderWidth = 'thin'
            outputField.style.borderColor = "black"
        }, 1500);
        if (rangeID != null) {
            editor.session.removeMarker(rangeID, "red", "text");
        }
    }
    listAutomata()

}

function htmlDecode(input) {
    var doc = new DOMParser().parseFromString(input, "text/html");
    return doc.documentElement.textContent;
}
function variableSelected(variable) {
  if(myDropdown.classList.contains('show')){
     if(htmlDecode(dropdownTitle.innerText)==variable){
       myDropdown.classList.remove('show');
       document.getElementById('inputField').style.pointerEvents = 'all';
       return
     }
  }else{
      myDropdown.classList.add('show');
      document.getElementById('inputField').style.pointerEvents = 'none';
  }
  dropdownTitle.innerText = variable;
}

window.onload = function() {
    codeSavedInCookie = getCookie('code')
    if (codeSavedInCookie == null) return
    editor.setValue(codeSavedInCookie);
    outputField.scrollTop = outputField.scrollHeight
}

window.onclick = function(event) {
  if(myDropdown.classList.contains('show')){
      if (!event.target.classList.contains('dropbtn')) {
          myDropdown.classList.remove('show');
          document.getElementById('inputField').style.pointerEvents = 'all';
      }
  }
}

function compile() {
    if (isSelected) {
        repl(editor.getSelectedText())
    } else {
        repl(':load')
    }
}

function closeModel() {
    var Cmodal = document.getElementById("myModal");
    Cmodal.style.display = "none";
}


function insert(text) {
    var customPosition = {
        row: 0,
        column: 0
    };
    //var text = "x = 'trt'";
    editor.setValue(text);
    //editor.session.insert(customPosition , text);
}

