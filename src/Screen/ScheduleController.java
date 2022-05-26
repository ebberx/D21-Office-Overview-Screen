package Screen;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ScheduleController extends Application {

    StackPane scheduleRoot;
    Scene scene;
    Stage stage;
    Canvas canvas;

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage; // save stage for later use
        stage.setTitle("Pomodoro Overview");
        stage.setFullScreen(true);

        // Setup UI layout
        scheduleRoot = new StackPane();
        scene = new Scene(scheduleRoot);

        canvas = new Canvas(100.0f, 100.0f);
        canvas.getGraphicsContext2D().setFontSmoothingType(FontSmoothingType.LCD);

        scheduleRoot.getChildren().addAll(canvas);
        stage.setScene(scene);
        stage.show();
    }


}
