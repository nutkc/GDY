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
	
	//构造方法
	public TableControl(int num) {
		/**
		 * 定义一个新的Table 同时设置人数上限
		 */
		table = new Table();
		player_num = num;
		player_ready = new HashMap<String, Boolean>();
		player_order = new HashMap<Integer, String>();
		lastProduce = null;
	}
	
	
	//重置
	public void reset() {
		updateScore();
		updatePlayer_ready();
		updatePlayer_order();
		changeBanker();
		lastProducePlayer = "0";
		lastProduce = null;
		table.reset();
	}
	
	//添加玩家
	public boolean addPlayer(String id) {
		HashMap<String, Player> players = table.getPlayers();
		if(players.size() == 0) {
			//当前桌玩家人数为0 将新进入的玩家设置为庄家
			if(table.addPlayer(true, id)) {
				player_ready.put(id, false);
				player_order.put(count++, id);
				return true;
			}
		} else if(players.size() < player_num) {
			//当前桌玩家人数未达上限 且至少已经有一个玩家
			//将新进入的玩家设置为闲家
			if(table.addPlayer(false, id)) {
				player_ready.put(id, false);
				player_order.put(count++, id);
				return true;
			}
		}
		//人数已达上限 拒绝加入
		return false;
	}
	
	//离开桌子
	public boolean leave(String id) {
		//判断玩家是否在当前桌内
		if(this.isInRoom(id)) {
			//判断当前玩家是否为庄家，是庄家则更换庄家
			if(getPlayer(id).isBanker()) {
				changeBanker();
			}
			table.getPlayers().remove(id);
			//更新palyer_ready和player_order两张表
			resetPlayer_order();
			updatePlayer_ready();
			return true;
		} else {
			throw new RuntimeException();
		}
	}
	
	//准备
	public boolean ready(String id) {
		/**
		 * 返回true 表示所有玩家已准备并成功开局
		 * 返回false 则表示未全部准备
		 */
		if(!isInRoom(id)) throw new RuntimeException();
		player_ready.put(id, true);
		if(isallReady()) {
			begging();
			//此处判断是否第一次开局 第一次开局需要对scoreRecord对象初始化
			if(scoreRecord == null) {
				scoreInit();
			} else {
				scoreUpdate();
			}
			return true;
		}
		return false;
	}
	
	//取消准备
	public boolean unReady(String id) {
		if(!isInRoom(id)) return false;
		player_ready.put(id, false);
		return true;
	}
	
	//出牌
	public boolean produce(String nickname, String id, LinkedList<Poker> pokers) {

		if(lastProducePlayer.equals(id)) {
			//此处表示已经转过一轮无人出牌 需要清除Rule对象的Record
			table.clearRecord();
		}
		Player p = getPlayer(id);
		//检查出牌列表是否为空，在JSP中已过滤 不应出现此情况
		if(pokers == null || pokers.size() == 0) return false;
		String id_turn = player_order.get(turn);
		if(id.equals(id_turn)) {
			if(table.produce(p, pokers)) {
				//出牌成功
				LinkedList<Poker> ps = getHandPokers().get(id);
				if(ps.size() == 0) {
					//检查玩家手牌是否出完
					table.setGameover(true);
					p.setWinner(true);
				}
				/*
				 * 1.轮到下一位玩家出牌
				 * 2.更新出牌玩家记录
				 */
				this.lastProduce = new Produce(nickname, pokers);
				nextTurn();
				lastProducePlayer = id;
				return true;
			}
		} 
		return false;
	}

	//不出
	public boolean pass(String id) {
		String order_id = player_order.get(turn);
		if(order_id.equals(id) && !lastProducePlayer.equals(id)) {
			//此处表示轮到该玩家出牌
			nextTurn();
			if(lastProducePlayer.equals(player_order.get(turn))) {
				addAPoker(lastProducePlayer);
			}
			return true;
		} 
		return false;
	}
	
	//获取指定玩家手牌
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

	//获取指定ID的玩家对象
	public Player getPlayer(String id) {
		HashMap<String, Player> players = table.getPlayers();
		return players.get(id);
	}

	//向指定玩家添加一张牌
	public LinkedList<Poker> addAPoker(String id) {
		if(table.addAPoker(id)) {
			return getPlayer(id).getPokers();
		}
		throw new RuntimeException();
	}

	//检查是否游戏结束状态
	public boolean isOver() {
		return table.isGameover();
	}

	//检查是否正在进行游戏
	public boolean isPlaying() {
		return table.isPlaying();
	}

	//获取上次出牌玩家及牌面
	public Produce getLastProduce() {
		return lastProduce;
	}

	//获取分数记录
	public HashMap<String, Integer> getScoreRecord() {
		return scoreRecord;
	}

	public int getTurn() {
		return turn;
	}
	
	//更新player_order表
		//只更新顺序但不校验人数
	private void updatePlayer_order() {
		String temp = player_order.get(1);
		for(int i=1; i<player_order.size(); i++) { 
			player_order.put(i, player_order.get(i+1));
		}
		player_order.put(player_order.size(), temp);
	}
	
	//重置playerorder
		//重新从Table的players获取playerlist但不更改顺序
	private void resetPlayer_order() {
		int pos = -1;
		for(Entry<Integer, String> e : player_order.entrySet()) {
			if(table.getPlayers().get(e.getValue()) == null) {
				//桌上不存在此玩家
				pos = e.getKey();
				break;
			}
		}
		if(pos != -1) {
			removePlayerOrder(pos);
			resetPlayer_order();
		}
	}
	
	//把player_order制定位置的player去除 后边前提 删除最后一个
	private void removePlayerOrder(int p) {
		for(int i=p; i<player_order.size(); i++) {
			player_order.put(i, player_order.get(i+1));
		}
		player_order.remove(player_order.size());
	}
	
	//更新player_ready表 且把所有值设为false
	private void updatePlayer_ready() {
		Set<String> ps = table.getPlayers().keySet();
		player_ready.clear();
		for(String p : ps) {
			player_ready.put(p, false);
		}
	}
	//更新分数记录
	private void scoreUpdate() {
		//检查此时房间所有人是否都在scoreRecord中   检查标准player_ready表
		Set<String> psInRoom = player_ready.keySet();
		for(String p : psInRoom) {
			if(scoreRecord.get(p) == null) {
				scoreRecord.put(p, 0);
			}
		}
	}
	//更新分数 
	private void updateScore() {
		HashMap<String,Integer> player_score = table.getScore();
		Set<String> ps = player_score.keySet();
		for(String p : ps) {
			Integer sr = this.scoreRecord.get(p);
			Integer s = player_score.get(p);
			this.scoreRecord.put(p, s + sr);
		}
	}
	//初始化分数记录
	private void scoreInit() {
		scoreRecord = new HashMap<String, Integer>();
		Set<String> ps = player_ready.keySet();
		for(String p : ps) {
			scoreRecord.put(p, 0);
		}
	}
	//检查某玩家是否在桌内
	private boolean isInRoom(String id) {
		HashMap<String, Player> players = table.getPlayers();
		Player p = players.get(id);
		if(p == null) return false;
		return true;
	}
	//更改出牌玩家
	private void nextTurn() {
		if(turn == table.getPlayers().size()) {
			turn = 1;
		} else {
			turn++;
		}
	}
	//检查所有玩家是否准备
	private boolean isallReady() {
		if(player_ready.size() == 0) return false;
		for(Entry<String,Boolean> e : player_ready.entrySet()) {
			if (!e.getValue()) return false;
		}
		return true;		
	}
	//开局 更新两张表		
	private boolean begging() {
		turn = 1;
		table.opening();
		return true;
	}
	
	//更换庄家
	private void changeBanker() {
		String banker_id = table.getBanker();
		table.getPlayers().get(banker_id).setBanker(false);
		int o = getOrder(banker_id);
		o = nextOrder(o);
		banker_id = player_order.get(o);
		table.setBnaker(banker_id);
	}
	
	
	
	//获取指定order的下一个order
	private int nextOrder(int o) {
		if(o == table.getPlayers().size()) {
			return 1;
		} else {
			return o + 1;
		}
	}
	//获取指定玩家的order
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
