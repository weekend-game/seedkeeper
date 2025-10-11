package game.weekend.seedkeeper.journals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

	public ObservableList<Brand> getList() {
		ObservableList<Brand> list = FXCollections.observableArrayList();

		Connection c = getConnection();
		if (c == null)
			return list;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, name, descr, link FROM Brands");
			while (rs.next())
				list.add(new Brand(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
		} catch (SQLException e) {
			System.out.println("BrandTables.getList() - " + e);
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

			ResultSet rs = s.executeQuery("SELECT id, name FROM Brands ORDER BY UPPER(name)");
			while (rs.next())
				list.add(new ComboItem(rs.getInt(1), rs.getString(2)));
		} catch (SQLException e) {
			System.out.println("BrandTables.getListForCombo() - " + e);
		}

		return list;
	}

	public Brand get(int id) {
		Brand brand = new Brand();

		Connection c = getConnection();
		if (c == null || id == 0)
			return brand;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, name, descr, link FROM Brands WHERE id = " + id);
			if (rs.next())
				brand = new Brand(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
		} catch (SQLException e) {
			System.out.println("BrandTables.get(int id) - " + e);
		}

		return brand;
	}

	public void add(Brand brand) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps = c.prepareStatement("INSERT INTO Brands (" + " name, descr, link ) VALUES (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, brand.getName());
			ps.setString(2, brand.getDescr());
			ps.setString(3, brand.getLink());
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				brand.setId(rs.getInt(1));
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println("BrandTables.add(Brand brand) - " + e);
		}
	}

	public void set(Brand brand) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps = c
					.prepareStatement("UPDATE Brands SET " + "  name = ?, descr = ?, link = ? WHERE id = ?");
			ps.setString(1, brand.getName());
			ps.setString(2, brand.getDescr());
			ps.setString(3, brand.getLink());
			ps.setInt(4, brand.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("BrandTables.set(Brand brand) - " + e);
		}
	}

	public boolean canRemove(Brand brand) {
		Connection c = getConnection();
		if (c == null)
			return false;

		return true;
	}

	public boolean remove(Brand brand) {
		boolean deleted = false;

		Connection c = getConnection();
		if (c == null)
			return deleted;

		try (Statement s = c.createStatement()) {
			s.executeUpdate("DELETE FROM Brands WHERE id = " + brand.getId());
			deleted = true;
		} catch (SQLException e) {
			System.out.println("BrandTables.remove(Brand brand) - " + e);
		}

		return deleted;
	}
}
