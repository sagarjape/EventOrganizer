
function fetchData() {
	user=localStorage.getItem("username");
	if(user==undefined)
		window.location.href = "../index.html";

	fetchResponse_("events?filterType=archive&username="+user).done(function(data){
		if(data){
			var data = JSON.parse(data);
			const list = document.getElementById("events");
			if(data.length===0){
				document.getElementById("no_event").innerHTML="No Events Archieved at the moment";
			}
			else{
				data.map(e=>e.date=formatDate(new Date(e.date)));
				list.innerHTML = data.map(e => `<li id=’${e.eventName} ‘class=’listItem’>
					Event Name: ${e.eventName} </br>
					Event Description: ${e.description} </br>
					Location: ${e.location} </br>
					Capacity: ${e.capacity} </br>
					Ticket Price: €${e.ticketPrice} </br>
					Date: ${e.date}</br>
					<button onclick='unarchieve(${e.eventId})'>Unarchive</button></li></br>`).join('\n');
				
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
  						res=res[1].split("Unarchive");
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

function unarchieve(eventId){
	post_("event/unarchieve/"+eventId+"?username="+user).done(function(data){
		if(data){
			$("#unarchived").modal('show');
			setTimeout(function(){ 			
				location.reload();
			}, 2000);
		}
	});
}

window.onload = function() {
	name=localStorage.getItem("name");
	document.getElementById("name_header").innerHTML=name;
	fetchData();

}