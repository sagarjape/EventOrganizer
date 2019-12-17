
	function signup() {
		if($("#username").val()==''||$("#userProfile").val()==''||$("#name").val()==''||$("#email").val()==''||$("#password").val()=='')
			$("#errorMsg").show();
		else{
			$("#errorMsg").hide();
			userData={"username":$("#username").val(),"userProfile":$("#userProfile").val(),"name":$("#name").val(),"email":$("#email").val(),"credentials":{"password":$("#password").val(),"username":$("#username").val()}};
			post_("user",userData).done(function(data){
				if(data){
					$("#signedup").modal('show');
				}
			});
		}
}
function signedup(){
	window.location.href = "../index.html";
}