<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dbo.DBAccess" %>
<%@ page import="java.util.Date" %>
<%@ page import="logincheck.LoginCheck" %>
<%@ page import="model.MessageInfo" %>
<%@ page import="log.Log" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<!--<meta http-equiv="refresh" content="5" />-->
<title>消息列表页</title>

<link rel="stylesheet" href="css/chatroom-black.css"></link>

</head>
<body>

<%
	LoginCheck login = LoginCheck.check(session);
	if(login == null){
%>
		<div class="warning">用户未登录！将于<a id="countdown" class="warning"></a>秒后跳转</div>
		<script type="text/javascript">
		var timeleft = 3;
		function jumpback(){
			var c = document.getElementById('countdown');
			if(timeleft > 0){
				c.innerHTML = timeleft;
				timeleft = timeleft - 1;
			} else {
				top.location.href="index.html";
			}
			setTimeout("jumpback()", 2000);
		}
		jumpback();
		</script>
<%
		return;
	}
%>

<% 
	MessageInfo[] msgs; 
	try{
		DBAccess da = new DBAccess();
		msgs = da.getMessageListSince(new Date(new Date().getTime() - 1000*60*60*24));
		if(msgs == null)
			throw new NullPointerException();
	} catch(Exception e) {
		Log.getInstance().logError("用户" + login.getUser().getUserId() + " " + login.getUser().getUsername() + "查询消息数据时异常").logErrorOnException(e);
%>
		<div class="warning">查询消息数据时发生异常！</div>
<%
		return;
	}
	session.setAttribute("lastupdate", new Date());
%>

<div class="msglist" id="msglist">
<% 
	for(MessageInfo msg : msgs){
%>
		<a name="#<%= msg.getId() %>"></a>
<% 
		if(msg.getUserInfo().getUserId().equals(login.getUser().getUserId())){
%>
			<div class="mymessagedisp"><img class="avatar" src="avatar?uid=<%= msg.getUserInfo().getUserId() %>" /><%= msg.toString() %></div>
<%
		} else {
%>
			<div class="messagedisp"><img class="avatar" src="avatar?uid=<%= msg.getUserInfo().getUserId() %>" /><%= msg.toString() %></div>
<% 
		}
	}
%>
<div class="messagedisp" id="bottomdiv"><a name="end">到底了</a></div>

</div>

<script type="text/javascript" src="js/ajaxInit.js"></script>
<script type="text/javascript">
function updateMsg(){
	var ajax = ajaxInit();
	ajax.open("get", "newmsgs.jsp", true);
	ajax.onreadystatechange = function(){
		if(ajax.readyState == 4 && ajax.status == 200){
			var msgs = ajax.responseText;
			//var re = /<a name="#\d+"><\/a>\s*<div class="(my)?messagedisp"><img class="avatar" src="avatar?uid=[\d-]{36}" \/>.*<\/div>/g;
			var re = /<a.*<\/div>/g;
			while((msg = re.exec(msgs)) != null){
				if(/<a><\/a>/.exec(msg) != null)top.location.href="login.html";
				var div = document.createElement("div");
				div.innerHTML = msg;
				nodes = div.childNodes;
				while (nodes.length != 0){
					document.getElementById("msglist").insertBefore(nodes[0], document.getElementById("bottomdiv"));
				}
			}
		}
	};
	ajax.send(null);
	setTimeout("updateMsg()", 3000);
}
updateMsg();
</script>

</body>
</html>