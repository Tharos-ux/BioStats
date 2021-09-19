package Main;

public class Duet {
	public String s;
	public int i;
	
	public Duet(String substring) {
		s=substring;
		i=substring.length();
	}
	
	public int valueOf() {
		return i;
	}
	
	public String stringOf() {
		return s;
	}
}
