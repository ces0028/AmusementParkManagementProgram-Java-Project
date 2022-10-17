package amusementParkManagementProgram;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBConnection {
	public static final int BY_MEMBERNUMBER = 1, BY_NAME = 2, BY_EXPIRATIONDATE1 = 3, BY_EXPIRATIONDATE2 = 5;
	private Connection connection = null;

	// Connection start
	public void connect() {
		Properties properties = new Properties();
		String file = "C:/java_test/amusementParkManagementProgram/src/amusementParkManagementProgram/db.properties";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			properties.load(fis);
		} catch (FileNotFoundException e) {
			System.out.println("FileInput Error : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("PropertiesLoad Error : " + e.getMessage());
		}

		try {
			Class.forName(properties.getProperty("driver"));
			connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("userid"),
					properties.getProperty("password"));
		} catch (ClassNotFoundException e) {
			System.out.println("Class.forNameLoad Error : " + e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Connection Error : " + e.getMessage());
		}
	}

	// Select from staffDB
	public List<Staff> selectstaffDB() {
		List<Staff> list = new ArrayList<Staff>();
		Statement statement = null;
		ResultSet rs = null;
		String selectStaffDBQuery = "SELECT * FROM staffTBL;";
		try {
			statement = connection.prepareStatement(selectStaffDBQuery);
			rs = statement.executeQuery(selectStaffDBQuery);
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			while (rs.next()) {
				String staffName = rs.getString("staffName");
				int staffNumber = rs.getInt("staffNumber");
				String password = rs.getString("password");

				list.add(new Staff(staffName, staffNumber, password));
			}
		} catch (SQLException e) {
			System.out.println("PrepareStatement SelectStaffDB Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Select From staffDB Error : " + e.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				System.out.println("Statement SelectStaffDB Close Error : " + e.getMessage());
			}
		}
		return list;
	}

	// Insert
	public int insert(Member member) {
		PreparedStatement ps = null;
		int insertReturnValue = -1;
		String insertQuery = "CALL procedure_insert_memberTBL(?, ?, ?, ?, ?, ?);";

		try {
			ps = connection.prepareStatement(insertQuery);
			ps.setString(1, member.getMemberName());
			ps.setString(2, member.getBirthDate());
			ps.setString(3, member.getTelNumber());
			ps.setString(4, member.getEmail());
			ps.setString(5, member.getMembershipType());
			ps.setString(6, member.getMembershipPurchaseDate());

			insertReturnValue = ps.executeUpdate();

		} catch (SQLException e) {
			System.out.println("Insert Error : " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Insert Close Error : " + e.getMessage());
			}
		}
		return insertReturnValue;
	}

	// Update
	public int update(Member member, int updates) {
		PreparedStatement ps = null;
		int updateReturnValue = -1;
		try {
			if (updates == BY_EXPIRATIONDATE2) {
				String updateQuery = "CALL procedure_update2_memberTBL(?, ?)";
				ps = connection.prepareStatement(updateQuery);
				ps.setString(1, member.getMembershipExpirationDate());
				ps.setInt(2, member.getMemberIntNumber());
				updateReturnValue = ps.executeUpdate();
			} else {
				String updateQuery = "CALL procedure_update1_memberTBL(?, ?, ?, ?, ?, ?, ?)";
				ps = connection.prepareStatement(updateQuery);
				ps.setString(1, member.getMemberName());
				ps.setString(2, member.getBirthDate());
				ps.setString(3, member.getTelNumber());
				ps.setString(4, member.getEmail());
				ps.setString(5, member.getMembershipType());
				ps.setString(6, member.getMembershipPurchaseDate());
				ps.setInt(7, member.getMemberIntNumber());
				;
				updateReturnValue = ps.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("Update Error : " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Update Close Error : " + e.getMessage());
			}
		}
		return updateReturnValue;
	}

	// Delete
	public int delete(int memberIntNumber) {
		PreparedStatement ps = null;
		int deleteReturnValue = -1;
		String deleteQuery = "CALL procedure_delete_memberTBL(?);";

		try {
			ps = connection.prepareStatement(deleteQuery);
			ps.setInt(1, memberIntNumber);
			deleteReturnValue = ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Delete Error : " + e.getMessage());
		} finally {
			try {
				while (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Delete Close Error : " + e.getMessage());
			}
		}
		return deleteReturnValue;
	}

	// Select search
	public List<Member> selectSearch(String data, int type) {
		List<Member> list = new ArrayList<Member>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String selectSearchQuary = "CALL procedure_selectSearch_memberTBL(?, ?, ?)";
		try {
			ps = connection.prepareStatement(selectSearchQuary);
			switch (type) {
			case BY_MEMBERNUMBER:
				ps.setInt(1, BY_MEMBERNUMBER);
				ps.setInt(2, Integer.valueOf(data));
				ps.setString(3, null);
				break;
			case BY_NAME:
				ps.setInt(1, BY_NAME);
				ps.setInt(2, 0);
				ps.setString(3, data);
				break;
			}
			rs = ps.executeQuery();

			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}

			while (rs.next()) {
				String memberNumber = String.format("%05d", rs.getInt("memberIntNumber"));
				String memberName = rs.getString("memberName");
				String birthDate = rs.getString("birthDate");
				String telNumber = rs.getString("telNumber");
				String email = rs.getString("email");
				String membershipType = rs.getString("membershipType");
				String membershipPurchaseDate = rs.getString("membershipPurchaseDate");
				String membershipExpirationDate = rs.getString("membershipExpirationDate");

				list.add(new Member(memberNumber, memberName, birthDate, telNumber, email, membershipType, membershipPurchaseDate, membershipExpirationDate));
			}
		} catch (Exception e) {
			System.out.println("SelectSearch Error : " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement SelectSearch Close Error : " + e.getMessage());
			}
		}
		return list;
	}

	// Select count
	public List<String> selectCount(int selectNumber) {
		final int by_MonthlyVolume = 1;
		List<String> selectCountList = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (selectNumber == by_MonthlyVolume) {
				for (int month = 1; month < 13; month++) {
					String selectCountQuery = "SELECT function_GetByMonthly(?) AS func_ByMonthly;";
					ps = connection.prepareStatement(selectCountQuery);
					ps.setInt(1, month);
					rs = ps.executeQuery();
					if (!(rs != null || rs.isBeforeFirst())) {
						return selectCountList;
					}
					while (rs.next()) {
						String count = rs.getString("func_ByMonthly");
						selectCountList.add(count);
					}
				}
			} else {
				for (int type = 0; type < 4; type++) {
					String selectCountQuery = "SELECT function_GetByType(?) AS func_ByType;";
					ps = connection.prepareStatement(selectCountQuery);
					ps.setInt(1, type);
					rs = ps.executeQuery();

					if (!(rs != null || rs.isBeforeFirst())) {
						return null;
					}
					while (rs.next()) {
						String count = rs.getString("func_ByType");
						selectCountList.add(count);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("SelectCount Error : " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement SelectOrderBy Close Error : " + e.getMessage());
			}
		}
		return selectCountList;
	}

	// Select
	public List<Member> select() {
		List<Member> list = new ArrayList<Member>();
		Statement statement = null;
		ResultSet rs = null;
		String selectQuary = "CALL procedure_select_memberTBL;";

		try {
			statement = connection.prepareStatement(selectQuary);
			rs = statement.executeQuery(selectQuary);
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			while (rs.next()) {
				String memberNumber = String.format("%05d", rs.getInt("memberIntNumber"));
				String memberName = rs.getString("memberName");
				String birthDate = rs.getString("birthDate");
				String telNumber = rs.getString("telNumber");
				String email = rs.getString("email");
				String membershipType = rs.getString("membershipType");
				String membershipPurchaseDate = rs.getString("membershipPurchaseDate");
				String membershipExpirationDate = rs.getString("membershipExpirationDate");

				list.add(new Member(memberNumber, memberName, birthDate, telNumber, email, membershipType, membershipPurchaseDate, membershipExpirationDate));
			}
		} catch (Exception e) {
			System.out.println("Select Error : " + e.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				System.out.println("Statement Select Close Error : " + e.getMessage());
			}
		}
		return list;
	}

	// Select order by
	public List<Member> selectOrderBy(int selectNumber) {
		List<Member> list = new ArrayList<Member>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String selectOrderByQuery = "CALL procedure_selectOrderBy_memberTBL(?)";
		try {
			ps = connection.prepareStatement(selectOrderByQuery);
			ps.setInt(1, selectNumber);
			rs = ps.executeQuery();
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			while (rs.next()) {
				int memberIntNumber = rs.getInt("memberIntNumber");
				String memberName = rs.getString("memberName");
				String birthDate = rs.getString("birthDate");
				String telNumber = rs.getString("telNumber");
				String email = rs.getString("email");
				String membershipType = rs.getString("membershipType");
				String membershipPurchaseDate = rs.getString("membershipPurchaseDate");
				String membershipExpirationDate = rs.getString("membershipExpirationDate");

				String memberNumber = String.format("%05d", memberIntNumber);

				list.add(new Member(memberNumber, memberName, birthDate, telNumber, email, membershipType, membershipPurchaseDate, membershipExpirationDate));
			}
		} catch (SQLException e) {
			System.out.println("SelectOrderBy Error : " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement SelectOrderBy Close Error : " + e.getMessage());
			}
		}
		return list;
	}

	// Select trigger
	public List<Member> selectTrigger() {
		List<Member> list = new ArrayList<Member>();
		Statement statement = null;
		ResultSet rs = null;
		String selectQuary = "CALL procedure_select_deletememberTBL";

		try {
			statement = connection.prepareStatement(selectQuary);
			rs = statement.executeQuery(selectQuary);
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			while (rs.next()) {
				String memberNumber = String.format("%05d", rs.getInt("memberIntNumber"));
				String memberName = rs.getString("memberName");
				String birthDate = rs.getString("birthDate");
				String telNumber = rs.getString("telNumber");
				String email = rs.getString("email");
				String membershipType = rs.getString("membershipType");
				String membershipPurchaseDate = rs.getString("membershipPurchaseDate");
				String membershipExpirationDate = rs.getString("membershipExpirationDate");

				list.add(new Member(memberNumber, memberName, birthDate, telNumber, email, membershipType, membershipPurchaseDate, membershipExpirationDate));
			}
		} catch (Exception e) {
			System.out.println("SelectTrigger Error : " + e.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				System.out.println("Statement SelectTrigger Close Error : " + e.getMessage());
			}
		}
		return list;
	}

	// Insert select
	public int insertSelect(int memberIntNumber) {
		PreparedStatement ps = null;
		int relocateReturnValue = -1;
		try {
			String relocateQuery = "CALL procedure_insertselect_deletememberTBL(?)";
			ps = connection.prepareStatement(relocateQuery);
			ps.setInt(1, memberIntNumber);
			relocateReturnValue = ps.executeUpdate();

		} catch (SQLException e) {
			System.out.println("InsertSelect Error : " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement InsertSelct Close Error : " + e.getMessage());
			}
		}
		return relocateReturnValue;
	}

	// Delete trigger
	public int triggerDelete(int memberIntNumber) {
		PreparedStatement ps = null;
		int relocateReturnValue = -1;
		try {
			String relocateQuery = "CALL procedure_delete_deletememberTBL(?)";
			ps = connection.prepareStatement(relocateQuery);
			ps.setInt(1, memberIntNumber);
			relocateReturnValue = ps.executeUpdate();

		} catch (SQLException e) {
			System.out.println("Delete TriggerError : " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement DeleteTrigger Close Error : " + e.getMessage());
			}
		}
		return relocateReturnValue;
	}

	// Delete by expirationDate
	public int deleteByExpirationDate(String date) {
		PreparedStatement ps = null;
		int deleteByExpirationReturnValue = -1;
		String deleteByExpirationDateQuery = "CALL procedure_delete_byExpirationDate_memberTBL(?);";

		try {
			ps = connection.prepareStatement(deleteByExpirationDateQuery);
			ps.setString(1, date);
			deleteByExpirationReturnValue = ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("DeleteByExpirationDate Error : " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement DeleteByExpirationDate Close Error : " + e.getMessage());
			}
		}
		return deleteByExpirationReturnValue;
	}

	// Connection close
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Connection Close Error : " + e.getMessage());
		}
	}
}