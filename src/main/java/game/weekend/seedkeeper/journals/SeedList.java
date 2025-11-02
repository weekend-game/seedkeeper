package game.weekend.seedkeeper.journals;

import java.util.function.Predicate;

import game.weekend.seedkeeper.controls.WGSearchableComboBox;
import game.weekend.seedkeeper.controls.WGTextField;
import game.weekend.seedkeeper.db.ComboItem;
import game.weekend.seedkeeper.general.Loc;
import game.weekend.seedkeeper.general.Proper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;

public class SeedList {
	private final WGSearchableComboBox comboBox = new WGSearchableComboBox();
	private final TableView<Seed> tableView;
	private final WGTextField textField = new WGTextField();

	private final SeedJournal journal;

	private ObservableList<Seed> tableItems;
	private FilteredList<Seed> filteredData;

	/**
	 * Работа с таблицей. Кроме собственно таблицы TableView, используется ComboBox
	 * для фильтрации по категориям и TextField для фильтрации по набираемому
	 * тексту. Изменение в ComboBox приводит к чтению списка из БД. Набор текста в
	 * TextField приводит к фильтации списка посредством установки предиката в
	 * FilteredList.
	 */
	public SeedList(SeedJournal journal) {
		this.tableView = journal.getTableView();
		this.journal = journal;
	}

	public void makeTableControls(int category) {
		// Чтобы можно было изменять cheсkbox в первой колонке. Для колонок
		// для которых редактирование ненужно, это ставится персонально.
		tableView.setEditable(true);

		// Отображаемые колонки в TableView //

		// Первая колонка

		// Checkbox для заголовка колонки
		CheckBox checkAll = new CheckBox();
		checkAll.setOnAction(e -> tableView.getItems().forEach(p -> p.markProperty().setValue(checkAll.isSelected())));
		// Собственно колонка
		TableColumn<Seed, Boolean> mark = new TableColumn<>(); // Колонка создаётся без заголовка
		mark.setGraphic(checkAll); // В качестве заголовка устанавливается checkAll
		mark.setCellFactory(CheckBoxTableCell.forTableColumn(i -> tableView.getItems().get(i).markProperty()));
		mark.setEditable(true);
		mark.setPrefWidth(30);
		tableView.getColumns().add(mark);

		// Остальные колонки экранной таблицы

		tableView.getColumns().add(journal.getTextColumn(Loc.get("name"), "name", 200));
		tableView.getColumns().add(journal.getTextColumn(Loc.get("article"), "article", 120));

//		tableView.getColumns().add(journal.getTextColumn("Номер", "numb", 45));
//		tableView.getColumns().add(journal.getIntColumn("Год", "use_by", 35));
//		tableView.getColumns().add(journal.getTextColumn("Вегетация", "vegetation", 60));
//		tableView.getColumns().add(journal.getTextColumn("Бренд", "brand", 80));
//		tableView.getColumns().add(journal.getTextColumn("Качество", "quality", 70));
//		tableView.getColumns().add(journal.getBooleanColumn("Гибрид", "hybrid", 50));

		// Раскрашиваю строки в зависимости от значения quality
//		tableView.setRowFactory(tv -> new TableRow<>() {
//			@Override
//			protected void updateItem(Seed item, boolean empty) {
//				super.updateItem(item, empty);
//
//				if (tv.getSelectionModel().getSelectedItem() == item || item == null || item.getQuality_color() == null)
//					setStyle("");
//				else
//					setStyle("-fx-background-color: #" + item.getQuality_color().trim() + ";");
//			}
//		});

		journal.makeTableHandlers(tableView);

		// ComboBox - указание категории //

		// Перехват смены значения ComboBox-а
		comboBox.valueProperty().addListener((ov, oldCategory, newCategory) -> {
			if (newCategory != null) {
				refresh(newCategory);
			}
		});

		// Данные для отображеня в ComboBox-е
		comboBox.setItems(journal.getDB().category.getListForCombo());
		comboBox.getItems().set(0, new ComboItem(0, Loc.get("all_categories")));

		// Тут сработает перехват смены значения comboBox (см. выше) и таблица
		// заполнится данными
		comboBox.getSelectionModel().select(category);

		// Настраиваем текстовое поле для фильтрации //

		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			// Ставлю предикат
			filteredData.setPredicate(seed -> {
				String pattern = newValue.trim().toLowerCase();

				if (pattern.length() < 2)
					return true;

				if (seed.getName().toLowerCase().contains(pattern))
					return true;

				return false;
			});

			// А теперь прошу TableView спозиционироваться на первом элементе списка
			tableView.getSelectionModel().selectFirst();
		});
	}

	public VBox getTablePane() {
		{
			int defaultCategory = Proper.getProperty("Filter.Category.Index", 0);
			makeTableControls(defaultCategory);

			comboBox.setPrefWidth(1024);

			tableView.setPrefHeight(4096);
		}

		VBox vb = new VBox(5, comboBox, tableView, textField);
		vb.setPadding(new Insets(5, 5, 5, 5));
		return vb;
	}

	private void refresh(ComboItem category) {
		// На эту позицию в списке постараемся вернуться, если не получится, то на первую
		Seed seed = tableView.getSelectionModel().getSelectedItem();

		// Получаем список из БД (ObservableList)
		tableItems = journal.getDB().seed.getList(comboBox.getSelectionModel().getSelectedItem().getId());

		// Делаем фильтрованный список с прежним предикатом, если он есть
		Predicate<? super Seed> oldPredicate = null;
		if (filteredData != null)
			oldPredicate = filteredData.getPredicate();

		filteredData = new FilteredList<>(tableItems, oldPredicate);

		// Из фильтрованного списка делаем сортированный и он
		// становится списком для отображения экранной таблицей
		SortedList<Seed> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);

		// Попытка позиционироваться на ранее запомненную строку, если не получится, то
		// на первую
		boolean selected = false;
		if (seed != null)
			for (Seed s : filteredData)
				if (s.getId() == (int) seed.getId()) {
					tableView.getSelectionModel().select(s);
					selected = true;
					break;
				}

		if (!selected)
			tableView.getSelectionModel().selectFirst();
	}

	public void setDisable(boolean value) {
		comboBox.setDisable(value);
		tableView.setDisable(value);
		textField.setEditable(!value);
	}

	public void add(Seed seed) {
		tableItems.add(seed);
	}

	public void set(Seed seed) {
		int i = -1;
		for (Seed s : tableItems) {
			++i;
			if (s.getId() == (int) seed.getId()) {
				tableItems.set(i, seed);
				tableView.getSelectionModel().clearAndSelect(i);
				break;
			}
		}
	}

	public void remove(Seed seed) {
		tableItems.remove(seed);
	}

	public int getCategoryID() {
		return comboBox.getSelectionModel().getSelectedItem().getId();
	}

	public int getFilterCategoryIndex() {
		return comboBox.getSelectionModel().getSelectedIndex();
	}
}
