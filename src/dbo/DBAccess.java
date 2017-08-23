package dbo;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.LinkedList;
import java.util.UUID;

import model.MessageInfo;
import model.UserInfo;

import log.Log;

/**
 * 处理数据库相关连接的类，使用model包的类及其数组进行通信.
 * 初始化此类时加载数据库驱动器的类
 * @author gong
 */
public class DBAccess {
	private Connection conn;
	
	private static String dburl="jdbc:mysql://localhost:3306/chatroom?useSSL=false&useUnicode=true&characterEncoding=utf-8";
	private static String dbuser="nowifi";
	private static String dbpassword="org.nowifi";
	
	/**
	 * 使用默认的数据库连接用户名和密码进行通信
	 * @throws SQLException
	 */
	public DBAccess() throws SQLException {
		conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
	}
	
	/**
	 * 类初始化时加载数据库驱动器
	 */
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver for database not found!");
			//!
		}
	}

	/**
	 * 检查用户名是否存在
	 * @param username 要检测的用户名
	 * @return 如果已存在返回true，不存在返回false
	 * @throws SQLException 当SQL异常发生时
	 */
	public boolean checkUser(String username) throws SQLException{
		if(username == null)return false;
		
		PreparedStatement sta = null;
		ResultSet rs = null;
		
		try {
			sta = conn.prepareStatement("select count(*) from cr_users where uname=?");
			sta.setString(1, username);
			rs = sta.executeQuery();
			if(rs.next()) {
				return rs.getInt(1) > 0;
			} else {
				return true;
			} 
		} catch (Exception e) {
			Log.getInstance().logError("检查用户名：" + username + "时发生异常！").logErrorOnException(e);
			throw e;
		} finally {
			if(sta != null) { sta.close(); }
			if(rs != null) { rs.close(); }
			conn.close();
		}
	}
	
	/**
	 * 注册或登录用户
	 * @param user 用户信息结构，含用户名及密码
	 * @param isRegister 当前操作是注册(true)还是登录(false)
	 * @return 验证成功时，用户信息结构，含用户名密码及id；失败时null
	 * @throws SQLException 当SQL异常发生时
	 */
	public UserInfo validateUser(UserInfo user, boolean isRegister) throws SQLException{
		if(user == null)return null;
		
		PreparedStatement sta = null; 
		ResultSet rs = null;
		
		try { 
			
			sta = conn.prepareStatement("select uid, upriv from cr_users where uname=? and upswd=?;");
			sta.setString(1, user.getUsername());
			sta.setString(2, user.getPassword());
			rs = sta.executeQuery();
			
			if(isRegister) {
				if(rs.next()) {
					return null;
				}
				else {
					user.setUserId(UUID.randomUUID().toString());
					PreparedStatement sta2 = null;
					int rslt = 0;
					try{
						sta2= conn.prepareStatement("insert into cr_users(uid, uname, upswd) values (?,?,?)");
						sta2.setString(1, user.getUserId());
						sta2.setString(2, user.getUsername());
						sta2.setString(3, user.getPassword());
						user.setUserpriv(UserInfo.Privilege.NORMAL);
					
						rslt = sta2.executeUpdate();
					} finally {
						if(sta2 != null)sta2.close();
					}
					if(rslt == 0)
						return null;
					else
						return user;
				}
			} else {
				if(rs.next()) {
					user.setUserId(rs.getString("uid"));
					user.setUserpriv(UserInfo.Privilege.valueOf(rs.getInt("upriv")));
					return user;
				}
				else {
					return null;
				}
			}
		} catch(SQLException e) {
			Log.getInstance().logError("登录或注册异常" + "用户名：" + user.getUsername());
			Log.getInstance().logError(e.getMessage());
			return null;
		} finally {
			if(sta != null)sta.close();
			if(rs != null)rs.close();
			conn.close();
		}
	}
	
	/**
	 * 获得用户列表.
	 * @return UserInfo[], 是不包含密码的用户数据的列表，失败时返回null
	 * @throws SQLException 
	 */
	public UserInfo[] getUserList() throws SQLException {
		Statement sta = null;
		LinkedList<UserInfo> users = new LinkedList<UserInfo>();
		
		ResultSet rs = null;
		try{
			sta = conn.createStatement();
			rs = sta.executeQuery("select uid, uname, upriv from cr_users l");
		
			if(rs != null) {
				while(rs.next()) {
					UserInfo user = new UserInfo();
					user.setUserId(rs.getString("uid"));
					user.setUsername(rs.getString("uname"));
					user.setUserpriv(UserInfo.Privilege.valueOf(rs.getInt("upriv")));
					users.addFirst(user);
				}
			}
		} finally {
			if(sta != null)sta.close();
			if(rs != null)rs.close();
			conn.close();
		}
		return users.toArray(new UserInfo[0]);
	}
	
	public MessageInfo[] getMessageList() throws SQLException {
		Statement sta = null;
		LinkedList<MessageInfo> msgs = new LinkedList<MessageInfo>();
		
		ResultSet rs = null;
		
		try {
			sta = conn.createStatement();
			rs = sta.executeQuery("select mid, uid, mtime, mcontent, uname, upriv, mhasprev from cr_msgs left outer join cr_users using(uid) order by mid desc limit 100");
			while(rs.next()) {
				MessageInfo msg = new MessageInfo();
				msg.setId(rs.getInt("mid"));
				msg.setMsgTime(rs.getTimestamp("mtime"));
				String msgcontent = rs.getString("mcontent");
				UserInfo msguser = new UserInfo();
				msguser.setUsername(rs.getString("uname"));
				msguser.setUserId(rs.getString("uid"));
				msguser.setUserpriv(UserInfo.Privilege.valueOf(rs.getInt("upriv")));
				msg.setUserInfo(msguser);
				while(rs.getBoolean("mhasprev")) {
					rs.next();
					msgcontent = rs.getString("mcontent") + msgcontent;
				}
				msg.setMsgContent(msgcontent);
				msgs.addFirst(msg);
			}
		} finally {
			if(sta != null)sta.close();
			if(rs != null)rs.close();
			conn.close();
		}
		return msgs.toArray(new MessageInfo[0]);
	}

	/**
	 * 获得某指定时间后的新消息.
	 * @param specifiedtime 指定时间
	 * @return 消息列表
	 * @throws SQLException 当SQL异常发生时
	 */
	public MessageInfo[] getMessageListSince(java.util.Date specifiedtime) throws SQLException {
		PreparedStatement sta = null;
		LinkedList<MessageInfo> msgs = new LinkedList<MessageInfo>();
		
		ResultSet rs = null;
		
		try {
			sta = conn.prepareStatement("select mid, uid, mtime, mcontent, uname, upriv, mhasprev from cr_msgs left outer join cr_users using(uid) where mtime > ? order by mid desc");
			sta.setTimestamp(1, new Timestamp(specifiedtime.getTime()));
			rs = sta.executeQuery();
			while(rs.next()) {
				MessageInfo msg = new MessageInfo();
				msg.setId(rs.getInt("mid"));
				msg.setMsgTime(rs.getTimestamp("mtime"));
				String msgcontent = rs.getString("mcontent");
				UserInfo msguser = new UserInfo();
				msguser.setUsername(rs.getString("uname"));
				msguser.setUserId(rs.getString("uid"));
				msguser.setUserpriv(UserInfo.Privilege.valueOf(rs.getInt("upriv")));
				msg.setUserInfo(msguser);
				while(rs.getBoolean("mhasprev")) {
					rs.next();
					msgcontent = rs.getString("mcontent") + msgcontent;
				}
				msg.setMsgContent(msgcontent);
				msgs.addFirst(msg);
			}
		} finally {
			if(sta != null)sta.close();
			if(rs != null)rs.close();
			conn.close();
		}
		return msgs.toArray(new MessageInfo[0]);
	}
	
	/**
	 * 发送新消息.
	 * @param msg 要发送的消息
	 * @return 是否成功
	 * @throws SQLException SQL异常
	 * @throws UnsupportedEncodingException 无法转换为UTF-8编码
	 */
	static final int msg_length = 90;
	public boolean sendNewMessage(MessageInfo msg, boolean locked) throws SQLException, UnsupportedEncodingException {
		PreparedStatement sta = null;
		int rslt = 0;
		try{
			String msgcontent = msg.getMsgContent();
			if(msgcontent.length() < 99) {
				sta = conn.prepareStatement("insert into cr_msgs(uid, mtime, mcontent, locked) values (?,?,?,?); ");
				sta.setString(1, msg.getUserInfo().getUserId());
				sta.setTimestamp(2, new Timestamp(msg.getMsgTime().getTime()));
				sta.setString(3, msgcontent);
				sta.setBoolean(4, locked);
				rslt = sta.executeUpdate();
			} else {
				// TODO 过长的内容
				Statement s = null;
				boolean transflag = false;
				try {
					s = conn.createStatement();
					transflag = s.execute("start transaction;");

					sta = conn.prepareStatement("insert into cr_msgs(uid, mtime, mcontent, locked, mhasprev) values (?,?,?,?,?); ");
					sta.setString(1, msg.getUserInfo().getUserId());
					sta.setTimestamp(2, new Timestamp(msg.getMsgTime().getTime()));
					sta.setInt(4, (msg.getUserInfo().getUserpriv() == null ? 0: 1));
					sta.setBoolean(5, false);
					int maxlength = msgcontent.length() - msg_length;
					sta.setString(3, msgcontent.substring(0, 90));
					rslt += sta.executeUpdate();
					sta.setBoolean(5, true);
					int i = msg_length;
					for(; i < maxlength; i += msg_length) {
						sta.setString(3, msgcontent.substring(i, i + msg_length));
						rslt += sta.executeUpdate();
					}
					sta.setString(3, msgcontent.substring(i));
					rslt += sta.executeUpdate();
				
					transflag = !s.execute("commit;");
				} catch(Exception e) {
					Log.getInstance().logWarning("插入长消息时出错！用户：" + msg.getUserInfo().getUserId());
					Log.getInstance().logWarning("消息：" + msg.getMsgContent());
					return false;
				} finally {
					if(s != null)s.close();
					if(transflag) {
						s = conn.createStatement();
						s.execute("rollback;");
						s.close();
					}
				}
			}
		} finally {
			if(sta != null)sta.close();
			conn.close();
		}
		if(rslt > 0)return true;
		return false;
	}
	
	/**
	 * 授予某用户指定权限.
	 * @param user 用户
	 * @param privilege 权限
	 * @return 成功返回true，失败返回false
	 * @version TODO 此函数未开放
	 * @deprecated
	 */
	public boolean grantPrivilege(UserInfo user, UserInfo.Privilege privilege) {
		return false;
	}
	
	/**
	 * 撤回某用户指定权限.
	 * @param user 用户
	 * @param privilege 权限
	 * @return 成功返回true，失败返回false
	 * @version TODO 此函数未开放
	 * @deprecated
	 */
	public boolean revokePrivilege(UserInfo user, UserInfo.Privilege privilege) {
		return false;
	}
	
	/**
	 * 向数据库中插入头像.
	 * @param user 用户
	 * @param inputstream 文件的输入流
	 * @return 成功返回true，否则false
	 * @throws SQLException 发生SQL异常
	 */
	public boolean setAvatar(UserInfo user, InputStream inputstream) throws SQLException {
		PreparedStatement sta1 = null;
		try {
			sta1 = conn.prepareStatement("delete from cr_avatars where uid = ?");
			sta1.setString(1, user.getUserId());
			sta1.executeUpdate();
		} finally {
			if(sta1 != null)sta1.close();
		}
		
		PreparedStatement sta = null;
		try {
			sta = conn.prepareStatement("insert into cr_avatars(uid, uavatar) values(?, ?) ");
			sta.setString(1, user.getUserId());
			sta.setBlob(2, inputstream);
			return sta.executeUpdate()>0;
		} catch(SQLException e) {
			Log.getInstance().logError("当更新头像时发生SQL异常，用户" + user).logErrorOnException(e);
			return false;
		} finally {
			if(sta != null)sta.close();
			conn.close();
		}
	}
	
	/**
	 * 从数据库获得头像.
	 * @param userId 用户ID
	 * @return 用户头像的二进制数据，失败返回null
	 * @throws SQLException
	 */
	public byte[] getAvatar(String userId) throws SQLException {
		PreparedStatement sta = null;
		ResultSet rs = null;
		try {
			sta = conn.prepareStatement("select uavatar from cr_avatars where uid=? ");
			sta.setString(1, userId);
			rs = sta.executeQuery();
			if(rs.next()) {
				return rs.getBytes(1);
			}
			//Log.getInstance().logWarning("未找到头像，用户id：" + userId);
			return null;
		} catch(SQLException e) {
			Log.getInstance().logError("当获取头像时发生SQL异常，用户id：" + userId).logErrorOnException(e);
			return null;
		} finally {
			if(sta != null)sta.close();
			if(rs != null)rs.close();
			conn.close();
		}
	}
}
