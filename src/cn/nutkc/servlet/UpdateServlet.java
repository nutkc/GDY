package cn.nutkc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import cn.nutkc.service.TableControl;

public class UpdateServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		String id = request.getRequestedSessionId();
		Map<String,String> user_room = (Map<String, String>) request.getServletContext().getAttribute("user_room");
		Map<String,Update> room_update = (Map<String,Update>) request.getServletContext().getAttribute("room_update");
		String room = user_room.get(id);
		Update update = room_update.get(room);
		String reStr = update.update(id);
		String header = update.getLatestProduce();
		if(header != null) {
			header = URLEncoder.encode(header, "utf-8");
			response.setHeader("latestproduce", header);
		}
		TableControl tc = update.getTableControl();
		//全部准备后 把玩家清单发给浏览器
		if(update.getTableControl().isPlaying()) {
			String pl = getNicknameList(request,response,tc);
			pl = URLEncoder.encode(pl, "utf-8");
			response.setHeader("playerlist", pl);
			response.addIntHeader("turn", tc.getTurn());
		}
		if(update.over()) {
			if(update.checkWinner(id)) {
				response.setHeader("over_winner", "true");
			} else {
				response.setHeader("over_winner", "false");
			}
			update.checked(id);
			System.out.println("id = " + id + "          checked");
			System.out.println("isAllchecked = " + update.isAllChecked());
			if(update.isAllChecked()) {
				tc = update.getTableControl();
				tc.reset();
				update.clearPlayer_checked();
				/*
				//全部准备后 把玩家清单发给浏览器
				String pl = getNicknameList(request,response,tc);
				pl = URLEncoder.encode(pl, "utf-8");
				response.setHeader("playerlist", pl);
				*/
			}
		}
		out.println(reStr);
		out.flush();
		out.close();
	}

	
	//获取昵称列表
	@SuppressWarnings("unchecked")
	public String getNicknameList(HttpServletRequest request, HttpServletResponse response, TableControl tc) {
		ServletContext sc = request.getServletContext();
		String id = request.getRequestedSessionId();
		int pos = -1;
		HashMap<String, String> order_nickname = new HashMap<String, String>();
		Map<String, String> id_nickname = (Map<String, String>) sc.getAttribute("id_nickname");
		HashMap<Integer, String> player_order = tc.getPlayer_order();
		for(int i=1; i<=player_order.size(); i++) {
			String p_id = player_order.get(i);
			order_nickname.put("p" + Integer.toString(i), id_nickname.get(p_id));
			if(p_id.equals(id)) {
				pos = i;
			}
		}
		JSONObject o = JSONObject.fromObject(order_nickname);
		if(o != null) {
			String result = o.toString().replaceFirst("}", ",\"pos\":" + pos + ",\"size\":" + player_order.size() + "}");
			return result;
		} 
		return null;
	}
}
