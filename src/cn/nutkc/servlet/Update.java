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
	
	//���췽��
	public Update(TableControl tc) {
		this.tc = tc;
	}
	
	//��ȡ�ϴγ��Ƶ�JSON
	public String getLatestProduce() {
		if(tc.getLastProduce() != null) {
			return tc.getLastProduce().toString();
		}
		return null;		
	}

	//��ȡָ���������JSON
	public String update(String id) throws IOException {
		HashMap<String, LinkedList<Poker>> hp = tc.getHandPokers();
		LinkedList<Poker> pokers = hp.get(id);
		return toJsonStr(pokers);
	}	
	
	//���winner����б�
	public void clearPlayer_checked() {
		player_checked = null;
	}
	//����winner����б�
	private void resetPlayer_checked() {
		Set<String> ps = tc.getHandPokers().keySet();
		if(player_checked == null || player_checked.size() != ps.size()) {
			player_checked = new HashMap<String, Boolean>();
		} 
		for(String p : ps) {
			player_checked.put(p, false);
		}
	}
	
	//���ָ��ID�Ƿ�ΪӮ��
	public boolean checkWinner(String id) {
		return tc.getPlayer(id).isWinner();
	}
	
	//�����Ϸ�Ƿ����״̬
	public boolean over() {
		return tc.isOver();
	}

	//����ָ������Ѽ��
	public void checked(String id) {
		if(player_checked == null) {
			resetPlayer_checked();
		}
		player_checked.put(id, true);
	}
	
	//����Ƿ��������ȷ����ʤ��
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
