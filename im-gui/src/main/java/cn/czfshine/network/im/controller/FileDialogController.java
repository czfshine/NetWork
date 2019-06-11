package cn.czfshine.network.im.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * @author:czfshine
 * @date:2019/6/11 10:17
 */
@ViewController(value = "fxml/FileDialog.fxml")
public class FileDialogController {

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    public ViewFlowContext getFlowContext() {
        return flowContext;
    }

    public JFXProgressBar getBar() {
        return bar;
    }

    public Label getInfoLabel() {
        return infoLabel;
    }

    public JFXButton getSelectBtn() {
        return selectBtn;
    }

    public JFXButton getCleanBtn() {
        return cleanBtn;
    }

    public MainController getMainController() {
        return mainController;
    }

    public JFXButton getOkBtn() {
        return okBtn;
    }

    @FXML
    private JFXTextField pathText;

    @FXML
    private JFXProgressBar bar;

    @FXML
    private Label infoLabel;

    @FXML
    private JFXButton selectBtn;

    @FXML
    private JFXButton cleanBtn;

    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    private Stage stage;
    private MainController mainController;

    @FXML
    private JFXButton okBtn;

    //所有事件函数全部移到主控制器内，这个控制器只有薄薄的一层用来传递事件
    @FXML
    void onSelect(ActionEvent event) {
        mainController.whenFileSelect();
    }

    @FXML
    void onOk(ActionEvent event) {
         mainController.whenFileWillSend();
    }

    @FXML
    void onClean(ActionEvent event) {
        mainController.whenFileClean();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public JFXTextField getPathText() {
        return pathText;
    }
}