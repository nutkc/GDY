package cn.nutkc.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.nutkc.domain.Poker;
import cn.nutkc.service.TableControl;

public class Update {
	private TableControl tc;
	private HashMap<String, Boolean> player_checked;
	
	//构造方法
	public Update(TableControl tc) {
		this.tc = tc;
	}
	
	//获取上次出牌的JSON
	public String getLatestProduce() {
		if(tc.getLastProduce() != null) {
			return tc.getLastProduce().toString();
		}
		return null;		
	}

	//获取指定玩家手牌JSON
	public String update(String id) throws IOException {
		HashMap<String, LinkedList<Poker>> hp = tc.getHandPokers();
		LinkedList<Poker> pokers = hp.get(id);
		return toJsonStr(pokers);
	}	
	
	//清除winner检查列表
	public void clearPlayer_checked() {
		player_checked = null;
	}
	//重置winner检查列表
	private void resetPlayer_checked() {
		Set<String> ps = tc.getHandPokers().keySet();
		if(player_checked == null || player_checked.size() != ps.size()) {
			player_checked = new HashMap<String, Boolean>();
		} 
		for(String p : ps) {
			player_checked.put(p, false);
		}
	}
	
	//检查指定ID是否为赢家
	public boolean checkWinner(String id) {
		return tc.getPlayer(id).isWinner();
	}
	
	//检查游戏是否结束状态
	public boolean over() {
		return tc.isOver();
	}

	//设置指定玩家已检查
	public void checked(String id) {
		if(player_checked == null) {
			resetPlayer_checked();
		}
		player_checked.put(id, true);
	}
	
	//检查是否所有玩家确定过胜负
	public boolean isAllChecked() {
		Set<String> ps = player_checked.keySet();
		for(String p : ps) {
			if(player_checked.get(p) == false) return false;
		}
		return true;
	}
	
	public TableControl getTableControl() {
		return tc;
	}
	public void setTableControl(TableControl tc) {
		this.tc = tc;
	}
	private String toJsonStr(List<Poker> pokers) {
		JSONArray list = JSONArray.fromObject(pokers);
		return list.toString();
	}


}
