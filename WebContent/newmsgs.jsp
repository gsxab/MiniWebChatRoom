<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="logincheck.LoginCheck" %>
<%@ page import="model.MessageInfo" %>
<%@ page import="dbo.DBAccess" %>
<%@ page import="java.util.Date" %>
<%@ page import="log.Log" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新消息查询页面，请勿直接访问</title>
</head>
<body>

<%
	response.setCharacterEncoding("UTF-8");
	response.setContentType("text/xml");
	LoginCheck login = LoginCheck.check(session);
	if(login == null){
%>
		<a></a><div class="warning">用户未登录！即将跳转</div>
<%
		return;
	}
	
	MessageInfo[] msgs; 
	try{
		DBAccess da = new DBAccess();
		Date lastupdate = (Date) session.getAttribute("lastupdate");
		if(lastupdate == null)
			throw new NullPointerException();
		msgs = da.getMessageListSince(lastupdate);
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
	for(MessageInfo msg : msgs){
		if(msg.getUserInfo().getUserId().equals(login.getUser().getUserId())){
%>
			<a name="#<%= msg.getId() %>"></a><div class="mymessagedisp"><img class="avatar" src="avatar?uid=<%= msg.getUserInfo().getUserId() %>" /><%= msg.toString() %></div>
<%
		} else {
%>
			<a name="#<%= msg.getId() %>"></a><div class="messagedisp"><img class="avatar" src="avatar?uid=<%= msg.getUserInfo().getUserId() %>" /><%= msg.toString() %></div>
<% 
		}
	}
%>
</body>
</html>