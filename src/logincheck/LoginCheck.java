package logincheck;

import javax.servlet.http.HttpSession;

import model.UserInfo;

public class LoginCheck {
	UserInfo user;
	boolean light;
	
	public LoginCheck() {
		// TODO Auto-generated constructor stub
	}
	public UserInfo getUser() {
		return user;
	};
	public boolean getLight() {
		return light;
	}

	static public LoginCheck check(HttpSession session) {
		LoginCheck login = ((LoginCheck)session.getAttribute("logininfo"));
		//UserInfo user = new UserInfo();
		//user.setUserId((String)session.getAttribute("loginid"));
		//user.setUsername((String)session.getAttribute("loginname"));
		//user.setUserpriv((UserInfo.Privilege)session.getAttribute("loginpriv"));
		//login.user = user;
		//login.light = ((Boolean)session.getAttribute("light")).booleanValue();
		return login;
	}
	
	static public void prepareToCheck(HttpSession session, UserInfo user, boolean light) {
		LoginCheck login = new LoginCheck();
		login.user = user;
		login.light = light;
		session.setAttribute("logininfo", login);
	}
}
