package cn.nutkc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.nutkc.domain.Player;
import cn.nutkc.domain.Poker;
import cn.nutkc.domain.ScoreRecord;
import cn.nutkc.service.TableControl;

@SuppressWarnings("serial")
public class GammingServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String methodType = request.getParameter("method");
		if(methodType.equals("produce")) {
			produce(request,response);
		} else if(methodType.equals("pass")) {
			pass(request,response);
		} else if(methodType.equals("getscore")) {
			getScore(request,response);
		} else if(methodType.equals("leave")) {
			leave(request,response);
		}
	}
	
	
	private void leave(HttpServletRequest request, HttpServletResponse response) throws IOException {
		TableControl tc = getTableControl(request);
		Player p = tc.getPlayer(request.getRequestedSessionId());
		if(p == null) {
			response.setHeader("leave-msg", "false");
			return;
		}
		tc.leave(request.getRequestedSessionId());
		response.setHeader("leave-msg", "true");		
	}


	@SuppressWarnings("unchecked")
	private void getScore(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		ServletContext sc = request.getServletContext();
		String id = request.getRequestedSessionId();
		HashMap<String, TableControl> rooms = (HashMap<String, TableControl>) sc.getAttribute("rooms");
		Map<String,String> id_nickname = (Map<String, String>) request.getServletContext().getAttribute("id_nickname");
		TableControl tc = rooms.get(((HashMap<String, String>)sc.getAttribute("user_room")).get(id));
		HashMap<String, Integer> scoreRecord = tc.getScoreRecord();
		List<ScoreRecord> scores = new ArrayList<ScoreRecord>();
		for(Entry<String, Integer> e : scoreRecord.entrySet()) {
			String e_id = e.getKey();
			String nickname = id_nickname.get(e_id);
			ScoreRecord sr = new ScoreRecord(nickname, e.getValue());
			scores.add(sr);
		}
		JSONArray ReStr = JSONArray.fromObject(scores);
		out.println(ReStr);
		out.flush();
		out.close();
	}


	@SuppressWarnings("unchecked")
	private void pass(HttpServletRequest request, HttpServletResponse response) {
		//查找到请求对应的房间的TableControl对象
		ServletContext sc = request.getServletContext();
		String id = request.getRequestedSessionId();
		HashMap<String, TableControl> rooms = (HashMap<String, TableControl>) sc.getAttribute("rooms");
		TableControl tc = rooms.get(((HashMap<String, String>) sc.getAttribute("user_room")).get(id));
		if(tc.pass(id)) {
			response.setHeader("pass_success", "true");
		} else {
			response.setHeader("pass_success", "false");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void produce(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//获取出牌列表、SessionID、输出流以及ServletContext
		PrintWriter out = response.getWriter();
		ServletContext sc = request.getServletContext();
		String str = request.getParameter("obj");
		String id = request.getRequestedSessionId();
		List<Poker> pokers = toBeans(str);
		
		//查找到请求对应的房间的TableControl对象
		HashMap<String, TableControl> rooms = (HashMap<String, TableControl>) sc.getAttribute("rooms");
		TableControl tc = rooms.get(((HashMap<String, String>) sc.getAttribute("user_room")).get(id));
		//查询昵称列表
		Map<String, String> id_nickname = (Map<String, String>) sc.getAttribute("id_nickname");
		//通过tc的produce方法出牌 
		boolean produce_success = tc.produce(id_nickname.get(id), id, (LinkedList<Poker>) pokers);
		
		//检查出牌是否成功
		if(!produce_success) {
			response.setHeader("error", "1");
		}
		//把玩家手牌相应给浏览器
		String reStr = this.toJsonStr(tc.getHandPokers().get(id));
		out.println(reStr);
		out.flush();
		out.close();		
	}
	
	private String toJsonStr(List<Poker> pokers) {
		JSONArray list = JSONArray.fromObject(pokers);
		return list.toString();
	}
	
	private List<Poker> toBeans(String str) {
		List<Poker> pokers = new LinkedList<Poker>();
		JSONArray objs = JSONArray.fromObject(str);
		for(Object o : objs) {
			if(o instanceof JSONObject) {
				JSONObject jo = (JSONObject) o;
				Poker p = (Poker) JSONObject.toBean(jo,Poker.class);
				pokers.add(p);
			}
		}
		return pokers;
	}
	@SuppressWarnings("unchecked")
	private TableControl getTableControl(HttpServletRequest req) {
		String id = req.getRequestedSessionId();
		Map<String,TableControl> rooms = (Map<String, TableControl>) req.getServletContext().getAttribute("rooms");
		Map<String,String> user_room = (Map<String, String>) req.getServletContext().getAttribute("user_room");
		String room = user_room.get(id);
		return rooms.get(room);
	}
}
