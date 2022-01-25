package application;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Main extends Application{

	private String appTitle = "Label Generator";

	private Label fileNameLabel;
	private TextField fileNameTextField;
	private Label numberOfCartonLabel;
	private TextField numberOfCartonTextField;
	private Label orderQuantityLabel;
	private TextField orderQuantityTextField;
	private Button generatorButton;
	private Label numberOfCartonError;
	private Label orderQuantityError;
	private Label errorMessage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			//State -> Scene -> layout -> elements (button)
			//Set title of the windows
			primaryStage.setTitle(appTitle);

			//Define layout of the windows
			GridPane root = new GridPane();
			root.setPadding(new Insets(40, 40, 0, 50));
			root.setVgap(15);
			root.setHgap(20);

			//Initialize all elements
			initializer();

			//Attached all elements to layout
			root.getChildren().addAll(
					fileNameLabel, 
					fileNameTextField,
					numberOfCartonLabel,
					numberOfCartonTextField, 
					orderQuantityLabel, 
					orderQuantityTextField,
					generatorButton,
					numberOfCartonError,
					orderQuantityError,
					errorMessage
					);

			//Define the scene of the layout
			Scene scene = new Scene(root,530,230); //attach the layout to the scene
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(scene); //attach the layout to the stage
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void initializer() {
		//File name label
		fileNameLabel = new Label("File Name:");
		GridPane.setConstraints(fileNameLabel, 0, 0);//col, row

		//Text box to input file name
		fileNameTextField = new TextField();
		fileNameTextField.setPromptText("File Name");
		fileNameTextField.setMaxWidth(250);
		GridPane.setConstraints(fileNameTextField, 1, 0);

		//Number of carton label
		numberOfCartonLabel = new Label("Number of Cartons:");
		GridPane.setConstraints(numberOfCartonLabel, 0, 1);

		//Text box to input number of carton
		numberOfCartonTextField = new TextField();
		numberOfCartonTextField.setPromptText("Number of Cartons");
		GridPane.setConstraints(numberOfCartonTextField, 1, 1);

		//Error Message for numberOfCartonTextField
		numberOfCartonError = new Label();
		GridPane.setConstraints(numberOfCartonError, 2, 1);

		//Order quantity label
		orderQuantityLabel = new Label("Order Quantity:");
		GridPane.setConstraints(orderQuantityLabel, 0, 2);

		//Text box to input order quantity
		orderQuantityTextField = new TextField();
		orderQuantityTextField.setPromptText("Order Quantity");
		GridPane.setConstraints(orderQuantityTextField, 1, 2);

		//Error Message for numberOfCartonTextField
		orderQuantityError = new Label();
		GridPane.setConstraints(orderQuantityError, 2, 2);

		//Generator button
		generatorButton = new Button("Generate Label");
		GridPane.setConstraints(generatorButton, 1, 3);


		generatorButton.setOnAction(e -> {
			try {
				dataHandler(
						fileNameTextField.getText().trim(),
						numberOfCartonTextField.getText().trim(),
						orderQuantityTextField.getText().trim());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});



		//Error Message for general purposes
		errorMessage = new Label();
		GridPane.setConstraints(errorMessage, 1, 4);
	}

	/**
	 * Method to handle data from text fields
	 * @param fileNameData
	 * @param numberOfCartonData
	 * @param orderQuantityData
	 */
	private void dataHandler(String fileNameData, String numberOfCartonData, String orderQuantityData) throws IOException {
		if(fileNameData.length() == 0 || numberOfCartonData.length() == 0 || orderQuantityData.length() == 0) {
			//Print to general error message and highlight message
			errorMessage.setText("Xin Điền Thông Tin Đầy Đủ");
			errorMessage.setTextFill(Color.web("#ff0000", 0.8));
			return;
		}

		//remove previous error message if set
		errorMessage.setText("");
		errorMessage.setStyle(null);

		//Initialize numberOfCarton and orderQuantity
		int numberOfCarton = 0;
		int orderQuantity = 0;

		//Validate numberOfCarton and orderQuantity
		if(!isInt(numberOfCartonData, numberOfCartonTextField, numberOfCartonError) || !isInt(orderQuantityData, orderQuantityTextField, orderQuantityError)) {
			//Force this second error to show to alert users if applicable
			isInt(orderQuantityData, orderQuantityTextField, orderQuantityError);
			return;
		}else {
			numberOfCarton = Integer.parseInt(numberOfCartonData);
			orderQuantity = Integer.parseInt(orderQuantityData);
		}

		//Print log message to console 
		System.out.println("*****Generating Label*****");
		System.out.println("File Name: " + fileNameData);
		System.out.println("Number of Carton: " + numberOfCarton);
		System.out.println("Order Quantity: " + orderQuantity);

		String nameString = fileNameData;
		//labelGenerator(fileNameData, numberOfCarton, orderQuantity); //generating label
		dumpFunction(nameString);
	}

	/**
	 * Validate if input is an integer
	 * @param input value from textfield
	 * @param textField textfield
	 * @param errorLabel error label right below the textfield
	 * @return boolean
	 */
	private boolean isInt(String input,TextField textField, Label errorLabel) {
		try {
			Integer.parseInt(input);
			errorLabel.setText("");
			textField.setStyle(null);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			//Format error label
			errorLabel.setText("Number Only");
			errorLabel.setTextFill(Color.web("#ff0000", 0.8));

			//Highlight text field
			textField.setStyle("-fx-border-color:red ; -fx-border-width: 2px ;");
			return false;
		}
	}

	/**
	 * This function will take in data and write to PDF file
	 * @param fileName input filename
	 * @param numberOfCarton number of boxes
	 * @param orderQuantity number of labels per box
	 */
	private void labelGenerator(String fileName, int numberOfCarton, int orderQuantity) {		
		if(numberOfCarton <= 0 || orderQuantity <= 0) {
			System.out.println("Invalid input");
			return;
		}

		int numberOfLoop = orderQuantity / 3; //each row has 3 label
		int remainder = orderQuantity % 3 == 0 ? 0 : orderQuantity % 3;
		int labelQuantity = 1;

		for(int i = 0; i < numberOfLoop; i++) {
			for(int j = 1; j <= numberOfCarton; j++) {
				System.out.println("Box: " + j + " Quantity: " + labelQuantity + " " + (labelQuantity+1) + " " + (labelQuantity+2));
			}
			System.out.println("***End Loop " + i + "***");
			labelQuantity += 3;
		}

		if(remainder == 1) {
			for(int j = 1; j <= numberOfCarton; j++) {
				System.out.println("Box: " + j + " Quantity: " + labelQuantity + " " + 0 + " " + 0);
			}
		}else if(remainder == 2) {
			for(int j = 1; j <= numberOfCarton; j++) {
				System.out.println("Box: " + j + " Quantity: " + labelQuantity + " " + (labelQuantity+1) + " " + 0);
			}
		}
	}

	private void dumpFunction(String fileName) throws IOException {
		String path = "/home/thuyn789/Downloads/TestingPDF.pdf";
		PdfWriter pdfWriter = new PdfWriter(path);
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);
		pdfDocument.setDefaultPageSize(PageSize.A4);

		float col = 280f;
		float columnWidth[] = {col, col};

		Table table = new Table(columnWidth);

		table.addCell(new Cell().add(new Paragraph("INVOICE")));

		document.add(table);
		document.close();
		System.out.println("PDF Created");
	}
}
