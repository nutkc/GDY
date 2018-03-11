package cn.nutkc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import cn.nutkc.domain.Player;
import cn.nutkc.domain.Poker;
import cn.nutkc.domain.Produce;
import cn.nutkc.domain.Table;

public class TableControl {
	private int player_num;
	private int turn;
	private int count = 1;
	private String lastProducePlayer="0";
	private Table table;
	private Produce lastProduce;
	private HashMap<String, Boolean> player_ready;
	private HashMap<Integer, String> player_order;
	private HashMap<String, Integer> scoreRecord;
	
	//���췽��
	public TableControl(int num) {
		/**
		 * ����һ���µ�Table ͬʱ������������
		 */
		table = new Table();
		player_num = num;
		player_ready = new HashMap<String, Boolean>();
		player_order = new HashMap<Integer, String>();
		lastProduce = null;
	}
	
	
	//����
	public void reset() {
		updateScore();
		updatePlayer_ready();
		updatePlayer_order();
		changeBanker();
		lastProducePlayer = "0";
		lastProduce = null;
		table.reset();
	}
	
	//������
	public boolean addPlayer(String id) {
		HashMap<String, Player> players = table.getPlayers();
		if(players.size() == 0) {
			//��ǰ���������Ϊ0 ���½�����������Ϊׯ��
			if(table.addPlayer(true, id)) {
				player_ready.put(id, false);
				player_order.put(count++, id);
				return true;
			}
		} else if(players.size() < player_num) {
			//��ǰ���������δ������ �������Ѿ���һ�����
			//���½�����������Ϊ�м�
			if(table.addPlayer(false, id)) {
				player_ready.put(id, false);
				player_order.put(count++, id);
				return true;
			}
		}
		//�����Ѵ����� �ܾ�����
		return false;
	}
	
	//�뿪����
	public boolean leave(String id) {
		//�ж�����Ƿ��ڵ�ǰ����
		if(this.isInRoom(id)) {
			//�жϵ�ǰ����Ƿ�Ϊׯ�ң���ׯ�������ׯ��
			if(getPlayer(id).isBanker()) {
				changeBanker();
			}
			table.getPlayers().remove(id);
			//����palyer_ready��player_order���ű�
			resetPlayer_order();
			updatePlayer_ready();
			return true;
		} else {
			throw new RuntimeException();
		}
	}
	
	//׼��
	public boolean ready(String id) {
		/**
		 * ����true ��ʾ���������׼�����ɹ�����
		 * ����false ���ʾδȫ��׼��
		 */
		if(!isInRoom(id)) throw new RuntimeException();
		player_ready.put(id, true);
		if(isallReady()) {
			begging();
			//�˴��ж��Ƿ��һ�ο��� ��һ�ο�����Ҫ��scoreRecord�����ʼ��
			if(scoreRecord == null) {
				scoreInit();
			} else {
				scoreUpdate();
			}
			return true;
		}
		return false;
	}
	
	//ȡ��׼��
	public boolean unReady(String id) {
		if(!isInRoom(id)) return false;
		player_ready.put(id, false);
		return true;
	}
	
	//����
	public boolean produce(String nickname, String id, LinkedList<Poker> pokers) {

		if(lastProducePlayer.equals(id)) {
			//�˴���ʾ�Ѿ�ת��һ�����˳��� ��Ҫ���Rule�����Record
			table.clearRecord();
		}
		Player p = getPlayer(id);
		//�������б��Ƿ�Ϊ�գ���JSP���ѹ��� ��Ӧ���ִ����
		if(pokers == null || pokers.size() == 0) return false;
		String id_turn = player_order.get(turn);
		if(id.equals(id_turn)) {
			if(table.produce(p, pokers)) {
				//���Ƴɹ�
				LinkedList<Poker> ps = getHandPokers().get(id);
				if(ps.size() == 0) {
					//�����������Ƿ����
					table.setGameover(true);
					p.setWinner(true);
				}
				/*
				 * 1.�ֵ���һλ��ҳ���
				 * 2.���³�����Ҽ�¼
				 */
				this.lastProduce = new Produce(nickname, pokers);
				nextTurn();
				lastProducePlayer = id;
				return true;
			}
		} 
		return false;
	}

	//����
	public boolean pass(String id) {
		String order_id = player_order.get(turn);
		if(order_id.equals(id) && !lastProducePlayer.equals(id)) {
			//�˴���ʾ�ֵ�����ҳ���
			nextTurn();
			if(lastProducePlayer.equals(player_order.get(turn))) {
				addAPoker(lastProducePlayer);
			}
			return true;
		} 
		return false;
	}
	
	//��ȡָ���������
	public HashMap<String, LinkedList<Poker>> getHandPokers() {
		HashMap<String, LinkedList<Poker>> handPokers = new HashMap<String, LinkedList<Poker>>();
		HashMap<String, Player> players = table.getPlayers();
		Set<String> players_id = players.keySet();
		for(String id : players_id) {
			LinkedList<Poker> p = players.get(id).getPokers();
			handPokers.put(id, p);
		}
		return handPokers;
	}	

