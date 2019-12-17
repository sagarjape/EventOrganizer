var base_url='EventManagement-env-1.kdqncjuwsr.us-east-1.elasticbeanstalk.com';
base_url='http://'+base_url+'/';
function post_(url_path, object){
var deferred = $.Deferred();
var url = base_url+url_path;

if(object!=undefined)
	var json = JSON.stringify(object);

var xhr = new XMLHttpRequest();
xhr.open("POST", url, true);
xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
xhr.onload = function () {
	var data = JSON.parse(xhr.responseText);
	if (xhr.readyState == 4 && (xhr.status == "200")||(xhr.status == "201")) {
		deferred.resolve(xhr.response);
	} else{
		deferred.reject("HTTP error: " + xhr.status);
	}
}
if(json!=undefined)
	xhr.send(json);
else
	xhr.send();
return deferred.promise();
};


function fetchResponse_(url_path){
var deferred = $.Deferred();
var url = base_url+url_path;


var xhr = new XMLHttpRequest();
xhr.open("GET", url, true);
xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
xhr.onload = function () {
	var data = JSON.parse(xhr.responseText);
	if (xhr.readyState == 4 && (xhr.status == "200")||(xhr.status == "201")) {
		deferred.resolve(xhr.response);
	} else{
		deferred.reject("HTTP error: " + xhr.status);
	}
}
xhr.send();
return deferred.promise();

};

function delete_(url_path){
var deferred = $.Deferred();
var url = base_url+url_path;


var xhr = new XMLHttpRequest();
xhr.open("DELETE", url, true);
xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
xhr.onload = function () {
	var data = xhr.responseText;
	if (xhr.readyState == 4 && (xhr.status == "200")||(xhr.status == "201")) {
		deferred.resolve(xhr.response);
	} else{
		deferred.reject("HTTP error: " + xhr.status);
	}
}
xhr.send();
return deferred.promise();
};


function put_(url_path, object){
var deferred = $.Deferred();
var url = base_url+url_path;

var json = JSON.stringify(object);

var xhr = new XMLHttpRequest();
xhr.open('PUT', url, true);
xhr.setRequestHeader('Content-type','application/json; charset=utf-8');

xhr.onload = function () {
	var data = JSON.parse(xhr.responseText);
	if (xhr.readyState == 4 && (xhr.status == "200")||(xhr.status == "201")) {
		deferred.resolve(xhr.response);
	} else{
		deferred.reject("HTTP error: " + xhr.status);
	}
}
xhr.send(json);
return deferred.promise();
};


function formatDate(date) {
  var monthNames = [
    "January", "February", "March",
    "April", "May", "June", "July",
    "August", "September", "October",
    "November", "December"
  ];

  var day = date.getDate();
  var monthIndex = date.getMonth();
  var year = date.getFullYear();

  return day + ' ' + monthNames[monthIndex] + ' ' + year;
}