package game.weekend.seedkeeper;

import game.weekend.seedkeeper.db.DB;
import game.weekend.seedkeeper.general.Journal;
import game.weekend.seedkeeper.general.Loc;
import game.weekend.seedkeeper.general.Proper;
import game.weekend.seedkeeper.journals.BrandJournal;
import game.weekend.seedkeeper.journals.SeedJournal;
import game.weekend.seedkeeper.journals.SetupJournal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A typical JavaFX application.
 * <p>
 * The scene is created using the getScene() method and represents a Tab Panel,
 * where each tab contains a journal entry. Pressing ENTER is intercepted, and
 * the enter() method of the active tab's journal entry is called. The scene
 * saves and restores its location on the desktop using the writeProp() and
 * readProp() methods.
 * </p>
 */
public class SeedKeeper extends Application {

	/** Application name */
	public static final String APP_NAME = "SeedKeeper";

	/** Version */
	public static final String APP_VERSION = "00.03";

	/** Date */
	public static final String APP_DATE = "18.10.2025";

	private static Stage stage = null;
	private static TabPane tabPane = null;

	public static void main(String[] args) {
		launch(args);
	}

	public static Stage getStage() {
		return stage;
	}

	public static String getTitle() {
		return APP_NAME + " (" + Loc.get("version") + ": " + APP_VERSION + " " + Loc.get("from") + " " + APP_DATE + ")";
	}

	@Override
	public void start(Stage stage) {
		SeedKeeper.stage = stage;
		SeedKeeper.tabPane = new TabPane();

		// Preferences keeper between application sessions
		Proper.read(APP_NAME);
		readProp();

		// Interface language
		Loc.setLanguage(Proper.getProperty("Language", "EN"));

		stage.setWidth(stage.getWidth() - 1); // (1) Otherwise, the scene does not correspond in size to the stage

		VBox vb = new VBox();
		stage.setScene(new Scene(vb));

		// The application may take a long time to start, so I display a message in the
		// window title.
		stage.setTitle(getTitle() + " " + Loc.get("starting_the_program") + "...");
		stage.show();

		stage.setScene(getScene());

		// I'm dropping the message in the title
		stage.setTitle(getTitle());
		stage.show();

		stage.setWidth(stage.getWidth() + 1); // (2) Otherwise, the scene does not correspond in size to the stage
	}

	@Override
	public void stop() {
		writeProp();

		for (Tab t : tabPane.getTabs())
			((SeedTab) t).journal.writeProp();

		Proper.save();
	}

	private Scene getScene() {
		tabPane.getTabs().add(new SeedTab(new SeedJournal(), Loc.get("seed")));
		tabPane.getTabs().add(new SeedTab(new BrandJournal(), Loc.get("brands")));
		tabPane.getTabs().add(new SeedTab(new SetupJournal(), Loc.get("setup")));

		// Event in the log: Pressing ENTER
		tabPane.setOnKeyPressed(ke -> {
			String key = ke.getCode().toString();
			if (key.equalsIgnoreCase("ENTER"))
				for (Tab t : tabPane.getTabs())
					if (t.isSelected())
						((SeedTab) t).journal.enter();
		});

		return new Scene(tabPane);
	}

	private void readProp() {
		stage.setX(Proper.getProperty("stage.X", 50));
		stage.setY(Proper.getProperty("stage.Y", 50));
		stage.setWidth(Proper.getProperty("stage.Width", 700));
		stage.setHeight(Proper.getProperty("stage.Height", 500));

		DB.setURL(Proper.getProperty("url", DB.getUrl()));
	}

	private void writeProp() {
		Proper.setProperty("stage.X", (int) stage.getX());
		Proper.setProperty("stage.Y", (int) stage.getY());
		Proper.setProperty("stage.Width", (int) stage.getWidth());
		Proper.setProperty("stage.Height", (int) stage.getHeight());

		Proper.setProperty("url", DB.getUrl());
	}

	static class SeedTab extends Tab {
		public final Journal<?> journal;

		// Events in the log: Activate and Deactivate
		public SeedTab(Journal<?> journal, String name) {
			super(name, journal.getPane());
			this.journal = journal;
			setClosable(false);
			setOnSelectionChanged(event -> {
				if (isSelected())
					journal.activate();
				else
					journal.deactivate();
			});
		}
	}
}
