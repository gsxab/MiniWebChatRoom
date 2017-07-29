function selectcss(color){
	link = document.getElementById('theme');
	var cssfile = 'css/chatroom-' + color + '.css';
	if(link.href != cssfile){
		link.href = cssfile;
		//location.reload();
	}
}
selectcss('white');