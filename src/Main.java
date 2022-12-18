import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        Map<Integer, String> authors = new TreeMap<>();
        String line;
        byte[] bytes = null;
        double max;
        double max2 = 0.0d;
        double min;
        double min2 = 10000.0;
        int idMaxAuthor = 0;
        int idMinAuthor = 0;
        String bookNameMax = null;
        String bookNameMin = null;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("data/csv/author.csv"))) {

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("id")) {
                    continue;
                }
                String[] arr = line.split(",");
                authors.put(Integer.valueOf((arr[0])), arr[1]);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        //authors.forEach((key, value) -> System.out.println(key + " " + value));

        File createDirectoryIfNot = new File("result/pictures");

        if (!createDirectoryIfNot.exists()) createDirectoryIfNot.mkdirs();

        File[] listFile = new File("data/images").listFiles();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("data/csv/book.csv"))) {

            while ((line = bufferedReader.readLine()) != null) {

                if (line.contains("id")) {
                    continue;
                }
                String[] arr = line.split(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
                // регулярка не моя - пока использовала её

                max = Double.parseDouble(arr[2]);
                if (max > max2) {
                    max2 = max;
                    bookNameMax = arr[1];
                    idMaxAuthor = Integer.parseInt(arr[5]);
                }

                min = Double.parseDouble(arr[2]);
                if (min < min2) {
                    min2 = min;
                    bookNameMin = arr[1];
                    idMinAuthor = Integer.parseInt(arr[5]);
                }

                if (listFile != null) {

                    for (File x : listFile) {
                        String strName = x.getName();

                        try (FileInputStream inputStream = new FileInputStream("data/images/" + strName)) {
                            bytes = inputStream.readAllBytes();
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                        if (strName.equals(arr[4])) {

                            String value = null;

                            for (Map.Entry<Integer, String> map : authors.entrySet()) {
                                if (map.getKey().equals(Integer.valueOf(arr[5]))) {
                                    value = map.getValue();
                                }
                            }
                            String nameFile = "result/pictures/"
                                    + "\"" + value + " - " + arr[1].replaceAll("\"", "")
                                    + "\"" + ".jpg";

                            try (FileOutputStream fileOutputStream = new FileOutputStream(nameFile)) {

                                fileOutputStream.write(bytes);

                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        String nameAuthorMax = null;
        String nameAuthorMin = null;

        for (Map.Entry<Integer, String> map : authors.entrySet()) {
            if (map.getKey().equals(idMaxAuthor)) {
                nameAuthorMax = map.getValue();
            }
            if (map.getKey().equals(idMinAuthor)) {
                nameAuthorMin = map.getValue();
            }
        }

        String strMaxPrice = "Max price = " + max2 + " " + "\"" + nameAuthorMax + " - " + bookNameMax + "\"";
        String strMinPrice = "Max price = " + min2 + " " + "\"" + nameAuthorMin + " - " + bookNameMin + "\"";
        System.out.println(strMaxPrice);
        // вывод минимального, но одной книжки - при дублировании цен -> список / массив
        System.out.println(strMinPrice);

        try (FileWriter fileWriter = new FileWriter("result/priceMaxMin.txt")) {
            fileWriter.write(strMaxPrice + "\n" + strMinPrice);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}