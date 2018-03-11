package cn.nutkc.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BoardImgFilter implements Filter {
    public BoardImgFilter() {}

	public void destroy() {}

	public void init(FilterConfig fConfig) throws ServletException {}
	

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		Map<String,String> user_room = (Map<String, String>) req.getServletContext().getAttribute("user_room");
		if(user_room.get(req.getRequestedSessionId()) != null) {
			chain.doFilter(request, response);
		} else {
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.sendRedirect(req.getContextPath() + "/select.jsp");
		}
	}

}
