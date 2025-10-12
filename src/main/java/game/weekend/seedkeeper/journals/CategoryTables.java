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
 * Categories
 */
public class CategoryTables extends DBTables {

	public ObservableList<Category> getList() {
		ObservableList<Category> list = FXCollections.observableArrayList();

		Connection c = getConnection();
		if (c == null)
			return list;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, numb, name FROM Categories");
			while (rs.next())
				list.add(new Category(rs.getInt(1), rs.getInt(2), rs.getString(3).trim()));
		} catch (SQLException e) {
			System.out.println("CategoryTables.getList() - " + e);
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

			ResultSet rs = s.executeQuery("SELECT id, name FROM Categories");
			while (rs.next())
				list.add(new ComboItem(rs.getInt(1), rs.getString(2)));
		} catch (SQLException e) {
			System.out.println("CategoryTables.getListForCombo() - " + e);
		}

		return list;
	}

	public Category get(int id) {
		Category category = new Category();

		Connection c = getConnection();
		if (c == null || id == 0)
			return category;

		try (Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery("SELECT id, numb, name FROM Categories WHERE id = " + id);
			if (rs.next())
				category = new Category(rs.getInt(1), rs.getInt(2), rs.getString(3));
		} catch (SQLException e) {
			System.out.println("CategoryTables.get(int id) - " + e);
		}

		return category;
	}

	public void add(Category category) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps;

			ps = c.prepareStatement("UPDATE Categories SET numb = numb + 1 WHERE numb >= ?");
			ps.setInt(1, category.getNumb());
			ps.executeUpdate();

			ps = c.prepareStatement("INSERT INTO Categories (numb, name) VALUES (?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, category.getNumb());
			ps.setString(2, category.getName());
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				category.setId(rs.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("CategoryTables.add(Category category) - " + e);
		}
	}

	public void set(Category category) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps = c.prepareStatement("UPDATE Categories SET numb = ?, name = ? WHERE id = ?");
			ps.setInt(1, category.getNumb());
			ps.setString(2, category.getName());
			ps.setInt(3, category.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("CategoryTables.set(Category category) - " + e);
		}
	}

	public boolean canRemove(Category category) {
		Connection c = getConnection();
		if (c == null)
			return false;

		return true;
	}

	public boolean remove(Category category) {
		boolean deleted = false;

		Connection c = getConnection();
		if (c == null)
			return deleted;

		try (Statement s = c.createStatement()) {
			s.executeUpdate("DELETE FROM Categories WHERE id = " + category.getId());
			deleted = true;

			s.executeUpdate("UPDATE Categories SET numb = numb - 1 WHERE numb > " + category.getNumb());
		} catch (SQLException e) {
			System.out.println("CategoryTables.remove(Category category) - " + e);
		}

		return deleted;
	}

	public void setNumb(int id1, int numb1, int id2, int numb2) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps;

			ps = c.prepareStatement("UPDATE Categories SET numb = ? WHERE id = ?");
			ps.setInt(1, numb1);
			ps.setInt(2, id1);
			ps.executeUpdate();

			ps = c.prepareStatement("UPDATE Categories SET numb = ? WHERE id = ?");
			ps.setInt(1, numb2);
			ps.setInt(2, id2);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("CategoryTables.setNumb(int id1, int numb1, int id2, int numb2) - " + e);
		}
	}
}
