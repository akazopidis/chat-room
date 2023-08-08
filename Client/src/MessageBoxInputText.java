import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MessageBoxInputText
{
    Stage stage;
    Label lbl;
    Button btnOK;
    String message;
    String title;
    TextField text;

    String return_string;

    public MessageBoxInputText(String message, String title) {
        this.message = message;
        this.title = title;
        return_string = "";
        show();
    }

    public void show()
    {
        lbl = new Label();
        lbl.setText(message);

        text = new TextField();
        text.setPromptText("Enter new Theme Name");

        btnOK = new Button();
        Image img = new Image("ok.png");
        ImageView view = new ImageView(img);
        btnOK.setGraphic(view);
        btnOK.setDefaultButton(true);
        btnOK.setPrefSize(60, 25);
        btnOK.setOnAction(e -> ok());


        VBox pane = new VBox();


        pane.getChildren().addAll(lbl, text, btnOK);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(20));

        Scene scene = new Scene(pane);


        stage = new Stage();
        stage.setTitle(title);
        stage.setMinWidth(250);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
    }

    public void ok() {
        return_string = text.getText();
        stage.close();
    }

    public String getResponse() {
        return return_string;
    }
}