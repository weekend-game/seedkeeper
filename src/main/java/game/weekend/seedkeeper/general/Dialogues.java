package game.weekend.seedkeeper.general;

import java.util.concurrent.atomic.AtomicReference;

import game.weekend.seedkeeper.SeedKeeper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dialogues {

	public static void ErrMes(String mes) {
		final Stage dialog = new Stage();
		dialog.setTitle(SeedKeeper.APP_NAME);
		dialog.setMinWidth(450);
		dialog.initModality(Modality.APPLICATION_MODAL);

		Label l = new Label(mes);

		Button b = new Button("OK");
		b.setCancelButton(true);
		b.setOnAction(arg -> dialog.close());

		VBox vb = new VBox();
		vb.setSpacing(20);
		vb.setPadding(new Insets(15, 20, 15, 20));
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().addAll(l, b);

		dialog.setScene(new Scene(vb));
		dialog.show();
	}

	public static void ConMes(String mes, EventHandler<ActionEvent> eh) {
		final Stage dialog = new Stage();
		dialog.setTitle(SeedKeeper.APP_NAME);
		dialog.setMinWidth(450);
		dialog.initModality(Modality.APPLICATION_MODAL);

		Label l = new Label(mes);
		l.setAlignment(Pos.BASELINE_CENTER);

		Button yesBtn = new Button(Loc.get("yes"));
		yesBtn.setDefaultButton(true);
		yesBtn.setOnAction(arg -> {
			dialog.close();
			eh.handle(arg);
		});

		Button noBtn = new Button(Loc.get("no"));
		noBtn.setCancelButton(true);
		noBtn.setOnAction(arg -> dialog.close());

		HBox hb = new HBox();
		hb.setAlignment(Pos.BASELINE_CENTER);
		hb.setSpacing(40.0);
		hb.getChildren().addAll(yesBtn, noBtn);

		VBox vb = new VBox();
		vb.setSpacing(20);
		vb.setPadding(new Insets(15, 20, 15, 20));
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().addAll(l, hb);

		dialog.setScene(new Scene(vb));
		dialog.show();
	}

	public static boolean getConfirmation(String mes) {
		AtomicReference<Boolean> result = new AtomicReference<>(false);

		Stage dialog = new Stage();
		dialog.setTitle(SeedKeeper.APP_NAME);
		dialog.setMinWidth(450);
		dialog.initModality(Modality.APPLICATION_MODAL);

		Label l = new Label(mes);
		l.setAlignment(Pos.BASELINE_CENTER);

		Button btnYes = new Button(Loc.get("yes"));
		btnYes.setDefaultButton(true);
		btnYes.setOnAction(arg -> {
			result.set(true);
			dialog.close();
		});

		Button btnNo = new Button(Loc.get("no"));
		btnNo.setCancelButton(true);
		btnNo.setOnAction(arg -> {
			arg.consume();
			dialog.close();
		});

		HBox hbButtons = new HBox();
		hbButtons.setAlignment(Pos.BASELINE_CENTER);
		hbButtons.setSpacing(40.0);
		hbButtons.getChildren().addAll(btnYes, btnNo);

		VBox vb = new VBox();
		vb.setSpacing(20);
		vb.setPadding(new Insets(15, 20, 15, 20));
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().addAll(l, hbButtons);

		dialog.setScene(new Scene(vb));
		btnNo.requestFocus();
		dialog.showAndWait();

		return result.get();
	}

	public static void ConMes2(String mes, EventHandler<ActionEvent> ehYes, EventHandler<ActionEvent> ehNo) {
		final Stage dialog = new Stage();
		dialog.setTitle(SeedKeeper.APP_NAME);
		dialog.setMinWidth(450);
		dialog.initModality(Modality.APPLICATION_MODAL);

		Label l = new Label(mes);
		l.setAlignment(Pos.BASELINE_CENTER);

		Button yesBtn = new Button(Loc.get("yes"));
		yesBtn.setDefaultButton(true);
		yesBtn.setOnAction(arg -> {
			dialog.close();
			ehYes.handle(arg);
		});

		Button noBtn = new Button(Loc.get("no"));
		noBtn.setCancelButton(true);
		noBtn.setOnAction(arg -> {
			dialog.close();
			ehNo.handle(arg);
		});

		HBox hb = new HBox();
		hb.setAlignment(Pos.BASELINE_CENTER);
		hb.setSpacing(40.0);
		hb.getChildren().addAll(yesBtn, noBtn);

		VBox vb = new VBox();
		vb.setSpacing(20);
		vb.setPadding(new Insets(15, 20, 15, 20));
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().addAll(l, hb);

		dialog.setScene(new Scene(vb));
		dialog.show();
	}
}
