package cn.nutkc.domain;

public class Poker {
	private PokerType type;
	private int value;
	@Override
	public String toString() {
		return "Poker [type=" + type + ", value=" + value + "]";
	}
	public String getType() {
		return type.name();
	}

	public void setType(String type) {
		PokerType t = PokerType.valueOf(type);
		if(t == null) throw new RuntimeException("错误Poker类型");
		this.type = t;
	}
	public void setType(PokerType type) {
		this.type = type;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Poker)) return false;
		Poker o = (Poker) obj;
		if(o.getValue() == this.value 
				&& o.getType() == this.getType()) return true;
		return false;
	}
}

enum PokerType {
	PLUM,HEART,BLOCK,SPADE,JOKER;
	/**
	 * 对应如下
	 * PLUM  --> 梅花
	 * HEART --> 红桃
	 * BLOCK --> 方块
	 * SPADE --> 黑桃
	 * JOKER --> 小丑
	 */
	
}
