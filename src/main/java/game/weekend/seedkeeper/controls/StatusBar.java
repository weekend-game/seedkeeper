package game.weekend.seedkeeper.controls;

import javax.swing.Timer;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class StatusBar {

	private static final int DELAY = 5000;
	private final Label lblStatus;
	private Timer tmr;

	public StatusBar(Label lblStatus) {
		this.lblStatus = lblStatus;
	}

	public void showMessage(String mes) {
		showMessage(mes, false);
	}

	public void showMessageWithDelay(String mes) {
		showMessage(mes, true);
	}

	private void showMessage(String mes, boolean withDelay) {
		if (withDelay)
			if (tmr != null && tmr.isRunning())
				tmr.stop();

		Platform.runLater(() -> {
			lblStatus.setTextFill(Color.BLACK);
			lblStatus.setText(mes);
		});

		if (withDelay) {
			tmr = new Timer(DELAY, event -> Platform.runLater(() -> lblStatus.setText("")));
			tmr.start();
		}
	}

	public void showErrorWithDelay(String mes) {
		showError(mes, true);
	}

	private void showError(String mes, boolean withDelay) {
		if (withDelay)
			if (tmr != null && tmr.isRunning())
				tmr.stop();

		Platform.runLater(() -> {
			lblStatus.setTextFill(Color.RED);
			lblStatus.setText(mes);
		});

		if (withDelay) {
			tmr = new Timer(DELAY, event -> Platform.runLater(() -> lblStatus.setText("")));
			tmr.start();
		}
	}
}
