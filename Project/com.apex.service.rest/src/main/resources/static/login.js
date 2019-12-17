
	function login() {
	cred={"username":$("#username").val(),"password":$("#password").val()};
	post_("login",cred).then(function(data){
		if(data){
			localStorage.setItem("username",cred.username);
			window.location.href = "../homepage/homepage.html";
		}
	},function(data){
		$("#invalidCredentials").modal('show');
	});
}
function logout(){
localStorage.clear();
}
logout();