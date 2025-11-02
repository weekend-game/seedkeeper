package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.controls.WGTextField;
import game.weekend.seedkeeper.general.Dialogues;
import game.weekend.seedkeeper.general.IReadOnly;
import game.weekend.seedkeeper.general.Journal;
import game.weekend.seedkeeper.general.Loc;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class KindJournal extends Journal<Kind> implements IReadOnly {

	// Fields
	private final WGTextField txtName;

	// Buttons
	private final Button btnNew = getButtonNew();
	private final Button btnEdit = getButtonEdit();
	private final Button btnDelete = getButtonDelete();

	private final Label lblTitle = new Label(Loc.get("kinds"));
	private final Label lblMode = new Label("");

	public KindJournal(Journal<?> parentJournal) {
		txtName = new WGTextField(parentJournal);
	}

	@Override
	public VBox getPane() {
		VBox vb = super.getPane();

		HBox hb1 = new HBox();
		hb1.getChildren().addAll(lblTitle, getHSpacer(20), lblMode);

		HBox hb2 = new HBox();
		hb2.getChildren().addAll(makeTableView(), makeTextFields(), getHSpacer());

		vb.getChildren().addAll(hb1, hb2, makeEditButtons());

		// Control objects are switched to viewing mode
		setEditMode(false);
		// The current line is the first line in the list.
		getTableView().getSelectionModel().selectFirst();

		return vb;
	}

	// Components of the journal //

	// Screen table
	private TableView<Kind> makeTableView() {
		// Table Columns

		// This column will be sorted by default
		TableColumn<Kind, String> colName = getTextColumn(Loc.get("name"), "name", 260);
		getTableView().getColumns().add(colName);

		// Data for the screen table
		getTableView().setItems(getDB().kind.getList());

		// Полезные перехваты клавиш и кликов
		makeTableHandlers(getTableView());

		// Useful key and click interceptions
		getTableView().getSortOrder().add(colName);
		getTableView().sort();

		// Размеры экранной таблицы
		getTableView().setPrefHeight(256);
		getTableView().setPrefWidth(300);

		return getTableView();
	}

	// Sorting by column by default
	private VBox makeTextFields() {
		VBox vbName = new VBox();
		vbName.getChildren().addAll(new Label(Loc.get("name") + ":"), getTextBox("", 8, txtName, 32));

		VBox vb = new VBox();
		vb.setPadding(new Insets(5, 5, 5, 10));
		vb.getChildren().addAll(vbName, getVSpacer());
		return vb;
	}

	// Edit buttons
	private HBox makeEditButtons() {
		HBox hb = new HBox(10);
		hb.setPadding(new Insets(5, 10, 0, 10));
		hb.getChildren().addAll(btnNew, btnEdit, btnDelete);
		return hb;
	}

	@Override
	protected void setEditMode(boolean editMode) {
		super.setEditMode(editMode);

		// List
		getTableView().setDisable(editMode);

		// Fields
		txtName.setEditable(editMode);

		// Buttons
		btnNew.setDisable(editMode);
		btnEdit.setDisable(editMode);
		btnDelete.setDisable(editMode);
	}

	@Override
	public Kind getEditedRecord() {
		Kind kind = new Kind(0, txtName.getText());
		if (!isAppendMode())
			kind.setId(getCurrentRecord().getId());

		return kind;
	}

	@Override
	public void setReadOnlyMode(boolean readOnly) {
		super.setReadOnlyMode(readOnly);

		btnNew.setDisable(readOnly);
		btnEdit.setDisable(readOnly);
		btnDelete.setDisable(readOnly);
	}

	@Override
	protected void displayMode(String mode) {
		if (mode.length() == 0)
			lblMode.setText("");
		else
			lblMode.setText("[ " + mode.trim() + " ]");
	}

	@Override
	protected void doDisplay(Kind kind) {
		if (kind == null) {
			kind = new Kind();
		}

		txtName.setText(kind.getName());
	}

	@Override
	protected boolean doNew() {
		if (!super.doNew())
			return false;

		Kind newKind = new Kind();
		doDisplay(newKind);

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

		Kind kind = getCurrentRecord();
		if (kind != null) {
			if (!getDB().kind.canRemove(kind)) {
				Dialogues.ErrMes(Loc.get("the_specified_kind_is_in_use_and_cannot_be_removed") + ".");
				requestFocusForTableView();
				return false;
			}

			String mes = Loc.get("are_you_sure_you_want_to_remove_the_kind") + ": \"" + kind.getName() + "\"?";
			Dialogues.ConMes(mes, (event) -> {
				if (getDB().kind.remove(kind)) {
					getTableView().getSelectionModel().selectBelowCell();
					getTableView().getItems().remove(kind);
				}
			});
		}

		requestFocusForTableView();
		return true;
	}

	@Override
	public void doOk() {
		Kind kind = getEditedRecord();

		if (isAppendMode()) {
			getDB().kind.setEdited(true);

			getDB().kind.add(kind);
			getTableView().getItems().add(kind);
		} else {
			if (getCurrentRecord().hasDifference(kind)) {
				getDB().kind.setEdited(true);

				getDB().kind.set(kind);
				getTableView().getItems().set(getTableView().getSelectionModel().getSelectedIndex(), kind);
			}
		}

		// The edited/new entry is made current
		getTableView().getSelectionModel().select(kind);

		// Scroll to new entry only when adding
		if (isAppendMode())
			getTableView().scrollTo(kind);

		// We don't sort. Edited items remain in place, and added items are added to the end of the list.
		// getTableView().sort();
		doCancel(false);
	}
}
