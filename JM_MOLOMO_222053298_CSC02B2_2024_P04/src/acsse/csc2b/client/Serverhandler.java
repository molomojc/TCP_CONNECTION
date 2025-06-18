package acsse.csc2b.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

/*
 * @author Jm Molomo
 * @version Practical 04
 */
public class Serverhandler {
	private int Port = 5431;
	private String localHost = "localhost";
	private Socket ss;
	private PrintWriter out; // writing to
	private BufferedReader in; // reading
	private DataOutputStream dous;
	private DataInputStream dips;
	private Pane _pane;
	
	public Serverhandler() {
		_pane = new Pane();
		
		starti();
	}
	public Pane retpane() {
		return _pane;
	}
	
	public void starti() {
		// Root Pane
		Pane root = new Pane();
		root.setPrefSize(700, 404);

		// Top Pink Pane with Label
		Pane topPane = new Pane();
		topPane.setLayoutX(-1);
		topPane.setLayoutY(-1);
		topPane.setPrefSize(700, 49);
		topPane.setStyle("-fx-background-color: pink;");

		Label label = new Label("Happy! Women' s Month");
		label.setLayoutX(276);
		label.setLayoutY(-4);
		label.setPrefSize(181, 58);
		label.setFont(new Font("Franklin Gothic Medium", 15));
		topPane.getChildren().add(label);

		// Titled Pane with Controls
		TitledPane titledPane = new TitledPane("Controls", null);
		titledPane.setAnimated(false);
		titledPane.setLayoutY(48);
		titledPane.setPrefSize(217, 273);

		AnchorPane controlPane = new AnchorPane();
		controlPane.setPrefSize(244, 176);
		TextField serverIdField = new TextField();
		serverIdField.setLayoutX(75);
		serverIdField.setLayoutY(13);
		serverIdField.setPrefSize(115, 28);
		Button connectButton = new Button("Connect:");
		connectButton.setLayoutX(8);
		connectButton.setLayoutY(14);
		connectButton.setOnAction(e -> {
			connect();
			if (ss != null)
				serverIdField.setText("Connected.");
			serverIdField.setPromptText("The list of available messages: ");
		});
		TextField uploadStatusField = new TextField();
		uploadStatusField.setLayoutX(91);
		uploadStatusField.setLayoutY(99);
		uploadStatusField.setPrefSize(124, 16);
		uploadStatusField.setPromptText("Upload Status");
		TextField uploadDetailField = new TextField();
		uploadDetailField.setLayoutX(4);
		uploadDetailField.setLayoutY(59);
		uploadDetailField.setPrefSize(207, 28);
		uploadDetailField.setPromptText("<ID> <NAME> <SIZE>");

		Button chooseImageButton = new Button("Choose Image");
		chooseImageButton.setLayoutX(10);
		chooseImageButton.setLayoutY(136);
		chooseImageButton.setPrefSize(195, 28);
		chooseImageButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
			Thread chooseImage = new Thread(() -> {
				upload(uploadDetailField, file, uploadStatusField);
				String lines[] = uploadDetailField.getText().split(" ");
				if (!new File("data/server/" + lines[1] + ".png").exists()) {
					uploadStatusField.setText("Success");
				} else {
					uploadStatusField.setText("Failed");
				} 
				
				stop();
				connect();
			});
			chooseImage.start();

		});
		TextArea textArea = new TextArea();
		textArea.setLayoutX(9);
		textArea.setLayoutY(1);
		textArea.setPrefSize(191, 67);

		Button showImageButton = new Button("Available images");
		showImageButton.setLayoutX(9);
		showImageButton.setLayoutY(172);
		showImageButton.setPrefSize(197, 29);
		showImageButton.setOnAction(event -> {

			textArea.setEditable(false);
			Thread ShowThread = new Thread(() -> {
				pull(textArea);
				stop();
				connect();
			});
			ShowThread.start();
		});
		TextField idField = new TextField();
		idField.setLayoutX(8);
		idField.setLayoutY(207);
		idField.setPrefSize(115, 28);

		Button fetchImageButton = new Button("Download");
		fetchImageButton.setLayoutX(133);
		fetchImageButton.setLayoutY(208);
		fetchImageButton.setOnAction(event -> {
			// String line = Fecth_ID.getText();
			Thread Display = new Thread(() -> {
				download(idField);

				stop();
				connect();
			});
			Display.start();
		});

		

		Pane imagePane = new Pane();
		imagePane.setLayoutX(217);
		imagePane.setLayoutY(49);
		imagePane.setPrefSize(482, 351);
		//imagePane.setStyle("-fx-background-color: blue;");

		// VBox for displaying images
		VBox imgDisplayVBox = new VBox();
		imgDisplayVBox.setStyle("-fx-background-color: white;");
		imgDisplayVBox.setLayoutX(0); // Ensure it's positioned to be visible within imagePane
		imgDisplayVBox.setLayoutY(0); // Positioned below the TextField
		imgDisplayVBox.setPrefSize(482, 351); // Adjust size to fit within imagePane
		imgDisplayVBox.setSpacing(10); // Set spacing between images
	
		// Add the VBox for image display to the imagePane
		imagePane.getChildren().add(imgDisplayVBox);
		
		
		Button downloadButton = new Button("Show Downloaded Images");
		downloadButton.setLayoutX(1);
		downloadButton.setLayoutY(1);
		downloadButton.setPrefSize(160, 16);
       imagePane.getChildren().add(downloadButton);
		downloadButton.setOnAction(e -> {
		    imgDisplayVBox.getChildren().clear(); // Clear previous images before displaying new ones

		    File imagesFolder = new File("data/client");
		    File[] imageFiles = imagesFolder.listFiles((dir, name) -> name.endsWith(".png")); // Filter for PNG files

		    if (imageFiles != null) {
		        for (File imageFile : imageFiles) {
		        	
		            System.out.println(imageFile.getAbsolutePath());
		            
		            Image image = new Image("file:" + imageFile.getAbsolutePath());
		            ImageView _imageView = new ImageView(image);
		            
		            
		            _imageView.setFitWidth(482); // Adjust the width as needed
		            _imageView.setFitHeight(320); // Adjust the height as needed
		            _imageView.setPreserveRatio(true); // Preserve the image ratio

		            imgDisplayVBox.getChildren().add(_imageView); // Add each image to the VBox
		        }
		    } else {
		        System.out.println("No images found in data/client/");
		    }
		    connect();
		});


		/*********** end ***********************/

		// Add all controls to the AnchorPane
		controlPane.getChildren().addAll(connectButton, chooseImageButton, showImageButton, idField, fetchImageButton,
				serverIdField, uploadDetailField, uploadStatusField);

		// Set the content of the TitledPane to the AnchorPane
		titledPane.setContent(controlPane);

		// ScrollPane with TextArea
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setLayoutX(-1);
		scrollPane.setLayoutY(320);
		scrollPane.setPrefSize(217, 82);

		AnchorPane textAreaPane = new AnchorPane();
		textAreaPane.setPrefSize(209, 144);

		textAreaPane.getChildren().add(textArea);
		scrollPane.setContent(textAreaPane);

		// Add everything to the root pane
		root.getChildren().addAll(topPane, titledPane, imagePane, scrollPane);
		_pane.getChildren().addAll(root);
	}

	public void upload(TextField uploadDetail, File file, TextField status) {
	    // Send the upload command and file name to the server
	    out.println("UP " + uploadDetail.getText());
	    out.flush();

	    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
	        byte[] byteArray = new byte[1024];
	        int bytesRead;

	        // Read the file and send it to the server
	        while ((bytesRead = bis.read(byteArray)) != -1) {
	            dous.write(byteArray, 0, bytesRead);
	        }
	        dous.flush(); // Ensure all data is sent
	        status.setText("Upload successful!");
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        status.setText("File not found.");
	    } catch (IOException e) {
	        e.printStackTrace();
	        status.setText("Upload failed.");
	    }
	}


	private void pull(TextArea txt_id2) {
		// TODO Auto-generated method stub
		out.println("LIST");
		out.flush();

		// handle the request
		String line;
		try {
			while ((line = in.readLine()) != null) {
				txt_id2.appendText(line + "\n");
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void download(TextField ID) {
	    out.println("DOWN " + ID.getText());
	    out.flush();
	    String response;

	    try {
	        if ((response = in.readLine()) != null) {
	            File outputFile = new File("data/client/" + response);
	            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {

	                byte[] byteArray = new byte[1024];
	                int bytesRead;

	                while ((bytesRead = dips.read(byteArray, 0, byteArray.length)) != -1) {
	                    bos.write(byteArray, 0, bytesRead);
	                }

	                System.out.println("Downloaded: " + outputFile.getName());
	            } catch (FileNotFoundException e) {
	                System.err.println("File not found: " + e.getMessage());
	            } catch (IOException e) {
	                System.err.println("I/O error: " + e.getMessage());
	            }

	        } else {
	            System.err.println("No response received from the server.");
	        }
	    } catch (IOException e) {
	        System.err.println("Error reading server response: " + e.getMessage());
	    }
	}

	/*
	 * @PARAM void method for connecting
	 */
	// We need three essential Methods
	public void connect() {
		try {
			ss = new Socket(localHost, Port);
			// the bufferes
			out = new PrintWriter(ss.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(ss.getInputStream()));
			dous = new DataOutputStream(ss.getOutputStream());
			dips = new DataInputStream(ss.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// the stop method
	// close connection
	public void stop() {
		try {
			ss.close();
			out.close();
			in.close();
			dous.close();
			dips.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
