package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.db.Error;
import game.weekend.seedkeeper.db.Record;
import game.weekend.seedkeeper.general.Loc;

public class Status extends Record {
	private static int NAME_LENGTH = -1;

	public static void setNAME_LENGTH(int val) {
		NAME_LENGTH = val;
	}

	private int id;
	private int numb;
	private String name;
	private String color;

	public Status() {
		this(0, 1, "", "");
	}

	public Status(int id, int numb, String name, String color) {
		setId(id);
		setNumb(numb);
		setName(name);
		setColor(color);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumb() {
		return numb;
	}

	public void setNumb(int numb) {
		this.numb = numb;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = getNormString(name, NAME_LENGTH);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		if (color == null || color.trim().length() != 6)
			color = "ffffff";
		this.color = color.trim();
	}

	public Error check() {
		if (getName() == null || getName().trim().length() == 0)
			return new Error(Loc.get("enter_the_status_name"), 2);

		return null;
	}

	@Override
	public boolean hasDifference(Object o) {
		StringBuilder sb = new StringBuilder();
		setDifferences("");

		if (this == o)
			return false;

		Status other = (Status) o;

		boolean result;
		result = checkDifference(Loc.get("of_the_name_is"), getName(), other.getName(), sb);
		result |= checkDifference(Loc.get("of_the_color_is"), getColor(), other.getColor(), sb);

		if (result)
			setDifferences(sb.toString());

		return result;
	}
}
