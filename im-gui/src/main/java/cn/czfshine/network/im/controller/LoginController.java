package cn.czfshine.network.im.controller;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.LinkAction;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import lombok.Data;

import javax.annotation.PostConstruct;

/**
 * @author:czfshine
 * @date:2019/6/9 0:29
 */
@Data
@ViewController(value = "/fxml/login.fxml")
public class LoginController {
    @FXMLViewFlowContext
    private ViewFlowContext flowContext;
    @FXML
    private JFXTextField portText;

    @FXML
    private JFXTextField usernameText;

    @FXML
    private JFXTextField addressText;

    @FXML
    @LinkAction(MainController.class)
    private JFXButton loginBtn;

    @PostConstruct
    private void init(){
        flowContext.register("login",this);
    }
}
