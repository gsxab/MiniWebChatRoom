package action;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
@Deprecated //操作已经移至send.jsp
public class SendAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public SendAction() {super();}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8;pageencoding=utf-8");
		response.getWriter().append("请输入正确内容访问！");
		response.addHeader("refresh", "2;url=index.html");
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
}
