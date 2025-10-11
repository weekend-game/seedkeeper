package game.weekend.seedkeeper.db;

import javafx.scene.control.ComboBox;

public class ComboItem {
	private int id;
	private String name;

	public ComboItem(int id, String name) {
		setId(id);
		setName(name);
	}

	public static void setValue(int id, ComboBox<ComboItem> combo) {
		for (ComboItem o : combo.getItems())
			if (o.getId() == id)
				combo.setValue(o);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}
}
