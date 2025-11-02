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
 * Categories
 */
public class CategoryTables extends DBTables {

	private PreparedStatement psList;
	private PreparedStatement psListForCombo;
	private PreparedStatement psSetNumb;
	private PreparedStatement psIncNumb;
	private PreparedStatement psDecNumb;
	private PreparedStatement psAdd;
	private PreparedStatement psSet;
	private PreparedStatement psCanRemove;
	private PreparedStatement psRemove;

	public CategoryTables(Connection c) {
		try {
			setFieldsLength(c);

			psList = c.prepareStatement("SELECT id, numb, name FROM Categories");

			psListForCombo = c.prepareStatement("SELECT id, name FROM Categories ORDER BY numb");

			psSetNumb = c.prepareStatement("UPDATE Categories SET numb = ? WHERE id = ?");
			psIncNumb = c.prepareStatement("UPDATE Categories SET numb = numb + 1 WHERE numb >= ?");
			psDecNumb = c.prepareStatement("UPDATE Categories SET numb = numb - 1 WHERE numb > ?");

			psAdd = c.prepareStatement("INSERT INTO Categories (numb, name) VALUES (?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			psSet = c.prepareStatement("UPDATE Categories SET numb = ?, name = ? WHERE id = ?");

			psCanRemove = c.prepareStatement("SELECT 1 FROM Seeds WHERE category_id = ?");

			psRemove = c.prepareStatement("DELETE FROM Categories WHERE id = ?");

		} catch (SQLException e) {
			System.out.println("CategoryTables.CategoryTables() - " + e);
		}
	}

	private void setFieldsLength(Connection c) throws SQLException {
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT name FROM Categories WHERE 0=1");
		ResultSetMetaData metaData = rs.getMetaData();

		Category.setNAME_LENGTH(metaData.getPrecision(1));

		rs.close();
		s.close();
	}

	public ObservableList<Category> getList() {
		ObservableList<Category> list = FXCollections.observableArrayList();

		try {
			ResultSet rs = psList.executeQuery();
			while (rs.next())
				list.add(new Category(rs.getInt(1), rs.getInt(2), rs.getString(3).trim()));
			rs.close();
		} catch (SQLException e) {
			System.out.println("CategoryTables.getList() - " + e);
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
			System.out.println("CategoryTables.getListForCombo() - " + e);
		}

		return list;
	}

	public void add(Category category) {
		try {
			psIncNumb.setInt(1, category.getNumb());
			psIncNumb.executeUpdate();

			psAdd.setInt(1, category.getNumb());
			psAdd.setString(2, category.getName());
			psAdd.executeUpdate();

			ResultSet rs = psAdd.getGeneratedKeys();
			if (rs.next())
				category.setId(rs.getInt(1));
			rs.close();
		} catch (SQLException e) {
			System.out.println("CategoryTables.add(Category category) - " + e);
		}
	}

	public void set(Category category) {
		try {
			psSet.setInt(1, category.getNumb());
			psSet.setString(2, category.getName());
			psSet.setInt(3, category.getId());
			psSet.executeUpdate();
		} catch (SQLException e) {
			System.out.println("CategoryTables.set(Category category) - " + e);
		}
	}

	public boolean canRemove(Category category) {
		boolean retValue = false;

		try {
			psCanRemove.setInt(1, category.getId());
			ResultSet rs = psCanRemove.executeQuery();
			retValue = !rs.next();
			rs.close();
		} catch (SQLException e) {
			System.out.println("CategoryTables.canRemove(Category category) - " + e);
		}

		return retValue;
	}

	public boolean remove(Category category) {
		boolean removed = false;

		try {
			psRemove.setInt(1, category.getId());
			psRemove.executeUpdate();

			psDecNumb.setInt(1, category.getNumb());
			psDecNumb.executeUpdate();

			removed = true;
		} catch (SQLException e) {
			System.out.println("CategoryTables.remove(Category category) - " + e);
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
			System.out.println("CategoryTables.setNumb(int id1, int numb1, int id2, int numb2) - " + e);
		}
	}
}
