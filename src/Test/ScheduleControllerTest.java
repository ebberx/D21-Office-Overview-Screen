package Test;


import org.junit.Test;
import static org.junit.Assert.*;
import Application.ScheduleController;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Collection of ScheduleController related tests.
 */
public class ScheduleControllerTest {

    double canvasWidth = 1200;

    // timeToCanvasX
    @Test
    public void timeToCanvasXTest01() {
        double value = ScheduleController.timeToCanvasX(LocalDateTime.now(), 1200);
        double expected = canvasWidth / 8 * 2;
        assertEquals(expected, value, 0.1);
    }
    @Test
    public void timeToCanvasXTest02() {
        double value = ScheduleController.timeToCanvasX(LocalDateTime.now().plusHours(1), 1200);
        double expected = canvasWidth / 8 * 3;
        assertEquals(expected, value, 0.1);
    }
    @Test
    public void timeToCanvasXTest03() {
        double value = ScheduleController.timeToCanvasX(LocalDateTime.now().minusHours(1), 1200);
        double expected = canvasWidth / 8;
        assertEquals(expected, value, 0.1);
    }

    // pixelsInTimespan
    @Test
    public void pixelsInTimespanTest01() {
        double value = ScheduleController.pixelsInTimespan(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(1)), canvasWidth);
        double expected = canvasWidth / 8;
        assertEquals(expected, value, 0.1);
    }
    @Test
    public void pixelsInTimespanTest02() {
        double value = ScheduleController.pixelsInTimespan(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(2)), canvasWidth);
        double expected = canvasWidth / 8 * 2;
        assertEquals(expected, value, 0.1);
    }
    @Test
    public void pixelsInTimespanTest03() {
        double value = ScheduleController.pixelsInTimespan(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(3)), canvasWidth);
        double expected = canvasWidth / 8 * 3;
        assertEquals(expected, value, 0.1);
    }
}
