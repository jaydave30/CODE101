import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CSVParser {
    public static void main(String[] args) {
        try {
            System.out.println("Enter the input file path: ");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            String filePath = reader.readLine().trim();
            parseFile(filePath);
        } catch (IOException e) {
            System.err.println("Error reading input : " + e.getMessage());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static void parseFile(String filePath) throws Exception {
        String header = "fruit-type,age-in-days,characteristic1,characteristic2";
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            if (lines.size() == 0) {
                throw new Exception("File is empty");
            }
            if (!lines.get(0).startsWith(header)) {
                throw new Exception("Invalid File format");
            }

            String[] headerTokens = lines.get(0).split(",");
            lines.remove(0);

            Map<String, Integer> fruitCount = new HashMap<>();
            Map<String, Integer> fruitAge = new HashMap<>();
            Map<String, Integer> fruitCharacterCount = new HashMap<>();

            for (String line : lines) {
                String[] tokens = line.split(",");
                if (tokens.length != headerTokens.length) {
                    throw new Exception("Invalid file format at data level");
                }

                String fruit = tokens[0];
                int age = Integer.parseInt(tokens[1]);
                fruitCount.put(fruit, fruitCount.getOrDefault(fruit, 0) + 1);

                fruitAge.put(fruit, Math.max(fruitAge.getOrDefault(fruit, Integer.MIN_VALUE), age));

                String charKey = getCharKey(tokens);
                fruitCharacterCount.put(charKey, fruitCharacterCount.getOrDefault(charKey, 0) + 1);
            }

            //print output.
            System.out.println("Total number of fruits:" + (lines.size()));
            System.out.println();
            System.out.println("Total types of fruit :" + fruitCount.size());
            System.out.println();
            System.out.println("Oldest fruit & age: ");
            int max = Integer.MIN_VALUE;
            for (Integer f : fruitAge.values()) {
                max = Math.max(f, max);
            }
            for (String s : fruitAge.keySet()) {
                if (max == fruitAge.get(s)) {
                    System.out.println(s + " : " + fruitAge.get(s));
                }
            }

            System.out.println();

            System.out.println("The number of each type of fruit in descending order");
            Map<String, Integer> sorted = sortByValue(fruitCount, false);
            for (String s : sorted.keySet()) {
                System.out.println(s + " : " + sorted.get(s));
            }

            System.out.println();
            System.out.println("The various characterics (count, color, shape, etc.) of each fruit by type:");
            Map<String, Integer> sortedFruitCharacterCount = sortByValue(fruitCharacterCount, false);
            for (String s : sortedFruitCharacterCount.keySet()) {
                String f[] = s.split(",");
                System.out.println(sortedFruitCharacterCount.get(s) + " " + f[0] + " : " +
                        s.substring(s.indexOf(",") + 1)
                );
            }

        } catch (IOException e) {
            throw new Exception("File not found" + e.getMessage());
        } catch (Exception ex) {
            throw new Exception("Error: " + ex.getMessage());
        }
    }


    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap, final boolean ascending) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

        list.sort((o1, o2) -> ascending ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }

    private static String getCharKey(String tokens[]) {
        String key = tokens[0] + ",";
        for (int i = 2; i < tokens.length; i++) {
            key += tokens[i] + ",";
        }
        return key.substring(0, key.length() - 1);
    }

}
