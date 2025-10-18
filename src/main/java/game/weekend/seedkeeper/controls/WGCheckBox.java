package game.weekend.seedkeeper.controls;

import game.weekend.seedkeeper.general.Journal;
import javafx.scene.control.CheckBox;

public class WGCheckBox extends CheckBox {

	/**
	 * If you specify a journal in the constructor, CheckBox will register itself in
	 * that journal as the current one when it receives focus. This will help you
	 * return to the same field when returning, for example, from another journal.
	 */
	public WGCheckBox(String text, Journal<?> journal) {
		super(text);

		focusedProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue)
				if (journal != null)
					journal.setCurrentNode(WGCheckBox.this);
		});
	}
}
