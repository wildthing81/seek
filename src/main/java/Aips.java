import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Aips {

    private long totalCars = 0;
    private final Map<LocalDate,Integer> carsPerDayMap = new HashMap<>();
    private Map<LocalDateTime,Integer> top3Map;
    private final Map<LocalDateTime,Integer> leastOneAndHalfMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Aips aips = new Aips();
        System.out.println("Input file path:");
        Map<LocalDateTime,Integer> trafficMap = aips.createTrafficMap(new Scanner(System.in).nextLine());
        aips.totalCarsAndCarsPerDay(trafficMap);
        aips.top3TrafficPeriod(trafficMap);
        aips.leastOneAndHalf(trafficMap);
        aips.printOutput();
    }

    /**
     * Create a traffic map with half-hour period & number of cars
     * @param filePath
     * @return
     * @throws IOException
     */
    Map<LocalDateTime, Integer> createTrafficMap(String filePath) throws IOException {
       return Files.lines(Path.of(filePath))
                .map(line -> line.split(" "))
                .collect(Collectors.toMap(s -> LocalDateTime.parse(s[0]),
                        s -> Integer.valueOf(s[1])));
    }

    /**
     * Compute total number of cars in total & cars seen per day
     * @param trafficMap
     */
    void totalCarsAndCarsPerDay(Map<LocalDateTime, Integer> trafficMap) {
        trafficMap.forEach( (key, value) -> {
            totalCars += value;
            carsPerDayMap.compute(key.toLocalDate(),(K, V) -> V == null? value : value+V);
            if (trafficMap.containsKey(key.plusMinutes(30))
                    && trafficMap.containsKey(key.plusMinutes(60)))
            {
               leastOneAndHalfMap.computeIfAbsent(key, k -> value
                                + trafficMap.get(key.plusMinutes(30))
                                + trafficMap.get(key.plusMinutes(60)));
            }
        });
    }

    /**
     *  Compute the 1.5 hour period with least cars
     *
     * @param trafficMap
     */
    void leastOneAndHalf(Map<LocalDateTime, Integer> trafficMap) {
        LocalDateTime result = Collections.min(leastOneAndHalfMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        leastOneAndHalfMap.clear();
        leastOneAndHalfMap.put(result, trafficMap.get(result));
        leastOneAndHalfMap.put(result.plusMinutes(30), trafficMap.get(result.plusMinutes(30)));
        leastOneAndHalfMap.put(result.plusMinutes(60), trafficMap.get(result.plusMinutes(60)));
    }

    /**
     * Compute top 3 half hours with most cars
     * @param trafficMap
     */
    void top3TrafficPeriod(Map<LocalDateTime, Integer> trafficMap) {
        top3Map = trafficMap.entrySet().stream()
                .sorted(Map.Entry.<LocalDateTime, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (o1, o2) -> o1, LinkedHashMap::new));
    }

    public long getTotalCars() {
        return totalCars;
    }

    public Map<LocalDate, Integer> getCarsPerDayMap() {
        return carsPerDayMap;
    }

    public Map<LocalDateTime, Integer> getTop3Map() {
        return top3Map;
    }

    public Map<LocalDateTime, Integer> getLeastOneAndHalfMap() {
        return leastOneAndHalfMap;
    }

    private void printOutput() {
        System.out.println(totalCars);
        carsPerDayMap.forEach((key, value) -> System.out.println(key+" "+value));
        top3Map.forEach((key, value) -> System.out.println(key+" "+value));
        leastOneAndHalfMap.forEach((key, value) -> System.out.println(key+" "+value));
    }
}
