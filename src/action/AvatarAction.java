package action;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import dbo.DBAccess;
import log.Log;
import logincheck.LoginCheck;

/**
 * Servlet implementation class AvatarAction
 */
@WebServlet({ "/AvatarAction", "/avataraction" })
public class AvatarAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AvatarAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoginCheck login = LoginCheck.check(request.getSession());
		if(login == null)return;
		boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
		if(!isMultiPart)return;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		File repo = new File("F:\\chatroom\\", login.getUser().getUserId());
		if(!repo.exists()) { repo.mkdir(); }
		factory.setRepository(repo);
		ServletFileUpload sfu = new ServletFileUpload(factory);
		try {
//			Iterator<FileItem> it = sfu.parseRequest(request).iterator();
//			while(it.hasNext()) {
//				FileItem item = it.next();
//				String filename = item.getName();
//				filename = filename.substring(filename.lastIndexOf(File.separatorChar) + 1);
//				System.out.print(filename);
//				item.write(new File(repo, filename));
//			}
			List<FileItem> fis = sfu.parseRequest(request);
			for(FileItem fi : fis) {
				if(fi.getSize() == 0L)continue;
				if(fi.getSize() > 60*1024L) {
					response.setCharacterEncoding("UTF-8");
					response.setContentType("text/html;charset=utf-8;pageencoding=utf-8");
					response.getWriter().println("头<del>像</del>过大请换一个重试！");
					continue;
				}
				DBAccess da = new DBAccess();
				if(da.setAvatar(login.getUser(), fi.getInputStream())) {
					Log.getInstance().log("更换头像成功 " + login.getUser());
					response.getWriter().println("更换头像成功");
				}
				break;
			}
		} catch (Exception e) {
			Log.getInstance().logError("发送头像时异常" + login.getUser().toString()).logErrorOnException(e);
			e.printStackTrace();
			response.getWriter().println("更换头像失败");
		}
	}
}
