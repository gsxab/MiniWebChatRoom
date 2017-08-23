package logincheck;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 关于是否已登录的过滤器.
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter({"*.jsp", "/img/em/*"})
public class LoginFilter implements Filter {

    /**
     * Default constructor. 
     */
    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		HttpServletRequest request1 = (HttpServletRequest)request;
		HttpServletResponse response1 = (HttpServletResponse)response;

		String uri = request1.getRequestURI();
		if(!uri.endsWith("/register.html") 
				&& !uri.endsWith("/login.html") 
				&& !uri.endsWith("/img/logo.png")
				&& !uri.endsWith("/loginaction.html")
				&& !uri.endsWith("/registeraction.html")
				&& !uri.endsWith("/css/chatroom-black.css")
				&& !uri.endsWith("/msgs.jsp")
				&& !uri.endsWith("/newmsgs.jsp")){
			if(LoginCheck.check(request1.getSession()) == null) {
				response1.sendRedirect("login.html");
				return;
			}
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
