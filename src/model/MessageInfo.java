package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消息信息模型
 * @author gong
 */
public class MessageInfo {
	public MessageInfo() {}
	
	/**
	 * 消息id
	 */
	private int id;
	/**
	 * 消息对应用户
	 */
	private UserInfo user;
	/**
	 * 消息时间
	 */
	private Date msgTime;
	/**
	 * 消息内容
	 */
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

	/**
	 * 管理员的高亮标识
	 */
	static final String highlightmark = "#[hl!]";
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
