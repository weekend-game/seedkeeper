package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.controls.WGTextField;
import game.weekend.seedkeeper.db.Error;
import game.weekend.seedkeeper.controls.WGColorPicker;
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

public class CategoryJournal extends Journal<Category> implements IReadOnly {

	// Fields
	private final WGTextField txtName;
	private final WGColorPicker cprColor;

	// Buttons
	private final Button btnNew = getButtonNew();
	private final Button btnEdit = getButtonEdit();
	private final Button btnDelete = getButtonDelete();
	private final Button btnShiftUp = getButtonUp();
	private final Button btnShiftDown = getButtonShiftDown();

	private final Label lblTitle = new Label(Loc.get("categories"));
	private final Label lblMode = new Label("");

	public CategoryJournal(Journal<?> parentJournal) {
		txtName = new WGTextField(parentJournal);
		cprColor = new WGColorPicker(parentJournal);
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
		// The current line is the first line in the list
		getTableView().getSelectionModel().selectFirst();

		return vb;
	}

	// Components of the journal //

	// Screen table
	private TableView<Category> makeTableView() {
		// Table Columns

		// This column will be sorted by default
		TableColumn<Category, Integer> colNumb = getIntColumn(Loc.get("number"), "numb", 60);
		getTableView().getColumns().add(colNumb);

		getTableView().getColumns().add(getTextColumn(Loc.get("name"), "name", 140));

		// Data for the screen table.
		getTableView().setItems(getDB().category.getList());

		// Useful key and click interceptions
		makeTableHandlers(getTableView());

		/*
		 * Since this is a directory with a custom record layout, I specify the numb
		 * column for sorting and sort the table list. The user can sort by other
		 * columns, but to keep the list sorted, the doOK() method must call
		 * getTableView().sort() and getTableView().refresh() if the value in the sorted
		 * column hasn't changed.
		 */
		getTableView().getSortOrder().add(colNumb);
		getTableView().sort();

		// Screen table dimensions
		getTableView().setPrefHeight(256);
		getTableView().setPrefWidth(300);

		return getTableView();
	}

	// Editing area for a record
	private VBox makeTextFields() {
		VBox vbName = new VBox();
		vbName.getChildren().addAll(new Label(Loc.get("name") + ":"), getTextBox("", 8, txtName, 32));

		VBox vb = new VBox();
		vb.setPadding(new Insets(5, 5, 5, 10));
		vb.getChildren().addAll(vbName, getVSpacer(), btnShiftUp, new Label(""), btnShiftDown, getVSpacer());
		return vb;
	}

	// Edit buttons
	private HBox makeEditButtons() {
		HBox hb = new HBox(10);
		hb.setPadding(new Insets(5, 10, 0, 10));
		hb.getChildren().addAll(btnNew, btnEdit, btnDelete);
		return hb;
	}

	// Turning editing mode on or off
	// (adding and adding a copy is also an editing mode)
	@Override
	protected void setEditMode(boolean editMode) {
		super.setEditMode(editMode);

		// List
		getTableView().setDisable(editMode);

		// Fields
		txtName.setEditable(editMode);
		cprColor.setDisable(!editMode);

		// Buttons
		btnNew.setDisable(editMode);
		btnEdit.setDisable(editMode);
		btnDelete.setDisable(editMode);
		btnShiftUp.setDisable(editMode);
		btnShiftDown.setDisable(editMode);
	}

	@Override
	public Category getEditedRecord() {
		int numb = (getCurrentRecord() == null) ? 0 : getCurrentRecord().getNumb();
		Category category = new Category(0, numb, txtName.getText());
		if (!isAppendMode())
			category.setId(getCurrentRecord().getId());
		else
			category.setNumb(++numb);

		return category;
	}

