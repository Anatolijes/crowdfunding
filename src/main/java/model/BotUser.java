package model;

import java.util.ArrayList;
import java.util.List;

public class BotUser {

	private String name;
	private String address;
	private String phoneNumber;
	private boolean isRegCompleted;
	private List<String> context = new ArrayList<>();


	public String getPhoneNumber() { return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<String> getContext() {
		return context;
	}

	public void setContext(List<String> context) {
		this.context = context;
	}

	public boolean isRegCompleated() {
		return isRegCompleted;
	}

	public void setRegCompleated(boolean regComplited) {
		isRegCompleted = regComplited;
	}
}
