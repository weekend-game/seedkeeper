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
 * Kind
 */
public class KindTables extends DBTables {

	public KindTables(Connection c) {
		if (c == null)
			return;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT name FROM Kinds WHERE 0=1");
			ResultSetMetaData metaData = rs.getMetaData();

			Kind.setNAME_LENGTH(metaData.getPrecision(1));
		} catch (SQLException e) {
			System.out.println("KindTables.KindTables() - " + e);
		}
	}

	public ObservableList<Kind> getList() {
		ObservableList<Kind> list = FXCollections.observableArrayList();

		Connection c = getConnection();
		if (c == null)
			return list;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, name FROM Kinds");
			while (rs.next())
				list.add(new Kind(rs.getInt(1), rs.getString(2)));
		} catch (SQLException e) {
			System.out.println("KindTables.getList() - " + e);
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

			ResultSet rs = s.executeQuery("SELECT id, name FROM Kinds ORDER BY UPPER(name)");
			while (rs.next())
				list.add(new ComboItem(rs.getInt(1), rs.getString(2)));
		} catch (SQLException e) {
			System.out.println("KindTables.getListForCombo() - " + e);
		}

		return list;
	}

	public Kind get(int id) {
		Kind kind = new Kind();

		Connection c = getConnection();
		if (c == null || id == 0)
			return kind;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, name FROM Kinds WHERE id = " + id);
			if (rs.next())
				kind = new Kind(rs.getInt(1), rs.getString(2));
		} catch (SQLException e) {
			System.out.println("KindTables.get(int id) - " + e);
		}

		return kind;
	}

	public void add(Kind kind) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps = c.prepareStatement("INSERT INTO Kinds (name) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, kind.getName());
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				kind.setId(rs.getInt(1));
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println("KindTables.add(Kind kind) - " + e);
		}
	}

	public void set(Kind kind) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps = c.prepareStatement("UPDATE Kinds SET name = ? WHERE id = ?");
			ps.setString(1, kind.getName());
			ps.setInt(2, kind.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("KindTables.set(Kind kind) - " + e);
		}
	}

	public boolean canRemove(Kind kind) {
		Connection c = getConnection();
		if (c == null)
			return false;

		return true;
	}

	public boolean remove(Kind kind) {
		boolean deleted = false;

		Connection c = getConnection();
		if (c == null)
			return deleted;

		try (Statement s = c.createStatement()) {
			s.executeUpdate("DELETE FROM Kinds WHERE id = " + kind.getId());
			deleted = true;
		} catch (SQLException e) {
			System.out.println("KindTables.remove(Kind kind) - " + e);
		}

		return deleted;
	}
}
