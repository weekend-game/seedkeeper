package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.db.Error;
import game.weekend.seedkeeper.db.Record;
import game.weekend.seedkeeper.general.Loc;

public class Brand extends Record {

	private static int NAME_LENGTH = -1;
	private static int DESCR_LENGTH = -1;
	private static int LINK_LENGTH = -1;

	public static void setNAME_LENGTH(int val) {
		NAME_LENGTH = val;
	}

	public static void setDESCR_LENGTH(int val) {
		DESCR_LENGTH = val;
	}

	public static void setLINK_LENGTH(int val) {
		LINK_LENGTH = val;
	}

	private int id;
	private String name;
	private String descr;
	private String link;

	public Brand() {
		this(0, "", "", "");
	}

	public Brand(int id, String name, String descr, String link) {
		setId(id);
		setName(name);
		setDescr(descr);
		setLink(link);
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

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = getNormString(descr, DESCR_LENGTH);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = getNormString(link, LINK_LENGTH);
	}

	public Error check() {
		if (getName() == null || getName().trim().length() == 0)
			return new Error(Loc.get("enter_the_brand_name"), 2);

		return null;
	}

	@Override
	public boolean hasDifference(Object o) {
		StringBuilder sb = new StringBuilder();
		setDifferences("");

		if (this == o)
			return false;

		Brand other = (Brand) o;

		boolean result = false;
		result |= checkDifference(Loc.get("of_the_name_is"), getName(), other.getName(), sb);
		result |= checkDifference(Loc.get("of_the_descr_is"), getDescr(), other.getDescr(), sb);
		result |= checkDifference(Loc.get("of_the_link_is"), getLink(), other.getLink(), sb);

		if (result)
			setDifferences(sb.toString());

		return result;
	}
}
