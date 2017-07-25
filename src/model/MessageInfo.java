package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageInfo {
	public MessageInfo() {}
	
	private int id;
	private UserInfo user;
	private Date msgTime;
	private String msgContent;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UserInfo getUserInfo() {
		return user;
	}
	public void setUserInfo(UserInfo userId) {
		this.user = userId;
	}
	public Date getMsgTime() {
		return msgTime;
	}
	public void setMsgTime(Date msgTime) {
		this.msgTime = msgTime;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	static final String highlightmark = "#[hl!]";
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
		String content;
//		try {
//			content = new String(msgContent.getBytes("ISO-8859-1"), "UTF-8");
//		} catch (UnsupportedEncodingException e) {
			content = msgContent;
//		}
		if(user.getUserpriv() == UserInfo.Privilege.ADMIN && content.startsWith(highlightmark)) {
			return user.getUsername() + "于" + sdf.format(msgTime) + "发言：<br/><p class='msgdhl'>" + content.substring(highlightmark.length()) + "</p>";
		}
		return user.getUsername() + "于" + sdf.format(msgTime) + "发言：<br/><p class='msgd'>" + content + "</p>";
	}
}
