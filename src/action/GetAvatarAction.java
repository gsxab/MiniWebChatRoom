package action;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbo.DBAccess;
import log.Log;
import logincheck.LoginCheck;

/**
 * Servlet implementation class GetAvatarAction
 */
@WebServlet({ "/GetAvatarAction", "/getavataraction", "/avatar" })
public class GetAvatarAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAvatarAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("image/jpeg");
		String uid = request.getParameter("uid");
		if(uid == null) {
			LoginCheck login = (LoginCheck) request.getSession().getAttribute("logininfo");
			if(login == null) { 
				response.sendRedirect("img/avatar/default.jpg");
				return;
			}
			uid = login.getUser().getUserId();
		}
		try {
			DBAccess da= new DBAccess();
			OutputStream os = response.getOutputStream();
			byte[] bytes = da.getAvatar(uid);
			if(bytes != null) {
				os.write(bytes);
			} else {
				response.sendRedirect("img/avatar/default.jpg");
			}
		} catch (Exception e) {
			Log.getInstance().logError("查询头像时发生异常" + uid).logErrorOnException(e);
			response.sendRedirect("img/avatar/default.jpg");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
