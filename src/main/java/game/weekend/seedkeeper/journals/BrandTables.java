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
 * Brands
 */
public class BrandTables extends DBTables {

	private PreparedStatement psList;
	private PreparedStatement psListForCombo;
	private PreparedStatement psAdd;
	private PreparedStatement psSet;
	private PreparedStatement psCanRemove;
	private PreparedStatement psRemove;

	public BrandTables(Connection c) {
		try {
			setFieldsLength(c);

			psList = c.prepareStatement("SELECT id, name, descr, link FROM Brands");

			psListForCombo = c.prepareStatement("SELECT id, name FROM Brands ORDER BY UPPER(name)");

			psAdd = c.prepareStatement("INSERT INTO Brands (name, descr, link) VALUES (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			psSet = c.prepareStatement("UPDATE Brands SET name = ?, descr = ?, link = ? WHERE id = ?");

			psCanRemove = c.prepareStatement("SELECT 1 FROM Seeds WHERE brand_id = ?");

			psRemove = c.prepareStatement("DELETE FROM Brands WHERE id = ?");

		} catch (SQLException e) {
			System.out.println("BrandTables.BrandTables() - " + e);
		}
	}

	private void setFieldsLength(Connection c) throws SQLException {
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT name, descr, link FROM Brands WHERE 0=1");
		ResultSetMetaData metaData = rs.getMetaData();

		Brand.setNAME_LENGTH(metaData.getPrecision(1));
		Brand.setDESCR_LENGTH(metaData.getPrecision(2));
		Brand.setLINK_LENGTH(metaData.getPrecision(3));

		rs.close();
		s.close();
	}

	public ObservableList<Brand> getList() {
		ObservableList<Brand> list = FXCollections.observableArrayList();

		try {
			ResultSet rs = psList.executeQuery();
			while (rs.next())
				list.add(new Brand(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			rs.close();
		} catch (SQLException e) {
			System.out.println("BrandTables.getList() - " + e);
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
			System.out.println("BrandTables.getListForCombo() - " + e);
		}

		return list;
	}

	public void add(Brand brand) {
		try {
			psAdd.setString(1, brand.getName());
			psAdd.setString(2, brand.getDescr());
			psAdd.setString(3, brand.getLink());
			psAdd.executeUpdate();

			ResultSet rs = psAdd.getGeneratedKeys();
			if (rs.next())
				brand.setId(rs.getInt(1));
			rs.close();
		} catch (SQLException e) {
			System.out.println("BrandTables.add(Brand brand) - " + e);
		}
	}

	public void set(Brand brand) {
		try {
			psSet.setString(1, brand.getName());
			psSet.setString(2, brand.getDescr());
			psSet.setString(3, brand.getLink());
			psSet.setInt(4, brand.getId());
			psSet.executeUpdate();
		} catch (SQLException e) {
			System.out.println("BrandTables.set(Brand brand) - " + e);
		}
	}

	public boolean canRemove(Brand brand) {
		boolean retValue = false;

		try {
			psCanRemove.setInt(1, brand.getId());
			ResultSet rs = psCanRemove.executeQuery();
			retValue = !rs.next();
			rs.close();
		} catch (SQLException e) {
			System.out.println("BrandTables.canRemove(Brand brand) - " + e);
		}

		return retValue;
	}

	public boolean remove(Brand brand) {
		boolean removed = false;

		try {
			psRemove.setInt(1, brand.getId());
			psRemove.executeUpdate();
			removed = true;
		} catch (SQLException e) {
			System.out.println("BrandTables.remove(Brand brand) - " + e);
		}

		return removed;
	}
}
