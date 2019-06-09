package cn.czfshine.network.im;
/**
 * @author:czfshine
 * @date:2019/6/8 11:05
 */


import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ChatGui extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    @Override
    public void start(Stage stage) throws Exception {
        initStyle(stage);
        stage.show();
    }

    private void initStyle(Stage stage) throws FlowException {
        Flow flow =new Flow(LoginController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        JFXDecorator decorator = new JFXDecorator(stage, container.getView());
        decorator.setCustomMaximize(true);
        decorator.setGraphic(new SVGGlyph(""));
        decorator.setAlignment(Pos.TOP_LEFT);

        stage.setTitle("Chat Desktop App");
        Scene scene = new Scene(decorator);
        final ObservableList<String> stylesheets = scene.getStylesheets();

        stylesheets.addAll(ChatGui.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                ChatGui.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                ChatGui.class.getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                ChatGui.class.getResource("/css/chat.css").toExternalForm());


        stage.setScene(scene);
    }
}
