package rocktable.javafx.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import rocktable.results.GameResult;
import rocktable.results.GameResultDao;
import rocktable.state.RockTableState;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("checkstyle:MissingJavadocType")
@Slf4j
public class GameController {

    @Inject
    private FXMLLoader fxmlLoader;

    @Inject
    private GameResultDao gameResultDao;

    private String playerName1;
    private String playerName2;
    private RockTableState gameState;
    private IntegerProperty activePlayerRocks = new SimpleIntegerProperty();
    private StringProperty activePlayerName = new SimpleStringProperty();
    private Instant startTime;

    @FXML
    private Label messageLabel;

    @FXML
    private Label gameErrorLabel;

    @FXML
    private GridPane gameGrid;

    @FXML
    private GridPane buttonGrid;

    @FXML
    private Label playerLabel;

    @FXML
    private Label rockLabel;

    @FXML
    private Label stopWatchLabel;

    private Timeline stopWatchTimeline;

    @FXML
    private Button endTurnButton;

    @FXML
    private Button giveUpButton;

    private BooleanProperty gameOver = new SimpleBooleanProperty();

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void setPlayerName1(String playerName) {
        this.playerName1 = playerName;
    }
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void setPlayerName2(String playerName) {
        this.playerName2 = playerName;
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    @FXML
    public void initialize() {

        playerLabel.textProperty().bind(activePlayerName);
        rockLabel.textProperty().bind(activePlayerRocks.asString());


        gameOver.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                log.info("Game is over");
                log.debug("Saving result to database...");
                gameResultDao.persist(createGameResult());
                stopWatchTimeline.stop();
            }
        });

        startGame();
    }


    private void startGame() {
        /*int[][] a = {{0,1,1,0,0},
                {0,1,0,0,0},
                {1,0,0,0,0},
                {0,1,1,0,0},
                {0,0,1,0,0}};*/

        gameState = new RockTableState();

        activePlayerRocks.set(0);
        activePlayerName.setValue(playerName1);

        startTime = Instant.now();
        gameOver.setValue(false);

        displayGameState();
        createStopWatch();
        Platform.runLater(() -> messageLabel.setText("Good luck!"));
    }

    private void displayGameState() {
        for (int i = 0; i < 5; i++) {

            Button rowButton = (Button)buttonGrid.getChildren().get(i);
            rowButton.setDisable(!gameState.isChoosableRow(i));

            for (int j = 0; j < 5; j++) {
                Circle rock = (Circle) gameGrid.getChildren().get((i * 5 + j)+25);
                Rectangle backg = (Rectangle) gameGrid.getChildren().get(i*5+j);
                if (gameState.getTable()[i][j]==0){
                    rock.setVisible(false);
                    backg.setVisible(true);
                }
                else {
                    rock.setVisible(true);
                    backg.setVisible(false);
                }
            }
        }

        endTurnButton.setDisable(!gameState.canBeEnded());
        activePlayerRocks.set(gameState.getRocks()[gameState.getActivePlayer()]);

        switch (gameState.getActivePlayer()){
            case 0:
                activePlayerName.set(playerName1);
                break;
            case 1:
                activePlayerName.set(playerName2);
                break;
        }
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void handleClickOnRock(MouseEvent mouseEvent) {
        int row = GridPane.getRowIndex((Node) mouseEvent.getSource());
        int col = GridPane.getColumnIndex((Node) mouseEvent.getSource());

        log.info("Rock ({}, {}) is clicked", row, col);

        if (gameState.isPickupable(col) && gameState.getChosenRow()==row){
            log.info("Not empty clicked.");
            gameState.pickupRock(col);
            displayGameState();
            gameErrorLabel.setText("");
        }
        else {
            gameErrorLabel.setText("Can't pick up!");
        }
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void handleClickOnBckg(MouseEvent mouseEvent) {
        int row = GridPane.getRowIndex((Node) mouseEvent.getSource());
        int col = GridPane.getColumnIndex((Node) mouseEvent.getSource());

        log.info("Rock ({}, {}) is clicked", row, col);

        if (gameState.isPlaceable(col) && gameState.getChosenRow()==row){
            log.info("Empty clicked.");
            gameState.placeRock(col);
            displayGameState();
            gameErrorLabel.setText("");
        }
        else {
            gameErrorLabel.setText("Can't place!");
        }

    }


    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void handleChooseButton0(ActionEvent actionEvent)  {

        if (gameState.isChoosableRow(0)){

            log.info("{} button is pressed.",((Button) actionEvent.getSource()).getText());
            log.info("Choosing row.");

            Button rowButton = (Button)buttonGrid.getChildren().get(0);
            rowButton.setText("CHOSEN");
            rowButton.setStyle("-fx-text-fill: black; -fx-base: green");

            gameState.setChosenRow(0);
            displayGameState();

        }
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void handleChooseButton1(ActionEvent actionEvent)  {
        if (gameState.isChoosableRow(1)){

            log.info("{} button is pressed.",((Button) actionEvent.getSource()).getText());
            log.info("Choosing row.");

            Button rowButton = (Button)buttonGrid.getChildren().get(1);
            rowButton.setText("CHOSEN");
            rowButton.setStyle("-fx-text-fill: black; -fx-base: green");

            gameState.setChosenRow(1);
            displayGameState();
        }
    }
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void handleChooseButton2(ActionEvent actionEvent)  {
        if (gameState.isChoosableRow(2)){

            log.info("{} button is pressed.",((Button) actionEvent.getSource()).getText());
            log.info("Choosing row.");

            Button rowButton = (Button)buttonGrid.getChildren().get(2);
            rowButton.setText("CHOSEN");
            rowButton.setStyle("-fx-text-fill: black; -fx-base: green");

            gameState.setChosenRow(2);
            displayGameState();
        }
    }
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void handleChooseButton3(ActionEvent actionEvent)  {
        if (gameState.isChoosableRow(3)){

            log.info("{} button is pressed.",((Button) actionEvent.getSource()).getText());
            log.info("Choosing row.");

            Button rowButton = (Button)buttonGrid.getChildren().get(3);
            rowButton.setText("CHOSEN");
            rowButton.setStyle("-fx-text-fill: black; -fx-base: green");

            gameState.setChosenRow(3);
            displayGameState();
        }
    }
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void handleChooseButton4(ActionEvent actionEvent)  {
        if (gameState.isChoosableRow(4)){

            log.info("{} button is pressed.",((Button) actionEvent.getSource()).getText());
            log.info("Choosing row.");

            Button rowButton = (Button)buttonGrid.getChildren().get(4);
            rowButton.setText("CHOSEN");
            rowButton.setStyle("-fx-text-fill: black; -fx-base: green");

            gameState.setChosenRow(4);
            displayGameState();
        }
    }


    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void handleEndTurnButton(ActionEvent actionEvent)  {
        if (gameState.canBeEnded()){
            gameState.endTurn();
            displayGameState();

            for (int i = 0; i < 5; i++) {
                Button temp = (Button) buttonGrid.getChildren().get(i);
                temp.setText("Choose");
                temp.setStyle(null);
            }

            if (gameState.isFinished()){
                giveUpButton.setText("Finish");
                messageLabel.setText("Congratulations, " + activePlayerName.getValue() + "!");
            }
        }
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void handleGiveUpButton(ActionEvent actionEvent) throws IOException {
        String buttonText = ((Button) actionEvent.getSource()).getText();

        log.debug("{} is pressed", buttonText);
        if (buttonText.equals("Give Up")) {
            log.info("The game has been given up");
            switch (gameState.getActivePlayer()){
                case 0:
                    activePlayerName.set(playerName2);
                    break;
                case 1:
                    activePlayerName.set(playerName1);
                    break;
            }
        }
        gameOver.setValue(true);

        log.info("Loading high scores scene...");

        fxmlLoader.setLocation(getClass().getResource("/fxml/highscores.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    private GameResult createGameResult() {
        GameResult result = GameResult.builder()
                .winner(activePlayerName.getValue())
                .duration(Duration.between(startTime, Instant.now()))
                .build();
        return result;
    }


    private void createStopWatch() {
        stopWatchTimeline = new Timeline(new KeyFrame(javafx.util.Duration.ZERO, e -> {
            long millisElapsed = startTime.until(Instant.now(), ChronoUnit.MILLIS);
            stopWatchLabel.setText(DurationFormatUtils.formatDuration(millisElapsed, "HH:mm:ss"));
        }), new KeyFrame(javafx.util.Duration.seconds(1)));
        stopWatchTimeline.setCycleCount(Animation.INDEFINITE);
        stopWatchTimeline.play();
    }

}
