(function(window, document){
	window.moduleC = window.moduleC || {};
	window.moduleC.print = function(){
		console.log("This is moduleC !");
		document.write("This is moduleC !");
	}
})(window, document)