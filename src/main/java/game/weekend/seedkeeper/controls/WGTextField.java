package game.weekend.seedkeeper.controls;

import game.weekend.seedkeeper.general.Journal;
import javafx.scene.control.TextField;

public class WGTextField extends TextField {

    public WGTextField() {
        super();

        // TextField generates two events when Esc is pressed.
        // This corrects this misunderstanding.
        setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ESCAPE"))
                e.consume();
        });
    }

    /**
     * If you specify a journal in the constructor, TextField will register itself in
	 * that journal as the current one when it receives focus. This will help you return
	 * to the same field when returning, for example, from another journal.
     */
    public WGTextField(Journal<?> journal) {
        this();

        focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue)
                if (journal != null)
                    journal.setCurrentNode(WGTextField.this);
        });
    }

}
