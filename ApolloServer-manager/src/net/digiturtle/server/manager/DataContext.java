package net.digiturtle.server.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import net.digiturtle.apollo.User;

public class DataContext {

	// Table Users:
	// Id (nvarchar), ProductKey (nvarcar), Coins (int), 
	// PowerupSpeed (int), PowerupDamage (int), PowerupResilience (int), PowerupExplosives (int)

	private Connection c;

	public DataContext (String file) {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + file);
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	public void setupTables () throws SQLException {
		Statement stmt = c.createStatement();
		String sql = "CREATE TABLE Users (Id CHAR(36), ProductKey CHAR(36), Coins INTEGER, PowerupSpeed INTEGER, PowerupDamage INTEGER, PowerupResilience INTEGER, PowerupExplosives INTEGER)"; 
		stmt.executeUpdate(sql);
	}
	
	public User getUser (UUID id) throws SQLException {
		PreparedStatement stmt = c.prepareStatement("SELECT * FROM Users WHERE Id = ?;");
		stmt.setString(1, id.toString());
		ResultSet rs = stmt.executeQuery();
		boolean record = rs.next();
		if (record) {
			User user = new User();
			user.setCoins(rs.getInt("Coins"));
			user.setDamagePowerup(rs.getInt("PowerupDamage"));
			user.setExplosivesPowerup(rs.getInt("PowerupExplosives"));
			user.setId(UUID.fromString(rs.getString("Id")));
			user.setProductKey(rs.getString("ProductKey"));
			user.setResiliencePowerup(rs.getInt("PowerupResilience"));
			user.setSpeedPowerup(rs.getInt("PowerupSpeed"));
			return user;
		} else {
			return null;
		}
	}

	private User getUser (String productKey) throws SQLException {
		PreparedStatement stmt = c.prepareStatement("SELECT * FROM Users WHERE ProductKey = ?;");
		stmt.setString(1, productKey);
		ResultSet rs = stmt.executeQuery();
		boolean record = rs.next();
		if (record) {
			User user = new User();
			user.setCoins(rs.getInt("Coins"));
			user.setDamagePowerup(rs.getInt("PowerupDamage"));
			user.setExplosivesPowerup(rs.getInt("PowerupExplosives"));
			user.setId(UUID.fromString(rs.getString("Id")));
			user.setProductKey(productKey);
			user.setResiliencePowerup(rs.getInt("PowerupResilience"));
			user.setSpeedPowerup(rs.getInt("PowerupSpeed"));
			return user;
		} else {
			return null;
		}
	}

	public User findUser (String productKey) throws SQLException {
		// u = SELECT TOP (1) FROM Users WHERE ProductKey = productKey;
		// IF (u = NULL) INSERT INTO Users (Id, ProductKey, Coins, PowerupSpeed, PowerupDamage, PowerupResilience, PowerupExplosives
		//							VALUES (NEWID(), productKey, 0, 1, 1, 1, 1)
		// u = SELECT TOP (1) FROM Users WHERE ProductKey = productKey;
		User user = getUser(productKey);
		if (user == null) {
			UUID id = UUID.randomUUID();
			PreparedStatement stmt = c.prepareStatement("INSERT INTO Users (Id, ProductKey, Coins, PowerupSpeed, PowerupDamage, PowerupResilience, PowerupExplosives) VALUES ('" + id.toString() + "', ?, 0, 1, 1, 1, 1);");
			stmt.setString(1, productKey);
			stmt.executeUpdate();
			user = getUser(productKey);
			stmt.close();
		}
		return user;
	}

	public boolean updateUser (User user) throws SQLException {
		PreparedStatement stmt = c.prepareStatement("UPDATE Users SET Coins = " + Integer.toString(user.getCoins()) + 
				", PowerupSpeed = " + Integer.toString(user.getSpeedPowerup()) + ", PowerupDamage = " + Integer.toString(user.getDamagePowerup()) + 
				", PowerupResilience = " + Integer.toString(user.getResiliencePowerup()) + ", PowerupExplosives = " + Integer.toString(user.getExplosivesPowerup()) + 
				" WHERE ProductKey = ?");
		stmt.setString(1, user.getProductKey());
		boolean result = stmt.executeUpdate() > 0;
		stmt.close();
		return result;
	}

}
