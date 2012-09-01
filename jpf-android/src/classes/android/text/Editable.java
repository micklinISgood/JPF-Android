package android.text;

public class Editable implements CharSequence{
	CharSequence ch ="";
	
	public Editable(CharSequence ch){
		this.ch = ch;
	}

	@Override
	public String toString() {
		return ch.toString();
	}

	@Override
	public int length() {
		return ch.length();
	}

	@Override
	public char charAt(int index) {
		return ch.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return ch.subSequence(start, end);
	}
	
	
	

}
