package net.m3aak.parentapp.Beans;

public class ChatMessage {
	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public String side;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String message;
	public String sender;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String status;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String time;
	/*public ChatMessage(String side, String message,String sender) {
		super();
		this.side = side;
		this.message = message;
		this.sender=sender;
	}*/
}