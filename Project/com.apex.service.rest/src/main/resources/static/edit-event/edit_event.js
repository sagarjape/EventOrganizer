
function edit() {
		var method_type, url;
		event={"eventName":$("#eventName").val(),"location":$("#location").val(),"host":{"username":user}
		,"date":$("#date").val(),"capacity":$("#capacity").val(),"ticketPrice":$("#ticketPrice").val()};

		put_("event/"+event_id, event).done(function(data){
		if(data){
			$("#edited").modal('show');
		}
	});
}


name=localStorage.getItem("name");
user=localStorage.getItem("username");

if(user==undefined)
	window.location.href = "../index.html";

window.onload = function() {
	document.getElementById("name_header").innerHTML=name;

event_id=localStorage.getItem("eventId");
if(event_id!=undefined){
	fetchResponse_('/event/'+event_id).done(function(data){
		if(data){
			var data = JSON.parse(data);
			$("#eventName").val(data.eventName);
			$("#location").val(data.location);
			$("#host").val(data.host.username);
			$("#date").val(data.date);
			$("#capacity").val(data.capacity);
			$("#ticketPrice").val(data.ticketPrice);
		}
	});
}
}