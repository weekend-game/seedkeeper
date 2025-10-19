package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.general.Dialogues;
import game.weekend.seedkeeper.general.IReadOnly;
import game.weekend.seedkeeper.general.Journal;
import game.weekend.seedkeeper.general.Loc;
import game.weekend.seedkeeper.general.Proper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@SuppressWarnings("rawtypes")
public class SetupJournal extends Journal implements IReadOnly {

	CategoryJournal categoriesJournal = new CategoryJournal(this);
	StatusJournal statusesJournal = new StatusJournal(this);
	ColorJournal colorsJournal = new ColorJournal(this);
	KindJournal kindsJournal = new KindJournal(this);

	// Button
	private final Button btnOk = getButtonOk();
	private final Button btnCancel = getButtonCancel();

	// Interface language switcher
	ToggleGroup btgLan = new ToggleGroup();
	RadioButton enButton = new RadioButton("EN");
	RadioButton ruButton = new RadioButton("RU");

	public SetupJournal() {

		enButton.setToggleGroup(btgLan);
		enButton.setSelected(Loc.getLanguage().equalsIgnoreCase("EN"));
		enButton.setToggleGroup(btgLan);
		enButton.setSelected(Loc.getLanguage().equalsIgnoreCase("EN"));
		enButton.setOnAction(event -> {
			String prevLanguage = Proper.getProperty("Language", "EN");
			Proper.setProperty("Language", "EN");
			if (!prevLanguage.equalsIgnoreCase("EN"))
				Dialogues.ErrMes(Loc.get("restart_the_application"));
		});

		ruButton.setToggleGroup(btgLan);
		ruButton.setSelected(Loc.getLanguage().equalsIgnoreCase("RU"));
		ruButton.setOnAction(event -> {
			String prevLanguage = Proper.getProperty("Language", "RU");
			Proper.setProperty("Language", "RU");
			if (!prevLanguage.equalsIgnoreCase("RU"))
				Dialogues.ErrMes(Loc.get("restart_the_application"));
		});

		categoriesJournal.addReadOnlyObject(statusesJournal);
		categoriesJournal.addReadOnlyObject(colorsJournal);
		categoriesJournal.addReadOnlyObject(kindsJournal);
		categoriesJournal.addReadOnlyObject(this);

		statusesJournal.addReadOnlyObject(categoriesJournal);
		statusesJournal.addReadOnlyObject(colorsJournal);
		statusesJournal.addReadOnlyObject(kindsJournal);
		statusesJournal.addReadOnlyObject(this);

		colorsJournal.addReadOnlyObject(categoriesJournal);
		colorsJournal.addReadOnlyObject(statusesJournal);
		colorsJournal.addReadOnlyObject(kindsJournal);
		colorsJournal.addReadOnlyObject(this);

		kindsJournal.addReadOnlyObject(categoriesJournal);
		kindsJournal.addReadOnlyObject(statusesJournal);
		kindsJournal.addReadOnlyObject(colorsJournal);
		kindsJournal.addReadOnlyObject(this);
	}

	@Override
	public VBox getPane() {
		VBox vb = super.getPane();

		HBox hb1 = new HBox();
		hb1.getChildren().addAll(categoriesJournal.getPane(), getHSpacer(), colorsJournal.getPane());

		HBox hb2 = new HBox();
		hb2.getChildren().addAll(statusesJournal.getPane(), getHSpacer(), kindsJournal.getPane());

		vb.getChildren().addAll(hb1, hb2, getVSpacer(), makeSaveButtons());
		return vb;
	}

	// Save buttons and language switcher
	private HBox makeSaveButtons() {
		HBox hb = new HBox();
		hb.setSpacing(10);
		hb.setPadding(new Insets(5, 10, 0, 10));
		hb.getChildren().addAll(enButton, ruButton, getHSpacer(), btnOk, btnCancel);
		return hb;
	}

	@Override
	public boolean isEditMode() {
		return statusesJournal.isEditMode() || colorsJournal.isEditMode() || kindsJournal.isEditMode();
	}

	@Override
	public void setReadOnlyMode(boolean readOnly) {
		super.setReadOnlyMode(readOnly);
		btnOk.setDisable(!readOnly);
		btnCancel.setDisable(!readOnly);
	}

	@Override
	public void activate() {
		btnCancel.setCancelButton(true);

		categoriesJournal.activate();
		statusesJournal.activate();
		colorsJournal.activate();
		kindsJournal.activate();

		// I return focus to the active field, otherwise it will remain on the Tab header
		if (getCurrentNode() != null)
			Platform.runLater(() -> getCurrentNode().requestFocus());
	}

	@Override
	public void deactivate() {
		btnCancel.setCancelButton(false);
	}

	@Override
	public void enter() {
		if (categoriesJournal.isEditMode())
			categoriesJournal.enter();
		if (statusesJournal.isEditMode())
			statusesJournal.enter();
		if (colorsJournal.isEditMode())
			colorsJournal.enter();
		if (kindsJournal.isEditMode())
			kindsJournal.enter();
	}

	@Override
	public void doOk() {
		if (categoriesJournal.isEditMode())
			categoriesJournal.doOk();
		if (statusesJournal.isEditMode())
			statusesJournal.doOk();
		if (colorsJournal.isEditMode())
			colorsJournal.doOk();
		if (kindsJournal.isEditMode())
			kindsJournal.doOk();
	}

	@Override
	public void doCancel(boolean checkChange) {
		if (categoriesJournal.isEditMode())
			categoriesJournal.doCancel(checkChange);
		if (statusesJournal.isEditMode())
			statusesJournal.doCancel(checkChange);
		if (colorsJournal.isEditMode())
			colorsJournal.doCancel(checkChange);
		if (kindsJournal.isEditMode())
			kindsJournal.doCancel(checkChange);
	}
}
