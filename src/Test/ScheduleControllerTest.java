package Test;


import org.junit.Test;
import static org.junit.Assert.*;
import Screen.ScheduleController;

import java.time.Duration;
import java.time.LocalDateTime;

public class ScheduleControllerTest {

    @org.junit.Before
    public void setup() {

    }

    @org.junit.After
    public void teardown() {

    }

    @Test
    public void timeToCanvasXTest01() {
        double canvasWidth = 1200;
        double value = ScheduleController.timeToCanvasX(LocalDateTime.now(), 1200);
        double expected = canvasWidth / 8 * 2;
        assertEquals(expected, value, 0.1);
    }
    @Test
    public void timeToCanvasXTest02() {
        double canvasWidth = 1200;
        double value = ScheduleController.timeToCanvasX(LocalDateTime.now().plusHours(1), 1200);
        double expected = canvasWidth / 8 * 3;
        assertEquals(expected, value, 0.1);
    }
    @Test
    public void timeToCanvasXTest03() {
        double canvasWidth = 1200;
        double value = ScheduleController.timeToCanvasX(LocalDateTime.now().minusHours(1), 1200);
        double expected = canvasWidth / 8;
        assertEquals(expected, value, 0.1);
    }

    @Test
    public void pixelsInTimespanTest01() {
        double canvasWidth = 1200;
        double value = ScheduleController.pixelsInTimespan(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(1)), canvasWidth);
        double expected = canvasWidth / 8;
        assertEquals(expected, value, 0.1);
    }
    @Test
    public void pixelsInTimespanTest02() {
        double canvasWidth = 1200;
        double value = ScheduleController.pixelsInTimespan(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(2)), canvasWidth);
        double expected = canvasWidth / 8 * 2;
        assertEquals(expected, value, 0.1);
    }
    @Test
    public void pixelsInTimespanTest03() {
        double canvasWidth = 1200;
        double value = ScheduleController.pixelsInTimespan(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(3)), canvasWidth);
        double expected = canvasWidth / 8 * 3;
        assertEquals(expected, value, 0.1);
    }


}
