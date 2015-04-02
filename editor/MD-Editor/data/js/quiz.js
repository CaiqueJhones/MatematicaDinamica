/*/
 * Conversor laTex para html
 * @author Caique Jhones
 * @version 1.0
 * 
 * Copyright [2014] [CAIQUE JHONES]
 * Licensed under the Apache License, Version 2.0 (the ?License?);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ?AS IS? BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
/*/

points = 0;
incorrect = 0;
n_q = 0;
id = 0;
artigo = "";

$(document).ready(function () {
	artigo = document.title;
	//exibe formularios jÃ¡ preenchidos
	var pos;
	var f = document.forms;
	for(pos = 0; pos < f.length; pos++) {
		if(getCookie(artigo+"-"+f[pos].name) != "") {
			$("#"+f[pos].name).slideDown("slow");
			elem = document.getElementById("acerto_"+f[pos].name);
			if(elem != null)
				elem.style.display = "block";
		}
	}
	
	temp = getCookie(artigo+"-id");
	if(temp != "" ) 
		id = parseInt(temp);
	else {
		var d = new Date();
		id = d.getTime();
		setCookie(artigo+"-id", id, 365);
		
	}

	temp = getCookie(artigo+"-points");
	if(temp != "" ) 
		points = parseInt(temp);
		
	temp = getCookie(artigo+"-incorrect");
	if(temp != "" ) 
		incorrect = parseInt(temp);
		
	temp = getCookie(artigo+"-n_questions");
	if(temp != "" ) 
		n_q = parseInt(temp);
	
	if(getCookie(artigo+"-submit") != "")
		showMessage();
});
//deprecated
function getSolution(form, head){
	if(getCookie(artigo+"-"+form.name) != "") {
		alert("VocÃª jÃ¡ respondeu!");
		return;
	}
	
	var pos;
		
	var text = "";
	var points_local = 0;
	var incorrect_local = 0;
	var max = form.max.value;
	var question = new Vector(max);
	var resp = new Vector(max);
	var acertos_bool = new Vector(max);
	var messages_a = new Vector(max);
	var messages_e = new Vector(max);
					
	for(var i = 0; i < form.length; i++){
		for(var j = 1; j <= max; j++){
			if(form[i].name == "question"+j){
				if(form[i].checked){
					question[j] = form[i].value;
				}
			}
			if(form[i].name == "h_question"+j){
				resp[j] = form[i].value;
			}
			
			if(form[i].name == "certo_question"+j){
				messages_a[j] = form[i].value;
			}
			
			if(form[i].name == "erro_question"+j){
				messages_e[j] = form[i].value;
			}
		}
	}
	
	for(var i = 1; i <= max; i++){
		if(question[i] == "" || question[i] == null){
			alert("VocÃª esqueceu de marcar a questÃ£o " + i +"!");
			return;
		}
		if(question[i] == resp[i]){
			text += messages_a[i] == null ? "VocÃª acertou!\n" : messages_a[i]+"\n";
			points_local++;
			acertos_bool[i] = true;
		}else{
			text += messages_e[i] == null ? "VocÃª errou!\n" : messages_e[i]+"\n";
			incorrect_local++;
			acertos_bool[i] = false;
		}
	}
	
	alert(text);
	points += points_local;
	incorrect += incorrect_local;
	
	setCookie(artigo+"-incorrect", incorrect, 365);
		
	errou = false;
	for(var i = 1; i <= max; i++){
		if(acertos_bool[i] == false){
			errou = true;
			break;
		}
	}
	
	if(!errou) {
		n_q += parseInt(max);
		setCookie(artigo+"-n_questions", n_q, 365);
		setCookie(artigo+"-"+form.name, "1", 365);
		setCookie(artigo+"-points", points, 365);
	}
	
	if(head != null){
		if(!errou){
			//document.getElementById(head).style.display = "block"; 
			$("#"+head).slideDown("slow");
		}
	}
		
	if(form.name == "submit" && !errou){
		$(document).ready(function() {             
			$.post("/templates/proc_quiz.php",
			{
			  id:id,
			  pontos:getCookie(artigo+"-points"),
			  incorretas:getCookie(artigo+"-incorrect"),
			  n_question:getCookie(artigo+"-n_questions"),
			  artigo:document.title.replace("'", ""),
			  leu_tudo:"sim",
			  agente:navigator.userAgent,
			},
			function(data,status){
			  console.log(data);
			});
		});
		document.getElementById('quiz-fim').innerHTML = "ParabÃ©ns, vocÃª concluiu o artigo, seu aproveitamento foi de "+Math.round((points/(points+incorrect))*100)+"%.";
		return;
	}
	
	$(document).ready(function() {             
		$.post("/templates/proc_quiz.php",
			{
			  id:id,
			  pontos:getCookie(artigo+"-points"),
			  incorretas:getCookie(artigo+"-incorrect"),
			  n_question:getCookie(artigo+"-n_questions"),
			  artigo:document.title.replace("'", ""),
			  leu_tudo:"nao",
			  agente:navigator.userAgent,
			},
			function(data,status){
			  console.log(data);
			}
		);
	});
}

function solution(form, head) {
	if(getCookie(artigo+"-"+form.name) != "") {
		alert("VocÃª jÃ¡ respondeu!");
		return;
	}
	
	selecionado = 0;
	resposta = 0;
	for(var i = 0; i < form.length; i++){
		if(form[i].name == "alternativa"){
			if(form[i].checked){
				selecionado = form[i].value;
			}
		}
		if(form[i].name == "resposta"){
			resposta = form[i].value;
		}
	}
	
	if(selecionado == 0) {
		document.getElementById("empty_"+form.name).style.display = "block";
		return;
	}
	
	if(selecionado == resposta) {
		document.getElementById("empty_"+form.name).style.display = "none";
		document.getElementById("erro_"+form.name).style.display = "none";
		document.getElementById("acerto_"+form.name).style.display = "block";
		
		points++;
		
		setCookie(artigo+"-"+form.name, "1", 365);
		setCookie(artigo+"-points", points, 365);
		
		if(head != null){
				//document.getElementById(head).style.display = "block"; 
				$("#"+head).slideDown("slow");
		}
		
		if(form.name == "submit") {
			showMessage();
		}
	}else {
		document.getElementById("empty_"+form.name).style.display = "none";
		document.getElementById("erro_"+form.name).style.display = "block";
		
		incorrect++;
		
		setCookie(artigo+"-incorrect", incorrect, 365);
	}
	
}

function resume_all(){
	$(".hid").slideDown("slow");
}

function Vector(n){
	this.length = n;
	for(i = 0; i < n; i++){
		this[i] = "";
	}
}

/**
 * cname nome do cookie
 * cvalue valor do cookie
 * exdays dias para expirar
 */
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

/**
 * return o valor cookie especÃ­fico.
 */
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
    }
    return "";
}

function reset() {
    var d = new Date();
    d.setTime(d.getTime() - (1*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    	
	var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
		if (c.lastIndexOf(artigo+"-") != -1) {
			document.cookie = c + "=1 " + "; " + expires;
		}
    }
	window.location.reload();
}

function showMessage() {
	document.getElementById('quiz-fim').innerHTML = "Parabéns, você concluiu o artigo, seu aproveitamento foi de "+Math.round((points/(points+incorrect))*100)+"/100.";
}
