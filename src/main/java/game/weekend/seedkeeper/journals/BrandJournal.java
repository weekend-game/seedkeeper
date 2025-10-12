package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.controls.StatusBar;
import game.weekend.seedkeeper.controls.WGTextField;
import game.weekend.seedkeeper.db.Error;
import game.weekend.seedkeeper.general.Dialogues;
import game.weekend.seedkeeper.general.Journal;
import game.weekend.seedkeeper.general.Loc;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A journal sorted by any column. When editing, the entry remains in its
 * original position, regardless of the current sorting. When adding, the entry
 * is added to the end of the journal.
 */
public class BrandJournal extends Journal<Brand> {

	private final WGTextField txtName = new WGTextField(this);
	private final WGTextField txtDescr = new WGTextField(this);
	private final WGTextField txtLink = new WGTextField(this);

	private final Button btnNew = getButtonNew();
	private final Button btnNewCopy = getButtonNewCopy();
	private final Button btnEdit = getButtonEdit();
	private final Button btnDelete = getButtonDelete();
	private final Button btnOk = getButtonOk();
	private final Button btnCancel = getButtonCancel();

	private StatusBar statusBar;

	private final Label lblMode = new Label("");

	@Override
	public VBox getPane() {
		VBox vb = super.getPane();

		vb.getChildren().addAll(makeTableView(), makeTextFields(), makeControls());

		// Control objects are switched to viewing mode
		setEditMode(false);
		// The current line is the first line in the list
		getTableView().getSelectionModel().selectFirst();

		return vb;
	}

	// Components of the journal //

	// Screen table
	private TableView<Brand> makeTableView() {
		// Table Columns

		// This column will be sorted by default
		TableColumn<Brand, String> colName = getTextColumn(Loc.get("name"), "name", 200);
		getTableView().getColumns().add(colName);

		getTableView().getColumns().add(getTextColumn(Loc.get("description"), "descr", 400));
		getTableView().getColumns().add(getLinkColumn(Loc.get("link"), "link", 800));

		// Data for the screen table.
		getTableView().setItems(getDB().brand.getList());

		// Useful key and click interceptions
		makeTableHandlers(getTableView());

		// Sorting by column by default
		getTableView().getSortOrder().add(colName);
		getTableView().sort();

		// Screen table dimensions
		getTableView().setPrefHeight(4096);

		return getTableView();
	}

	// Editing area for a record
	private VBox makeTextFields() {
		HBox hb1 = getTextBox(Loc.get("name") + ":", 85, txtName, 32);
		HBox hb2 = getTextBox(Loc.get("description") + ":", 85, txtDescr, 80);
		HBox hb3 = getTextBox(Loc.get("link") + ":", 85, txtLink, 120);

		VBox vb = new VBox();
		vb.setPadding(new Insets(5, 5, 5, 5));
		vb.getChildren().addAll(hb1, hb2, hb3);
		return vb;
	}

	// Control buttons, messages, status bar
	private HBox makeControls() {
		// Edit mode message
		lblMode.setMinWidth(150);
		lblMode.setAlignment(Pos.CENTER);

		// Status bar
		Label lblStatus = new Label("");
		lblStatus.setMinWidth(150);
		lblStatus.setAlignment(Pos.CENTER);
		statusBar = new StatusBar(lblStatus);

		HBox hb = new HBox();
		hb.setSpacing(10);
		hb.setPadding(new Insets(5, 10, 0, 10));
		hb.setAlignment(Pos.CENTER_LEFT);
		hb.getChildren().addAll(btnNew, btnNewCopy, btnEdit, lblMode, btnDelete, lblStatus, getHSpacer(), btnOk,
				btnCancel);
		return hb;
	}

