function selectcss(color){
	link = document.getElementById('theme');
	var cssfile = 'css/chatroom-' + color + '.css';
	if(link.href != cssfile){
		link.href = cssfile;
		//location.reload();
	}
}
//'black', 'white', 'blue'
var themecolor = 'blue';
if(document.cookie.length > 0){
	themecolormatch = document.cookie.match(/(^| )themecolor=([^;]+)(;|$)/);
	if(themecolormatch != null)themecolor = unescape(themecolormatch[2])
}
selectcss(themecolor);