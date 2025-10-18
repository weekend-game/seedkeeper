package game.weekend.seedkeeper.controls;

import static java.lang.Thread.sleep;

import game.weekend.seedkeeper.db.ComboItem;
import game.weekend.seedkeeper.general.Journal;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;

public class WGSearchableComboBox extends ComboBox<ComboItem> {

	private Thread searchThread = null;
	private boolean searchThreadRun = false;

	private final StringBuilder searchText = new StringBuilder();
	private boolean searchPress = false;

	public WGSearchableComboBox() {
		super();

		// Two events are intercepted

		// 1) By clicking the button, we add a letter to searchText and search for
		// searchText in the list without regard to case
		this.setOnKeyTyped(keyEvent -> {

			String s = keyEvent.getCharacter().trim();
			if (s.length() > 0) {
				searchPress = true;
				searchText.append(s.toUpperCase());

				// If searchText is found,
				int i = seek(searchText.toString());
				if (i >= 0) {
					// we make this entry current
					getSelectionModel().clearAndSelect(i);

					// and scroll the list so that the current entry is visible
					ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>) getSkin();
					((ListView<?>) skin.getPopupContent()).scrollTo(i);
				}
			}
		});

		// 2) When focus is gained, a thread is started that, after a second of
		// inactivity, clears searchText and thus begins searching for a new word.
		// When
		// focus is lost, the thread stops.
		this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {

			if (newValue) {
				// A thread that, after a second of inactivity, clears searchText and thus
				// starts searching for a new word.
				searchThread = new Thread(() -> {
					searchText.delete(0, searchText.length());
					searchThreadRun = true;
					while (searchThreadRun) {
						try {
							sleep(700);
						} catch (InterruptedException ignored) {
						}

						if (searchText.length() > 0) {
							if (searchPress) {
								searchPress = false;
							} else {
								searchText.delete(0, searchText.length());
							}
						}
					}
				});
				searchThread.setDaemon(true); // Otherwise, this thread will not allow the application to complete.
				searchThread.start();

			} else {
				searchThreadRun = false;
			}
		});
	}

	/**
	 * If you specify a journal in the constructor, ComboBox will register itself in
	 * that journal as the current one when it receives focus. This will help you
	 * return to the same field when returning, for example, from another journal.
	 */
	public WGSearchableComboBox(Journal<?> journal) {
		this();

		focusedProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue)
				if (journal != null)
					journal.setCurrentNode(WGSearchableComboBox.this);
		});
	}

	private int seek(String pattern) {
		int i = -1;
		int len = pattern.length();
		ObservableList<ComboItem> list = getItems();

		if (len == 0) {
			return -1;
		} else {
			for (ComboItem lfc : list) {
				++i;
				String s = lfc.getName().toUpperCase();
				if (s.length() >= len) {
					s = s.substring(0, len);
					int r = pattern.compareTo(s);
					if (r == 0)
						break;
					if (r < 0)
						return -1;
				}
			}
			return i;
		}
	}
}
