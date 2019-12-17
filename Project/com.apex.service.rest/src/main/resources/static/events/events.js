

	function fetchData() {
	user=localStorage.getItem("username");
	if(user==undefined)
		window.location.href = "../index.html";

	fetchResponse_("events?filterType=username&username="+user).done(function(data){
		if(data){
			var data = JSON.parse(data);
			const list = document.getElementById("events");
			if(data.length===0){
				document.getElementById("no_event").innerHTML="No Events at the moment";
			}
			else{
				list.innerHTML = data.map(function(e) {
				if(Date.parse(e.date)-Date.parse(new Date())<0){
					data.map(e=>e.date=formatDate(new Date(e.date)));
					return `<li id=’${e.eventName} ‘class=’listItem’>
					Event Name: ${e.eventName} </br>
					Event Description: ${e.description} </br>
					Location: ${e.location} </br>
					Capacity: ${e.capacity} </br>
					Ticket Price: €${e.ticketPrice} </br>
					Date: ${e.date}</br>
				<button onclick='register(${e.eventId}, user)'>Register</button>
					<button onclick='archive(${e.eventId})'>Archive</button></li></br>`
				}
				else{
					data.map(e=>e.date=formatDate(new Date(e.date)));
					return `<li id=’${e.eventName} ‘class=’listItem’>
				Event Name: ${e.eventName} </br>
					Event Description: ${e.description} </br>
					Location: ${e.location} </br>
					Capacity: ${e.capacity} </br>
					Ticket Price: €${e.ticketPrice} </br>
				Date: ${e.date}</br>
				<button onclick='register(${e.eventId}, user)'>Register</button>
					<button disabled onclick='archive(${e.eventId})'>Archive</button></li></br>`
				}
				}).join('');
				
				$.expr[":"].contains = $.expr.createPseudo(function(arg) {
    				return function( elem ) {
        				return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    				};
				});

				// https://stackoverflow.com/a/7091640/1366033
				$(window).on("keydown",function (e) {
					var KEYS = { F3: 114, F: 70 }
    				if (e.keyCode === KEYS.F3 || ((e.ctrlKey || e.metaKey) && e.keyCode === KEYS.F)) { 
        				e.preventDefault();
        				$("#filter-search").focus();
    				}
				})

				$("#filter-search").on('keyup change',function(){

					var searchText = this.value;
  					var searchTerms = searchText.replace(/ /g,"|");
  					var searchRegex = new RegExp(searchTerms, "i");
  
  					var hasFilter = searchText.length > 0;
  
  					var $list = $("#events")	
  
  					$list.toggleClass("filtered", hasFilter);
  					$("#filter-results").toggleClass("filtered", hasFilter);
  
  					$list.find("li").each(function(i, el){
  						var $el = $(el)
  						var result=[];
    					var res;
  						var elText = $el.text();
  						res = elText.split("Event Name: ")[1].split("Event Description: ");
  						result[0]=res[0];
  						res=res[1].split("Location: ");
  						result[1]=res[0];
  						res=res[1].split("Capacity: ");
  						result[2]=res[0];
  						res=res[1].split("Ticket Price: ");
  						result[3]=res[0];
  						res=res[1].split("Date: ");
  						result[4]=res[0];
  						res=res[1].split("Register");
  						result[5]=res[0];
	    				var match = searchRegex.test(result);
    
    					$el.toggleClass("found", match)
  					})
  
  					var totalfields = $list.find("li").length;
  					var foundFields = $list.find("li.found").length;
  
  					var resultsMessage = "Showing " + foundFields + " of " + totalfields + " fields";
  					$("#filter-results-text").html(resultsMessage);
				});

				$("#filter-clear").click(function(e) {
					e.preventDefault();
   					$("#filter-search").val("").change();
				})
			}
		}
	});
}

function register(eventId, user){
	post_("event/"+eventId+"?username="+user).done(function(data){
		if(data){
			$("#registered").modal('show');
			setTimeout(function(){ 			
				location.reload();
			}, 2000);
		}
	});
}

function archive(eventId){
	post_("event/archive/"+eventId+"?username="+user).done(function(data){
		if(data){
			$("#archived").modal('show');
			setTimeout(function(){ 			
				location.reload();
			}, 2000);
		}
	});
}

window.onload = function() {
	fetchData();
	name=localStorage.getItem("name");
	document.getElementById("name_header").innerHTML=name;
}