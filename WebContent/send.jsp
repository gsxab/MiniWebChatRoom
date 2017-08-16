<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dbo.DBAccess" %>
<%@ page import="model.MessageInfo" %>
<%@ page import="model.UserInfo" %>
<%@ page import="logincheck.LoginCheck" %>
<%@ page import="java.util.Date" %>
<%@ page import="log.Log" %>
<% 
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	response.setContentType("text/html;charset=utf-8;pageencoding=utf-8");
	
	LoginCheck login = LoginCheck.check(request.getSession());
	
	if(login != null) {
		Date time = new Date();
		String msgcontent = request.getParameter("msgcontent");
		//msgcontent = new String(msgcontent.getBytes("ISO-8859-1"), "UTF-8");
		
		if(msgcontent != null) {
			msgcontent = msgcontent.replace("&", "&amp;");
			msgcontent = msgcontent.replace("<", "&lt;").replace(">", "&gt;").replace(" ", "&nbsp;");
			msgcontent = msgcontent.replace("\r\n", "<br/>").replace("\n", "<br/>").replace("\r", "<br/>");
			msgcontent = msgcontent.replaceAll("#\\[([gsb])\\]\\((1?\\d)\\)", "<img src='img/em/$1/$2.gif' alt='表情' class='meme'/>");
			
			MessageInfo msg = new MessageInfo();
			msg.setMsgContent(msgcontent);
			msg.setMsgTime(time);
			UserInfo user = login.getUser();
			boolean locked = (request.getParameter("locked") != null);
			msg.setUserInfo(user);
			
			//String redirectTarget = (login.getLight() ? "send.jsp?light=" : "send.jsp");
			
			try {
				DBAccess da = new DBAccess();
				da.sendNewMessage(msg, locked);
				Log.getInstance().log(msg.toString());
			} catch (Exception e) {
				e.printStackTrace();
				Log.getInstance().logError("发送消息时SQL错误或编码错误：用户" + user.getUserId() + "，消息：" + msg.toString());
				//response.getWriter().print("未知错误无法发送消息，可能是无法编码。");
				//response.addHeader("refresh", "3;url=" + redirectTarget);
				//return;
			}
		}
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title>发送页面</title>

<link id="theme" type="text/css" rel="stylesheet" href=""></link>
<script type="text/javascript" src="js/selectcss.js"></script>

<script type="text/javascript" src="js/ajaxInit.js"></script>
<script type="text/javascript">
function check(){
	msg = document.getElementById('msgcontent').value;
	if(msg.length == 0){
		return false;
	}
	return true;
}
function ctrlenter(event){
	if(event.keyCode==13 && event.ctrlKey){
		if(check())
			document.sendmsg.submit();
		else 
			return false;
	}
}
function logoutfunc(){
/*	var date=new Date(); 
	date.setTime(date.getTime()-100); 
	document.cookie="loginid=; expires="+date.toGMTString(); 
	document.cookie="loginname=; expires="+date.toGMTString(); 
	document.cookie="loginadmin=; expires="+date.toGMTString();
	top.location.href="index.html"; */
	var hr = ajaxInit();
	hr.open("get", "loginaction.html?logout=", true);
	hr.onreadystatechange = function(){ 
		if(hr.readyState == 4 && hr.status == 200){
			top.location.href = "index.html";
		}
	}
	hr.send(null);
}
</script>

</head>
<body>

<%
	if(login == null){
%>
		<div class="warning">用户未登录！</div>
<%
		return;
	}
%>

<form name="sendmsg" onsubmit="return check()" action="send.jsp" method="post">
<div class="sendarea">
<textarea class="send" name="msgcontent" id="msgcontent" rows="5" onkeyup="return ctrlenter(event);"></textarea>
<div id="memelist" style="text-align:left;overflow-y:auto;display:none" ></div>
<div class="send_foot">
	<input type="button" class="meme_sel" onclick="togglememes()" />
	<table class="cssselector" >
		<tr>
		<td><input type="button" class="cssselectorbtn" style="background-color:black" title="经典黑" onclick="setColorCookie('black')" /></td>
		<td><input type="button" class="cssselectorbtn" style="background-color:white" title="简朴白" onclick="setColorCookie('white')" /></td>
		</tr>
		<tr>
		<td><input type="button" class="cssselectorbtn" style="background-color:#09f" title="湖水青" onclick="setColorCookie('blue')" /></td>
		<td><input type="button" class="cssselectorbtn" style="background-color:#c9dbe9" title="没wifi" onclick="setColorCookie('lucky')" /></td>
		</tr>
	</table>
	<center><table style="width:auto"><tr>
		<td><input type="submit" value="发送" /></td>
		<td><input type="reset" value="清空" /></td>
		<td><input type="button" value="注销" id="logout" onclick="logoutfunc()" /></td>
<%
	if(login.getUser().getUserpriv() == UserInfo.Privilege.ADMIN){
%>
		<td><input type="checkbox" name="locked" value="1" /></td>
		<td><div class="prompt">锁定此消息</div></td>
<%
	}
%>
	</tr></table></center>
</div>
</div>
</form>

<script type="text/javascript">
function setColorCookie(color) {
	if(color.length == 0){
		document.cookie = "themecolor=;expires=" + (new Date()).toGMTString();
	} else {
		document.cookie = "themecolor=" + color;
	}
	top.location.reload();
}
var isShowImg = false;
var imglist = [ // 表情列表，在轻量模式下不显示后两系列
	// 杀小兵系列
	{faceCode:'#[s](1)',facePath:'/s/1.gif',faceName:'杀小兵_冰'},
	{faceCode:'#[s](2)',facePath:'/s/2.gif',faceName:'杀小兵_害羞'},
	{faceCode:'#[s](3)',facePath:'/s/3.gif',faceName:'杀小兵_汗'},
	{faceCode:'#[s](4)',facePath:'/s/4.gif',faceName:'杀小兵_黑线'},
	{faceCode:'#[s](5)',facePath:'/s/5.gif',faceName:'杀小兵_奸笑'},
	{faceCode:'#[s](6)',facePath:'/s/6.gif',faceName:'杀小兵_惊'},
	{faceCode:'#[s](7)',facePath:'/s/7.gif',faceName:'杀小兵_哭'},
	{faceCode:'#[s](8)',facePath:'/s/8.gif',faceName:'杀小兵_雷'},
	{faceCode:'#[s](9)',facePath:'/s/9.gif',faceName:'杀小兵_怒'},
	{faceCode:'#[s](10)',facePath:'/s/10.gif',faceName:'杀小兵_睡'},
	{faceCode:'#[s](11)',facePath:'/s/11.gif',faceName:'杀小兵_吐'},
	{faceCode:'#[s](12)',facePath:'/s/12.gif',faceName:'杀小兵_吐血'},
	{faceCode:'#[s](13)',facePath:'/s/13.gif',faceName:'杀小兵_笑'},
	{faceCode:'#[s](14)',facePath:'/s/14.gif',faceName:'杀小兵_色'},
	{faceCode:'#[s](15)',facePath:'/s/15.gif',faceName:'杀小兵_晕'},
<%
	if(request.getParameter("light") == null){
%>
	// Q版郭嘉系列
	{faceCode:'#[g](0)',facePath:'/g/0.gif',faceName:'郭嘉_叹气'},
	{faceCode:'#[g](1)',facePath:'/g/1.gif',faceName:'郭嘉_汗'},
	{faceCode:'#[g](2)',facePath:'/g/2.gif',faceName:'郭嘉_黑线'},
	{faceCode:'#[g](3)',facePath:'/g/3.gif',faceName:'郭嘉_贱笑'},
	{faceCode:'#[g](4)',facePath:'/g/4.gif',faceName:'郭嘉_囧'},
	{faceCode:'#[g](5)',facePath:'/g/5.gif',faceName:'郭嘉_哭'},
	{faceCode:'#[g](6)',facePath:'/g/6.gif',faceName:'郭嘉_雷'},
	{faceCode:'#[g](7)',facePath:'/g/7.gif',faceName:'郭嘉_卖萌'},
	{faceCode:'#[g](8)',facePath:'/g/8.gif',faceName:'郭嘉_怒'},
	{faceCode:'#[g](9)',facePath:'/g/9.gif',faceName:'郭嘉_石化'},
	{faceCode:'#[g](10)',facePath:'/g/10.gif',faceName:'郭嘉_睡'},
	{faceCode:'#[g](11)',facePath:'/g/11.gif',faceName:'郭嘉_吐'},
	{faceCode:'#[g](12)',facePath:'/g/12.gif',faceName:'郭嘉_吐血'},
	{faceCode:'#[g](13)',facePath:'/g/13.gif',faceName:'郭嘉_笑'},
	{faceCode:'#[g](14)',facePath:'/g/14.gif',faceName:'郭嘉_心'},
	{faceCode:'#[g](15)',facePath:'/g/15.gif',faceName:'郭嘉_晕'},
	{faceCode:'#[g](16)',facePath:'/g/16.gif',faceName:'郭嘉_耳光'},
	{faceCode:'#[g](17)',facePath:'/g/17.gif',faceName:'郭嘉_害羞'},
	{faceCode:'#[g](18)',facePath:'/g/18.gif',faceName:'郭嘉_胜利'},
	{faceCode:'#[g](19)',facePath:'/g/19.gif',faceName:'郭嘉_思考'}, 
	// Q版甄姬系列
	{faceCode:'#[b](0)',facePath:'/b/0.gif',faceName:'甄姬_爪子'},
	{faceCode:'#[b](1)',facePath:'/b/1.gif',faceName:'甄姬_点点'},
	{faceCode:'#[b](2)',facePath:'/b/2.gif',faceName:'甄姬_飞吻'},
	{faceCode:'#[b](3)',facePath:'/b/3.gif',faceName:'甄姬_羞'},
	{faceCode:'#[b](4)',facePath:'/b/4.gif',faceName:'甄姬_鬼脸'},
	{faceCode:'#[b](5)',facePath:'/b/5.gif',faceName:'甄姬_汗'},
	{faceCode:'#[b](6)',facePath:'/b/6.gif',faceName:'甄姬_黑线'},
	{faceCode:'#[b](7)',facePath:'/b/7.gif',faceName:'甄姬_哼'},
	{faceCode:'#[b](8)',facePath:'/b/8.gif',faceName:'甄姬_坏笑'},
	{faceCode:'#[b](9)',facePath:'/b/9.gif',faceName:'甄姬_囧'},
	{faceCode:'#[b](10)',facePath:'/b/10.gif',faceName:'甄姬_哭'},
	{faceCode:'#[b](11)',facePath:'/b/11.gif',faceName:'甄姬_卖萌'},
	{faceCode:'#[b](12)',facePath:'/b/12.gif',faceName:'甄姬_怒'},
	{faceCode:'#[b](13)',facePath:'/b/13.gif',faceName:'甄姬_胜利'},
	{faceCode:'#[b](14)',facePath:'/b/14.gif',faceName:'甄姬_石化'},
	{faceCode:'#[b](15)',facePath:'/b/15.gif',faceName:'甄姬_睡'},
	{faceCode:'#[b](16)',facePath:'/b/16.gif',faceName:'甄姬_微笑'},
	{faceCode:'#[b](17)',facePath:'/b/17.gif',faceName:'甄姬_问号'},
	{faceCode:'#[b](18)',facePath:'/b/18.gif',faceName:'甄姬_晕'},
	{faceCode:'#[b](19)',facePath:'/b/19.gif',faceName:'甄姬_晕倒'}, 
<%
	}
%>
];
function togglememes(){
	var memelist = document.getElementById('memelist');
	if(isShowImg==false){ 
		isShowImg=true;  
	    document.getElementById('msgcontent').disabled = true;
		textareaheight = 105; // 5(rows)*20(line-height)+5(border-radius)
		memelist.style.height = textareaheight + 'px';
		memelist.style.width = '105%'
		memelist.style.marginTop = '-' + textareaheight + 'px';
		memelist.style.display = 'block';
		if(memelist.getElementsByTagName('img').length==0){ 
			for(var i=0; i < imglist.length; ++i){ 
				var img = document.createElement('img'); 
				img.title = imglist[i].faceName; 
				img.alt = imglist[i].faceCode;
				img.src = 'img/em/' + imglist[i].facePath;
				img.className = 'memeshow';
				img.onclick = function(){selectmeme(this)};
				memelist.appendChild(img); 
			}
		}
	} else {
		isShowImg=false;
	    document.getElementById('msgcontent').disabled = false;
		memelist.style.display = 'none';
		//memelist.style.marginTop='0';
		//memelist.style.height='0';
	}
}
function selectmeme(whichimg){
	isShowImg=false; 
    document.getElementById('msgcontent').disabled = false;
    document.getElementById('msgcontent').value += whichimg.alt;
    document.getElementById('msgcontent').focus();
    memelist.style.display = 'none';
	//memelist.style.marginTop='0';
	//memelist.style.height='0';
}
</script>

</body>
</html>