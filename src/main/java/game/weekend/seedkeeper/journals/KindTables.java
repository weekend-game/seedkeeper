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

	private PreparedStatement psList;
	private PreparedStatement psListForCombo;
	private PreparedStatement psAdd;
	private PreparedStatement psSet;
	private PreparedStatement psCanRemove;
	private PreparedStatement psRemove;

	public KindTables(Connection c) {
		try {
			setFieldsLength(c);

			psList = c.prepareStatement("SELECT id, name FROM Kinds");

			psListForCombo = c.prepareStatement("SELECT id, name FROM Kinds ORDER BY UPPER(name)");

			psAdd = c.prepareStatement("INSERT INTO Kinds (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);

			psSet = c.prepareStatement("UPDATE Kinds SET name = ? WHERE id = ?");

			psCanRemove = c.prepareStatement("SELECT 1 FROM Seeds WHERE kind_id = ?");

			psRemove = c.prepareStatement("DELETE FROM Kinds WHERE id =  ?");

		} catch (SQLException e) {
			System.out.println("KindTables.KindTables() - " + e);
		}
	}

	private void setFieldsLength(Connection c) throws SQLException {
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT name FROM Kinds WHERE 0=1");
		ResultSetMetaData metaData = rs.getMetaData();

		Kind.setNAME_LENGTH(metaData.getPrecision(1));

		rs.close();
		s.close();
	}

	public ObservableList<Kind> getList() {
		ObservableList<Kind> list = FXCollections.observableArrayList();

		try {
			ResultSet rs = psList.executeQuery();
			while (rs.next())
				list.add(new Kind(rs.getInt(1), rs.getString(2)));
			rs.close();
		} catch (SQLException e) {
			System.out.println("KindTables.getList() - " + e);
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
			System.out.println("KindTables.getListForCombo() - " + e);
		}

		return list;
	}

	public void add(Kind kind) {
		try {
			psAdd.setString(1, kind.getName());
			psAdd.executeUpdate();

			ResultSet rs = psAdd.getGeneratedKeys();
			if (rs.next())
				kind.setId(rs.getInt(1));
			rs.close();
		} catch (SQLException e) {
			System.out.println("KindTables.add(Kind kind) - " + e);
		}
	}

	public void set(Kind kind) {
		try {
			psSet.setString(1, kind.getName());
			psSet.setInt(2, kind.getId());
			psSet.executeUpdate();
		} catch (SQLException e) {
			System.out.println("KindTables.set(Kind kind) - " + e);
		}
	}

	public boolean canRemove(Kind kind) {
		boolean retValue = false;

		try {
			psCanRemove.setInt(1, kind.getId());
			ResultSet rs = psCanRemove.executeQuery();
			retValue = !rs.next();
			rs.close();
		} catch (SQLException e) {
			System.out.println("KindTables.canRemove(Kind kind) - " + e);
		}

		return retValue;
	}

	public boolean remove(Kind kind) {
		boolean removed = false;

		try {
			psRemove.setInt(1, kind.getId());
			psRemove.executeUpdate();
			removed = true;
		} catch (SQLException e) {
			System.out.println("KindTables.remove(Kind kind) - " + e);
		}

		return removed;
	}
}
