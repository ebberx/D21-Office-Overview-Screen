package Screen;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.converter.DurationConverter;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Properties;

public class ScheduleController extends Application {

    StackPane scheduleRoot;
    Scene scene;
    Stage stage;
    Canvas canvas;
    ArrayList<Consultant> consultants = new ArrayList<Consultant>();
    ArrayList<Workday> workdays = new ArrayList<Workday>();
    GraphicsContext gc;
    String officeName;

    @Override
    public void start(Stage stage) throws Exception {

        // Load office name from configuration file
        {
            try {
                Properties props = new Properties();
                props.load(new FileInputStream("conf.properties"));
                officeName = props.getProperty("office", "Main office");
            }
            catch (Exception e) { e.printStackTrace(); }
        }

        this.stage = stage; // save stage for later use
        stage.setTitle("Pomodoro Overview");
        stage.setFullScreen(true);

        // Setup test data
        PomodoroSettings ps = new PomodoroSettings("11", "25", "35");

        consultants.add(new Consultant("test@testesen.dk", "Test Testesen"));
        consultants.add(new Consultant("test2@testesen.dk", "Test 2 Testesen"));
        consultants.add(new Consultant("test3@testesen.dk", "Test 3 Testesen"));
        consultants.add(new Consultant("test4@testesen.dk", "Test 4 Testesen"));
        consultants.add(new Consultant("test5@testesen.dk", "Test 5 Testesen"));

        workdays.add(new Workday(0, consultants.get(0), LocalDateTime.now(), LocalDateTime.now().plusHours(8), "123"));
        workdays.add(new Workday(1, consultants.get(1), LocalDateTime.now(), LocalDateTime.now().plusHours(8), "123"));
        workdays.add(new Workday(2, consultants.get(2), LocalDateTime.now(), LocalDateTime.now().plusHours(8), "123"));
        workdays.add(new Workday(3, consultants.get(3), LocalDateTime.now(), LocalDateTime.now().plusHours(8), "123"));

        workdays.get(0).pomodoros.add(new Pomodoro(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(25)), Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(7)), LocalDateTime.now(), LocalDateTime.now().plusMinutes(32)));
        workdays.get(0).pomodoros.add(new Pomodoro(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(25)), Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(7)), LocalDateTime.now().plusMinutes(32), LocalDateTime.now().plusMinutes(64)));
        workdays.get(0).pomodoros.add(new Pomodoro(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(25)), Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(7)), LocalDateTime.now().plusMinutes(64), LocalDateTime.now().plusMinutes(96)));

        // Setup UI layout
        scheduleRoot = new StackPane();
        //scheduleRoot.setStyle("-fx-background-color: ");
        scene = new Scene(scheduleRoot);

        // Canvas the size of the screen
        double width = Screen.getPrimary().getBounds().getWidth();
        double height = Screen.getPrimary().getBounds().getHeight();
        System.out.println("[Screen] Width: " + width + " Height: " + height);
        canvas = new Canvas(width, height);
        canvas.getGraphicsContext2D().setFontSmoothingType(FontSmoothingType.LCD);

        // Update frequency
        Timeline tl = new Timeline(new KeyFrame(javafx.util.Duration.millis(1000), (e) ->{
            System.out.printf("Render time: %d microseconds\n", render() / 1000);
        }));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();

        // Add to stage/scene
        scheduleRoot.getChildren().add(canvas);
        stage.setScene(scene);
        stage.show();

        // Exit properly
        stage.setOnCloseRequest(t -> {
            exit();
        });
    }

    int schedulePaddingTop = 60;
    public long render() {
        long start = System.nanoTime();

        if(gc == null)
            gc = canvas.getGraphicsContext2D();

        // Clear canvas before drawing
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw time
        gc.setFill(Color.BLACK);
        Font font = new Font("Segoe UI", 38);
        gc.setFont(font);
        gc.fillText("12:00", canvas.getWidth() / 2 - 50, 30);

        font = new Font("Segoe UI", 18);
        gc.setFont(font);

        int i = 0;
        for(Workday w : workdays) {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, schedulePaddingTop + 18 + (100*i), canvas.getWidth(), 54);
            gc.setFill(Color.WHITE);
            gc.fillRect(0, schedulePaddingTop + 20 + (100*i), canvas.getWidth(), 50);

            // Draw schedule
            for(Pomodoro p : w.pomodoros) {
                gc.setFill(Color.web("FF6962"));
                gc.fillRect(timeToCanvasX(p.start), schedulePaddingTop + 20 + (100*i), pixelsInTimespan(p.workDuration), 50);

                gc.setFill(Color.web("77DD76"));
                double breakX = timeToCanvasX(p.start) + pixelsInTimespan(p.workDuration);
                gc.fillRect(breakX, schedulePaddingTop + 20 + (100*i), pixelsInTimespan(p.breakDuration), 50);
            }

            // Draw name
            gc.setFill(Color.BLACK);
            gc.fillText(w.consultant.getEmail(), 10, schedulePaddingTop + 12 + (100*i));
            i++;
        }

        // Draw time slider
        double sliderX = canvas.getWidth() / 8 * 2;
        double sliderY = 0;
        double sliderW = 4;
        double sliderH = canvas.getHeight();
        gc.setFill(Color.BLACK);
        gc.fillRect(sliderX, sliderY, sliderW, sliderH);

        return System.nanoTime() - start;
    }

    public double timeToCanvasX(LocalDateTime time) {
        double currentTime = canvas.getWidth() / 8 * 2;
        //    Canvas timespan: canvas width divided by 8
        double pixelsPerHour = canvas.getWidth() / 8;
        java.time.Duration delta = java.time.Duration.between(LocalDateTime.now(), time);
        return (((double)delta.toSeconds() / 3600) * pixelsPerHour) + currentTime;
    }

    public double pixelsInTimespan(java.time.Duration time) {
        double pixelsPerHour = canvas.getWidth() / 8;
        return ((double)time.toSeconds() / 3600) * pixelsPerHour;
    }

    public void exit() {
        Platform.setImplicitExit(true);
        Platform.exit();
        System.exit(0);
    }
}
