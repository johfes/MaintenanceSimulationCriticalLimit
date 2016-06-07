package biz.fesenmeyer;

public class Quality {
	private int value;

	public Quality(int value){
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Qualität [Wert=" + value + "]";
	}
}


