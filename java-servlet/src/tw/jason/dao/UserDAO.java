package tw.jason.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tw.jason.demo.User;

/**
 * Abstract DAO provide the CRUD operation
 * 
 * @author chses910372
 */
public class UserDAO {
	private static final String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false";
	private static final String jdbcUsername = "root";
	private static final String jdbcPwd = "reborn20519";
	private static final String INSERT_USERS = "insert into users" + " (name, email, address) VALUES " + "(?, ?, ?);";
	private static final String SELECT_USER_BY_ID = "select * from users where id = ?;";
	private static final String SELECT_ALL_USERS = "select * from users;";
	private static final String DELETE_USER = "delete from users where id = ?;";
	private static final String UPDATE_USER = "update users set name = ?, email = ?, address = ? where id = ?;";

	public UserDAO() {
	}

	protected Connection getConnection() {
		Connection cn = null;
		System.out.println("Start Connect...");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			cn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPwd);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Cannot find class");
			e.getMessage();
		}

		return cn;
	}

	public void insertUser(User user) throws SQLException {
		System.out.println(INSERT_USERS);
		try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(INSERT_USERS)) {
			ps.setString(1, user.getName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getAddress());
			System.out.println(ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			printSQLException(e);
		}
	}

	public User selectUser(int id) {
		User user = null;
		try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(SELECT_USER_BY_ID);) {
			ps.setInt(1, id);
			System.out.println(ps);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				String email = rs.getString("email");
				String address = rs.getString("address");
				user = new User(id, name, email, address);
			}

		} catch (SQLException e) {
			printSQLException(e);
		}
		return user;
	}

	public List<User> selectAllUsers() {
		List<User> users = new ArrayList<>();

		try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(SELECT_ALL_USERS);) {
			System.out.println(ps);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String address = rs.getString("address");
				users.add(new User(id, name, email, address));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}

		return users;
	}

	public boolean deleteUser(int id) throws SQLException {
		boolean deleted = false;
		try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(DELETE_USER);) {
			ps.setInt(1, id);
			deleted = ps.executeUpdate() > 0;
		}
		return deleted;
	}

	public boolean updateUser(User user) throws SQLException {
		boolean updated = false;
		try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(UPDATE_USER);) {
			ps.setString(1, user.getName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getAddress());
			ps.setInt(4, user.getId());
			updated = ps.executeUpdate() > 0;
		}
		return updated;
	}

	public void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}
}
