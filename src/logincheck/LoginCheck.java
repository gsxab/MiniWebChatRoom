package logincheck;

import javax.servlet.http.HttpSession;

import model.UserInfo;

/**
 * 登录信息封装类，也提供用于该信息的提取和检测的函数.
 * @author gong
 */
public class LoginCheck {
	/**
	 * 已登录的用户信息
	 */
	UserInfo user;	
	/**
	 * 是否是轻量级页面
	 */
	boolean light;
	
	/**
	 * 构造函数，空内容 
	 */
	public LoginCheck() {
		
	}
	/**
	 * 获得用户信息的getter.
	 * @return
	 */
	public UserInfo getUser() {
		return user;
	};
	/**
	 * 获得轻量级的 getter
	 * @return
	 */
	public boolean getLight() {
		return light;
	}

	/**
	 * 从一个session中提取登录信息
	 * @param session
	 * @return 登录信息
	 */
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
	
	/**
	 * 向一个session中写入登录信息
	 * @param session
	 * @param user 用户信息
	 * @param light 轻量情况
	 */
	static public void prepareToCheck(HttpSession session, UserInfo user, boolean light) {
		LoginCheck login = new LoginCheck();
		login.user = user;
		login.light = light;
		session.setAttribute("logininfo", login);
	}
}
