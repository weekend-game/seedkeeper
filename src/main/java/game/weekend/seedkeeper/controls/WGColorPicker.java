package game.weekend.seedkeeper.controls;

import game.weekend.seedkeeper.general.Journal;
import javafx.scene.control.ColorPicker;

public class WGColorPicker extends ColorPicker {

	/**
	 * If you specify a journal in the constructor, ColorPicker will register itself in
	 * that journal as the current one when it receives focus. This will help you return
	 * to the same field when returning, for example, from another journal.
	 */
	public WGColorPicker(Journal<?> journal) {
		super();

		focusedProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue)
				if (journal != null)
					journal.setCurrentNode(WGColorPicker.this);
		});
	}
}
