package action;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbo.DBAccess;
import log.Log;
import logincheck.LoginCheck;
import model.UserInfo;

/**
 * Servlet implementation class LoginAction
 */
@WebServlet({ "/LoginAction", "/loginaction", "/loginaction.html" })
public class LoginAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginAction() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String str = request.getParameter("check");
		if(str == null) {
			str = request.getParameter("logout");
			if(str == null) {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/html;charset=utf-8;pageencoding=utf-8");
				response.getWriter().append("请输入正确内容登录！");
				response.addHeader("refresh", "2;url=index.html");
			} else {
				request.getSession().invalidate();
			}
		} else {
			try{
				DBAccess da = new DBAccess();
				if(da.checkUser(str)) {
					response.getWriter().print(1);
					return;
				} else {
					//合法性检查
					response.getWriter().print(0);
				}
			} catch(Exception e) {
				response.getWriter().print(3); //内部数据库错误
				return;
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8;pageEncoding=utf-8");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if(username == null || password == null) {
			//direct access
			Log.getInstance().log("来自" + request.getRemoteAddr() + "的直接访问。");
			out.println("请使用正确方法登录！");
			response.addHeader("refresh", "2;url=index.html");
			return;
		}
		
		//username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
		//password = new String(password.getBytes("ISO-8859-1"), "UTF-8");
		Log.getInstance().log("用户名：" + username + "登录");
		
		UserInfo user = new UserInfo();
		user.setUsername(username);
		user.setPassword(password);
		
		try {
			DBAccess dba = new DBAccess();
			user = dba.validateUser(user, false);
			if(user == null) {
				//login failure
				out.println("用户名或密码错误，请检查用户名及密码");
				Log.getInstance().log("用户名：" + username + "登录验证失败，使用密码：" + password);
				response.addHeader("refresh", "2;url=index.html");
				return;
			}
		} catch (SQLException e) {
			//error when login
			Log.getInstance().logWarning("检查用户名" + username + "时抛出SQL异常！");
			e.printStackTrace();
			return;
		}
		
		//login success
		user.setPassword(null);
		String light = request.getParameter("light");
		if(LoginCheck.check(request.getSession()) != null) {
			request.getSession().invalidate();
		}
		LoginCheck.prepareToCheck(request.getSession(), user, (light != null));
		Log.getInstance().log("登录成功 " + user);
		
		//redirect
		if(light == null)
			response.sendRedirect("frames.html");
		else 
			response.sendRedirect("frames.html?light=");
	}
}
