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

	private PreparedStatement psList;
	private PreparedStatement psListForCombo;
	private PreparedStatement psSetNumb;
	private PreparedStatement psIncNumb;
	private PreparedStatement psDecNumb;
	private PreparedStatement psAdd;
	private PreparedStatement psSet;
	private PreparedStatement psCanRemove;
	private PreparedStatement psRemove;

	public StatusTables(Connection c) {
		try {
			setFieldsLength(c);

			psList = c.prepareStatement("SELECT id, numb, name, color FROM Statuses");

			psListForCombo = c.prepareStatement("SELECT id, name FROM Statuses");

			psSetNumb = c.prepareStatement("UPDATE Statuses SET numb = ? WHERE id = ?");
			psIncNumb = c.prepareStatement("UPDATE Statuses SET numb = numb + 1 WHERE numb >= ?");
			psDecNumb = c.prepareStatement("UPDATE Statuses SET numb = numb - 1 WHERE numb > ?");

			psAdd = c.prepareStatement("INSERT INTO Statuses (numb, name, color) VALUES (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			psSet = c.prepareStatement("UPDATE Statuses SET numb = ?, name = ?, color = ? WHERE id = ?");

			psCanRemove = c.prepareStatement("SELECT 1 FROM Seeds WHERE status_id = ?");

			psRemove = c.prepareStatement("DELETE FROM Statuses WHERE id = ?");

		} catch (SQLException e) {
			System.out.println("StatusTables.StatusTables() - " + e);
		}
	}

	private void setFieldsLength(Connection c) throws SQLException {
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT name FROM Statuses WHERE 0=1");
		ResultSetMetaData metaData = rs.getMetaData();

		Status.setNAME_LENGTH(metaData.getPrecision(1));

		rs.close();
		s.close();
	}

	public ObservableList<Status> getList() {
		ObservableList<Status> list = FXCollections.observableArrayList();

		try {
			ResultSet rs = psList.executeQuery();
			while (rs.next())
				list.add(new Status(rs.getInt(1), rs.getInt(2), rs.getString(3).trim(), rs.getString(4)));
			rs.close();
		} catch (SQLException e) {
			System.out.println("StatusTables.getList() - " + e);
		}

		return list;
	}

	@Override
	public ObservableList<ComboItem> getListForCombo() {
		ObservableList<ComboItem> list = FXCollections.observableArrayList();
		list.add(new ComboItem(0, ""));

		try {
			ResultSet rs = psListForCombo.executeQuery();
			while (rs.next())
				list.add(new ComboItem(rs.getInt(1), rs.getString(2)));
			rs.close();
		} catch (SQLException e) {
			System.out.println("StatusTables.getListForCombo() - " + e);
		}

		return list;
	}

	public void add(Status status) {
		try {
			psIncNumb.setInt(1, status.getNumb());
			psIncNumb.executeUpdate();

			psAdd.setInt(1, status.getNumb());
			psAdd.setString(2, status.getName());
			psAdd.setString(3, status.getColor());
			psAdd.executeUpdate();

			ResultSet rs = psAdd.getGeneratedKeys();
			if (rs.next())
				status.setId(rs.getInt(1));
			rs.close();
		} catch (SQLException e) {
			System.out.println("StatusTables.add(Status status) - " + e);
		}
	}

	public void set(Status status) {
		try {
			psSet.setInt(1, status.getNumb());
			psSet.setString(2, status.getName());
			psSet.setString(3, status.getColor());
			psSet.setInt(4, status.getId());
			psSet.executeUpdate();
		} catch (SQLException e) {
			System.out.println("StatusTables.set(Status status) - " + e);
		}
	}

	public boolean canRemove(Status status) {
		boolean retValue = false;

		try {
			psCanRemove.setInt(1, status.getId());
			ResultSet rs = psCanRemove.executeQuery();
			retValue = !rs.next();
			rs.close();
		} catch (SQLException e) {
			System.out.println("StatusTables.canRemove(Status status) - " + e);
		}

		return retValue;
	}

	public boolean remove(Status status) {
		boolean removed = false;

		try {
			psRemove.setInt(1, status.getId());
			psRemove.executeUpdate();

			psDecNumb.setInt(1, status.getNumb());
			psDecNumb.executeUpdate();

			removed = true;
		} catch (SQLException e) {
			System.out.println("StatusTables.remove(Status status) - " + e);
		}

		return removed;
	}

	public void setNumb(int id1, int numb1, int id2, int numb2) {
		try {
			psSetNumb.setInt(1, numb1);
			psSetNumb.setInt(2, id1);
			psSetNumb.executeUpdate();

			psSetNumb.setInt(1, numb2);
			psSetNumb.setInt(2, id2);
			psSetNumb.executeUpdate();
		} catch (SQLException e) {
			System.out.println("StatusTables.setNumb(int id1, int numb1, int id2, int numb2) - " + e);
		}
	}
}
