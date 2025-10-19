package game.weekend.seedkeeper.journals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import game.weekend.seedkeeper.db.ComboItem;
import game.weekend.seedkeeper.db.DBTables;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Colors
 */
public class ColorTables extends DBTables {

	public ColorTables(Connection c) {
		if (c == null)
			return;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT name FROM Colors WHERE 0=1");
			ResultSetMetaData metaData = rs.getMetaData();

			Color.setNAME_LENGTH(metaData.getPrecision(1));

		} catch (SQLException e) {
			System.out.println("ColorTables.ColorTables() - " + e);
		}
	}

	public ObservableList<Color> getList() {
		ObservableList<Color> list = FXCollections.observableArrayList();

		Connection c = getConnection();
		if (c == null)
			return list;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, name FROM Colors");
			while (rs.next())
				list.add(new Color(rs.getInt(1), rs.getString(2)));
		} catch (SQLException e) {
			System.out.println("ColorTables.getList() - " + e);
		}

		return list;
	}

	public ObservableList<ComboItem> getListForCombo() {
		ObservableList<ComboItem> list = FXCollections.observableArrayList();

		Connection c = getConnection();
		if (c == null)
			return list;

		try (Statement s = c.createStatement()) {

			list.add(new ComboItem(0, ""));

			ResultSet rs = s.executeQuery("SELECT id, name FROM Colors ORDER BY UPPER(name)");
			while (rs.next())
				list.add(new ComboItem(rs.getInt(1), rs.getString(2)));
		} catch (SQLException e) {
			System.out.println("ColorTables.getListForCombo() - " + e);
		}

		return list;
	}

	public Color get(int id) {
		Color color = new Color();

		Connection c = getConnection();
		if (c == null || id == 0)
			return color;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, name FROM Colors WHERE id = " + id);
			if (rs.next())
				color = new Color(rs.getInt(1), rs.getString(2));
		} catch (SQLException e) {
			System.out.println("ColorTables.get(int id) - " + e);
		}

		return color;
	}

	public void add(Color color) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps = c.prepareStatement("INSERT INTO Colors (name) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, color.getName());
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				color.setId(rs.getInt(1));
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println("ColorTables.add(Color color) - " + e);
		}
	}

	public void set(Color color) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps = c.prepareStatement("UPDATE Colors SET name = ? WHERE id = ?");
			ps.setString(1, color.getName());
			ps.setInt(2, color.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ColorTables.set(Color color) - " + e);
		}
	}

	public boolean canRemove(Color color) {
		Connection c = getConnection();
		if (c == null)
			return false;

		return true;
	}

	public boolean remove(Color color) {
		boolean deleted = false;

		Connection c = getConnection();
		if (c == null)
			return deleted;

		try (Statement s = c.createStatement()) {
			s.executeUpdate("DELETE FROM Colors WHERE id = " + color.getId());
			deleted = true;
		} catch (SQLException e) {
			System.out.println("ColorTables.remove(Color color) - " + e);
		}

		return deleted;
	}
}
