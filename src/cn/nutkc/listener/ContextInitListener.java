package cn.nutkc.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cn.nutkc.service.TableControl;
import cn.nutkc.servlet.Update;


public class ContextInitListener implements ServletContextListener {

    public ContextInitListener() {}

    public void contextDestroyed(ServletContextEvent e) {}

    public void contextInitialized(ServletContextEvent e) {
        ServletContext sc = e.getServletContext();
        if(sc.getAttribute("rooms") == null) {
        	Map<String,TableControl> rooms = new HashMap<String, TableControl>();
            sc.setAttribute("rooms", rooms);
        }
        if(sc.getAttribute("user_room") == null) {
        	Map<String,String> user_room = new HashMap<String, String>();
        	sc.setAttribute("user_room", user_room);
        }
        if(sc.getAttribute("room_update") == null) {
        	Map<String, Update> room_update = new HashMap<String, Update>();
        	sc.setAttribute("room_update", room_update);
        }
        if(sc.getAttribute("id_nickname") == null) {
        	Map<String, String> id_nickname = new HashMap<String, String>();
        	sc.setAttribute("id_nickname", id_nickname);
        }
    }

}
