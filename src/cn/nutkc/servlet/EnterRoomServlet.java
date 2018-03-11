package cn.nutkc.servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import cn.nutkc.domain.Poker;
import cn.nutkc.domain.Produce;
import cn.nutkc.service.TableControl;

public class EnterRoomServlet extends HttpServlet {
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 此Servlet用来让用户进入游戏房间
		 */
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		//1.获取用户的sessionid
		String session_id = request.getRequestedSessionId();
		//2.获取请求的房间号
		String roomname = request.getParameter("roomname");
		String nickname = request.getParameter("nickname");
		if(nickname == null || nickname.trim().isEmpty()) {
			request.setAttribute("msg", "昵称不能为空，请输入昵称");
			request.getRequestDispatcher("/select.jsp").forward(request, response);
			return;
		}
		//3.判断application的rooms是否存在这个房间 不存在则创建
		//同时把该用户设置为庄家加入到table中
		Map<String,TableControl> rooms = (Map<String, TableControl>) request.getServletContext().getAttribute("rooms");
		Map<String,String> user_room = (Map<String, String>) request.getServletContext().getAttribute("user_room");
		Map<String,Update> room_update = (Map<String,Update>) request.getServletContext().getAttribute("room_update");
		Map<String,String> id_nickname = (Map<String, String>) request.getServletContext().getAttribute("id_nickname");
		/*
		 * 玩家选择进入新房间时需要检查已有房间中是否包含此玩家 可以通过user_room表检查
		 * 1.若已经存在 则需先把玩家对象从原有房间删除
		 * 2.若不存在，则直接执行一下代码 同时注册到user_room表中
		 */
		String inThisRoom;
		if((inThisRoom = user_room.get(session_id)) != null) {
			//玩家已经存在于一个房间 需要进行退场操作
			//获取房间的TableControl
			
			//判断玩家请求的进入的房间是否还是原来的房间
			if(!inThisRoom.equals(roomname)) {
				TableControl tc = rooms.get(inThisRoom);
				tc.leave(session_id);
			} else {
				id_nickname.put(session_id, nickname);
				request.setAttribute("isFlush", "true");
				TableControl tc = rooms.get(inThisRoom);
				if(tc.isPlaying()) request.setAttribute("isPlaying", "true");
				request.setAttribute("nickname", nickname);
				request.getRequestDispatcher("/table.jsp").forward(request, response);
				return;
			}
			
		}
		
		//判断房间名是否存在，不存在则创建一个并存到application中
		TableControl table = rooms.get(roomname);
		if(table == null) {
			table = new TableControl(3); //设置房间人数
			Update update = new Update(table);
			room_update.put(roomname, update);
			rooms.put(roomname, table);
		}
		//向房间添加以SessionId作为唯一标识的Player
		if(table.addPlayer(session_id)) {
			//添加成功，把用户注册到application中的user_room中	
			//然后转发到table.jsp
			user_room.put(session_id, roomname);
			id_nickname.put(session_id, nickname);
			request.setAttribute("nickname", nickname);
			request.getRequestDispatcher("/table.jsp").forward(request, response);
		} else {
			//添加失败，req中添加msg错误信息 转发给select.jsp
			request.setAttribute("msg", "对不起，房间已满进入失败");
			request.getRequestDispatcher("/select.jsp").forward(request, response);
		}
	}

}
