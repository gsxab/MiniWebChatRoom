package model;

/**
 * 用户信息类
 * @author gong
 */
public class UserInfo {
	/**
	 * 特权的枚举
	 * @author gong
	 */
	public enum Privilege{
		NORMAL(0),
		ADMIN(-1), 
		VIP(1),
		SVIP(2);
		
		private int value;
		private Privilege(int i){
			value = i;
		}
		public int toInt() {
			return value;
		}
		public static Privilege valueOf(int i) {
			switch(i) {
			case 0:
				return NORMAL;
			case 1:
				return VIP;
			case 2:
				return SVIP;
			case -1:
				return ADMIN;
			default:
				return NORMAL;
			}
		}
	};
	
	/**
	 * 默认构造函数
	 */
	public UserInfo() {}
	
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 用户名
	 */
	private String username;	
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 用户身份
	 */
	private Privilege userpriv;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Privilege getUserpriv() {
		return userpriv;
	}
	public void setUserpriv(Privilege userpriv) {
		this.userpriv = userpriv;
	}
	
	public String toString() {
		return "用户id：" + userId + " 用户名：" + username;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(this.getClass() != obj.getClass())return false;
		UserInfo other = (UserInfo) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		return true;
	}
}
