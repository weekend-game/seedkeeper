package game.weekend.seedkeeper.controls;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import game.weekend.seedkeeper.general.Loc;
import game.weekend.seedkeeper.general.Proper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MakerPhoto {
	private static final String currntFolderProp = "MakerPhoto.currentFolder";
	private static final KeyCombination keyCombC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
	private static final KeyCombination keyCombV = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
	private static final Label lblStatus = new Label("");
	private static final StatusBar statusBar = new StatusBar(lblStatus);
	private static MakerPhoto makerPhoto;
	private final Stage stage;
	private final VBox photoPane;
	private final ImageView imageView;
	private final Rectangle rectangle;
	private final Button btnLoadPhoto = new Button(Loc.get("load_photo") + "...");;
	private final Button btnSavePhoto = new Button(Loc.get("save_photo_to_disk") + "...");

	private final FileChooser fileChooser;
	private boolean editable;
	private boolean edited;

	private ContextMenu contextMenu;
	private MenuItem itemCopy;
	private MenuItem itemPast;

	private MakerPhoto(Stage stage) {
		this.stage = stage;

		makeContextMenu();

		Image image = new Image(InputStream.nullInputStream());
		imageView = new ImageView(image);

//        imageView.setBlendMode(BlendMode.LIGHTEN);
		rectangle = new Rectangle(image.getHeight(), image.getHeight(), Color.TOMATO);
		Group g = new Group(rectangle, imageView);

		ScrollPane scrollPane = new ScrollPane(g);
		scrollPane.setMinWidth(600);
		scrollPane.setPrefWidth(600);
		scrollPane.setMinHeight(450);
		scrollPane.setPrefHeight(550);
		makeMouseHandler(scrollPane);
		makeKeyHandler(scrollPane);

		String rb1Text = Loc.get("as_is");
		String rb2Text = Loc.get("by_width");
		String rb3Text = Loc.get("by_height");

		final ToggleGroup tg = new ToggleGroup();
		tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				Toggle toggle = tg.getSelectedToggle();
				if (toggle != null) {
					String text = ((RadioButton) toggle).getText();

					if (text.equals(rb1Text)) {
						imageView.setFitWidth(0);
						imageView.setFitHeight(0);

						rectangle.setWidth(image.getWidth());
						rectangle.setHeight(image.getHeight());
					}

					if (text.equals(rb2Text)) {
						imageView.setFitWidth(scrollPane.getWidth());
						imageView.setFitHeight(0);
						imageView.setPreserveRatio(true);

						rectangle.setWidth(imageView.getFitWidth());
						rectangle.setHeight(image.getHeight());
					}

					if (text.equals(rb3Text)) {
						imageView.setFitWidth(0);
						imageView.setFitHeight(scrollPane.getHeight());
						imageView.setPreserveRatio(true);

						rectangle.setWidth(image.getWidth());
						rectangle.setHeight(imageView.getFitHeight());
					}
				}
			}
		});

		RadioButton rb1 = new RadioButton(rb1Text);
		rb1.setToggleGroup(tg);
		RadioButton rb2 = new RadioButton(rb2Text);
		rb2.setToggleGroup(tg);
		RadioButton rb3 = new RadioButton(rb3Text);
		rb3.setToggleGroup(tg);

		// Текущий первый вариант
		tg.selectToggle(rb1);

		btnLoadPhoto.setOnAction((e) -> makerPhoto.loadPhotoFromFile());
		btnSavePhoto.setOnAction((e) -> makerPhoto.savePhotoToFile());

		HBox buttonPane = new HBox();
		buttonPane.setSpacing(5);
		buttonPane.setPadding(new Insets(10, 10, 10, 10));
		Label lblSpacing = new Label();
		lblSpacing.setPrefWidth(50);
		buttonPane.getChildren().addAll(rb1, rb2, rb3, lblSpacing, lblStatus, btnLoadPhoto, btnSavePhoto);

		photoPane = new VBox();
		photoPane.getChildren().addAll(scrollPane, buttonPane);

		fileChooser = new FileChooser();
		makeFileChooser();

		setEditable(false);
	}

	public static MakerPhoto getMakerPhoto(Stage stage) {
		if (makerPhoto == null)
			makerPhoto = new MakerPhoto(stage);
		return makerPhoto;
	}

	public boolean isEdited() {
		return edited;
	}

	public VBox getPhotoPane() {
		return photoPane;
	}

	private void loadPhotoFromFile() {
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			try (FileInputStream fis = new FileInputStream(file)) {
				setImage(new Image(new ByteArrayInputStream(fis.readAllBytes())));
				edited = true;
				Proper.setProperty(currntFolderProp, file.getParent());
			} catch (IOException e) {
				System.out.println("MakerPhoto.setPhotoFromFile() - " + e);
			}
		}
	}

	private void savePhotoToFile() {
		if (imageView.getImage().getHeight() == 0)
			return;

		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			BufferedImage bi = SwingFXUtils.fromFXImage(imageView.getImage(), null);
			try {
				String fileName = file.getName();
				int i = fileName.lastIndexOf(".");
				String fileExtension = i < 0 ? "" : fileName.substring(i + 1, fileName.length());

				if (!fileExtension.equalsIgnoreCase("jpg") && !fileExtension.equalsIgnoreCase("gif")
						&& !fileExtension.equalsIgnoreCase("png") && !fileExtension.equalsIgnoreCase("bmp"))
					fileExtension = "jpg";

				ImageIO.write(bi, fileExtension, file);
			} catch (IOException e) {
				System.out.println("MakerPhoto.savePhotoToFile() - " + e);
			}
		}
	}

	public void setPhotoFromByteArray(byte[] byteArray) {
		if (byteArray == null)
			setImage(new Image(InputStream.nullInputStream()));
		else
			setImage(new Image(new ByteArrayInputStream(byteArray)));
	}

	public byte[] getByteArray() {

		BufferedImage bi = SwingFXUtils.fromFXImage(imageView.getImage(), null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (bi != null)
			try {
				ImageIO.write(bi, "BMP", baos);
			} catch (IOException e) {
				System.out.println("MakerPhoto.getByteArray() - " + e);
			}
		return baos.toByteArray();
	}

	public void setEditable(boolean editable) {
		edited = false;
		this.editable = editable;
		itemPast.setDisable(!editable);
		btnLoadPhoto.setDisable(!editable);
		btnSavePhoto.setDisable(!editable);
	}

	public void showMessage(String message) {
		statusBar.showMessage(message);
	}

	private void makeMouseHandler(ScrollPane scrollPane) {
		scrollPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				contextMenu.show(scrollPane, e.getScreenX(), e.getScreenY());
				e.consume();
			}
		});
	}

	private void makeKeyHandler(ScrollPane scrollPane) {
		scrollPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
			if (keyCombC.match(event)) {
				imageCopy();
			} else if (keyCombV.match(event)) {
				if (editable)
					imagePast();
			}
		});
	}

	private void makeContextMenu() {
		contextMenu = new ContextMenu();

		itemCopy = new MenuItem("Копировать");
		itemCopy.setOnAction(e -> imageCopy());
		itemCopy.setAccelerator(keyCombC);
		contextMenu.getItems().add(itemCopy);

		contextMenu.getItems().add(new SeparatorMenuItem());

		itemPast = new MenuItem("Вставить");
		itemPast.setOnAction(e -> imagePast());
		itemPast.setAccelerator(keyCombV);
		contextMenu.getItems().add(itemPast);
	}

	private void makeFileChooser() {
		fileChooser.setTitle("Укажите фото");
		fileChooser.setInitialDirectory(new File(Proper.getProperty(currntFolderProp, "C:\\")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("GIF", "*.gif"), new FileChooser.ExtensionFilter("PNG", "*.png"),
				new FileChooser.ExtensionFilter("BMP", "*.bmp"), new FileChooser.ExtensionFilter("Все файлы", "*.*"));
	}

	private void imageCopy() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.putImage(imageView.getImage());
		clipboard.setContent(content);
	}

	private void imagePast() {
		final Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			try {
				BufferedImage bi = (BufferedImage) t.getTransferData(DataFlavor.imageFlavor);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(bi, "BMP", baos);
				byte[] b = baos.toByteArray();

				Image image = new Image(new ByteArrayInputStream(b));
				setImage(image);

				edited = true;
			} catch (IOException | UnsupportedFlavorException e) {
				System.out.println("imagePast() : " + e);
			}
		}
	}

	private void setImage(Image image) {
		imageView.setImage(image);
		rectangle.setWidth(image.getWidth());
		rectangle.setHeight(image.getHeight());
	}
}
