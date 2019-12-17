function share() {
	user=localStorage.getItem("username");
	shareWrapper={"eventNo":$("#eventName").val(),"usernames":$("#users").val().trim().split(' '),"currentUsername":user}
	post_("event/share", shareWrapper).then(function(data){
		if(data){
      $("#shared").modal('show');
		}
	},function(data){
    if(data){
      $("#error").modal('show');
    }
  });
}
shareEventId=localStorage.getItem("event_to_share");
name=localStorage.getItem("name");
user=localStorage.getItem("username");
if(user==undefined)
	window.location.href = "../index.html";

window.onload = function() {

	var usernames={};
	var events;
	document.getElementById("name_header").innerHTML=name;
	fetchResponse_('events?filterType=host&host='+user+'&newEvents=true').done(function(data){
		if(data){
			data = JSON.parse(data);
			events=data;
			createEventsDropDown(data);
		}
	});
    $('#eventName').on('change', function(){
    eventChanged(this.value);
	});

function eventChanged(eventId){
  document.getElementById('hidden_div').style.display = eventId != 'default'? 'block' : 'none';
  var optionSelected = $("option:selected", this);
    var valueSelected = eventId;
    for(index in events) {
    if(valueSelected==events[index].eventId){
      document.getElementById("ticketPrice").innerHTML=events[index].ticketPrice;
      $("#ticketPrice").val(events[index].ticketPrice);
      document.getElementById("location").innerHTML=events[index].location;
      $("#location").val(events[index].location);
      document.getElementById("host").innerHTML=events[index].host.username;
      $("#host").val(events[index].username);
      document.getElementById("date").innerHTML=events[index].date;
      $("#date").val(events[index].date);
      document.getElementById("capacity").innerHTML=events[index].capacity;
      $("#capacity").val(events[index].capacity);
      fetchResponse_('users?filterUsername='+user+'&eventNo='+valueSelected).done(function(data){
        if(data){
          createUsersArray(data);
        }
      });
    }
  }
}

function createUsersArray(data){
	data = JSON.parse(data);
  usernames={};
	for(index in data) {
		usernames[index]=data[index];
	}

}



function createEventsDropDown(data){
var select = document.getElementById("eventName");

if(shareEventId==undefined){
  select.options[select.options.length] = new Option('Select Event','default');
  $('#eventName').val('default');
  $('#eventName option:selected').attr('disabled','disabled');
}
for(index in data) {
    select.options[select.options.length] = new Option(data[index].eventName, data[index].eventId);
}
if(shareEventId!=undefined){
   $('#eventName').val(shareEventId);
   eventChanged(shareEventId);
   localStorage.removeItem("event_to_share");
}
}


function split(val) {
    return val.split(" ");
  }

  function extractLast(term) {
    return split(term).pop();
  }
  
  $("#users")
    // don't navigate away from the field on tab when selecting an item
    .bind("keydown", function(event) {
      if (event.keyCode === $.ui.keyCode.TAB &&
        $(this).autocomplete("instance").menu.active) {
        event.preventDefault();
      }
    })
    .autocomplete({
      minLength: 1,
      source: function(request, response) {
        // delegate back to autocomplete, but extract the last term
        response($.ui.autocomplete.filter(
          Object.values(usernames), extractLast(request.term)));
      },
      focus: function() {
        // prevent value inserted on focus
        return false;
      },
      select: function(event, ui) {
        var terms = split(this.value);
        // remove the current input
        terms.pop();
        // add the selected item
        terms.push(ui.item.value);
        // add placeholder to get the comma-and-space at the end
        terms.push("");
        this.value = terms.join(" ");
        return false;
      }
    });
}
