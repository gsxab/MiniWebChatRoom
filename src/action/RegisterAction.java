package action;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

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
 * Servlet implementation class RegisterAction
 */
@WebServlet({ "/RegisterAction", "/registeraction", "/registeraction.html" })
public class RegisterAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	response.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8;pageencoding=utf-8");
		response.getWriter().append("请输入正确内容登录！");
		response.addHeader("refresh", "2;url=login.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8;pageencoding=utf-8");
		PrintWriter out = response.getWriter();
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String password2 = request.getParameter("password2");
		
		if(username == null || password == null || password2 == null || !password2.equals(password)) {
			//direct access
			out.println("请使用正确方法注册！");
			response.addHeader("refresh", "2;url=login.html");
			return;
		}

		//username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
		//password = new String(password.getBytes("ISO-8859-1"), "UTF-8");
		Log.getInstance().log("用户名：" + username + "注册");
		
		UserInfo user = new UserInfo();
		user.setUsername(username);
		user.setPassword(password);
		
		try {
			DBAccess dba = new DBAccess();
			user = dba.validateUser(user, true);
			if(user == null) {
				//register failure
				Log.getInstance().log("注册用户名" + username + "时发生重复！");
				out.println("用户名已存在，请使用不同的用户名注册");
				response.addHeader("refresh", "2;url=login.html");
				return;
			}
		} catch (SQLException e) {
			//error when register
			Log.getInstance().logWarning("注册用户名" + username + "时出现SQL问题！");
			e.printStackTrace();
			return;
		}
		
		//register success
		user.setPassword(null);
		String light = request.getParameter("light");
		if(LoginCheck.check(request.getSession()) != null) {
			request.getSession().invalidate();
		}
		LoginCheck.prepareToCheck(request.getSession(), user, (light != null));
		
		//redirect
		Log.getInstance().log("用户名：" + username + "注册成功，id=" + user.getUserId());
		if(light == null) {
			response.sendRedirect("frames.html");
		} else {
			response.sendRedirect("frames.html?light=");
		}
	}
}
