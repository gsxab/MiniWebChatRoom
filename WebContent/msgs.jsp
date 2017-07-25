<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dbo.DBAccess" %>
<%@ page import="logincheck.LoginCheck" %>
<%@ page import="model.MessageInfo" %>
<%@ page import="log.Log" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="refresh" content="5" />
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
			setTimeout("jumpback()", 1000);
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
		msgs = da.getMessageList();
		if(msgs == null)
			throw new NullPointerException();
	} catch(Exception e) {
		Log.getInstance().logWarning("用户" + login.getUser().getUserId() + " " + login.getUser().getUsername() + "查询消息数据时异常");
		Log.getInstance().logErrorOnException(e);
%>
		<div class="warning">查询消息数据时发生异常！</div>
<%
		return;
	}
%>

<div class="msglist">
<% 
	for(MessageInfo msg : msgs){
%>
		<a name="#<%= msg.getId() %>"></a>
<%
		if(msg.getUserInfo().getUserId().equals(login.getUser().getUserId())){
%>
			<div class="mymessagedisp"><%= msg.toString() %></div>
<%
		} else {
%>
			<div class="messagedisp"><%= msg.toString() %></div>
<% 
		}
	}
%>
</div>
<a name="end"></a>

</body>
</html>