
	function fetchData() {
	user=localStorage.getItem("username");
	if(user==undefined)
		window.location.href = "../index.html";

	fetchResponse_("userProfile/"+user).done(function(data){
		if(data){
			var data = JSON.parse(data);
			document.getElementById("email").innerHTML+=data.email;
			document.getElementById("name").innerHTML+="\n"+data.name;
			document.getElementById("userProfile").innerHTML+="\n"+data.userProfile;
			document.getElementById("name_header").innerHTML=data.name;
			localStorage.setItem("name",data.name);
		}
	});
	fetchResponse_("events?filterType=host&host="+user).done(function(data){
		if(data){
			var data = JSON.parse(data);
			const list = document.getElementById("eventsHosted");
			list.innerHTML = data.map(function(e) { 
				if(Date.parse(e.date)-Date.parse(new Date())>0){
				data.map(e=>e.date=formatDate(new Date(e.date)));
				return `<li id=’${e.eventName} ‘class=’listItem’ style='border: 1px solid grey;padding: 5px;'>
				Event Name: ${e.eventName}</br>
				Event Description: ${e.description} </br>
				Location: ${e.location} </br>
				Capacity: ${e.capacity} </br>
				Ticket Price: €${e.ticketPrice} </br>
				Date: ${e.date}</br>
						<button onclick='edit_event(${e.eventId})'>Edit</button><button onclick='delete_event(${e.eventId})'>Delete</button><button onclick='share(${e.eventId})'>Share</button></li></br>`;
				}
				else{
				data.map(e=>e.date=formatDate(new Date(e.date)));
				return `<li id=’${e.eventName} ‘class=’listItem’  style='border: 1px solid grey;padding: 5px;'>
				Event Name: ${e.eventName}</br>
				Event Description: ${e.description} </br>
				Location: ${e.location} </br>
				Capacity: ${e.capacity} </br>
				Ticket Price: €${e.ticketPrice} </br>
				Date: ${e.date}</br>
						<button disabled onclick='edit_event(${e.eventId})'>Edit</button><button disabled onclick='delete_event(${e.eventId})'>Delete</button><button disabled onclick='share(${e.eventId})'>Share</button></li></br>`;
				}
			}).join('');
		}
	});
	fetchResponse_("events/registeredEvents/"+user).done(function(data){
		if(data){
			var data = JSON.parse(data);
			const list = document.getElementById("eventsRegistered");
			list.innerHTML = data.map(e => `<li id=’${e.eventName} ‘class=’listItem’ style='border: 1px solid grey;padding: 5px;'>
				Event Name: ${e.eventName}</br>
				Event Description: ${e.description} </br>
				Location: ${e.location} </br>
				Capacity: ${e.capacity} </br>
				Ticket Price: €${e.ticketPrice} </br>
				Date: ${e.date}</br>
				<button onclick='unregister_event(${e.eventId})'>Unregister</button></li></br>`).join('\n');
			
		}
	});
}

window.onload = function() {
	fetchData();
}

function share(eventId){
	localStorage.setItem("event_to_share",eventId);
	window.location.href = "../share-event/share.html";
}

function delete_event(eventId){
	delete_("event/"+eventId).done(function(data){
			$("#deleted").modal('show');
			setTimeout(function(){ 			
				location.reload();
			}, 2000);
	});
}


function unregister_event(eventId){
	user=localStorage.getItem("username");
	post_("event/unregister/"+eventId+"?username="+user).done(function(data){
		$("#unregistered").modal('show');
		setTimeout(function(){ 			
				location.reload();
			}, 2000);
	});
}

function edit_event(eventId){
	localStorage.setItem("eventId",eventId);
	window.location.href = "../edit-event/edit_event.html";
}