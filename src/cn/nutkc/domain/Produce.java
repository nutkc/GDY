package cn.nutkc.domain;

import java.util.List;

import net.sf.json.JSONObject;

public class Produce {
	String nickname;
	List<Poker> pokers;
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public List<Poker> getPokers() {
		return pokers;
	}
	public void setPokers(List<Poker> pokers) {
		this.pokers = pokers;
	}
	public Produce(String nickname, List<Poker> pokers) {
		super();
		this.nickname = nickname;
		this.pokers = pokers;
	}
	@Override
	public String toString() {
		JSONObject o = JSONObject.fromObject(this);
		return o.toString();
	}
}
