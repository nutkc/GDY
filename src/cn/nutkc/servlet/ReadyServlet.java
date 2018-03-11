package cn.nutkc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.nutkc.service.TableControl;

@SuppressWarnings("serial")
public class ReadyServlet extends HttpServlet {

	/**
	 * 
	 * ��Servlet���ڸ����׼��
	 * 1.��ȡreq��getready���� 
	 * 2.���Ϊtrue �����TableContol��ready��������ȴ�
	 * 3.���Ϊfalse ����unready����ȡ���ȴ�
	 * 
	 * 
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		String isready = request.getParameter("getready");
		/**
		 * ����ҵ�sessionid��ȡTableControl���� 
		 */
		TableControl tc = getTableControl(request);
		if(isready.equals("true")) {
			if(tc.isPlaying()) {
				response.setHeader("success_ready", "false");
			} else {
				response.setHeader("success_ready", "true");
			}
			tc.ready(request.getRequestedSessionId());
		} else {
			if(tc.isPlaying()) {
				response.setHeader("success_unready", "false");
			} else {
				response.setHeader("success_unready", "true");
				tc.unReady(request.getRequestedSessionId());
			}
		}
		out.flush();
		out.close();
	}
	

	
	@SuppressWarnings("unchecked")
	private TableControl getTableControl(HttpServletRequest req) {
		String id = req.getRequestedSessionId();
		Map<String,TableControl> rooms = (Map<String, TableControl>) req.getServletContext().getAttribute("rooms");
		Map<String,String> user_room = (Map<String, String>) req.getServletContext().getAttribute("user_room");
		String room = user_room.get(id);
		return rooms.get(room);
	}
	@SuppressWarnings("unchecked")
	private Update getUpdate(HttpServletRequest req) {
		String id = req.getRequestedSessionId();
		Map<String,Update> room_update = (Map<String,Update>) req.getServletContext().getAttribute("room_update");
		Map<String,String> user_room = (Map<String, String>) req.getServletContext().getAttribute("user_room");
		String room = user_room.get(id);
		return room_update.get(room);
	}
}
