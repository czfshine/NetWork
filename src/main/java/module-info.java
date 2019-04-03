module sample {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    // 暴露包 sample 给 javafx 的模块们，使其可以在运行时使用反射访问
    opens sample to javafx.graphics, javafx.fxml;
}