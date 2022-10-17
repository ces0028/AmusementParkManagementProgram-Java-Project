package amusementParkManagementProgram;

import java.util.Objects;

public class Staff {
	private String staffName;
	private int staffNumber;
	private String password;

	public Staff(String staffName, int staffNumber, String password) {
		this.staffName = staffName;
		this.staffNumber = staffNumber;
		this.password = password;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public int getStaffNumber() {
		return staffNumber;
	}

	public void setStaffNumber(int staffNumber) {
		this.staffNumber = staffNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Staff))
			return false;
		Staff login = (Staff) obj;
		return  Objects.equals(this.staffNumber, login.staffNumber);
	}

	@Override
	public int hashCode() {
		return this.staffNumber;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}