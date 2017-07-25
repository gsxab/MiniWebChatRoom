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

<link rel="stylesheet" href="css/chatroom-black.css"></link>

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
		Log.getInstance().logWarning("用户" + login.getUser().getUserId() + " " + login.getUser().getUsername() + "查询消息数据时异常");
		Log.getInstance().logWarning(e.getMessage());
%>
		<div class="warning">查询用户数据时发生异常！</div>
<%
		return;
	}
%>

<div class="userlist">
<img src="img/logo.png" class="smalllogo" />
<div class="userlistheader">用户列表：</div>
<div class="myuserdispmini"><%= login.getUser().getUsername() %></div>
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
			<div class="userdispmini"><%= user.getUsername() %></div>
<%
		} else {
%>
			<div class="userdispminipriv"><%= user.getUsername() %></div>
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
				<div class="userdispmini"><%= user.getUsername() %></div>
<%
			} else {
%>
				<div class="userdispminipriv"><%= user.getUsername() %></div>
<%
			}
		}
	}
%>
</div>

</body>
</html>