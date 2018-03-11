package cn.nutkc.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


public class GameRule {
	private Record lastRecord;
	
	
	public void clear() {
		lastRecord = null;
	}
	/**
	 * 将给定的poker序列与记录比较 
	 * 符合规则则为true
	 */
	public boolean compare(LinkedList<Poker> pokers) {
		Record record = isType(pokers);
		if(record == null) return false;
		if(lastRecord != null) {
			if(record.getType() == ProduceType.BOMB) {
				if(lastRecord.getType() == ProduceType.BOMB) {
					if(record.getValue() != lastRecord.getValue() + 1) {
						return false;
					}	
				} else {
					lastRecord = record;
					return true;
				}
			}
			if(record.getType() != lastRecord.getType() || 
					record.getLength() != lastRecord.getLength() ||
					(record.getValue() != lastRecord.getValue() +1 && record.getValue() != 13) ||
					record.getValue() == lastRecord.getValue()) return false;
		}
		lastRecord = record;
		return true;
	}
	
	public boolean checkBomb(LinkedList<Poker> pokers) {
		if(isBomb(pokers) != 0) return true;
		return false;
	}
	
	
	/**
	 * 判定给定的poker序列是否符合出牌规则并返回类型 否则返回null
	 */
	private Record isType(LinkedList<Poker> pokers) {
		int size = pokers.size();
		if(pokers == null || size == 0) return null;
		
		Record record = new Record();
		int res;
		record.setLength(size);
		//判断是否为单张
		if(size == 1) {
			if((res=isSingle(pokers)) != 0) {
				record.setType(ProduceType.SINGLE);
				record.setValue(res);
				return record;
			}
		}
		
		//判断是否为对子
		if(size == 2) {
			if((res=isDouble(pokers)) != 0) {
				record.setType(ProduceType.DOUBLE);
				record.setValue(res);
				return record;
			}
		}
		
		//判断是否为顺子或者炸
		if(size >= 3) {
			if((res=isBomb(pokers)) != 0) {
				record.setType(ProduceType.BOMB);
				record.setValue(res);
				return record;
			}
			if(isCombo(pokers) != 0) {
				record.setType(ProduceType.COMBO);
				record.setValue(res);
				return record;
			}
		}
		
		return null;
	}
	
	/**
	 * 判断传入的扑克列表是否为单牌
	 */
	private int isSingle(LinkedList<Poker> pokers) {
		if(pokers.size() != 1) return 0;
		Poker poker = pokers.getFirst();
		if(PokerType.valueOf(poker.getType()) == PokerType.JOKER) return 0;
		return poker.getValue();
	}
	
	/**
	 * 判断传入的扑克列表是否为单牌
	 */
	private int isDouble(LinkedList<Poker> pokers) {
		if(pokers.size() != 2) return 0;
		int kingNum = 0;
		int result = 0;
		for(Poker poker : pokers) {
			if(PokerType.valueOf(poker.getType()) == PokerType.JOKER) {
				kingNum++;
			} else {
				result = poker.getValue();
			}
		}
		if(kingNum == 1) return result;
		int value1 = pokers.getFirst().getValue();
		int value2 = pokers.getLast().getValue();
		if(value1 != value2) return 0;
		return result;
	}
	
	/**
	 * 判断传入的扑克列表是否为炸
	 */
	private int isBomb(LinkedList<Poker> pokers) {
		if(pokers.size() != 3) return 0;
		int jokerNum = 0;
		for(Poker poker : pokers) {
			if(PokerType.valueOf(poker.getType()) == PokerType.JOKER) {
				jokerNum ++;
			}
		}
		
		int temp = -1;
		switch(jokerNum) {
		case 0:
			for(Poker poker : pokers) {
				if(temp == -1) {
					temp = poker.getValue();
				}
				if(poker.getValue() != temp) return 0;
			}
			return temp;
		case 1:
			for(Poker poker : pokers) {
				if(PokerType.valueOf(poker.getType()) != PokerType.JOKER) {
					if(temp == -1) {
						temp = poker.getValue();
					}
					if(poker.getValue() != temp) return 0;
				}
			}
			return temp;
		case 2:
			for(Poker poker : pokers) {
				if(PokerType.valueOf(poker.getType()) != PokerType.JOKER) {
					temp = poker.getValue();
				}
			}
			return temp;
		}
		return 0;
	}

	/**
	 * 判断传入的扑克列表是否为顺子
	 */
	private int isCombo(LinkedList<Poker> pokers) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		/**
		 * 判断扑克列表长度是否符合要求
		 */
		if(pokers.size() < 3) return 0;
		
		/**
		 * 定义两个ArrayList存放普通牌和王牌的值
		 * 对两个List进行排序
		 */
		ArrayList<Integer> ordinaryValues = new ArrayList<Integer>();
		ArrayList<Integer> JokerValues = new ArrayList<Integer>();
		
		for(Poker poker : pokers) {
			if(PokerType.valueOf(poker.getType()) == PokerType.JOKER) {
				JokerValues.add(poker.getValue());
				continue;
			}
			ordinaryValues.add(poker.getValue());
		}
		
		Collections.sort(ordinaryValues);
		Collections.sort(JokerValues);
		
		/**
		 * 检查ordinaryValues是否连续
		 * 定义一个temp变量值为-1
		 * 遍历ordinaryValues判断temp是否为-1
		 * 是则初始化，否则对++temp进行比较
		 */
		int temp = -1;
		for(Integer i : ordinaryValues) {
			/**
			 * temp初始化
			 */
			if(temp == -1) {
				temp = i;
				result.add(temp);
				continue;
			}
			/**
			 * 1.判断i是否为下一个连续的值
			 * 2.否则判断i与应该存在的连续值的差 是否大于king牌个数
			 * 3.否则循环减少king的个数并增加temp值
			 */
			if(i == ++temp) {	//判断连续   无论是否连续此时的temp已经被+1了
				result.add(temp);
				continue;
			} else if(i - temp > JokerValues.size()) {	//king牌个数判断
				return 0;
			} else {
				for(int j=0; j<i-temp; j++) {
					JokerValues.remove(0);
					temp += 1;
					result.add(temp);
				}
				continue;
			}
		}
		result.addAll(JokerValues);
		if(result.get(result.size()-1) > 13) return result.get(0) * (-1);
		return result.get(0);
	}

}


enum ProduceType {
	COMBO,DOUBLE,SINGLE,BOMB;
}

