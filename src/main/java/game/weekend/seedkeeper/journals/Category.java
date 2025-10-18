package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.db.Error;
import game.weekend.seedkeeper.db.Record;
import game.weekend.seedkeeper.general.Loc;

public class Category extends Record {
	private static int NAME_LENGITH = 64;

	private int id;
	private int numb;
	private String name;

	public Category() {
		this(0, 1, "");
	}

	public Category(int id, int numb, String name) {
		setId(id);
		setNumb(numb);
		setName(name);
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
		this.name = getNormString(name, NAME_LENGITH);
	}

	public Error check() {
		if (getName() == null || getName().trim().length() == 0)
			return new Error(Loc.get("enter_the_category_name") + ".", 2);

		return null;
	}

	@Override
	public boolean hasDifference(Object o) {
		StringBuilder sb = new StringBuilder();
		setDifferences("");

		if (this == o)
			return false;

		Category other = (Category) o;

		boolean result;
		result = checkDifference(Loc.get("of_the_name_is"), getName(), other.getName(), sb);

		if (result)
			setDifferences(sb.toString());

		return result;
	}
}
