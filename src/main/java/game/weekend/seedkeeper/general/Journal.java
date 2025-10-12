package game.weekend.seedkeeper.general;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import game.weekend.seedkeeper.controls.WGTableView;
import game.weekend.seedkeeper.controls.WHHyperlinkCellFactory;
import game.weekend.seedkeeper.db.DB;
import game.weekend.seedkeeper.db.Record;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

/**
 * Document journal, dictionary...
 */
public abstract class Journal<T> {

	// It's assumed that the journal must have a visual table. Here, it's just
	// created, while columns and other settings are configured in the derived
	// class.
	private final WGTableView<T> tableView = new WGTableView<>(this);

	// The journal can be in four states.

	// ReadOnly is a mode in which the log does not respond to attempts to edit the
	// record.
	private boolean readOnlyMode = false;

	// Edit mode for the current entry.
	private boolean editMode = false;

	// Adding or adding a copy of a record. The flag is set simultaneously with
	// editMode.
	private boolean appendMode = false;

	// And the fourth state is one in which all three flags are false. This is the
	// normal document viewing state, with the ability to edit any document.

	// Fixed a strange behavior when intercepting the Enter key. While the
	// corresponding on-screen button is triggered, focus is then transferred to the
	// on-screen table, and the Enter key is triggered again, now as if the Enter
	// key was pressed on the current table entry.
	private boolean enterWhileEditing = false;

	/**
	 * A list of visual objects that should be set to ReadOnly display mode when a
	 * journal enters EditMode. When entering EditMode for one journal, it's
	 * recommended to set the other journal to ReadOnly mode. Otherwise, editing
	 * will be possible in the second journal, and doOK() and doCancel() will be
	 * executed for both journals simultaneously.
	 */
	private ArrayList<IReadOnly> listOfReadOnly = new ArrayList<>();

	private Node currentNode = null;

	/**
	 * Specify a visual object that should be set to ReadOnly display mode when the
	 * journal enters EditMode. This will be done in the setEditMode() method.
	 */
	public void addReadOnlyObject(IReadOnly object) {
		listOfReadOnly.add(object);
	}

	/**
	 * The journal is located in a VBox. It consists of a visual journal table
	 * located at the left or top of the VBox and input objects for editing the
	 * entry (document). The getPane() method itself is public, but it typically
	 * uses a number of private methods to create all the necessary parts of the
	 * visual interface.
	 */
	public VBox getPane() {
		VBox vb = new VBox();
		vb.setPadding(new Insets(10, 10, 10, 10));
		return vb;
	}

	/**
	 * Get an object for working with the database. This method exists for
	 * convenience only.
	 */
	public DB getDB() {
		return DB.getInstance();
	}

	/**
	 * Get Screen Table Object The method exists for convenience only.
	 */
	public TableView<T> getTableView() {
		return tableView;
	}

	/**
	 * Operating modes
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * In the descendants of this method, the record field input objects are
	 * switched to edit mode, the OK and Cancel buttons are activated, the New,
	 * Edit, and Remove buttons are closed, and the visual table is closed.
	 */
	protected void setEditMode(boolean editMode) {
		// For all objects from the list listOfReadOnly the
		// setReadOnlyMode(editMode) method is executed.
		for (IReadOnly o : listOfReadOnly)
			o.setReadOnlyMode(editMode);

		this.editMode = editMode;
	}

	/**
	 * The method is used in the implementation of saving an edited record doOK().
	 */
	protected boolean isAppendMode() {
		return appendMode;
	}

	protected void setAppendMode(boolean appendMode) {
		this.appendMode = appendMode;
	}

	protected void setReadOnlyMode(boolean readOnly) {
		this.readOnlyMode = readOnly;
	}

	/**
	 * Used mainly by doNew(), doEdit(), doDelete() methods to refuse to perform the
	 * corresponding action in the viewing mode.
	 */
	protected boolean isReadOnlyMode() {
		return readOnlyMode;
	}

	/**
	 * Current entry in the screen table
	 */
	public T getCurrentRecord() {
		return tableView.getSelectionModel().getSelectedItem();
	}

	public T getEditedRecord() {
		return tableView.getSelectionModel().getSelectedItem();
	}

	/**
	 * Display the current mode (usually: Add, Add Copy, Edit)
	 */
	protected void displayMode(String mode) {
	}

	// TableView templates //

	/**
	 * A helper method that hooks the following in TableView: Insert - adding
	 * (doNew()), Enter - editing (doEdit()), Delete - deleting (doDelete()), Double
	 * click - editing (doEdit()), change current entry in TableView -
	 * doDisplay(newVal).
	 */
	public void makeTableHandlers(TableView<T> tableView) {

		// Intercept keystrokes: Insert - adding, Enter - editing, Delete - deleting
		tableView.addEventHandler(KeyEvent.KEY_RELEASED, (e) -> {
			if (e.getCode() == KeyCode.INSERT) {
				doNew();
			} else if (e.getCode() == KeyCode.ENTER) {
				if (enterWhileEditing) // If it is ENTER when finishing editing,
					enterWhileEditing = false; // then we do nothing, but drop the flag.
				else
					doEdit();
			} else if (e.getCode() == KeyCode.DELETE) {
				doDelete();
			}
		});

		// Double-click interception
		tableView.setOnMouseClicked(me -> {
			if (me.getClickCount() == 2)
				doEdit();
		});

		// Intercepting the change of the current TableView record
		tableView.getSelectionModel().selectedItemProperty().addListener((value, oldVal, newVal) -> doDisplay(newVal));

	}

