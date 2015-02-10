/*
 * Conversor laTex para html
 * @author Caique Jhones
 * @version 1.0
 * 
 * Copyright [2014] [CAIQUE JHONES]
 * Licensed under the Apache License, Version 2.0 (the �License�);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an �AS IS� BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
document.write(unescape("%3Cscript src='http://ajax.googleapis.com/ajax/libs/jquery/1.8/jquery.min.js' type='text/javascript'%3E%3C/script%3E"));
window.addEventListener("load",  function() {	 
	//texto
	 vet = document.getElementsByClassName('article');
	for (i = 0; i < vet.length; i++){
		 toHTML = vet[i].innerHTML;
		 if (toHTML != null){
			//var toHTML = document.body.innerHTML;
			 translate();
			 //document.body.innerHTML = toHTML ;
			 document.getElementsByClassName('article')[0].innerHTML = toHTML ;
			 document.getElementsByClassName('article')[0].style.display = "block"; 
		 }
		$(".toogle_title").click(function(){
		$(this).toggleClass("toggle_panel").next().slideToggle("fast");
		});
	}
	
	
	 	 
});

function translate() {
	
    theoremCount   = {};
	theoremName    = {};
	theoremPointer = {};
		
	//coment�rio
	if(/%+.*/.test(toHTML)) {
		test = toHTML.match(/%+.*/g);
		//console.log(test);
		for ( i=0; i < test.length ; i++) {
			array = test[i].match(/%(.*)/);
			replaceComments(array[0], array[1]);
		}
		
	}
	
	//retira os asteriscos
	replaceAsterisco();
			
	//newcommand
	replaceSpecialNewCommand();
	if(/newcommand{[\w-\\]+}[[0-9]*]*{.*}/.test(toHTML)) {
		test = toHTML.match(/newcommand{[\w-\\]+}[[0-9]*]*{.*}/g);
		//console.log(test);
		for ( i=0; i < test.length ; i++) {
			array = test[i].match(/newcommand{([\w-\\]+)}[[0-9]*]*{(.*)}/);
			replaceNewCommand(array[0], array[1], array[2]);
		}
		
	}
	
	//newthoerem do tipo \newtheorem{nome}{texto}
	if(/\\newtheorem{(\w+)}{(.+)}/.test(toHTML)) {
		test = toHTML.match(/\\newtheorem{(\w+)}{(.+)}/g);
		//console.log(test);
		for ( i=0; i < test.length ; i++) {
			array = test[i].match(/\\newtheorem{(\w+)}{(.+)}/);
			newTheorem(array[0], array[1], array[2], null);
		}
	}
	
	//newthoerem do tipo \newtheorem{nome}[contador]{texto}
	if(/\\newtheorem{(\w+)}\[(\w+)\]{(.+)}/.test(toHTML)) {
		test = toHTML.match(/\\newtheorem{(\w+)}\[(\w+)\]{(.+)}/g);
		//console.log(test);
		for ( i=0; i < test.length ; i++) {
			array = test[i].match(/\\newtheorem{(\w+)}\[(\w+)\]{(.+)}/);
			newTheorem(array[0], array[1], array[3], array[2]);
		}
	}
			
	//Item
	if(/\\[a-z]+{*}*.+/.test(toHTML)) {
		test = toHTML.match(/\\[a-z]+{*}*.+/g);
		//console.log(test);
		for ( i=0; i < test.length ; i++) {
			array = test[i].match(/\\([a-z]+){*}*(.+)/);
			replaceItems(array[0], array[1], array[2]);
		}
		
	}
	
	//Comandos do tipo comando{com}
	if(/\\[a-z]+{.[^}]+}/.test(toHTML)) {
		test = toHTML.match(/\\[a-z]+{.[^}]+}/g);
		//console.log(test);
		for ( i=0; i < test.length ; i++){
			array = test[i].match(/\\([a-z]+){(.[^}]+)}/);
			replaceCommands(array[0], array[1], array[2]);
		}
	}
	
	//comandos do tipo \comando
	if(/\\[a-z]+/.test(toHTML)) {
		test = toHTML.match(/\\([a-z]+)/g);
		//console.log(test);
		replaceLine(test);
	}
	
	//preambulo
	if(/\\[a-z]+[[\w-,]*]*{[a-z]*}*/.test(toHTML)) {
		test = toHTML.match(/\\[a-z]+[[\w-,]*]*{[a-z]*}*/g);
		//console.log(test);
		for ( i=0; i < test.length ; i++) {
			array = test[i].match(/\\([a-z]+)[[\w-,]*]*{[a-z]*}*/);
			replacePreAmbule(array[0], array[1]);
		}
	}
	
	//Comandos do tipo $$equa��o$$
	if(/\$+\$([\w\s\W][^\$]+)\$?\$/.test(toHTML)) {
		test = toHTML.match(/\$+\$([\w\s\W][^\$]+)\$?\$/g);
		//console.log(test);
		for ( i=0; i < test.length ; i++){
			array = test[i].match(/\$+\$([\w\s\W][^\$]+)\$?\$/);
			replaceEquation2(array[0], array[1]);
		}
	}	
	
	//Comandos do tipo $equa��o$
	if(/\$+([\w\W\s][^\$]*)\$+/.test(toHTML)) {
		test = toHTML.match(/\$+([\w\W\s][^\$]*)\$+/g);
		//console.log(test);
		for ( i=0; i < test.length ; i++){
			array = test[i].match(/\$+([\w\W\s][^\$]*)\$+/);
			replaceEquationLine(array[0], array[1]);
		}
	}	
		
}

