package es.upm.cbgp.semphi.objects;

public class Notification {

	private int type;
	private String message;
	
	public Notification(int t, String m) {
		this.type = t;
		this.message = m;
	}

	public int getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

}
