package game.weekend.seedkeeper.db;

public class Error {
	public final String mes;
	public final int fieldNum;

	public Error(String mes, int fieldNum) {
		this.mes = mes;
		this.fieldNum = fieldNum;
	}
}
