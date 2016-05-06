package utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CheatSheetHelper {

    public static String[] sortSheets(String[] sheetFiles) {
        // first letters of file name before "-" serves as sort index
        Arrays.sort(sheetFiles, new Comparator<String>() {

            public int compare(String f1, String f2) {

                if (f1.contains("-") && f2.contains("-")) {
                    return f1.substring(0, f1.indexOf("-"))
                            .compareTo(f2.substring(0, f1.indexOf("-")));
                } else {
                    return f1.compareTo(f2);
                }
            }
        });

        return sheetFiles;
    }

    public static String getCategoryTitle(String category) {
        // split camelCaseWord into separate words
        String[] parts   = category.trim().split("(?<!^)(?=[A-Z])");
        StringBuilder title = new StringBuilder();

        // capitalize first char of each word
        for (String part : parts) {
            if (part.length() > 0) {
                title.append(Character.toUpperCase(part.charAt(0)));

                if (part.length() > 1) {
                    title.append(part.substring(1));
                }
                title.append(" ");
            }
        }

        return title.toString().trim();
    }

    public static Map<String, String> listCategoriesAndTitles(String[] categories) {

        Arrays.sort(categories);

        Map<String, String> categoriesAndTitles = new LinkedHashMap<String, String>();

        for (String category : categories) {
            categoriesAndTitles.put(category, getCategoryTitle(category));
        }

        return categoriesAndTitles;
    }
}