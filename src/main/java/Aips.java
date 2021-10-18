import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Aips {

    public static void main(String[] args) throws IOException {
        Aips aips = new Aips();
        System.out.println("Input file path:");
        aips.processTrafficMap(aips.createTrafficMap(new Scanner(System.in).nextLine()));
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
    void processTrafficMap(Map<LocalDateTime, Integer> trafficMap) {
        AtomicLong totalCars = new AtomicLong();
        final Map<LocalDate,Integer> carsPerDayMap = new HashMap<>();
        final Map<LocalDateTime,Integer> oneAndHalfPeriodMap = new HashMap<>();
        Tuple first = new Tuple();
        Tuple second = new Tuple();
        Tuple third = new Tuple();

        trafficMap.forEach( (key, value) -> {
            totalCars.addAndGet(value);
            carsPerDayMap.compute(key.toLocalDate(),(K, V) -> V == null? value : value+V);
            getOneAndHalfPeriod(key,value,trafficMap, oneAndHalfPeriodMap);
            top3TrafficPeriod(key,value,first,second,third);
        });

        printOutput(totalCars.get(),carsPerDayMap,
                    computeLeastOneAndHalfPeriod(trafficMap, oneAndHalfPeriodMap),
                    Arrays.asList(first,second,third));
    }

    /**
     * Create map with the 1.5 hour periods
     * @param key
     * @param value
     * @param trafficMap
     * @param oneAndHalfPeriodMap
     */
     void getOneAndHalfPeriod(LocalDateTime key, Integer value,
                               Map<LocalDateTime, Integer> trafficMap,
                               Map<LocalDateTime, Integer> oneAndHalfPeriodMap) {
        if (trafficMap.containsKey(key.plusMinutes(30))
                && trafficMap.containsKey(key.plusMinutes(60)))
        {
            oneAndHalfPeriodMap.put(key,value
                    + trafficMap.get(key.plusMinutes(30))
                    + trafficMap.get(key.plusMinutes(60)));
        }
    }

    /**
     * Compute top 3 half hours with most cars
     *
     */
     void top3TrafficPeriod(LocalDateTime key, Integer value, Tuple first, Tuple second, Tuple third) {
        if (value > first.getValue()){
            third.setKeyValue(second.key,second.value);
            second.setKeyValue(first.key,first.value);
            first.setKeyValue(key,value);
        }
     }

    /**
     * Compute 1.5 hr period with least cars
     * @param trafficMap
     * @param oneAndHalfPeriodMap
     * @return
     */
    private List computeLeastOneAndHalfPeriod(Map<LocalDateTime, Integer> trafficMap,
                                              Map<LocalDateTime, Integer> oneAndHalfPeriodMap) {
        LocalDateTime leastOneAndHalfPeriod = Collections.min(oneAndHalfPeriodMap.entrySet(),
                                                                Map.Entry.comparingByValue()).getKey();

        List<Tuple> result = new ArrayList<>();
        result.add(new Tuple(leastOneAndHalfPeriod,trafficMap.get(leastOneAndHalfPeriod)));
        result.add(new Tuple(leastOneAndHalfPeriod.plusMinutes(30),
                             trafficMap.get(leastOneAndHalfPeriod.plusMinutes(30))));
        result.add(new Tuple(leastOneAndHalfPeriod.plusMinutes(60),
                                trafficMap.get(leastOneAndHalfPeriod.plusMinutes(60))));

        return result;
    }

    private void printOutput(long totalCars,
                             Map<LocalDate, Integer> carsPerDayMap,
                             List<Tuple> leastOneAndHalfList,
                             List<Tuple> top3HalfList) {
        System.out.println(totalCars);
        carsPerDayMap.forEach((key, value) -> System.out.println(key+" "+value));

        System.out.println(leastOneAndHalfList.get(0).key+" "+leastOneAndHalfList.get(0).value);
        System.out.println(leastOneAndHalfList.get(1).key+" "+leastOneAndHalfList.get(1).value);
        System.out.println(leastOneAndHalfList.get(2).key+" "+leastOneAndHalfList.get(2).value);

        System.out.println(top3HalfList.get(0).key+" "+top3HalfList.get(0).value);
        System.out.println(top3HalfList.get(1).key+" "+top3HalfList.get(1).value);
        System.out.println(top3HalfList.get(2).key+" "+top3HalfList.get(2).value);
    }

    private static class Tuple {
        private LocalDateTime key;
        private Integer value = Integer.MIN_VALUE;

        public Tuple(LocalDateTime key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public Tuple() {

        }

        Integer getValue() {
            return value;
        }

        void setKeyValue(LocalDateTime key,Integer value) {
            this.key = key;
            this.value =  value;
        }
    }
}
