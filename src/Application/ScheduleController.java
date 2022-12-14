package Application;

import Domain.*;
import Foundation.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Handles the fetching and rendering of data about consultants and their pomodoro schedules.
 * The UI interface consists of a StackPane with a nested canvas. This is to facilitate further additions to the interface.
 * Current limitation in UI resizing makes the UI dimensions defined on application launch, as opposed to runtime resizable.
 * @author Esben Christensen
 */
public class ScheduleController extends Application {

    /**
     * Root element of the JavaFX application
     */
    StackPane scheduleRoot;
    /**
     * Main scene of the JavaFX application
     */
    Scene scene;
    /**
     * Stage of the JavaFX application
     */
    Stage stage;
    /**
     * Canvas element which will be drawn to
     */
    Canvas canvas;
    /**
     * List of consultants, storing all information about schedules
     */
    ArrayList<Consultant> consultants = new ArrayList<Consultant>();
    /**
     * The canvas' current graphics context. Used for drawing.
     */
    GraphicsContext gc;

    // Configuration values
    /**
     * The office name of which to display pomodoro schedules
     */
    String officeName;
    /**
     * Which screen to display to
     */
    int displayScreen;

    /**
     * Returns the x value along the time axis in the applications timeline for a given time value.
     * @param time The time parameter that is to be converted to a value x along the time axis.
     * @param canvasWidth Width of the timespan in pixels
     * @return X value along the time axis
     */
    public static double timeToCanvasX(LocalDateTime time, double canvasWidth) {
        double pixelsPerHour = canvasWidth / 8;
        double currentTimeInPixels = pixelsPerHour * 2;
        java.time.Duration delta = java.time.Duration.between(LocalDateTime.now(), time);
        return (((double)delta.toSeconds() / 3600) * pixelsPerHour) + currentTimeInPixels;
    }

    /**
     * Returns an amount of pixels represented by a timespan.
     * @param time The time span
     * @param canvasWidth The width of the total timespan of 8 hours
     * @return Timespan in pixels
     */
    public static double pixelsInTimespan(java.time.Duration time, double canvasWidth) {
        double pixelsPerHour = canvasWidth / 8;
        return ((double)time.toSeconds() / 3600) * pixelsPerHour;
    }

    /**
     * Entry point of application
     * @param stage JavaFX element that holds main scene.
     */
    @Override
    public void start(Stage stage) {

        // Load office name from configuration file
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("conf.properties"));
            officeName = props.getProperty("office", "Main office");
            displayScreen = Integer.parseInt(props.getProperty("screen", "1"));

