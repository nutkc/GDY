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
		 * ��Servlet�������û�������Ϸ����
		 */
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		//1.��ȡ�û���sessionid
		String session_id = request.getRequestedSessionId();
		//2.��ȡ����ķ����
		String roomname = request.getParameter("roomname");
		String nickname = request.getParameter("nickname");
		if(nickname == null || nickname.trim().isEmpty()) {
			request.setAttribute("msg", "�ǳƲ���Ϊ�գ��������ǳ�");
			request.getRequestDispatcher("/select.jsp").forward(request, response);
			return;
		}
		//3.�ж�application��rooms�Ƿ����������� �������򴴽�
		//ͬʱ�Ѹ��û�����Ϊׯ�Ҽ��뵽table��
		Map<String,TableControl> rooms = (Map<String, TableControl>) request.getServletContext().getAttribute("rooms");
		Map<String,String> user_room = (Map<String, String>) request.getServletContext().getAttribute("user_room");
		Map<String,Update> room_update = (Map<String,Update>) request.getServletContext().getAttribute("room_update");
		Map<String,String> id_nickname = (Map<String, String>) request.getServletContext().getAttribute("id_nickname");
		/*
		 * ���ѡ������·���ʱ��Ҫ������з������Ƿ��������� ����ͨ��user_room����
		 * 1.���Ѿ����� �����Ȱ���Ҷ����ԭ�з���ɾ��
		 * 2.�������ڣ���ֱ��ִ��һ�´��� ͬʱע�ᵽuser_room����
		 */
		String inThisRoom;
		if((inThisRoom = user_room.get(session_id)) != null) {
			//����Ѿ�������һ������ ��Ҫ�����˳�����
			//��ȡ�����TableControl
			
			//�ж��������Ľ���ķ����Ƿ���ԭ���ķ���
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
		
		//�жϷ������Ƿ���ڣ��������򴴽�һ�����浽application��
		TableControl table = rooms.get(roomname);
		if(table == null) {
			table = new TableControl(3); //���÷�������
			Update update = new Update(table);
			room_update.put(roomname, update);
			rooms.put(roomname, table);
		}
		//�򷿼������SessionId��ΪΨһ��ʶ��Player
		if(table.addPlayer(session_id)) {
			//��ӳɹ������û�ע�ᵽapplication�е�user_room��	
			//Ȼ��ת����table.jsp
			user_room.put(session_id, roomname);
			id_nickname.put(session_id, nickname);
			request.setAttribute("nickname", nickname);
			request.getRequestDispatcher("/table.jsp").forward(request, response);
		} else {
			//���ʧ�ܣ�req�����msg������Ϣ ת����select.jsp
			request.setAttribute("msg", "�Բ��𣬷�����������ʧ��");
			request.getRequestDispatcher("/select.jsp").forward(request, response);
		}
	}

}
