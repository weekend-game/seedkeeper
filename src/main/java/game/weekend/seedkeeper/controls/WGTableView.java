package game.weekend.seedkeeper.controls;

import game.weekend.seedkeeper.general.Journal;
import javafx.scene.control.TableView;

public class WGTableView<T> extends TableView<T> {

    /**
     * If you specify a journal in the constructor, TableView will register itself in
	 * that journal as the current one when it receives focus. This will help you return
	 * to the same field when returning, for example, from another journal.
     */
    public WGTableView(Journal<?> journal) {
        super();

        focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue)
                if (journal != null)
                    journal.setCurrentNode(WGTableView.this);
        });
    }
}
