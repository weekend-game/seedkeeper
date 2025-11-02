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

	private PreparedStatement psList;
	private PreparedStatement psListForCombo;
	private PreparedStatement psAdd;
	private PreparedStatement psSet;
	private PreparedStatement psCanRemove;
	private PreparedStatement psRemove;

	public ColorTables(Connection c) {
		try {
			setFieldsLength(c);

			psList = c.prepareStatement("SELECT id, name FROM Colors");

			psListForCombo = c.prepareStatement("SELECT id, name FROM Colors ORDER BY UPPER(name)");

			psAdd = c.prepareStatement("INSERT INTO Colors (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);

			psSet = c.prepareStatement("UPDATE Colors SET name = ? WHERE id = ?");

			psCanRemove = c.prepareStatement("SELECT 1 FROM Seeds WHERE color_id = ?");

			psRemove = c.prepareStatement("DELETE FROM Colors WHERE id = ?");

		} catch (SQLException e) {
			System.out.println("ColorTables.ColorTables() - " + e);
		}
	}

	private void setFieldsLength(Connection c) throws SQLException {
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT name FROM Colors WHERE 0=1");
		ResultSetMetaData metaData = rs.getMetaData();

		Color.setNAME_LENGTH(metaData.getPrecision(1));

		rs.close();
		s.close();
	}

	public ObservableList<Color> getList() {
		ObservableList<Color> list = FXCollections.observableArrayList();

		try {
			ResultSet rs = psList.executeQuery();
			while (rs.next())
				list.add(new Color(rs.getInt(1), rs.getString(2)));
			rs.close();
		} catch (SQLException e) {
			System.out.println("ColorTables.getList() - " + e);
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
			System.out.println("ColorTables.getListForCombo() - " + e);
		}

		return list;
	}

	public void add(Color color) {
		try {
			psAdd.setString(1, color.getName());
			psAdd.executeUpdate();

			ResultSet rs = psAdd.getGeneratedKeys();
			if (rs.next())
				color.setId(rs.getInt(1));
			rs.close();
		} catch (SQLException e) {
			System.out.println("ColorTables.add(Color color) - " + e);
		}
	}

	public void set(Color color) {
		try {
			psSet.setString(1, color.getName());
			psSet.setInt(2, color.getId());
			psSet.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ColorTables.set(Color color) - " + e);
		}
	}

	public boolean canRemove(Color color) {
		boolean retValue = false;

		try {
			psCanRemove.setInt(1, color.getId());
			ResultSet rs = psCanRemove.executeQuery();
			retValue = !rs.next();
			rs.close();
		} catch (SQLException e) {
			System.out.println("ColorTables.canRemove(Color color) - " + e);
		}

		return retValue;
	}

	public boolean remove(Color color) {
		boolean removed = false;

		try {
			psRemove.setInt(1, color.getId());
			psRemove.executeUpdate();
			removed = true;
		} catch (SQLException e) {
			System.out.println("ColorTables.remove(Color color) - " + e);
		}

		return removed;
	}
}