	// Checking the record
	private boolean check(Category category) {
		Error err = category.check();
		if (err != null) {
			Dialogues.ErrMes(err.mes);
			switch (err.fieldNum) {
			case 2:
				this.txtName.requestFocus();
				break;
			}
			return false;
		}
		return true;
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
	protected void doDisplay(Category category) {
		if (category == null) {
			category = new Category();
		}

		txtName.setText(category.getName());
	}

	@Override
	protected boolean doNew() {
		if (!super.doNew())
			return false;

		Category newQuality = new Category();
		doDisplay(newQuality);

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

		Category category = getCurrentRecord();
		if (category != null) {

			if (!getDB().category.canRemove(category)) {
				Dialogues.ErrMes(Loc.get("the_specified_category_is_in_use_and_cannot_be_removed") + ".");
				requestFocusForTableView();
				return false;
			}

			String mes = Loc.get("are_you_sure_you_want_to_remove_the_category") + ": \"" + category.getName() + "\"?";
			Dialogues.ConMes(mes, (event) -> {
				if (getDB().category.remove(category)) {

					// Renumbering a table list
					for (Category q : getTableView().getItems())
						if (q.getNumb() > category.getNumb())
							q.setNumb(q.getNumb() - 1);

					getTableView().getSelectionModel().selectBelowCell();
					getTableView().getItems().remove(category);

					getTableView().sort();
					getTableView().refresh();
					getTableView().requestFocus();
				}
			});
		}

		requestFocusForTableView();
		return true;
	}

	@Override
	public void doOk() {
		Category category = getEditedRecord();
		if (!check(category))
			return;

		if (isAppendMode()) {
			getDB().category.setEdited(true);

			// Adding a record to the table
			getDB().category.add(category);

			// Renumbering a table list
			for (Category q : getTableView().getItems())
				if (q.getNumb() >= category.getNumb())
					q.setNumb(q.getNumb() + 1);

			// Adding a record to the table view (it can be done at the end, since the
			// method will end with a call to getTableView().sort()
			getTableView().getItems().add(category);

		} else {
			if (getCurrentRecord().hasDifference(category)) { // This method will check two fields and these are the
																// ones that are important for setEdited()
				getDB().category.setEdited(true);

				getDB().category.set(category);
				getTableView().getItems().set(getTableView().getSelectionModel().getSelectedIndex(), category);
			}
		}

		// The edited/new entry is made current
		getTableView().getSelectionModel().select(category);

		// It is necessary to sort
		getTableView().sort();

		doCancel(false);
	}

	@Override
	protected void doShiftUp() {
		Category currentCategory = getCurrentRecord();
		Category nextCategory = null;

		int currentNumb = (currentCategory == null) ? 0 : currentCategory.getNumb();
		if (currentNumb > 1) {

			for (Category s : getTableView().getItems())
				if (s.getNumb() == currentNumb - 1) {
					nextCategory = s;
					break;
				}

			if (nextCategory != null) {
				currentCategory.setNumb(currentNumb - 1);
				nextCategory.setNumb(currentNumb);

				getDB().category.setNumb(currentCategory.getId(), currentCategory.getNumb(), nextCategory.getId(),
						nextCategory.getNumb());
			}

			getTableView().sort();

			// If the current sorting is not by the numb field, or does not exist at all,
			// then without this call the change in numb will not be displayed on the
			// screen.
			getTableView().refresh();
		}
		getTableView().requestFocus();
	}

	@Override
	protected void doShiftDown() {
		Category currentCategory = getCurrentRecord();
		Category nextCategory = null;

		int currentNumb = (currentCategory == null) ? 0 : currentCategory.getNumb();
		if (currentNumb > 0 && currentNumb < getTableView().getItems().size()) {

			for (Category q : getTableView().getItems())
				if (q.getNumb() == currentNumb + 1) {
					nextCategory = q;
					break;
				}

			if (nextCategory != null) {
				currentCategory.setNumb(currentNumb + 1);
				nextCategory.setNumb(currentNumb);

				getDB().category.setNumb(currentCategory.getId(), currentCategory.getNumb(), nextCategory.getId(),
						nextCategory.getNumb());
			}

			getTableView().sort();

			// If the current sorting is not by the numb field, or does not exist at all,
			// then without this call the change in numb will not be displayed on the
			// screen.
			getTableView().refresh();
		}
		getTableView().requestFocus();
	}
}