            // -== Validation of configuration values ==-
            // Is screen is valid? Use primary if not
            if(displayScreen >= Screen.getScreens().size())
                displayScreen = 0;
        }
        catch (Exception e) { e.printStackTrace(); }

        this.stage = stage; // save stage for later use
        stage.setTitle("Pomodoro Overview");
        stage.setFullScreen(true);

        // Setup UI layout
        scheduleRoot = new StackPane();
        //scheduleRoot.setStyle("-fx-background-color: ");
        scene = new Scene(scheduleRoot);

        // Canvas the size of the screen
        double width = Screen.getScreens().get(displayScreen).getBounds().getWidth();
        double height = Screen.getScreens().get(displayScreen).getBounds().getHeight();
        System.out.println("[Screen] Width: " + width + " Height: " + height);
        canvas = new Canvas(width, height);
        canvas.getGraphicsContext2D().setFontSmoothingType(FontSmoothingType.LCD);

        // Add to stage/scene
        scheduleRoot.getChildren().add(canvas);
        stage.setScene(scene);
        stage.setX(Screen.getScreens().get(displayScreen).getBounds().getMinX());
        stage.setY(Screen.getScreens().get(displayScreen).getBounds().getMinY());
        stage.show();

        // Exit properly
        stage.setOnCloseRequest(t -> {
            exit();
        });

        // Start data update with frequency of 10 sec
        Timeline t1 = new Timeline(new KeyFrame(javafx.util.Duration.millis(10000), (e) ->{
            System.out.printf("Fetching schedule update time: %d microseconds\n", fetchScheduleUpdates() / 1000);
        }));
        t1.setCycleCount(Timeline.INDEFINITE);
        t1.play();

        // Start render update with frequency of 1 sec
        Timeline t2 = new Timeline(new KeyFrame(javafx.util.Duration.millis(1000), (e) ->{
            System.out.printf("Render time: %d microseconds\n", render() / 1000);
        }));
        t2.setCycleCount(Timeline.INDEFINITE);
        t2.play();
    }

    /**
     * Checks for new data in the database, retrieves it, and prepares it for rendering
     * @return time spend fetching schedule update in nanoseconds
     */
    public long fetchScheduleUpdates() {
        long start = System.nanoTime();

        DB.getInstance().connect();
        // Get consultants and check if we received data
        consultants = DB.getInstance().getConsultantsInOffice(officeName);
        if(consultants == null)
            return System.nanoTime() - start;

        for(Consultant c : consultants) {
            // Get workdays of consutants after current date at 00:00
            DB.getInstance().getWorkdaysOfConsultant(c, LocalDate.now().toString()+" 00:00:00");

            if(c.getWorkdays() != null) {
                for(Workday w : c.getWorkdays()) {
                    // Get pomodoros for each workday
                    DB.getInstance().getPomodorosInWorkday(w);
                }
            }
        }
        DB.getInstance().disconnect();
        return System.nanoTime() - start;
    }

    /**
     * Padding variable for schedule layout
     */
    int schedulePaddingTop = 100;
    /**
     * Renders the pomodoro schedule of consultants to screen
     * @return time spent rendering in nanoseconds
     */
    public long render() {
        long start = System.nanoTime();

        if(gc == null)
            gc = canvas.getGraphicsContext2D();

        // Clear canvas before drawing
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Background
        gc.setFill(Color.web("#EEF5FD"));
        gc.fillRect(0,0,canvas.getWidth(), canvas.getHeight());

        // Draw time
        gc.setFill(Color.BLACK);
        Font font = new Font("Segoe UI", 38);
        gc.setFont(font);
        gc.fillText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), canvas.getWidth() / 2 - 50, 46);

        font = new Font("Segoe UI", 18);
        gc.setFont(font);

        int i = 0;
        if(consultants != null) {
            for(Consultant c : consultants) {
                gc.setFill(Color.BLACK);
                gc.fillRect(0, schedulePaddingTop + 18 + (100*i), canvas.getWidth(), 54);
                gc.setFill(Color.WHITE);
                gc.fillRect(0, schedulePaddingTop + 20 + (100*i), canvas.getWidth(), 50);

                // Draw schedule
                if(c.getWorkdays() != null) {
                    for(Workday w : c.getWorkdays()) {
                        for(Pomodoro p : w.getPomodoros()) {
                            gc.setFill(Color.web("FF6962"));
                            gc.fillRect(timeToCanvasX(p.getStart(), canvas.getWidth()), schedulePaddingTop + 20 + (100*i), pixelsInTimespan(p.getWorkDuration(), canvas.getWidth()), 50);

                            gc.setFill(Color.web("77DD76"));
                            double breakX = timeToCanvasX(p.getStart(), canvas.getWidth()) + pixelsInTimespan(p.getWorkDuration(), canvas.getWidth());
                            gc.fillRect(breakX, schedulePaddingTop + 20 + (100*i), pixelsInTimespan(p.getBreakDuration(), canvas.getWidth()), 50);
                        }
                    }
                }

                // Draw name
                gc.setFill(Color.BLACK);
                gc.fillText(c.getName(), 10, schedulePaddingTop + 12 + (100*i));
                i++;
            }
        }

        // Draw time slider
        double sliderX = canvas.getWidth() / 8 * 2;
        double sliderY = 0;
        double sliderW = 4;
        double sliderH = canvas.getHeight();
        gc.setFill(Color.BLACK);
        gc.fillRect(sliderX, sliderY, sliderW, sliderH);

        // Draw timeline
        double timelineY = canvas.getHeight() - (canvas.getHeight() / 20);
        double timelineH = canvas.getHeight() / 20;
        gc.setFill(Color.WHITE);
        gc.fillRect(0, timelineY, canvas.getWidth(), timelineH);

        gc.setFill(Color.BLACK);
        font = new Font("Segoe UI", 12);
        gc.setFont(font);

        gc.setLineWidth(1);
        double l = timelineH/8;
        gc.setLineDashes(l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l,l);
        gc.strokeLine(0,timelineY+timelineH/2, canvas.getWidth(), timelineY+timelineH/2);
        gc.setLineWidth(2);
        gc.setLineDashes(0);
        for(int j = -3; j < 8; j++){
            LocalTime hour = LocalTime.of(LocalTime.now().getHour(), 0);
            LocalDateTime date = LocalDateTime.of(LocalDate.now(), hour);

            gc.strokeLine(timeToCanvasX(date.plusHours(j), canvas.getWidth()),
                    timelineY+2, timeToCanvasX(date.plusHours(j), canvas.getWidth()), timelineY+timelineH);
            gc.setFill(Color.WHITE);
            gc.fillRect(timeToCanvasX(date.plusHours(j), canvas.getWidth())-19, timelineY + timelineH / 2 -10, 40, 20);
            gc.setFill(Color.BLACK);
            gc.fillText(hour.plusHours(j).format(DateTimeFormatter.ofPattern("HH:mm")),
                    timeToCanvasX(date.plusHours(j), canvas.getWidth())-14, timelineY + timelineH / 2 + 4);
        }
        gc.setFill(Color.BLACK);
        gc.strokeRect(0, timelineY, canvas.getWidth(), timelineH);

        return System.nanoTime() - start;
    }

    /**
     * Called when the program exits, and makes sure everything exits correctly
     */
    public void exit() {
        Platform.setImplicitExit(true);
        Platform.exit();
        System.exit(0);
    }
}
