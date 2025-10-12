package game.weekend.seedkeeper.db;

import game.weekend.seedkeeper.general.Loc;

public class Record {

	private String differences = "";

	public String getDifferences() {
		String result = this.differences;
		this.differences = "";
		return result;
	}

	protected void setDifferences(String differences) {
		this.differences = differences;
	}

	protected boolean checkDifference(String fieldName, Object oldValue, Object newValue, StringBuilder sb) {
		oldValue = oldValue == null ? "" : "" + oldValue;
		newValue = newValue == null ? "" : "" + newValue;

		if (!oldValue.equals(newValue)) {
			if (sb != null)
				sb.append(Loc.get("old_value") + " " + fieldName + " \"" + oldValue + "\", " + Loc.get("and_new_value")
						+ " \"" + newValue + "\".\n");

			return true;
		}
		return false;
	}

	public boolean hasDifference(Object o) {
		return true;
	}

	public String getNormString(String string, int length) {

		if (string != null)
			string = string.trim();

		if (string.length() > length)
			string = string.substring(0, length);

		return string;
	}
}
