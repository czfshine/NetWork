package cn.czfshine.network.im.controller;

import cn.czfshine.network.im.dto.FileSend;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * @author:czfshine
 * @date:2019/6/11 10:22
 */

public class ReceiveFileController {

    @FXML
    private Label infoLabel;

    @FXML
    private JFXProgressBar bar;
    @FXML
    private JFXButton acceptBtn;
    @FXML
    private JFXButton refuseBtn;

    private MainController mainController;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;
    private FileSend fileSend;
    @FXML
    void onAccept(ActionEvent event) {
        mainController.whenFileAccept(fileSend);
    }

    @FXML
    void onRefuse(ActionEvent event) {
        mainController.whenFileRefuse(fileSend);
    }

    public FileSend getFileSend() {
        return fileSend;
    }

    public void setFileSend(FileSend fileSend) {
        this.fileSend = fileSend;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public JFXButton getRefuseBtn() {
        return refuseBtn;
    }

    public JFXButton getAcceptBtn() {
        return acceptBtn;
    }

    public JFXProgressBar getBar() {
        return bar;
    }

    public Label getInfoLabel() {
        return infoLabel;
    }
}
