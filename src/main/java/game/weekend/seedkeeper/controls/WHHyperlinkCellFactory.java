package game.weekend.seedkeeper.controls;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class WHHyperlinkCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {

	@Override
	public TableCell<T, String> call(TableColumn<T, String> param) {
		return new TableCell<>() {
			final Hyperlink hyperlink = new Hyperlink();

			{
				hyperlink.setOnAction(event -> {
					String url = getItem();
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (IOException | URISyntaxException e) {
						System.out.println(e);
					}
				});
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
				} else {
					hyperlink.setText(item);
					setGraphic(hyperlink);
				}
			}
		};
	}

}
