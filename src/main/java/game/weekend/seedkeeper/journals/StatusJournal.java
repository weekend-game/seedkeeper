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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class StatusJournal extends Journal<Status> implements IReadOnly {

	// Fields
	private final WGTextField txtName;
	private final WGColorPicker cprColor;

	// Buttons
	private final Button btnNew = getButtonNew();
	private final Button btnEdit = getButtonEdit();
	private final Button btnDelete = getButtonDelete();
	private final Button btnShiftUp = getButtonUp();
	private final Button btnShiftDown = getButtonShiftDown();

	private final Label lblTitle = new Label(Loc.get("statuses"));
	private final Label lblMode = new Label("");

	public StatusJournal(Journal<?> parentJournal) {
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
	private TableView<Status> makeTableView() {
		// Table Columns

		// This column will be sorted by default
		TableColumn<Status, Integer> colNumb = getIntColumn(Loc.get("number"), "numb", 60);
		getTableView().getColumns().add(colNumb);

		getTableView().getColumns().add(getTextColumn(Loc.get("name"), "name", 140));
		getTableView().getColumns().add(getTextColumn(Loc.get("colors"), "color", 50));

		// I color the lines depending on the value of the color field
		getTableView().setRowFactory(tv -> new TableRow<>() {
			@Override
			protected void updateItem(Status quality, boolean empty) {
				super.updateItem(quality, empty);

				if (tv.getSelectionModel().getSelectedItem() == quality || quality == null
						|| quality.getColor().trim().length() == 0)
					setStyle("");
				else
					setStyle("-fx-background-color: #" + quality.getColor().trim() + ";");
			}
		});

		// Data for the screen table.
		getTableView().setItems(getDB().status.getList());

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

		// Размеры экранной таблицы
		getTableView().setPrefHeight(256);
		getTableView().setPrefWidth(256);

		return getTableView();
	}

	// Editing area for a record
	private VBox makeTextFields() {
		HBox hb1 = getTextBox(Loc.get("name") + ":", 35, txtName, 9);
		HBox hb2 = getColorBox(cprColor);

		VBox vb = new VBox();
		vb.setPadding(new Insets(5, 5, 5, 10));
		vb.getChildren().addAll(hb1, hb2, getVSpacer(), btnShiftUp, new Label(""), btnShiftDown, getVSpacer());
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
	public Status getEditedRecord() {
		int numb = (getCurrentRecord() == null) ? 0 : getCurrentRecord().getNumb();
		String color = cprColor.getValue().toString().substring(2, 8);

		Status quality = new Status(0, numb, txtName.getText(), color);
		if (!isAppendMode())
			quality.setId(getCurrentRecord().getId());
		else
			quality.setNumb(++numb);

		return quality;
	}

	// Checking the record
	private boolean check(Status status) {
		Error err = status.check();
		if (err != null) {
			Dialogues.ErrMes(err.mes);
			switch (err.fieldNum) {
			case 2:
				this.txtName.requestFocus();
				break;
			}

			return false;
		} else {
			return true;
		}
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
	protected void doDisplay(Status status) {
		if (status == null) {
			status = new Status();
		}

		txtName.setText(status.getName());
		cprColor.setValue(Color.web("0x" + status.getColor(), 1));
	}

	@Override
	protected boolean doNew() {
		if (!super.doNew())
			return false;

		Status newQuality = new Status();
		newQuality.setColor("ffffff");
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

		Status status = getCurrentRecord();
		if (status != null) {

			if (!getDB().status.canRemove(status)) {
				Dialogues.ErrMes(Loc.get("the_specified_status_is_in_use_and_cannot_be_removed"));
				requestFocusForTableView();
				return false;
			}

			String mes = Loc.get("are_you_sure_you_want_to_remove_the_status") + ": \"" + status.getName() + "\"?";
			Dialogues.ConMes(mes, (event) -> {
				if (getDB().status.remove(status)) {

					// Renumbering a table list
					for (Status q : getTableView().getItems())
						if (q.getNumb() > status.getNumb())
							q.setNumb(q.getNumb() - 1);

					getTableView().getItems().remove(status);
					getTableView().getSelectionModel().selectBelowCell();

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
		Status status = getEditedRecord();
		if (!check(status))
			return;

		if (isAppendMode()) {
			getDB().status.setEdited(true);

			// Adding a record to the table
			getDB().status.add(status);

			// Renumbering a table list
			for (Status q : getTableView().getItems())
				if (q.getNumb() >= status.getNumb())
					q.setNumb(q.getNumb() + 1);

			// Adding a record to the table view (it can be done at the end, since the
			// method will end with a call to getTableView().sort()
			getTableView().getItems().add(status);

		} else {
			if (getCurrentRecord().hasDifference(status)) { // This method will check two fields and these are the
															// ones that are important for setEdited()
				getDB().status.setEdited(true);

				getDB().status.set(status);
				getTableView().getItems().set(getTableView().getSelectionModel().getSelectedIndex(), status);
			}
		}

		// The edited/new entry is made current
		getTableView().getSelectionModel().select(status);

		// It is necessary to sort
		getTableView().sort();

		doCancel(false);
	}

	@Override
	protected void doShiftUp() {
		Status currentStatus = getCurrentRecord();
		Status nextStatus = null;

		int currentNumb = (currentStatus == null) ? 0 : currentStatus.getNumb();
		if (currentNumb > 1) {

			for (Status s : getTableView().getItems())
				if (s.getNumb() == currentNumb - 1) {
					nextStatus = s;
					break;
				}

			if (nextStatus != null) {
				currentStatus.setNumb(currentNumb - 1);
				nextStatus.setNumb(currentNumb);

				getDB().status.setNumb(currentStatus.getId(), currentStatus.getNumb(), nextStatus.getId(),
						nextStatus.getNumb());
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
		Status currentStatus = getCurrentRecord();
		Status nextStatus = null;

		int currentNumb = (currentStatus == null) ? 0 : currentStatus.getNumb();
		if (currentNumb > 0 && currentNumb < getTableView().getItems().size()) {

			for (Status q : getTableView().getItems())
				if (q.getNumb() == currentNumb + 1) {
					nextStatus = q;
					break;
				}

			if (nextStatus != null) {
				currentStatus.setNumb(currentNumb + 1);
				nextStatus.setNumb(currentNumb);

				getDB().status.setNumb(currentStatus.getId(), currentStatus.getNumb(), nextStatus.getId(),
						nextStatus.getNumb());
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
