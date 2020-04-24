(function(window, document){
	window.moduleB = window.moduleB || {};
	window.moduleB.print = function(){
		console.log("This is moduleB !");
		document.write("This is moduleB !");
	}
})(window, document)