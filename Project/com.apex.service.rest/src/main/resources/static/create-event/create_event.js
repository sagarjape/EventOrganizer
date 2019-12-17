function create() {
		if($("#eventName").val()==''||$("#location").val()==''||$("#capacity").val()==''||$("#date").val()==''||$("#ticketPrice").val()==''||$("#description").val()=='')
			$("#errorMsg").show();
		else{
		$("#errorMsg").hide();
		var method_type, url;
		event={"eventName":$("#eventName").val(),"location":$("#location").val(),"host":{"username":user}
		,"date":$("#date").val(),"capacity":$("#capacity").val(),"ticketPrice":$("#ticketPrice").val(),"description":$("#description").val()};

		post_("event", event).done(function(data){
		if(data){
			$("#created").modal('show');
		}
	});
	}
}
name=localStorage.getItem("name");
user=localStorage.getItem("username");
window.onload = function() {
	document.getElementById("name_header").innerHTML=name;
}

if(user==undefined)
	window.location.href = "../index.html";