	// Checking the record
	private boolean check(Brand brand) {
		Error err = brand.check();
		if (err != null) {
			statusBar.showErrorWithDelay(err.mes);
			switch (err.fieldNum) {
			case 2:
				this.txtName.requestFocus();
				break;
			case 3:
				this.txtDescr.requestFocus();
				break;
			case 4:
				this.txtLink.requestFocus();
				break;
			}

			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void setEditMode(boolean editMode) {
		super.setEditMode(editMode);

		// List
		getTableView().setDisable(editMode);

		// Fields
		txtName.setEditable(editMode);
		txtDescr.setEditable(editMode);
		txtLink.setEditable(editMode);

		// Buttons
		btnNew.setDisable(editMode);
		btnNewCopy.setDisable(editMode);
		btnEdit.setDisable(editMode);
		btnDelete.setDisable(editMode);
		btnOk.setDisable(!editMode);
		btnCancel.setDisable(!editMode);
	}

	@Override
	public Brand getEditedRecord() {
		Brand brand = new Brand(0, txtName.getText(), txtDescr.getText(), txtLink.getText());
		if (!isAppendMode())
			brand.setId(getCurrentRecord().getId());

		return brand;
	}

	@Override
	protected void displayMode(String mode) {
		if (mode.length() == 0)
			lblMode.setText("");
		else
			lblMode.setText("[ " + mode + " ]");
	}

	@Override
	public void activate() {
		btnCancel.setCancelButton(true);

		if (isEditMode()) {
			if (getCurrentNode() != null)
				Platform.runLater(() -> getCurrentNode().requestFocus());
		} else
			requestFocusForTableView();
	}

	@Override
	public void deactivate() {
		btnCancel.setCancelButton(false);
	}

	@Override
	protected void doDisplay(Brand brand) {
		if (brand == null) {
			brand = new Brand();
		}

		txtName.setText(brand.getName());
		txtDescr.setText(brand.getDescr());
		txtLink.setText(brand.getLink());
	}

	@Override
	protected boolean doNew() {
		if (!super.doNew())
			return false;

		doDisplay(new Brand());

		txtName.requestFocus();

		return true;
	}

	@Override
	protected boolean doNewCopy() {
		if (!super.doNewCopy())
			return false;

		txtName.requestFocus();

		return true;
	}

	@Override
	protected boolean doEdit() {
		if (!super.doEdit())
			return false;

		txtName.requestFocus();

		return true;
	}

	@Override
	protected boolean doDelete() {
		if (!super.doDelete())
			return false;

		Brand brand = getCurrentRecord();
		if (brand != null) {
			if (!getDB().brand.canRemove(brand)) {
				Dialogues.ErrMes(Loc.get("the_specified_brand_is_in_use_and_cannot_be_removed"));
				requestFocusForTableView();
				return false;
			}

			String mes = Loc.get("are_you_sure_you_want_to_remove_the_brand") + ": \"" + brand.getName() + "\"?";
			Dialogues.ConMes(mes, (event) -> {
				if (getDB().brand.remove(brand)) {
					getTableView().getItems().remove(brand);
					getTableView().getSelectionModel().selectBelowCell();
				}
			});
		}

		requestFocusForTableView();
		return true;
	}

	@Override
	public void doOk() {
		Brand brand = getEditedRecord();
		if (!check(brand))
			return;

		if (isAppendMode()) {
			getDB().brand.setEdited(true);

			getDB().brand.add(brand);
			getTableView().getItems().add(brand);
		} else {
			if (getCurrentRecord().hasDifference(brand)) {
				String oldValue = getCurrentRecord().getName() == null ? "" : getCurrentRecord().getName().trim();
				String newValue = brand.getName() == null ? "" : brand.getName().trim();
				if (!oldValue.equals(newValue))
					getDB().brand.setEdited(true);

				getDB().brand.set(brand);
				getTableView().getItems().set(getTableView().getSelectionModel().getSelectedIndex(), brand);
			}
		}

		// The edited/new entry is made current
		getTableView().getSelectionModel().select(brand);

		// Scroll to new entry only when adding
		if (isAppendMode())
			getTableView().scrollTo(brand);

		// We don't sort. Edited items remain in place, and added items are added to the end of the list.
		// getTableView().sort();

		doCancel(false);
	}
}
