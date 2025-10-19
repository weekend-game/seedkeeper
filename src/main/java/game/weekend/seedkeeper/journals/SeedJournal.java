package game.weekend.seedkeeper.journals;

import game.weekend.seedkeeper.controls.StatusBar;
import game.weekend.seedkeeper.controls.WGCheckBox;
import game.weekend.seedkeeper.controls.WGSearchableComboBox;
import game.weekend.seedkeeper.controls.WGTextArea;
import game.weekend.seedkeeper.controls.WGTextField;
import game.weekend.seedkeeper.db.ComboItem;
import game.weekend.seedkeeper.db.Error;
import game.weekend.seedkeeper.general.Dialogues;
import game.weekend.seedkeeper.general.Journal;
import game.weekend.seedkeeper.general.Loc;
import game.weekend.seedkeeper.general.Proper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A journal sorted by any column. When editing, the entry remains in its
 * original position, regardless of the current sorting. When adding, the entry
 * is added to the end of the journal.
 */
public class SeedJournal extends Journal<Seed> {
	private SplitPane splitPane;
	private double lastDividerPosition;
	private StatusBar statusBar;

	private final WGSearchableComboBox cboBrand = new WGSearchableComboBox(this);
	private final WGSearchableComboBox cboCategory = new WGSearchableComboBox(this);
	private final WGSearchableComboBox cboStatus = new WGSearchableComboBox(this);
	private final WGSearchableComboBox cboColor = new WGSearchableComboBox(this);
	private final WGSearchableComboBox cboKind = new WGSearchableComboBox(this);

	private final WGTextField txtName = new WGTextField(this);
	private final WGTextField txtArticle = new WGTextField(this);
	private final WGCheckBox chkHybrid = new WGCheckBox(Loc.get("hybrid"), this);
	private final WGTextArea tarDescription = new WGTextArea(this);
	private final WGTextField txtVegetation = new WGTextField(this);
	private final WGTextField txtMass = new WGTextField(this);
	private final WGTextField txtHeight = new WGTextField(this);
	private final WGTextField txtYield = new WGTextField(this);
	private final WGTextField txtLength = new WGTextField(this);
	private final WGTextField txtSowing_time = new WGTextField(this);
	private final WGTextField txtTransplant_time = new WGTextField(this);
	private final WGTextField txtIn_ground = new WGTextField(this);
	private final WGTextField txtPlanting_scheme = new WGTextField(this);
	private final WGTextField txtGround = new WGTextField(this);

	private final Button btnNew = getButtonNew();
	private final Button btnNewCopy = getButtonNewCopy();
	private final Button btnEdit = getButtonEdit();
	private final Button btnDelete = getButtonDelete();
	private final Button btnOk = getButtonOk();
	private final Button btnCancel = getButtonCancel();

	private final Label lblMode = new Label("");

	public SeedJournal() {
		lastDividerPosition = Proper.getProperty("splitPane.DividerPositions", 0.25);
	}

	@Override
	public VBox getPane() {
		TableView<Seed> leftPane = makeTableView();

		BorderPane rightPane = new BorderPane();
		rightPane.setCenter(makeTextFields());
		rightPane.setBottom(makeControls());

		splitPane = new SplitPane(leftPane, rightPane);
		splitPane.setDividerPosition(0, lastDividerPosition);

		VBox vb = super.getPane();
		vb.getChildren().addAll(splitPane);

		setEditMode(false);

		getTableView().getSelectionModel().selectFirst();

		return vb;
	}

	// Components of the journal //

