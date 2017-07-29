<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dbo.DBAccess" %>
<%@ page import="model.UserInfo" %>
<%@ page import="logincheck.LoginCheck" %>
<%@ page import="log.Log" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="refresh" content="60" />
<title>用户列表页</title>

<link id="theme" type="text/css" rel="stylesheet" href="css/chatroom-black.css"></link>
<script type="text/javascript" src="js/selectcss.js"></script>

</head>
<body>

<%
	LoginCheck login = LoginCheck.check(session);
	if(login == null){
%>
		<div class="warning">用户未登录！</div>
<%
		return;
	}
%>

<% 
	UserInfo[] users; 
	try{
		DBAccess da = new DBAccess();
		users = da.getUserList();
		if(users == null)
			throw new NullPointerException();
	} catch(Exception e) {
		Log.getInstance().logError("查询用户时异常" + login.getUser().toString()).logErrorOnException(e);
%>
		<div class="warning">查询用户数据时发生异常！</div>
<%
		return;
	}
%>

<div class="userlist">
<img src="img/logo.png" class="smalllogo" />
<div class="myuserdispmini"><%= login.getUser().getUsername() %><a href="avatar.html" target="_blank"><img class="myuseravatar" src="avatar?uid=<%= login.getUser().getUserId() %>" title="点击更换头像" /></a></div>
<div class="userlistheader">用户列表：</div>
<% 
	Integer usercount = (Integer)application.getAttribute("usersonlinecount");
	if(usercount != null){
%>
		<div class="userlistheader">当前在线：<%= usercount.intValue() %></div>
<%	
	}
	if(application.getAttribute("usersonline") != null)
	for(UserInfo user : (Iterable<UserInfo>)application.getAttribute("usersonline")){
		if(user.getUserpriv() == UserInfo.Privilege.NORMAL){
%>
			<div class="userdispmini"><%= user.getUsername() %><img class="avatar" src="avatar?uid=<%= user.getUserId() %>" /></div>
<%
		} else {
%>
			<div class="userdispminipriv"><%= user.getUsername() %><img class="avatar" src="avatar?uid=<%= user.getUserId() %>" /></div>
<%
		}
	}
%>
		<div class="userlistheader">所有用户列表：</div>
<%
	for(UserInfo user : users){
		if(!user.getUserId().equals(login.getUser().getUserId())){
			if(user.getUserpriv() == UserInfo.Privilege.NORMAL){
%>
				<div class="userdispmini"><%= user.getUsername() %><img class="avatar" src="avatar?uid=<%= user.getUserId() %>" /></div>
<%
			} else {
%>
				<div class="userdispminipriv"><%= user.getUsername() %><img class="avatar" src="avatar?uid=<%= user.getUserId() %>" /></div>
<%
			}
		}
	}
%>
</div>

</body>
</html>