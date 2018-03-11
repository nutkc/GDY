package cn.nutkc.domain;

public class ScoreRecord {
	private String nickname;
	private int score;
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public ScoreRecord(String nickname, int score) {
		super();
		this.nickname = nickname;
		this.score = score;
	}
}