	/**
	 * String column
	 */
	public TableColumn<T, String> getTextColumn(String title, String field, int width) {
		TableColumn<T, String> column = new TableColumn<>(title);
		column.setCellValueFactory(new PropertyValueFactory<>(field));
		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setEditable(false);
		column.setPrefWidth(width);
		return column;
	}

	/**
	 * Integer column
	 */
	public TableColumn<T, Integer> getIntColumn(String title, String field, int width) {
		TableColumn<T, Integer> column = new TableColumn<>(title);
		column.setCellValueFactory(new PropertyValueFactory<>(field));
		column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		column.setEditable(false);
		column.setPrefWidth(width);
		return column;
	}

	/**
	 * Column for date
	 */
	public TableColumn<T, Date> getDateColumn(String title, String field, int width) {
		TableColumn<T, Date> column = new TableColumn<>(title);
		column.setCellValueFactory(new PropertyValueFactory<>(field));

		column.setCellFactory(col -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null)
						setText(null);
					else
						setText(format.format(item));
				}
			};

			return cell;
		});

		column.setEditable(false);
		column.setPrefWidth(width);
		return column;
	}

	/**
	 * Links column
	 */
	public TableColumn<T, String> getLinkColumn(String title, String field, int width) {
		TableColumn<T, String> column = new TableColumn<>(title);
		column.setCellValueFactory(new PropertyValueFactory<>(field));
		column.setCellFactory(new WHHyperlinkCellFactory<T>());
		column.setEditable(false);
		column.setPrefWidth(width);
		return column;
	}

	/**
	 * Boolean column
	 */
	public TableColumn<T, Boolean> getBooleanColumn(String title, String field, int width) {
		TableColumn<T, Boolean> column = new TableColumn<>(title);
		column.setCellValueFactory(new PropertyValueFactory<>(field));
		column.setCellFactory(CheckBoxTableCell.forTableColumn(column));
		column.setEditable(false);
		column.setPrefWidth(width);
		return column;
	}

	// Edit field templates //

	/**
	 * Text field
	 */
	protected HBox getTextBox(String name, double width, TextField txtField, int columnCount) {
		Label lblName = new Label(name);
		lblName.setMinWidth(width);
		txtField.setPrefColumnCount(columnCount);

		HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setPadding(new Insets(3, 0, 3, 0));
		hb.setAlignment(Pos.CENTER_LEFT);
		hb.getChildren().addAll(lblName, txtField);

		return hb;
	}

	/**
	 * Text area
	 */
	protected HBox getTextArea(double width, TextArea tarField, int tarWitdth, int tarHeight) {
		Label lblName = new Label(Loc.get("add_copy") + ":");
		lblName.setMinWidth(width);

		tarField.setPrefWidth(tarWitdth);
		tarField.setPrefHeight(tarHeight);
		tarField.setWrapText(true);

		HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setPadding(new Insets(3, 0, 3, 0));
		hb.setAlignment(Pos.TOP_LEFT);
		hb.getChildren().addAll(lblName, tarField);

		return hb;
	}

	/**
	 * Date
	 */
	protected HBox getDatePicker(String name, double width, DatePicker datePicker) {
		Label lblName = new Label(name);
		lblName.setMinWidth(width);

		datePicker.showWeekNumbersProperty();

		datePicker.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				try {
					datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
				} catch (Exception e) {
					datePicker.getEditor().setText(datePicker.getConverter().toString(datePicker.getValue()));
				}
			}
		});

		datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				try {
					datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
				} catch (Exception e) {
					datePicker.getEditor().setText(datePicker.getConverter().toString(datePicker.getValue()));
				}
			}
		});

		HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setPadding(new Insets(3, 0, 3, 0));
		hb.setAlignment(Pos.CENTER_LEFT);
		hb.getChildren().addAll(lblName, datePicker);

		return hb;
	}

	/**
	 * Arbitrary number of Nodes
	 */
	protected HBox getControlBox(String name, double width, Node... node) {
		Label lblName = new Label(name);
		lblName.setMinWidth(width);

		HBox hb = new HBox(5);
		hb.setPadding(new Insets(3, 0, 3, 0));
		hb.setAlignment(Pos.CENTER_LEFT);
		hb.getChildren().add(lblName);
		hb.getChildren().addAll(node);

		return hb;
	}

	/**
	 * Color indication field
	 */
	protected HBox getColorBox(ColorPicker colorPicker) {
		Label lblName = new Label(Loc.get("color") + ":");
		lblName.setMinWidth(35);

		colorPicker.getStyleClass().add("button");

		HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setPadding(new Insets(3, 0, 3, 0));
		hb.setAlignment(Pos.CENTER_LEFT);
		hb.getChildren().addAll(lblName, colorPicker);

		return hb;
	}

	// Screen button templates //

	protected Button getButtonNew() {
		Button btn = new Button(Loc.get("add"));
		btn.setOnAction((e) -> doNew());
		return btn;
	}

	protected Button getButtonNewCopy() {
		Button btn = new Button(Loc.get("add_copy"));
		btn.setOnAction((e) -> doNewCopy());
		return btn;
	}

	protected Button getButtonEdit() {
		Button btn = new Button(Loc.get("edit"));
		btn.setOnAction((e) -> doEdit());
		return btn;
	}

	protected Button getButtonDelete() {
		Button btn = new Button(Loc.get("delete") + "...");
		btn.setOnAction((e) -> doDelete());
		return btn;
	}

	protected Button getButtonOk() {
		Button btn = new Button(Loc.get("ok"));
		btn.setOnAction((e) -> doOk());
		return btn;
	}

	protected Button getButtonCancel() {
		Button btn = new Button(Loc.get("cancel"));
		btn.setOnAction(e -> doCancel(true));
		return btn;
	}

	protected Button getButtonUp() {
		Button btn = new Button(Loc.get("up"));
		btn.setOnAction((e) -> doShiftUp());
		btn.setPrefWidth(90);
		return btn;
	}

	protected Button getButtonShiftDown() {
		Button btn = new Button(Loc.get("down"));
		btn.setOnAction((e) -> doShiftDown());
		btn.setPrefWidth(90);
		return btn;
	}

	// Helper methods for placing input objects //

	/**
	 * Horizontal expander
	 */
	protected Pane getHSpacer() {
		Pane spacer = new Pane();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		spacer.setMinSize(5, 5);
		return spacer;
	}

	protected Pane getHSpacer(int width) {
		Pane spacer = new Pane();
		spacer.setMinSize(width, 5);
		return spacer;
	}

	/**
	 * Vertical expander
	 */
	protected Pane getVSpacer() {
		Pane spacer = new Pane();
		VBox.setVgrow(spacer, Priority.ALWAYS);
		spacer.setMinSize(5, 5);
		return spacer;
	}

	protected Pane getVSpacer(int hight) {
		Pane spacer = new Pane();
		spacer.setMinHeight(hight);
		return spacer;
	}

	// Events generated by the outer container //

	public void activate() {
	}

	public void deactivate() {
	}

	public void writeProp() {
	}

	public void enter() {
		if (isEditMode()) {
			enterWhileEditing = true;
			doOk();
		}
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Node node) {
		this.currentNode = node;
	}

	public void requestFocusForTableView() {
		Platform.runLater((() -> getTableView().requestFocus()));
		Platform.runLater((() -> getTableView().getFocusModel().focusRightCell()));
	}

	// //

	/**
	 * Displaying the specified record (record object)
	 */
	protected void doDisplay(T o) {
	}

	/**
	 * New
	 */
	protected boolean doNew() {
		if (isReadOnlyMode())
			return false;

		displayMode(Loc.get("adding"));
		setAppendMode(true);
		setEditMode(true);

		return true;
	}

	/**
	 * New copy
	 */
	protected boolean doNewCopy() {
		if (isReadOnlyMode())
			return false;

		if (getCurrentRecord() == null)
			return false;

		displayMode(Loc.get("adding_a_copy"));
		setAppendMode(true);
		setEditMode(true);

		return true;
	}

	/**
	 * Edit
	 */
	protected boolean doEdit() {
		if (isReadOnlyMode())
			return false;

		if (getCurrentRecord() == null)
			return false;

		displayMode(Loc.get("editing"));
		setAppendMode(false);
		setEditMode(true);

		return true;
	}

	/**
	 * Delete
	 */
	protected boolean doDelete() {
		return (!isReadOnlyMode());
	}

	/**
	 * Ok
	 */
	public void doOk() {
	}

	/**
	 * Cancel
	 */
	public void doCancel(boolean checkChange) {
		if (checkChange) {
			Record newRec = (Record) getEditedRecord();
			Record oldRec = (Record) getCurrentRecord();

			if (isAppendMode() || oldRec.hasDifference(newRec))
				if (!Dialogues.getConfirmation(
						Loc.get("are_you_sure_you_want_to_discard_the_changes") + "?\n" + oldRec.getDifferences()))
					return;
		}

		displayMode("");
		setAppendMode(false);
		setEditMode(false);

		doDisplay(getCurrentRecord());
		if (getTableView() != null)
			getTableView().requestFocus();
	}

	/**
	 * Shift Up
	 */
	protected void doShiftUp() {
	}

	/**
	 * Shift Down
	 */
	protected void doShiftDown() {
	}
}
