package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.db.Error;
import game.weekend.seedkeeper.db.Record;
import game.weekend.seedkeeper.general.Loc;

public class Color extends Record {
	private static int NAME_LENGITH = 64;

	private int id;
	private String name;

	public Color() {
		this(0, "");
	}

	public Color(int id, String name) {
		setId(id);
		setName(name);
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
		this.name = getNormString(name, NAME_LENGITH);
	}

	public Error check() {
		if (getName() == null || getName().trim().length() == 0)
			return new Error(Loc.get("enter_the_color_name"), 2);

		return null;
	}

	@Override
	public boolean hasDifference(Object o) {
		StringBuilder sb = new StringBuilder();
		setDifferences("");

		if (this == o)
			return false;

		Color other = (Color) o;

		boolean result = checkDifference(Loc.get("of_the_name_is"), name, other.name, sb);

		if (result)
			setDifferences(sb.toString());

		return result;
	}
}
