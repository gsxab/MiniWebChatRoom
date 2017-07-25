package logincheck;

import java.util.LinkedList;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import log.Log;
import model.UserInfo;

/**
 * Application Lifecycle Listener implementation class LoginCount
 *
 */
@WebListener
public class LoginCount implements HttpSessionListener, HttpSessionAttributeListener {

    /**
     * Default constructor. 
     */
    public LoginCount() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent arg0)  { 
    	HttpSession session = arg0.getSession();
    	session.setMaxInactiveInterval(60);
    	ServletContext application = session.getServletContext();
    	
    	//ArrayList<UserInfo> usersonline = (ArrayList<UserInfo>)(application.getAttribute("usersonline"));
    	//if(usersonline == null) {
    	//	usersonline = new ArrayList<UserInfo>();
    	//}
		//usersonline.add(LoginCheck.check(session).getUser());
		//System.out.print(usersonline);
    	Integer countobj = (Integer)application.getAttribute("usersonlinecount");
    	int count;
    	if(countobj == null) {
    		count = 0;
    	} else {
    		count = countobj.intValue();
    	}
    	count++;
    	application.setAttribute("usersonlinecount", count);
    	Log.getInstance().log("登录设备数增加至：" + count);
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent arg0)  { 
    	HttpSession session = arg0.getSession();
		ServletContext application = session.getServletContext();

     	LinkedList<UserInfo> usersonline = (LinkedList<UserInfo>)(application.getAttribute("usersonline"));
     	Integer countobj = (Integer)application.getAttribute("usersonlinecount");
     	int count = 0;
     	if(countobj != null) {
     		count = (countobj).intValue();
     		if(count>0)count--;
     		Log.getInstance().log("登录设备数减少至：" + count);
        }
     	application.setAttribute("usersonlinecount", count);
    	if(usersonline == null) {
     	} else {
     		LoginCheck login = LoginCheck.check(session);
     		if(login != null) {
     			UserInfo user = login.getUser();
     			Log.getInstance().log("退出登录 " + user);
     			usersonline.remove(user);
     			Log.getInstance().log("当前在线用户：" + usersonline.toString());
     		}
     	}
    }

	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		HttpSession session = arg0.getSession();
		LoginCheck login = (LoginCheck) session.getAttribute("logininfo");

		ServletContext application = session.getServletContext();
    	LinkedList<UserInfo> usersonline = (LinkedList<UserInfo>)(application.getAttribute("usersonline"));
    	if(usersonline == null) {
    		usersonline = new LinkedList<UserInfo>();
    	}
		usersonline.add(login.user);
		application.setAttribute("usersonline", usersonline);
		Log.getInstance().log("当前在线用户：" + usersonline.toString());
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		try{
			arg0.getSession().invalidate();
		} catch (Exception e) {}
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
