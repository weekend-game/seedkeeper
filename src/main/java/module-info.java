module SeedKeeper {
	requires javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.graphics;
	requires transitive java.sql;
	requires org.apache.derby.engine;
	requires javafx.swing;

	opens game.weekend.seedkeeper to javafx.graphics, javafx.fxml;

	exports game.weekend.seedkeeper;
	exports game.weekend.seedkeeper.controls;
	exports game.weekend.seedkeeper.db;
	exports game.weekend.seedkeeper.general;
	exports game.weekend.seedkeeper.journals;
}