	// Screen table
	private TableView<Seed> makeTableView() {
		// Table Columns

		// This column will be sorted by default
		TableColumn<Seed, String> colName = getTextColumn(Loc.get("name"), "name", 200);
		getTableView().getColumns().add(colName);

		getTableView().getColumns().add(getTextColumn(Loc.get("article"), "article", 120));

		// Data for the screen table
		getTableView().setItems(getDB().seed.getList());

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
		HBox hb1 = getTextBox(Loc.get("name") + ":", 90, txtName, 80);

		cboCategory.setPrefWidth(120);
		cboCategory.setEditable(false);
		cboCategory.setItems(getDB().category.getListForCombo());

		cboBrand.setPrefWidth(120);
		cboBrand.setEditable(false);
		cboBrand.setItems(getDB().brand.getListForCombo());

		txtVegetation.setPrefColumnCount(8);

		cboColor.setPrefWidth(120);
		cboColor.setEditable(false);
		cboColor.setItems(getDB().color.getListForCombo());

		HBox hb2 = getControlBox(Loc.get("article") + ":", 75, txtArticle, new Label(Loc.get("category") + ":"),
				cboCategory, new Label(Loc.get("brand") + ":"), cboBrand, new Label(Loc.get("vegetation") + ":"),
				txtVegetation, new Label(Loc.get("color") + ":"), cboColor);

		txtMass.setPrefColumnCount(8);
		txtHeight.setPrefColumnCount(8);
		txtYield.setPrefColumnCount(8);

		cboKind.setPrefWidth(120);
		cboKind.setEditable(false);
		cboKind.setItems(getDB().kind.getListForCombo());

		txtLength.setPrefColumnCount(8);

		cboStatus.setPrefWidth(120);
		cboStatus.setEditable(false);
		cboStatus.setItems(getDB().status.getListForCombo());

		HBox hb3 = getControlBox(Loc.get("mass") + ":", 75, txtMass, new Label(Loc.get("height") + ":"), txtHeight,
				new Label(Loc.get("yield") + ":"), txtYield, new Label(Loc.get("kind") + ":"), cboKind,
				new Label(Loc.get("length") + ":"), txtLength, new Label(Loc.get("status") + ":"), cboStatus);

		txtSowing_time.setPrefColumnCount(8);
		txtTransplant_time.setPrefColumnCount(8);
		txtIn_ground.setPrefColumnCount(8);
		txtPlanting_scheme.setPrefColumnCount(8);
		txtGround.setPrefColumnCount(8);

		HBox hb4 = getControlBox(Loc.get("sowing_time") + ":", 75, txtSowing_time,
				new Label(Loc.get("transplant_time") + ":"), txtTransplant_time, new Label(Loc.get("in_ground") + ":"),
				txtIn_ground, new Label(Loc.get("planting_scheme") + ":"), txtPlanting_scheme,
				new Label(Loc.get("ground") + ":"), this.txtGround, chkHybrid);

		tarDescription.setPrefWidth(1024);
		tarDescription.setPrefHeight(380);
		tarDescription.setWrapText(true);

		VBox photoPane = new VBox(5, new Label(Loc.get("description") + ":"), tarDescription);
		VBox descPane = new VBox(5);
		HBox photoAndDesc = new HBox(5);
		photoAndDesc.getChildren().addAll(photoPane, descPane);
		photoAndDesc.setPadding(new Insets(0, 5, 0, 5));

		VBox vb = new VBox();
		vb.setPadding(new Insets(5, 5, 5, 5));
		vb.getChildren().addAll(hb1, hb2, hb3, hb4, photoAndDesc);

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
		hb.setPadding(new Insets(5, 10, 10, 10));
		hb.setAlignment(Pos.CENTER_LEFT);
		hb.getChildren().addAll(btnNew, btnNewCopy, btnEdit, lblMode, btnDelete, lblStatus, getHSpacer(), btnOk,
				btnCancel);
		return hb;
	}

