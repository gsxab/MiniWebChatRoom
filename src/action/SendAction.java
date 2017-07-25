package action;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbo.DBAccess;
import log.Log;
import logincheck.LoginCheck;
import model.MessageInfo;
import model.UserInfo;

/**
 * Servlet implementation class SendAction
 */
@WebServlet({ "/SendAction", "/sendaction", "/sendaction.html" })
public class SendAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8;pageencoding=utf-8");
		response.getWriter().append("请输入正确内容访问！");
		response.addHeader("refresh", "2;url=index.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8;pageencoding=utf-8");
		
		LoginCheck login = LoginCheck.check(request.getSession());
		
		if(login == null) {
			response.sendRedirect("send.jsp");
		}
		
		Date time = new Date();
		String msgcontent = request.getParameter("msgcontent");
		//msgcontent = new String(msgcontent.getBytes("ISO-8859-1"), "UTF-8");
		
		msgcontent = msgcontent.replace("&", "&amp;");
		msgcontent = msgcontent.replace("<", "&lt;").replace(">", "&gt;").replace(" ", "&nbsp;");
		msgcontent = msgcontent.replace("\r\n", "<br/>").replace("\n", "<br/>").replace("\r", "<br/>");
		msgcontent = msgcontent.replaceAll("#\\[([gsb])\\]\\((1?\\d)\\)", "<img src='img/em/$1/$2.gif' alt='表情' class='meme'/>");
		
		MessageInfo msg = new MessageInfo();
		msg.setMsgContent(msgcontent);
		msg.setMsgTime(time);
		UserInfo user = login.getUser();
		user.setUserpriv(null);
		if(request.getParameter("locked") != null)user.setUserpriv(UserInfo.Privilege.ADMIN);
		msg.setUserInfo(user);
		
		String redirectTarget = (login.getLight() ? "send.jsp?light=" : "send.jsp");
		
		try {
			DBAccess da = new DBAccess();
			da.sendNewMessage(msg);
			Log.getInstance().log(msg.toString());
			response.sendRedirect(redirectTarget);
		} catch (SQLException e) {
			// send error
			Log.getInstance().logError("发送消息时SQL错误：用户" + user.getUserId() + "，消息：" + msg.toString());
			response.getWriter().print("未知错误无法发送消息，可能是消息过长。");
			response.addHeader("refresh", "3;url=" + redirectTarget);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			Log.getInstance().logError("发送消息时编码错误：用户" + user.getUserId() + "，消息：" + msg.toString());
			response.getWriter().print("未知错误无法发送消息，可能是无法编码。");
			response.addHeader("refresh", "3;url=" + redirectTarget);
			return;
		}
		
		//response.sendRedirect("send.jsp");
	}

}
