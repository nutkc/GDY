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
	 * ��������poker�������¼�Ƚ� 
	 * ���Ϲ�����Ϊtrue
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
	 * �ж�������poker�����Ƿ���ϳ��ƹ��򲢷������� ���򷵻�null
	 */
	private Record isType(LinkedList<Poker> pokers) {
		int size = pokers.size();
		if(pokers == null || size == 0) return null;
		
		Record record = new Record();
		int res;
		record.setLength(size);
		//�ж��Ƿ�Ϊ����
		if(size == 1) {
			if((res=isSingle(pokers)) != 0) {
				record.setType(ProduceType.SINGLE);
				record.setValue(res);
				return record;
			}
		}
		
		//�ж��Ƿ�Ϊ����
		if(size == 2) {
			if((res=isDouble(pokers)) != 0) {
				record.setType(ProduceType.DOUBLE);
				record.setValue(res);
				return record;
			}
		}
		
		//�ж��Ƿ�Ϊ˳�ӻ���ը
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
	 * �жϴ�����˿��б��Ƿ�Ϊ����
	 */
	private int isSingle(LinkedList<Poker> pokers) {
		if(pokers.size() != 1) return 0;
		Poker poker = pokers.getFirst();
		if(PokerType.valueOf(poker.getType()) == PokerType.JOKER) return 0;
		return poker.getValue();
	}
	
	/**
	 * �жϴ�����˿��б��Ƿ�Ϊ����
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
	 * �жϴ�����˿��б��Ƿ�Ϊը
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
	 * �жϴ�����˿��б��Ƿ�Ϊ˳��
	 */
	private int isCombo(LinkedList<Poker> pokers) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		/**
		 * �ж��˿��б����Ƿ����Ҫ��
		 */
		if(pokers.size() < 3) return 0;
		
		/**
		 * ��������ArrayList�����ͨ�ƺ����Ƶ�ֵ
		 * ������List��������
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
		 * ���ordinaryValues�Ƿ�����
		 * ����һ��temp����ֵΪ-1
		 * ����ordinaryValues�ж�temp�Ƿ�Ϊ-1
		 * �����ʼ���������++temp���бȽ�
		 */
		int temp = -1;
		for(Integer i : ordinaryValues) {
			/**
			 * temp��ʼ��
			 */
			if(temp == -1) {
				temp = i;
				result.add(temp);
				continue;
			}
			/**
			 * 1.�ж�i�Ƿ�Ϊ��һ��������ֵ
			 * 2.�����ж�i��Ӧ�ô��ڵ�����ֵ�Ĳ� �Ƿ����king�Ƹ���
			 * 3.����ѭ������king�ĸ���������tempֵ
			 */
			if(i == ++temp) {	//�ж�����   �����Ƿ�������ʱ��temp�Ѿ���+1��
				result.add(temp);
				continue;
			} else if(i - temp > JokerValues.size()) {	//king�Ƹ����ж�
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

