package game.weekend.seedkeeper.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import game.weekend.seedkeeper.journals.BrandTables;

public class DB {

	/**
	 * There are three possible options for connecting to the database.
	 * <p>
	 * 1) Client server private static final String url =
	 * "jdbc:derby://localhost:1527/db";
	 * </p>
	 * <p>
	 * 2) File exclusive access. The database is located in the application folder
	 * private static final String url = "jdbc:derby:db";
	 * </p>
	 * <p>
	 * 3) Read-only access to the database in the form of a JAR file. The JAR file
	 * is an uncompressed ZIP file of the DB folder renamed to JAR. The file must be
	 * located in the application folder private static final String url =
	 * "jdbc:derby:jar:(db.jar)db";
	 * </p>
	 */
	private static DB instance;

	private static String url = "jdbc:derby:db";
	private static String user = "user";
	private static String password = "user";

	private final Connection connection;

	public final BrandTables brand;

	public static void setURL(String url) {
		DB.url = url;
	}

	public static String getUrl() {
		return url;
	}

	public static DB getInstance() {
		if (instance == null) {
			instance = new DB();
		}
		return instance;
	}

	private DB() {
		Connection c = null;
		try {
			c = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("DB() - " + e);
		}
		connection = c;

		brand = new BrandTables();
	}

	public Connection getConnection() {
		return connection;
	}
}
