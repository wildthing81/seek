import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AipsTest {

    private final Aips testInstance = new Aips();

    private Map<LocalDateTime,Integer> testTrafficMap;
    private URL fileUrl;

    @BeforeEach
    void setUp(){
        String testFileName = "test_traffic.txt";
        fileUrl = getClass().getClassLoader().getResource(testFileName);
    }
    @Test
    void testInputFile() throws IOException {
        assertNotNull(fileUrl);

        testTrafficMap = testInstance.createTrafficMap(fileUrl.getPath());
        assertNotNull(testTrafficMap);
        assertEquals(24, testTrafficMap.size());
        assertEquals(Integer.valueOf(5), testTrafficMap.get(LocalDateTime.parse("2016-12-01T05:00:00")));
        assertEquals(Integer.valueOf(42), testTrafficMap.get(LocalDateTime.parse("2016-12-01T08:00:00")));
        assertEquals(Integer.valueOf(33), testTrafficMap.get(LocalDateTime.parse("2016-12-08T18:00:00")));
    }

    @Test
    void testTotalCarsAndCarsPerDay() throws IOException {
        testTrafficMap = testInstance.createTrafficMap(fileUrl.getPath());
        testInstance.totalCarsAndCarsPerDay(testTrafficMap);

        assertEquals(398,testInstance.getTotalCars());
        assertEquals(4,testInstance.getCarsPerDayMap().size());
        assertEquals(Integer.valueOf(179),testInstance.getCarsPerDayMap().get(LocalDate.parse("2016-12-01")));
        assertEquals(Integer.valueOf(4),testInstance.getCarsPerDayMap().get(LocalDate.parse("2016-12-09")));
    }

    @Test
    void testTop3TrafficPeriod() throws IOException {
        testTrafficMap = testInstance.createTrafficMap(fileUrl.getPath());
        testInstance.top3TrafficPeriod(testTrafficMap);

        assertTrue(testInstance.getTop3Map().containsKey(LocalDateTime.parse("2016-12-01T07:30:00")));
        assertTrue(testInstance.getTop3Map().containsKey(LocalDateTime.parse("2016-12-01T08:00:00")));
        assertTrue(testInstance.getTop3Map().containsKey(LocalDateTime.parse("2016-12-08T18:00:00")));
    }

    @Test
    void testLeastOneAndHalfPeriod() throws IOException {
        testTrafficMap = testInstance.createTrafficMap(fileUrl.getPath());
        testInstance.totalCarsAndCarsPerDay(testTrafficMap);
        testInstance.leastOneAndHalf(testTrafficMap);

        assertTrue(testInstance.getLeastOneAndHalfMap().containsKey(LocalDateTime.parse("2016-12-01T05:00:00")));
        assertTrue(testInstance.getLeastOneAndHalfMap().containsKey(LocalDateTime.parse("2016-12-01T05:30:00")));
        assertTrue(testInstance.getLeastOneAndHalfMap().containsKey(LocalDateTime.parse("2016-12-01T06:00:00")));
    }
}
