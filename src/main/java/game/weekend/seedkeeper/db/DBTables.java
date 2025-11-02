package game.weekend.seedkeeper.db;

import java.sql.Connection;

import javafx.collections.ObservableList;

public abstract class DBTables {

	private boolean edited = false;

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	public DB getDB() {
		return DB.getInstance();
	}

	abstract public ObservableList<ComboItem> getListForCombo();

	protected Connection getConnection() {
		return getDB().getConnection();
	}

}