function replaceEquation(txt, key){
	toHTML = toHTML.replace(txt, "<p><div lang='latex' style='text-align: center;'>"+key+"</div></p>");
}

function replaceEquation2(txt, key){
	key2 = key;
	while(key2.search(" ") != -1){
		key2 = key2.replace(" ", "&space;");
	}
	while(key2.search("\n") != -1){
		key2 = key2.replace("\n","&space;");
	}
	toHTML = toHTML.replace(txt, "<p style='text-align: center;'><img src=\"http://latex.codecogs.com/gif.latex?"+key2+"\" title=\""+key+"\"/></p>");
}

function replaceEquationLine(txt, key){
	toHTML = toHTML.replace(txt, "<span>$ "+key+" $</span>");
}

function newTheorem(txt, name, text, pointer){
	if(pointer == null)
		theoremCount[name] = 0;
	else
		theoremPointer[name] = pointer;
	theoremName[name] = text;
	toHTML = toHTML.replace(txt, "");
}

/*
 * por enquanto s� do estilo definition
 */
function replaceTheorem(name, txt){
	if(theoremCount[name] != null){
		theoremCount[name] += 1;
		toHTML = toHTML.replace(txt, "</br><span class='list-quiz'>"+theoremName[name]+" "+theoremCount[name]+"</span><div class='theorem'>");
	}else if(theoremPointer[name] != null) {
		p = theoremPointer[name];
		theoremCount[p] += 1;
		toHTML = toHTML.replace(txt, "</br><span class='list-quiz'>"+theoremName[p]+" "+theoremCount[name]+"</span><div class='theorem'>");
	}	
}

function replaceAsterisco(){
	toHTML = toHTML.replace("\\section*","\\section");
	toHTML = toHTML.replace("\\subsection*","\\subsection");
	toHTML = toHTML.replace("\\subsubsection*","\\subsubsection");
	toHTML = toHTML.replace("\\chapter*","\\chapter");
}

/*
 * Comandos simples
 */
function replaceLine(array) {
	for (i in array) {
		test = array[i];
		if(test == "\\par"){
			toHTML = toHTML.replace(test,"");
		}
		else if(test == "\\noindent"){
			toHTML = toHTML.replace(test,"");
		}
		else if(test == "\\newline"){
			toHTML = toHTML.replace(test,"<br>");
		}
		else if(test == "\\ldots"){
			toHTML = toHTML.replace(test,"...");
		}
		else if(test == "\\maketitle"){
			toHTML = toHTML.replace(test,"");
		}
		else if(test == "\\setlength"){
			toHTML = toHTML.replace(test,"");
		}
		else if(test == "\\space"){
			toHTML = toHTML.replace(test," ");
		}
	}
}

