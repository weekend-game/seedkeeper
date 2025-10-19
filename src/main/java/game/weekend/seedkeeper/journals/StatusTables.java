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
 * Statuses
 */
public class StatusTables extends DBTables {

	public StatusTables(Connection c) {
		if (c == null)
			return;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT name FROM Statuses WHERE 0=1");
			ResultSetMetaData metaData = rs.getMetaData();

			Status.setNAME_LENGTH(metaData.getPrecision(1));
		} catch (SQLException e) {
			System.out.println("StatusTables.StatusTables() - " + e);
		}
	}

	public ObservableList<Status> getList() {
		ObservableList<Status> list = FXCollections.observableArrayList();

		Connection c = getConnection();
		if (c == null)
			return list;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, numb, name, color FROM Statuses");
			while (rs.next())
				list.add(new Status(rs.getInt(1), rs.getInt(2), rs.getString(3).trim(), rs.getString(4)));
		} catch (SQLException e) {
			System.out.println("StatusTables.getList() - " + e);
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

			ResultSet rs = s.executeQuery("SELECT id, name FROM Statuses");
			while (rs.next())
				list.add(new ComboItem(rs.getInt(1), rs.getString(2)));
		} catch (SQLException e) {
			System.out.println("StatusTables.getListForCombo() - " + e);
		}

		return list;
	}

	public Status get(int id) {
		Status Status = new Status();

		Connection c = getConnection();
		if (c == null || id == 0)
			return Status;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, numb, name, color FROM Statuses WHERE id = " + id);
			if (rs.next())
				Status = new Status(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4));
		} catch (SQLException e) {
			System.out.println("StatusTables.get(int id) - " + e);
		}

		return Status;
	}

	public void add(Status status) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps;

			ps = c.prepareStatement("UPDATE Statuses SET numb = numb + 1 WHERE numb >= ?");
			ps.setInt(1, status.getNumb());
			ps.executeUpdate();

			ps = c.prepareStatement("INSERT INTO Statuses (numb, name, color) VALUES (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, status.getNumb());
			ps.setString(2, status.getName());
			ps.setString(3, status.getColor());
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				status.setId(rs.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("StatusTables.add(Status status) - " + e);
		}
	}

	public void set(Status status) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps = c.prepareStatement("UPDATE Statuses SET numb = ?, name = ?, color = ? WHERE id = ?");
			ps.setInt(1, status.getNumb());
			ps.setString(2, status.getName());
			ps.setString(3, status.getColor());
			ps.setInt(4, status.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("StatusTables.set(Status status) - " + e);
		}
	}

	public boolean canRemove(Status Status) {
		Connection c = getConnection();
		if (c == null)
			return false;

		return true;
	}

	public boolean remove(Status status) {
		boolean deleted = false;

		Connection c = getConnection();
		if (c == null)
			return deleted;

		try (Statement s = c.createStatement()) {
			s.executeUpdate("DELETE FROM Statuses WHERE id = " + status.getId());
			deleted = true;

			s.executeUpdate("UPDATE Statuses SET numb = numb - 1 WHERE numb > " + status.getNumb());
		} catch (SQLException e) {
			System.out.println("StatusTables.remove(Status status) - " + e);
		}

		return deleted;
	}

	public void setNumb(int id1, int numb1, int id2, int numb2) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps;

			ps = c.prepareStatement("UPDATE Statuses SET numb = ? WHERE id = ?");
			ps.setInt(1, numb1);
			ps.setInt(2, id1);
			ps.executeUpdate();

			ps = c.prepareStatement("UPDATE Statuses SET numb = ? WHERE id = ?");
			ps.setInt(1, numb2);
			ps.setInt(2, id2);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("StatusTables.setNumb(int id1, int numb1, int id2, int numb2) - " + e);
		}
	}
}
