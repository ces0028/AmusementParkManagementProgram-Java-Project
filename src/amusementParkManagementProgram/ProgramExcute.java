package amusementParkManagementProgram;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ProgramExcute {
	public static Scanner scanner = new Scanner(System.in);
	public static final int INPUT = 1, UPDATE = 2, DELETE = 3, SEARCH = 4, STATS = 5, OUTPUT = 6, SORT = 7,
			WITHDRAWAL = 8, ARRANGE = 9, EXIT = 0;
	public static final int CHECK_EXAMPLE2 = 1, CHECK_EXAMPLE3 = 2, CHECK_EXAMPLE4 = 3, CHECK_EXAMPLE5 = 4,
			CHECK_NAME = 5, CHECK_STAFFNUMBER = 6, CHECK_PASSWORD = 7, CHECK_MEMBERNUMBER = 8, CHECK_BIRTHDATE = 9,
			CHECK_TELNUMBER = 10, CHECK_DATE = 11;
	public static final int FAILED = -1, NOT_FOUND = 0, SUCCESS = 1, DATA_EMPTY = 0;
	public static final int VIP = 1, STANDARD_PLUS = 2, STANDARD = 3;

	public static void main(String[] args) {
		DBConnection dbCon = new DBConnection();
		dbCon.connect();

		boolean loopFlagLogin = false;
		while (!loopFlagLogin) {
			int login = login();
			if (login == 1)
				loopFlagLogin = true;
		}

		boolean loopFlag = false;
		while (!loopFlag) {
			int selectNumber = displayMenu();
			switch (selectNumber) {
			case INPUT:
				inputMemberData();
				break;
			case UPDATE:
				updateMemberData();
				break;
			case DELETE:
				deleteMemberData();
				break;
			case SEARCH:
				searchMemberData();
				break;
			case STATS:
				statsMemberData();
				break;
			case OUTPUT:
				outputMemberData();
				break;
			case SORT:
				sortMemberData();
				break;
			case WITHDRAWAL:
				outputWithdrawalMemberData();
				break;
			case ARRANGE:
				arrangeMemberData();
				break;
			case EXIT:
			case FAILED:
				loopFlag = true;
				break;
			default:
				System.out.println("보기를 확인 후 다시 입력해주세요.");
			}
		}
		System.out.print("프로그램이 종료되었습니다.");
	}

	// Arrange member data
	public static void arrangeMemberData() {
		final int todayDate = 1;
		List<Member> list = new ArrayList<Member>();
		Date nowDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
		scanner.nextLine();

		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.select();

			if (list.size() <= DATA_EMPTY) {
				System.out.println("정리할 회원정보가 없습니다. 먼저 데이터를 입력해주세요.");
				return;
			}
			
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.println("| 정리 기준일를 입력해주세요 >  1. 오늘 자 | 2. 특정 일자　　　            　　　　　                                                    |");
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.print("입력 > ");
			int selectNumber = scanner.nextInt();
			boolean value = checkInputPattern(String.valueOf(selectNumber), CHECK_EXAMPLE2);
			if (!value)
				return;
			int deleteByExpirationDateReturnValue = 0;
			String today = simpleDateFormat.format(nowDate);
			if(selectNumber == todayDate) {
				deleteByExpirationDateReturnValue = dbCon.deleteByExpirationDate(today);
			} else {
				scanner.nextLine();
				System.out.print("기준이 되는 일자를 입력해주세요(yyyy-mm-dd) : ");
				String date = scanner.nextLine();
				value = checkInputPattern(date, CHECK_DATE);
				deleteByExpirationDateReturnValue = dbCon.deleteByExpirationDate(date);
			}
			if (deleteByExpirationDateReturnValue == FAILED) {
				System.out.println("정리에 실패했습니다.");
				return;
			} else {
				System.out.println("데이터를 정상적으로 정리했습니다.");
			}
			
			dbCon.close();
		} catch (Exception e) {
			System.out.println("Database Delete Error : " + e.getMessage());
			return;
		}		
	}

	// Output withdrawal member data
	public static void outputWithdrawalMemberData() {
		final int RESTORAITON = 2;
		List<Member> list = new ArrayList<Member>();
		scanner.nextLine();

		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.selectTrigger();

			if (list.size() <= DATA_EMPTY) {
				System.out.println("출력할 탈퇴회원 정보가 없습니다. 먼저 데이터를 입력해주세요.");
				return;
			}
			System.out.println("회원번호\t회원명\t생년월일\t\t휴대전화번호\t\t이메일주소\t\t\t연간회원권종류\t회원탈퇴일");
			for (Member member : list) {
				member.outputWithdrawalMemberData();
			}
			
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.println("| 희망하는 업무를 선택해주세요 >  1. 나가기 | 2. 탈퇴한 회원 복원시키기　                                             　                 |");
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.print("입력 > ");
			int selectNumber = scanner.nextInt();
			boolean value = checkInputPattern(String.valueOf(selectNumber), CHECK_EXAMPLE2);
			if (!value)
				return;
			if (selectNumber == RESTORAITON) {
				scanner.nextLine();
				System.out.print("복원할 회원번호를 입력해주세요 : ");
				String memberNumber = scanner.nextLine();
				value = checkInputPattern(memberNumber, CHECK_MEMBERNUMBER);
				if (!value)
					return;

				int memberIntNumber = Integer.valueOf(memberNumber);
				int relocatereturnValue = dbCon.insertSelect(memberIntNumber);
				relocatereturnValue = dbCon.triggerDelete(memberIntNumber);
				
				System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
				System.out.println("| 연간회원권 종류를 선택해주세요>  1. VIP | 2. 스탠다드+ | 3. 스탠다드 | 4. 라이트　　　　                                                |");
				System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
				System.out.print("입력 > ");
				int membershipTypeByNumber = scanner.nextInt();
				value = checkInputPattern(String.valueOf(membershipTypeByNumber), CHECK_EXAMPLE4);
				if (!value)
					return;
				String membershipType = null;
				if (membershipTypeByNumber == VIP) {
					membershipType = "VIP";
				} else if (membershipTypeByNumber == STANDARD_PLUS) {
					membershipType = "스탠다드+";
				} else if (membershipTypeByNumber == STANDARD) {
					membershipType = "스탠다드";
				} else {
					membershipType = "라이트";
				}
				scanner.nextLine();
				System.out.print("연간회원권 구매일자를 입력해주세요(yyyy-mm-dd) : ");
				String membershipPurchaseDate = scanner.nextLine();
				value = checkInputPattern(membershipPurchaseDate, CHECK_DATE);
				if (!value)
					return;

				Member member = list.get(0);
				member.setMembershipType(membershipType);
				member.setMemberIntNumber(memberIntNumber);
				member.setMembershipPurchaseDate(membershipPurchaseDate);
				
				int updatereturnValue = dbCon.update(member, CHECK_DATE);
				
				if (relocatereturnValue == FAILED) {
					System.out.println("복원에 실패했습니다.");
					return;
				} else if (relocatereturnValue == NOT_FOUND) {
					System.out.println("해당 회원 데이터를 찾지 못했습니다.");
					return;
				} else {
					System.out.println("데이터가 정상적으로 복원되었습니다.");
				}
				if (updatereturnValue == FAILED) {
					System.out.println("연간회원권 구매일자 등록에 실패했습니다.");
					return;
				} else if (updatereturnValue == NOT_FOUND) {
					System.out.println("해당 회원번호를 찾지 못했습니다.");
					return;
				}
			}

			dbCon.close();
		} catch (Exception e) {
			System.out.println("Database Trigger Error : " + e.getMessage());
		}
		return;
	}

	// Sort member data
	public static void sortMemberData() {
		List<Member> list = new ArrayList<Member>();
		scanner.nextLine();

		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.select();

			if (list.size() <= DATA_EMPTY) {
				System.out.println("정렬할 회원정보가 없습니다. 먼저 데이터를 입력해주세요.");
				return;
			}

			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.println("| 정렬 기준을 선택해주세요 >  1. 회원번호 | 2. 회원명 | 3. 연간회원권만료일                                                      　　   　|");
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.print("입력 > ");
			int selectNumber = scanner.nextInt();
			boolean value = checkInputPattern(String.valueOf(selectNumber), CHECK_EXAMPLE3);
			if (!value)
				return;
			list = dbCon.selectOrderBy(selectNumber);
			
			outputDataList(list);

			dbCon.close();
		} catch (Exception e) {
			System.out.println("Database Sort Error : " + e.getMessage());
			return;
		}
	}

	// Output member data
	public static void outputMemberData() {
		List<Member> list = new ArrayList<Member>();
		scanner.nextLine();

		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.select();

			if (list.size() <= DATA_EMPTY) {
				System.out.println("출력할 회원정보가 없습니다. 먼저 데이터를 입력해주세요.");
				return;
			}

			outputDataList(list);

			dbCon.close();
		} catch (Exception e) {
			System.out.println("Database Output Error : " + e.getMessage());
		}
		return;
	}

	// Member data statistics
	public static void statsMemberData() {
		final int BY_MONTH = 1;
		List<Member> list = new ArrayList<Member>();
		scanner.nextLine();

		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.select();

			if (list.size() <= DATA_EMPTY) {
				System.out.println("통계를 낼 회원정보가 없습니다. 먼저 데이터를 입력해주세요.");
				return;
			}
			
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.println("| 통계항목을 선택해주세요 >  1. 월별 연간회원권 판매 현황 | 2. 종류별 연간회원권 판매 현황      　                                          　　|");
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.print("입력 > ");
			int selectNumber = scanner.nextInt();
			boolean value = checkInputPattern(String.valueOf(selectNumber), CHECK_EXAMPLE2);
			if (!value)
				return;
			scanner.nextLine();

			List<String> selectCountList = new ArrayList<String>();
			selectCountList = dbCon.selectCount(selectNumber);

			if (selectNumber == BY_MONTH) {
				System.out.println("1월\t2월\t3월\t4월\t5월\t6월\t7월\t8월\t9월\t10월\t11월\t12월");
				for (String data : selectCountList) {
					System.out.print(data + "\t");
				}
				System.out.println();
			} else {
				System.out.println("VIP\t스탠다드+\t스탠다드\t라이트");
				for (String data : selectCountList) {
					System.out.print(data + "\t");
				}
				System.out.println();
			}
			
			dbCon.close();
		} catch (Exception e) {
			System.out.println("Database Stats Error : " + e.getMessage());
			return;
		}
	}

	// Search member data
	public static void searchMemberData() {
		final int BY_MEMBERNUMBER = 1;
		List<Member> list = new ArrayList<Member>();
		scanner.nextLine();

		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.select();

			if (list.size() <= DATA_EMPTY) {
				System.out.println("검색할 회원정보가 없습니다. 먼저 데이터를 입력해주세요.");
				return;
			}
			
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.println("| 검색할 방법을 선택해주세요 >  1. 회원번호 | 2. 회원명                                                                          　|");
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.print("입력> ");
			int searchMethod = scanner.nextInt();
			boolean value = checkInputPattern(String.valueOf(searchMethod), CHECK_EXAMPLE2);
			if (!value)
				return;
			scanner.nextLine();

			if (searchMethod == BY_MEMBERNUMBER) {
				System.out.print("검색할 회원번호를 입력해주세요 : ");
				String memberNumber = scanner.nextLine();
				value = checkInputPattern(memberNumber, CHECK_MEMBERNUMBER);
				if (!value)
					return;
				list = dbCon.selectSearch(memberNumber, searchMethod);
			} else {
				System.out.print("검색할 회원명을 입력해주세요 : ");
				String memberName = scanner.nextLine();
				value = checkInputPattern(memberName, CHECK_NAME);
				if (!value)
					return;
				list = dbCon.selectSearch(memberName, searchMethod);
			}

			outputDataList(list);

			dbCon.close();
		} catch (InputMismatchException e) {
			System.out.println("보기를 확인 후 다시 입력해주세요.");
			return;
		} catch (Exception e) {
			System.out.println("Database Search Error : " + e.getMessage());

		}
	}

	// Delete member data
	public static void deleteMemberData() {
		List<Member> list = new ArrayList<Member>();
		scanner.nextLine();

		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.select();

			if (list.size() <= DATA_EMPTY) {
				System.out.println("삭제할 회원정보가 없습니다. 먼저 데이터를 입력해주세요.");
				return;
			}
			System.out.print("삭제할 회원번호를 입력해주세요 : ");
			String memberNumber = scanner.nextLine();
			boolean value = checkInputPattern(String.valueOf(memberNumber), CHECK_MEMBERNUMBER);
			if (!value)
				return;
			int memberIntNumber = Integer.valueOf(memberNumber);

			int deletereturnValue = dbCon.delete(memberIntNumber);
			if (deletereturnValue == FAILED) {
				System.out.println("삭제에 실패했습니다.");
				return;
			} else if (deletereturnValue == NOT_FOUND) {
				System.out.println("해당 회원 데이터를 찾지 못했습니다.");
				return;
			} else {
				System.out.println("데이터를 정상적으로 삭제했습니다.");
			}
			
			dbCon.close();
		} catch (Exception e) {
			System.out.println("Database Delete Error : " + e.getMessage());
			return;
		}
	}

	// Update member data
	public static void updateMemberData() {
		final int BY_MEMBERNUMBER = 1, TELNUMBER = 1, EMAIL = 2, TYPE = 3, PURCHASEDATE = 4, EXPIRATIONDATE = 5;
		List<Member> list = new ArrayList<Member>();
		scanner.nextLine();

		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.select();

			if (list.size() <= DATA_EMPTY) {
				System.out.println("수정할 회원정보가 없습니다. 먼저 데이터를 입력해주세요.");
				return;
			}
			System.out.print("수정할 회원번호를 입력해주세요 : ");
			String memberNumber = scanner.nextLine();
			boolean value = checkInputPattern(String.valueOf(memberNumber), CHECK_MEMBERNUMBER);
			if (!value)
				return;

			list = dbCon.selectSearch(memberNumber, BY_MEMBERNUMBER);

			outputDataList(list);

			Member member = list.get(0);
			member.setMemberIntNumber(Integer.valueOf(memberNumber));

			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.println("| 수정할 항목을 선택해주세요 >  1. 휴대전화번호 | 2. 이메일 | 3. 연간회원권종류 | 4. 연간회원권구매일자 | 5. 연간회원권만료일자　　                     |");
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.print("입력 > ");
			int updates = scanner.nextInt();
			value = checkInputPattern(String.valueOf(updates), CHECK_EXAMPLE5);
			scanner.nextLine();
			switch (updates) {
			case TELNUMBER:
				System.out.print("휴대전화번호를 입력해주세요 : ");
				String telNumber = scanner.nextLine();
				value = checkInputPattern(telNumber, CHECK_TELNUMBER);
				if (!value)
					return;
				member.setTelNumber(telNumber);
				break;
			case EMAIL:
				System.out.print("이메일 주소를 입력해주세요 : ");
				String email = scanner.nextLine();
				member.setEmail(email);
				break;
			case TYPE:
				System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
				System.out.println("| 연간회원권종류를 선택해주세요 >  1. VIP | 2. 스탠다드+ | 3. 스탠다드 | 4. 라이트                  　　　                              　|");
				System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
				System.out.print("입력 > ");
				int membershipTypeByNumber = scanner.nextInt();
				value = checkInputPattern(String.valueOf(membershipTypeByNumber), CHECK_EXAMPLE4);
				if (!value)
					return;
				String membershipType = null;
				if (membershipTypeByNumber == VIP) {
					membershipType = "VIP";
				} else if (membershipTypeByNumber == STANDARD_PLUS) {
					membershipType = "스탠다드+";
				} else if (membershipTypeByNumber == STANDARD) {
					membershipType = "스탠다드";
				} else {
					membershipType = "라이트";
				}
				member.setMembershipType(membershipType);
				break;
			case PURCHASEDATE:
				System.out.print("연간회원권 구매일자를 입력해주세요(yyyy-mm-dd) : ");
				String membershipPurchaseDate = scanner.nextLine();
				value = checkInputPattern(membershipPurchaseDate, CHECK_DATE);
				if (!value)
					return;
				member.setMembershipPurchaseDate(membershipPurchaseDate);
				break;
			case EXPIRATIONDATE:
				System.out.print("연간회원권 만료일자를 입력해주세요(yyyy-mm-dd) : ");
				String membershipExpirationDate = scanner.nextLine();
				value = checkInputPattern(membershipExpirationDate, CHECK_DATE);
				if (!value)
					return;
				member.setMembershipExpirationDate(membershipExpirationDate);
				break;
			default:
				System.out.println("보기를 확인 후 다시 입력해주세요.");
				return;
			}

			int updatereturnValue = dbCon.update(member, updates);

			if (updatereturnValue == FAILED) {
				System.out.println("수정에 실패했습니다.");
				return;
			} else if (updatereturnValue == NOT_FOUND) {
				System.out.println("해당 회원번호를 찾지 못했습니다.");
				return;
			} else {
				System.out.println("데이터가 정상적으로 수정되었습니다.");
			}
			dbCon.close();
		} catch (Exception e) {
			System.out.println("DaCorrection itemtabase Update Error : " + e.getMessage());
			return;
		}
	}

	// Input member data
	public static void inputMemberData() {
		scanner.nextLine();
		DBConnection dbCon = new DBConnection();
		dbCon.connect();
		try {
			System.out.print("회원명 > ");
			String memberName = scanner.nextLine();
			boolean value = checkInputPattern(memberName, CHECK_NAME);
			if (!value)
				return;
			System.out.print("생년월일(yyyy-mm-dd) > ");
			String birthDate = scanner.nextLine();
			value = checkInputPattern(birthDate, CHECK_BIRTHDATE);
			if (!value)
				return;
			System.out.print("휴대전화번호(-포함) > ");
			String telNumber = scanner.nextLine();
			value = checkInputPattern(telNumber, CHECK_TELNUMBER);
			if (!value)
				return;
			System.out.print("이메일 > ");
			String email = scanner.nextLine();
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.println("| 연간회원권 종류를 선택해주세요 >  1. VIP | 2. 스탠다드+ | 3. 스탠다드 | 4. 라이트　　　　                                                |");
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.print("입력 > ");
			int membershipTypeByNumber = scanner.nextInt();
			value = checkInputPattern(String.valueOf(membershipTypeByNumber), CHECK_EXAMPLE4);
			if (!value)
				return;
			String membershipType = null;
			if (membershipTypeByNumber == VIP) {
				membershipType = "VIP";
			} else if (membershipTypeByNumber == STANDARD_PLUS) {
				membershipType = "스탠다드+";
			} else if (membershipTypeByNumber == STANDARD) {
				membershipType = "스탠다드";
			} else {
				membershipType = "라이트";
			}
			scanner.nextLine();
			System.out.print("연간회원권 구매일자(yyyy-mm-dd) > ");
			String membershipPurchaseDate = scanner.nextLine();
			value = checkInputPattern(membershipPurchaseDate, CHECK_DATE);
			if (!value)
				return;

			Member member = new Member(memberName, birthDate, telNumber, email, membershipType, membershipPurchaseDate);
			int insertreturnValue = dbCon.insert(member);
			if (insertreturnValue == FAILED) {
				System.out.println("회원 데이터 등록에 실패하였습니다.");
			} else {
				System.out.println("회원 데이터가 정상적으로 등록되었습니다.");
			}
			
			dbCon.close();
		} catch (InputMismatchException e) {
			System.out.println("보기를 확인 후 다시 입력해주세요.");
			return;
		} catch (Exception e) {
			System.out.println("Database Input Error : " + e.getMessage());
			return;
		}
	}

	// Output member data list
	public static void outputDataList(List<Member> list) {
		if(list.size() == 0) {
			System.out.println("해당되는 데이터가 존재하지 않습니다.");
			return;
		}
		System.out.println("회원번호\t회원명\t생년월일\t\t휴대전화번호\t\t이메일주소\t\t\t연간회원권종류\t연간회원권구매일\t연간회원권만료일");
		for (Member member : list) {
			System.out.println(member);
		}
	}

	// Display program menu
	public static int displayMenu() {
		try {
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.println("| 1. 입력  |  2. 수정  |  3. 삭제  |  4. 검색  |  5. 통계  |  6. 출력  |  7. 정렬  |  8. 탈퇴회원  |  9. 회원정보 정리  |  0. 종료　　　　 |");
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.print("입력 > ");
			int selectNumber = scanner.nextInt();
			return selectNumber;
		} catch (InputMismatchException e) {
			System.out.println("보기를 확인 후 다시 입력해주세요.");
			return FAILED;
		}
	}

	// Login
	public static int login() {
		List<Staff> list = new ArrayList<Staff>();
		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.selectstaffDB();

			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.println("|　　　　　　　　　　　　　　　　　　　　        　    　미래랜드 고객관리 프로그램에 접속하셨습니다.　　　　　　　　                                       |");
			System.out.println("+-------------------------------------------------------------------------------------------------------------------+");
			System.out.print("사원번호 > ");
			String staffNumber = scanner.nextLine();
			boolean value = checkInputPattern(staffNumber, CHECK_STAFFNUMBER);
			if (!value)
				return FAILED;

			System.out.print("비밀번호 > ");
			String password = scanner.nextLine();
			value = checkInputPattern(String.valueOf(password), CHECK_PASSWORD);
			if (!value)
				return FAILED;

			for (int i = 0; i < list.size(); i++) {
				Staff staff = list.get(i);
				if ((Integer.valueOf(staffNumber) == (staff.getStaffNumber()))) {
					if ((password.equals(staff.getPassword()))) {
						System.out.println(staff.getStaffName() + "님 로그인되었습니다.\n");
						return SUCCESS;
					}
				}
			}
			System.out.println("사원번호 또는 비밀번호가 잘못되었습니다.");
			
			dbCon.close();
			return FAILED;
		} catch (Exception e) {
			System.out.println("Login Error : " + e.getMessage());
		}
		return FAILED;
	}

	// Check the pattern
	public static boolean checkInputPattern(String data, int patternType) {
		String pattern = null;
		String message = null;
		boolean regex = false;
		switch (patternType) {
		case CHECK_EXAMPLE2:
			pattern = "^[1-2]$";
			message = "보기를 확인 후 정확하게 입력해주세요.";
			break;
		case CHECK_EXAMPLE3:
			pattern = "^[1-3]$";
			message = "보기를 확인 후 정확하게 입력해주세요.";
			break;
		case CHECK_EXAMPLE4:
			pattern = "^[1-4]$";
			message = "보기를 확인 후 정확하게 입력해주세요.";
			break;
		case CHECK_EXAMPLE5:
			pattern = "^[1-5]$";
			message = "보기를 확인 후 정확하게 입력해주세요.";
			break;
		case CHECK_NAME:
			pattern = "^[가-힣]{2,5}$";
			message = "성명을 정확하게 입력해주세요.\n";
			break;
		case CHECK_STAFFNUMBER:
			pattern = "^[0-9]{4}$";
			message = "사원번호를 정확하게 입력해주세요.\n";
			break;
		case CHECK_PASSWORD:
			pattern = "^[a-z]{3}[0-9]{4}$";
			message = "비밀번호를 정확하게 입력해주세요.\n";
			break;
		case CHECK_MEMBERNUMBER:
			pattern = "^[0-9]{5}$";
			message = "회원번호를 정확하게 입력해주세요.";
			break;
		case CHECK_BIRTHDATE:
			pattern = "^[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]$";
			message = "생년월일을 정확하게 입력해주세요.\n";
			break;
		case CHECK_TELNUMBER:
			pattern = "^010-[1-9][0-9]{3}-[0-9]{4}$";
			message = "전화번호를 정확하게 입력해주세요.";
			break;
		case CHECK_DATE:
			pattern = "^20[0-9]{2}-[0-1][0-9]-[0-3][0-9]$";
			message = "날짜를 정확하게 입력해주세요.\n";
			break;
		}
		regex = Pattern.matches(pattern, data);
		if (!regex) {
			System.out.println(message);
			return false;
		}
		return true;
	}
}