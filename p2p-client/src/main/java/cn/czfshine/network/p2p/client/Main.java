package cn.czfshine.network.p2p.client;

import cn.czfshine.network.p2p.client.ui.Controller;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author:czfshine
 * @date:2019/4/17 14:53
 */

public class Main  extends Application {


    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    @Override
    public void start(Stage stage) throws Exception {
        initStyle(stage);
        stage.show();
    }

    /**
     * 初始化界面样式
     * @param stage
     * @throws FlowException
     */
    private void initStyle(Stage stage) throws FlowException {

        Flow flow =new Flow(Controller.class);

        //主容器
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        //内容
        StackPane contant = flow.createHandler(flowContext).start(container);
        stage.setTitle("My p2p Desktop App");
        double width = 725;
        double height = 540;
        Scene scene = new Scene(contant, width, height);
        stage.setScene(scene);
    }
}