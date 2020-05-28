package rocktable.javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;

@Slf4j
public class LaunchController {

    @Inject
    private FXMLLoader fxmlLoader;

    @FXML
    private TextField playerNameTextField;
    @FXML
    private TextField playerNameTextField1;

    @FXML
    private Label errorLabel;

    public void startAction(ActionEvent actionEvent) throws IOException {
        if (playerNameTextField.getText().isEmpty() || playerNameTextField1.getText().isEmpty()) {
            errorLabel.setText("Enter player names!");
        }
        else if (playerNameTextField.getText().length()>=10 || playerNameTextField1.getText().length()>=10){
            errorLabel.setText("Player names' length must not exceed 10!");
        }
        else if (playerNameTextField.getText().equals(playerNameTextField1.getText())){
            errorLabel.setText("Choose different names!");
        }
        else {
            fxmlLoader.setLocation(getClass().getResource("/fxml/game.fxml"));
            Parent root = fxmlLoader.load();
            fxmlLoader.<GameController>getController().setPlayerName1(playerNameTextField.getText());
            fxmlLoader.<GameController>getController().setPlayerName2(playerNameTextField1.getText());

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            log.info("The player1's name is set to {}", playerNameTextField.getText());
            log.info("The player2's name is set to {}", playerNameTextField1.getText());
            log.info("Loading game scene...");
        }
    }

}
