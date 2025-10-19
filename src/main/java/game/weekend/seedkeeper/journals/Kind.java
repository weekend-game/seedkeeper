package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.db.Error;
import game.weekend.seedkeeper.db.Record;
import game.weekend.seedkeeper.general.Loc;

public class Kind extends Record {
	private static int NAME_LENGTH = -1;

	public static void setNAME_LENGTH(int val) {
		NAME_LENGTH = val;
	}

	private int id;
	private String name;

	public Kind() {
		this(0, "");
	}

	public Kind(int id, String name) {
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
		this.name = getNormString(name, NAME_LENGTH);
	}

	public Error check() {
		if (getName() == null || getName().trim().length() == 0)
			return new Error(Loc.get("enter_the_kind_name"), 2);

		return null;
	}

	@Override
	public boolean hasDifference(Object o) {
		StringBuilder sb = new StringBuilder();
		setDifferences("");

		if (this == o)
			return false;

		Kind other = (Kind) o;

		boolean result = checkDifference(Loc.get("of_the_name_is"), getName(), other.getName(), sb);

		if (result)
			setDifferences(sb.toString());

		return result;
	}
}