	// Checking the record
	private boolean check(Seed seed) {
		Error err = seed.check();
		if (err != null) {
			statusBar.showErrorWithDelay(err.mes);
			switch (err.fieldNum) {
			case 2:
				this.txtName.requestFocus();
				break;
//			case 3:
//				this.txtDescr.requestFocus();
//				break;
//			case 4:
//				this.txtLink.requestFocus();
//				break;
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
		cboBrand.setDisable(!editMode);
		cboCategory.setDisable(!editMode);
		cboStatus.setDisable(!editMode);
		cboColor.setDisable(!editMode);
		cboKind.setDisable(!editMode);

		txtName.setEditable(editMode);
		txtArticle.setEditable(editMode);
		chkHybrid.setDisable(!editMode);
		tarDescription.setEditable(editMode);
		txtVegetation.setEditable(editMode);
		txtMass.setEditable(editMode);
		txtHeight.setEditable(editMode);
		txtYield.setEditable(editMode);
		txtLength.setEditable(editMode);
		txtSowing_time.setEditable(editMode);
		txtTransplant_time.setEditable(editMode);
		txtIn_ground.setEditable(editMode);
		txtPlanting_scheme.setEditable(editMode);
		txtGround.setEditable(editMode);

		// Buttons
		btnNew.setDisable(editMode);
		btnNewCopy.setDisable(editMode);
		btnEdit.setDisable(editMode);
		btnDelete.setDisable(editMode);
		btnOk.setDisable(!editMode);
		btnCancel.setDisable(!editMode);
	}

	@Override
	public Seed getEditedRecord() {
		Seed seed = new Seed();

		Integer id = null;
		ComboItem ci = null;

		ci = cboBrand.getValue();
		if (ci != null)
			id = cboBrand.getValue().getId();
		seed.setBrand_id(id);

		ci = cboCategory.getValue();
		if (ci != null)
			id = cboCategory.getValue().getId();
		seed.setCategory_id(id);

		ci = cboStatus.getValue();
		if (ci != null)
			id = cboStatus.getValue().getId();
		seed.setStatus_id(id);

		ci = cboColor.getValue();
		if (ci != null)
			id = cboColor.getValue().getId();
		seed.setColor_id(id);

		ci = cboKind.getValue();
		if (ci != null)
			id = cboKind.getValue().getId();
		seed.setKind_id(id);

		seed.setName(txtName.getText());
		seed.setArticle(txtArticle.getText());
		seed.setHybrid(chkHybrid.isSelected());
		seed.setDescription(tarDescription.getText());
		seed.setVegetation(txtVegetation.getText());
		seed.setMass(txtMass.getText());
		seed.setHeight(txtHeight.getText());
		seed.setYield(txtYield.getText());
		seed.setLength(txtLength.getText());
		seed.setSowing_time(txtSowing_time.getText());
		seed.setTransplant_time(txtTransplant_time.getText());
		seed.setIn_ground(txtIn_ground.getText());
		seed.setPlanting_scheme(txtPlanting_scheme.getText());
		seed.setGround(txtGround.getText());

		if (!isAppendMode())
			seed.setId(getCurrentRecord().getId());

		return seed;
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

		if (getDB().brand.isEdited())
			cboBrand.setItems(getDB().brand.getListForCombo());

		if (getDB().category.isEdited())
			cboCategory.setItems(getDB().category.getListForCombo());

		if (getDB().status.isEdited())
			cboStatus.setItems(getDB().status.getListForCombo());

		if (getDB().color.isEdited())
			cboColor.setItems(getDB().color.getListForCombo());

		if (getDB().kind.isEdited())
			cboBrand.setItems(getDB().kind.getListForCombo());

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
	public void writeProp() {
		double[] divPos = splitPane.getDividerPositions();
		Proper.setProperty("splitPane.DividerPositions", divPos[0]);
	}

	@Override
	protected void doDisplay(Seed seed) {
		// Objects in the table view do not contain all the fields of the record (to
		// speed up table reading and save memory), so before displaying the record it
		// must be read
		if (seed == null) {
			seed = new Seed();
		} else {
			if (seed.getId() != null)
				getDB().seed.get(seed);
		}

		ComboItem.setValue(seed.getBrand_id() == null ? 0 : seed.getBrand_id(), cboBrand);
		ComboItem.setValue(seed.getCategory_id() == null ? 0 : seed.getCategory_id(), cboCategory);
		ComboItem.setValue(seed.getStatus_id() == null ? 0 : seed.getStatus_id(), cboStatus);
		ComboItem.setValue(seed.getColor_id() == null ? 0 : seed.getColor_id(), cboColor);
		ComboItem.setValue(seed.getKind_id() == null ? 0 : seed.getKind_id(), cboKind);

		txtName.setText(seed.getName());
		txtArticle.setText(seed.getArticle());
		chkHybrid.setSelected(seed.getHybrid());
		tarDescription.setText(seed.getDescription());
		txtVegetation.setText(seed.getVegetation());
		txtMass.setText(seed.getMass());
		txtHeight.setText(seed.getHeight());
		txtYield.setText(seed.getYield());
		txtLength.setText(seed.getLength());
		txtSowing_time.setText(seed.getSowing_time());
		txtTransplant_time.setText(seed.getTransplant_time());
		txtIn_ground.setText(seed.getIn_ground());
		txtPlanting_scheme.setText(seed.getPlanting_scheme());
		txtGround.setText(seed.getGround());
	}

	@Override
	protected boolean doNew() {
		if (!super.doNew())
			return false;

		doDisplay(new Seed());

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

		Seed seed = getCurrentRecord();
		if (seed != null) {
			String mes = Loc.get("are_you_sure_you_want_to_remove_the_seed") + ": \"" + seed.getName() + "\"?";
			Dialogues.ConMes(mes, (event) -> {
				if (getDB().seed.remove(seed)) {
					getTableView().getItems().remove(seed);
					getTableView().getSelectionModel().selectBelowCell();
				}
			});
		}

		requestFocusForTableView();
		return true;
	}

	@Override
	public void doOk() {
		Seed seed = getEditedRecord();
		if (!check(seed))
			return;

		if (isAppendMode()) {
			getDB().seed.setEdited(true);
			getDB().seed.add(seed);
			getTableView().getItems().add(seed);
		} else {
			if (getCurrentRecord().hasDifference(seed)) {
				String oldValue = getCurrentRecord().getName() == null ? "" : getCurrentRecord().getName().trim();
				String newValue = seed.getName() == null ? "" : seed.getName().trim();
				if (!oldValue.equals(newValue))
					getDB().seed.setEdited(true);

				getDB().seed.set(seed, true);
				getTableView().getItems().set(getTableView().getSelectionModel().getSelectedIndex(), seed);
			}
		}

		// The edited/new entry is made current
		getTableView().getSelectionModel().select(seed);

		// Scroll to new entry only when adding
		if (isAppendMode())
			getTableView().scrollTo(seed);

		doCancel(false);
	}
}