function replaceCommands(txt, key1, key2){
	if(key1 == "begin"){
		if(key2 == "document"){
			toHTML = toHTML.replace(txt, "");
		}
		
		else if(key2 == "equation"){
			toHTML = toHTML.replace(txt, "<p><div lang='latex' style='text-align: center'>");
		}
		
		if(key2 == "slider"){
			toHTML = toHTML.replace(txt, "<div class='toogle_title'>Clique para exibir</div><div class='toogle_panel'>");
		}
		
		if(key2 == "slider_app"){
			toHTML = toHTML.replace(txt, "<div class='toogle_title'>Clique para exibir o gráfico</div><div class='toogle_panel'><iframe width='540' height='360' src='");
		}
		
		else if(key2 == "enumerate"){
			toHTML = toHTML.replace(txt, "<ol>");
		}
		
		else if(key2 == "itemize"){
			toHTML = toHTML.replace(txt, "<ul>");
		}
		
		else if(key2 == "quote"){
			toHTML = toHTML.replace(txt, "<cite>");
		}
		
		else if(key2 == "flushleft"){
			toHTML = toHTML.replace(txt, "<p align='left'>");
		}
		
		else if(key2 == "flushright"){
			toHTML = toHTML.replace(txt, "<p align='right'>");
		}
		
		else if(key2 == "center"){
			toHTML = toHTML.replace(txt, "<p align='center'>");
		}
		
		else if(key2 == "comment"){
			toHTML = toHTML.replace(txt, "<!-- ");
		}
		
		else if(key2 == "description"){
			toHTML = toHTML.replace(txt, "");
		}
		
		else if(theoremName[key2] != null){
			replaceTheorem(key2, txt);
		}
		
	}
	
	else if(key1 == "end"){
		
		if(key2 == "document"){
			toHTML = toHTML.replace(txt, "");
		}
		
		else if(key2 == "equation"){
			toHTML = toHTML.replace(txt, "</div></p>");
		}
		
		if(key2 == "slider"){
			toHTML = toHTML.replace(txt, "</div>");
		}
		
		if(key2 == "slider_app"){
			toHTML = toHTML.replace(txt, "'></iframe></div>");
		}
		
		else if(key2 == "enumerate"){
			toHTML = toHTML.replace(txt, "</ol>");
		}
		
		else if(key2 == "itemize"){
			toHTML = toHTML.replace(txt, "</ul>");
		}
		
		else if(key2 == "quote"){
			toHTML = toHTML.replace(txt, "</cite>");
		}
		
		else if(key2 == "flushleft"){
			toHTML = toHTML.replace(txt, "</p");
		}
		
		else if(key2 == "flushright"){
			toHTML = toHTML.replace(txt, "</p>");
		}
		
		else if(key2 == "center"){
			toHTML = toHTML.replace(txt, "</p>");
		}
		
		else if(key2 == "description"){
			toHTML = toHTML.replace(txt, " -->");
		}
		
		else if(key2 == "comment"){
			toHTML = toHTML.replace(txt, "");
		}
		
		else if(theoremName[key2] != null){
			toHTML = toHTML.replace(txt, "</div>");
		}
		
	}
	
	else if(key1 == "title"){
		toHTML = toHTML.replace(txt, "<h2>"+key2+"</h2>");
	}
	
	else if(key1 == "section" || key1 == "chapter"){
		toHTML = toHTML.replace(txt, "<h3>"+key2+"</h3>");
	}
	
	else if(key1 == "subsection"){
		toHTML = toHTML.replace(txt, "<h4>"+key2+"</h4>");
	}
	
	else if(key1 == "subsubsection"){
		toHTML = toHTML.replace(txt, "<h5>"+key2+"</h5>");
	}
	
	else if(key1 == "textbf"){
		toHTML = toHTML.replace(txt, "<b>"+key2+"</b>");
	}
	
	else if(key1 == "emph"){
		toHTML = toHTML.replace(txt, "<em>"+key2+"</em>");
	}
	
	else if(key1 == "underline"){
		toHTML = toHTML.replace(txt, "<u>"+key2+"</u>");
	}
	
	else if(key1 == "fbox"){
		toHTML = toHTML.replace(txt, "<table border='1'><tr><td>"+key2+"</td></tr></table");
	}
	//comentado pois o MathJax j� faz isso
	// else if(key1 == "label"){
		// toHTML = toHTML.replace(txt, "<a id='"+key2+">"+key2+"</a>");
	// }
	
	// else if(key1 == "ref"){
		// toHTML = toHTML.replace(txt, "<a href='#"+key2+"'></a>");
	// }
	
	else if(key1 == "author"){
		toHTML = toHTML.replace(txt, "<p>"+key2+"</p>");
	}
	
	else if(key1 == "date"){
		toHTML = toHTML.replace(txt, "<p>"+key2+"</p>");
	}
			
	//N�o usados pelo html
	else if(key1 == "documentclass"){
		toHTML = toHTML.replace(txt, "");
	}
	
	else if(key1 == "pagestyle"){
		toHTML = toHTML.replace(txt, "");
	}
	
	else if(key1 == "thispagestyle"){
		toHTML = toHTML.replace(txt, "");
	}
	
	else if(key1 == "noident"){
		toHTML = toHTML.replace(txt, "");
	}
	
	else if(key1 == "usepackage"){
		toHTML = toHTML.replace(txt, "");
	}			
	
	else if(key1 == "parindent"){
		toHTML = toHTML.replace(txt, "");
	}
}

function replaceItems(txt, key1, key2){
	if(key1 == "item"){
		toHTML = toHTML.replace(txt, "<li>"+key2+"</li>");
	}
}

function replaceComments(txt, value){
	toHTML = toHTML.replace(txt, "<!-- "+value+" -->");
}

function replacePreAmbule(txt, key){
	if(key == "documentclass" || key == "usepackage"){
		toHTML = toHTML.replace(txt, "");
	}
}

function replaceNewCommand(txt, name, value){
	toHTML = toHTML.replace("\\"+txt, "");
	toHTML = toHTML.replace(name, value);
}

function replaceSpecialNewCommand(){
	if(/\\slider+{.*}{.*}/.test(toHTML)) {
		test = toHTML.match(/\\slider+{.*}{.*}/g);
		console.log(test);
		for ( i=0; i < test.length ; i++) {
			array = test[i].match(/\\slider+{(.*)}{(.*)}/);
			toHTML = toHTML.replace(array[0], "==slider="+array[1]+"==<p>"+array[2]+"</p>==/sliders==");
		}
	}
}
