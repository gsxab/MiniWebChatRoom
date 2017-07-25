package model;

public class UserInfo {
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
	
	public UserInfo() {}
	
	private String userId;
	private String username;
	private String password;
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