	//��ȡָ��ID����Ҷ���
	public Player getPlayer(String id) {
		HashMap<String, Player> players = table.getPlayers();
		return players.get(id);
	}

	//��ָ��������һ����
	public LinkedList<Poker> addAPoker(String id) {
		if(table.addAPoker(id)) {
			return getPlayer(id).getPokers();
		}
		throw new RuntimeException();
	}

	//����Ƿ���Ϸ����״̬
	public boolean isOver() {
		return table.isGameover();
	}

	//����Ƿ����ڽ�����Ϸ
	public boolean isPlaying() {
		return table.isPlaying();
	}

	//��ȡ�ϴγ�����Ҽ�����
	public Produce getLastProduce() {
		return lastProduce;
	}

	//��ȡ������¼
	public HashMap<String, Integer> getScoreRecord() {
		return scoreRecord;
	}

	public int getTurn() {
		return turn;
	}
	
	//����player_order��
		//ֻ����˳�򵫲�У������
	private void updatePlayer_order() {
		String temp = player_order.get(1);
		for(int i=1; i<player_order.size(); i++) { 
			player_order.put(i, player_order.get(i+1));
		}
		player_order.put(player_order.size(), temp);
	}
	
	//����playerorder
		//���´�Table��players��ȡplayerlist��������˳��
	private void resetPlayer_order() {
		int pos = -1;
		for(Entry<Integer, String> e : player_order.entrySet()) {
			if(table.getPlayers().get(e.getValue()) == null) {
				//���ϲ����ڴ����
				pos = e.getKey();
				break;
			}
		}
		if(pos != -1) {
			removePlayerOrder(pos);
			resetPlayer_order();
		}
	}
	
	//��player_order�ƶ�λ�õ�playerȥ�� ���ǰ�� ɾ�����һ��
	private void removePlayerOrder(int p) {
		for(int i=p; i<player_order.size(); i++) {
			player_order.put(i, player_order.get(i+1));
		}
		player_order.remove(player_order.size());
	}
	
	//����player_ready�� �Ұ�����ֵ��Ϊfalse
	private void updatePlayer_ready() {
		Set<String> ps = table.getPlayers().keySet();
		player_ready.clear();
		for(String p : ps) {
			player_ready.put(p, false);
		}
	}
	//���·�����¼
	private void scoreUpdate() {
		//����ʱ�����������Ƿ���scoreRecord��   ����׼player_ready��
		Set<String> psInRoom = player_ready.keySet();
		for(String p : psInRoom) {
			if(scoreRecord.get(p) == null) {
				scoreRecord.put(p, 0);
			}
		}
	}
	//���·��� 
	private void updateScore() {
		HashMap<String,Integer> player_score = table.getScore();
		Set<String> ps = player_score.keySet();
		for(String p : ps) {
			Integer sr = this.scoreRecord.get(p);
			Integer s = player_score.get(p);
			this.scoreRecord.put(p, s + sr);
		}
	}
	//��ʼ��������¼
	private void scoreInit() {
		scoreRecord = new HashMap<String, Integer>();
		Set<String> ps = player_ready.keySet();
		for(String p : ps) {
			scoreRecord.put(p, 0);
		}
	}
	//���ĳ����Ƿ�������
	private boolean isInRoom(String id) {
		HashMap<String, Player> players = table.getPlayers();
		Player p = players.get(id);
		if(p == null) return false;
		return true;
	}
	//���ĳ������
	private void nextTurn() {
		if(turn == table.getPlayers().size()) {
			turn = 1;
		} else {
			turn++;
		}
	}
	//�����������Ƿ�׼��
	private boolean isallReady() {
		if(player_ready.size() == 0) return false;
		for(Entry<String,Boolean> e : player_ready.entrySet()) {
			if (!e.getValue()) return false;
		}
		return true;		
	}
	//���� �������ű�		
	private boolean begging() {
		turn = 1;
		table.opening();
		return true;
	}
	
	//����ׯ��
	private void changeBanker() {
		String banker_id = table.getBanker();
		table.getPlayers().get(banker_id).setBanker(false);
		int o = getOrder(banker_id);
		o = nextOrder(o);
		banker_id = player_order.get(o);
		table.setBnaker(banker_id);
	}
	
	
	
	//��ȡָ��order����һ��order
	private int nextOrder(int o) {
		if(o == table.getPlayers().size()) {
			return 1;
		} else {
			return o + 1;
		}
	}
	//��ȡָ����ҵ�order
	private int getOrder(String p_id) {
		for(Entry<Integer, String> e : player_order.entrySet()) {
			if(e.getValue().equals(p_id)) return e.getKey();
		}
		return -1;
	}
	
	public HashMap<Integer, String> getPlayer_order() {
		return player_order;
	}
}
