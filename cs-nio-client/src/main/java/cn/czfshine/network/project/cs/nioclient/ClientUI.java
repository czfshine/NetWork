package cn.czfshine.network.project.cs.nioclient;

import cn.czfshine.network.project.cs.nioclient.ClientController;
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
 * 客户端界面
 * @author:czfshine
 * @date:2019/4/2 21:49
 */

public class ClientUI extends Application {


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

        Flow flow =new Flow(ClientController.class);

        //主容器
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        //内容
        StackPane contant = flow.createHandler(flowContext).start(container);
        stage.setTitle("Desktop App");
        double width = 1400;
        double height = 600;
        Scene scene = new Scene(contant, width, height);
        stage.setScene(scene);
    }
}
