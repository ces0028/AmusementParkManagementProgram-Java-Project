package amusementParkManagementProgram;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Member implements Comparable<Member>, Serializable {
	private String memberNumber;
	private int memberIntNumber;
	private String memberName;
	private String birthDate;
	private String telNumber;
	private String email;
	private String membershipType;
	private String membershipPurchaseDate;
	private String membershipExpirationDate;
	
	public Member(String memberName, String birthDate, String telNumber, String email, String membershipType, String membershipPurchaseDate) {
		this(null, memberName, birthDate, telNumber, email, membershipType, membershipPurchaseDate, null);
	}

	public Member(String memberNumber, String memberName, String birthDate, String telNumber, String email, String membershipType, String membershipPurchaseDate, String membershipExpirationDate) {
		this.memberNumber = memberNumber;
		this.memberName = memberName;
		this.birthDate = birthDate;
		this.telNumber = telNumber;
		this.email = email;
		this.membershipType = membershipType;
		this.membershipPurchaseDate = membershipPurchaseDate;
		this.membershipExpirationDate = membershipExpirationDate;
	}

	public int getMemberIntNumber() {
		return memberIntNumber;
	}

	public void setMemberIntNumber(int memberIntNumber) {
		this.memberIntNumber = memberIntNumber;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMembershipType() {
		return membershipType;
	}

	public void setMembershipType(String membershipType) {
		this.membershipType = membershipType;
	}

	public String getMembershipPurchaseDate() {
		return membershipPurchaseDate;
	}

	public void setMembershipPurchaseDate(String membershipPurchaseDate) {
		this.membershipPurchaseDate = membershipPurchaseDate;
	}

	public String getMemberNumber() {
		return memberNumber;
	}

	public String getMembershipExpirationDate() {
		return membershipExpirationDate;
	}

	public void setMembershipExpirationDate(String membershipExpirationDate) {
		this.membershipExpirationDate = membershipExpirationDate;
	}

	public void conversionMemberNumber() {
		this.memberNumber = String.valueOf(memberIntNumber);
		this.memberIntNumber = Integer.valueOf(memberNumber);
	}

	public void outputWithdrawalMemberData() {
		System.out.println(memberNumber + "\t" + memberName + "\t" + birthDate + "\t" + telNumber + "\t" + email + "\t"	+ membershipType + "\t\t" + getMembershipExpirationDate());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Member))
			return false;
		Member member = (Member) obj;
		return this.telNumber.equals(member.telNumber);
	}

	@Override
	public int hashCode() {
		return this.telNumber.hashCode();
	}

	@Override
	public int compareTo(Member o) {
		return this.memberNumber.compareToIgnoreCase(o.memberNumber);
	}

	@Override
	public String toString() {
		return memberNumber + "\t" + memberName + "\t" + birthDate + "\t" + telNumber + "\t" + email + "\t" + membershipType + "\t\t" + membershipPurchaseDate + "\t" + getMembershipExpirationDate();
	}
}