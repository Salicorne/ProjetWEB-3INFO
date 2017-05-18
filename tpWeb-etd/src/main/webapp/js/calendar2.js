"use strict";
let server = "http://localhost:8080/";

document.getElementById('loadCalendar').onclick = function() {
	//let week = document.getElementById('semaine').value;
	let idRes = document.getElementById('ressource').value;
    let req = new XMLHttpRequest(); // To be able to send REST commands

    req.onreadystatechange = function() { // This function will be called each time the state of the response changes.
        if (req.readyState != 4) return; // While not finished, do nothing.

        if(req.status==200) {// The HTTP code 200 is 'OK'
            console.log(req.responseType + " " + req.response);
            let tab = JSON.parse(req.response);
            console.log(tab); // Open the Firefox / Chrome console to explore the structure of the object.
            let output = "<table><tr><th>Horaire</th><th>Type</th><th>Matière</th><th>Promo</th><th>Enseignant</th><th>Durée</th></tr>";
            let empty = true;
            tab.forEach(function(e){
                empty = false;
                output += "<tr><td>"+e.horaire + "</td><td>" + e.type + "</td><td>" + e.matiere.name + "</td><td>"+e.matiere.annee + "INFO</td><td>"+e.ens.name+"</td><td>" + e.duration+"</td></tr>";
            });
            output += "</table>"
            document.getElementById('info').innerHTML = (empty) ? "Cannot be retrieved" : output;
        } else {
            document.getElementById('info').innerHTML = "Cannot be retrieved";
        }
    };

    // Sending a REST command to the server./
    req.open("GET", server+"calendar/getIdUse/"+idRes, true);// TODO May have to update the URI according to your definition in your CalendarResource.java
    req.send();
}



// Helpers

function getAnnee(xmlDoc) {
	return xmlDoc.getElementsByTagName("matiere")[0].getElementsByTagName("annee")[0].textContent;
}

function getMatiere(xmlDoc) {
	return xmlDoc.getElementsByTagName("matiere")[0].getElementsByTagName("name")[0].textContent;
}

function getHoraire(xmlDoc) {
	return new Date(xmlDoc.getElementsByTagName("horaire")[0].textContent);
}

function getDuree(xmlDoc) {
	return xmlDoc.getElementsByTagName("duration")[0].textContent;
}

function getEns(xmlDoc) {
	let ens = xmlDoc.getElementsByTagName("ens")[0];
	return ens.getElementsByTagName("name")[0].textContent;
}

