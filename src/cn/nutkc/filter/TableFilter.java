package cn.nutkc.filter;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class TableFilter implements Filter {

    public TableFilter() {}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HashMap<String, String> user_room = (HashMap<String, String>)request.getServletContext().getAttribute("user_room");
		HttpServletRequest req = (HttpServletRequest) request;
		String id = req.getRequestedSessionId();
		String nickname = user_room.get(id);
		if(nickname != null) {
			chain.doFilter(request, response);
		} else {
			req.setAttribute("msg", "�����뷿�������ǳ��ٴγ��Խ�����Ϸ");
			req.getRequestDispatcher("/select.jsp").forward(req, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {}

}
