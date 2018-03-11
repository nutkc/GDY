package cn.nutkc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import cn.nutkc.domain.Player;
import cn.nutkc.domain.Poker;
import cn.nutkc.domain.Table;

public class TServlet extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		HashMap<String, Table> rooms = (HashMap<String, Table>) request.getServletContext().getAttribute("rooms");
		String roomName = ((HashMap<String, String>)request.getServletContext().getAttribute("user_room")).get(request.getRequestedSessionId());
		Table table = rooms.get(roomName);
		table.opening();
		Player player = table.getPlayers().get(request.getSession().getId());
		LinkedList<Poker> pokers = player.getPokers();
		String str = toJsonStr(pokers);
		response.setHeader("unready", "false");
		out.println(str);
		out.flush();
		out.close();
	}
	private String toJsonStr(List<Poker> pokers) {
		JSONArray list = JSONArray.fromObject(pokers);
		return list.toString();
	}
	

}
