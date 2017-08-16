function selectcss(color){
	link = document.getElementById('theme');
	var cssfile = 'css/chatroom-' + color + '.css';
	if(link.href != cssfile){
		link.href = cssfile;
		//location.reload();
	}
}
//'black', 'white', 'blue', 'lucky'
var themecolor = 'lucky'; //default
if(document.cookie.length > 0){
	themecolormatch = document.cookie.match(/(^| )themecolor=([^;]+)(;|$)/);
	if(themecolormatch != null)themecolor = unescape(themecolormatch[2])
}
function IsPC() {
    var userAgentInfo = navigator.userAgent;
    var Agents = ["Android", "iPhone",
                "SymbianOS", "Windows Phone",
                "iPad", "iPod"];
    var flag = true;
    for (var v = 0; v < Agents.length; v++) {
        if (userAgentInfo.indexOf(Agents[v]) != -1) {
            flag = false;
            break;
        }
    }
    return flag;
}
if(themecolor == 'lucky'){
	if(IsPC()){} else {themecolor = 'black'; }
}
selectcss(themecolor);