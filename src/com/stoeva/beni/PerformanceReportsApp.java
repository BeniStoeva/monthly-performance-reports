package com.stoeva.beni;

import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PerformanceReportsApp {

    public static void main(String[] args) {

        String dataFile = args[0];
        String reportDefFile = args[1];
        String reportsInformation, employeeInformation;
        final String[] CSV_COLUMN_NAMES = new String[]{"name", "score"};

        try {
            employeeInformation = new String((Files.readAllBytes(Paths.get(dataFile))));
            JSONObject employeeJsonData = new JSONObject(employeeInformation);

            reportsInformation = new String((Files.readAllBytes(Paths.get(reportDefFile))));
            JSONObject reportJsonData = new JSONObject(reportsInformation);

            JSONArray employees = employeeJsonData.getJSONArray("data");

            FileWriter outputCsvFile = new FileWriter("output/result.csv");
            CSVWriter csvWriter = new CSVWriter(outputCsvFile);
            csvWriter.writeNext(CSV_COLUMN_NAMES);

            for (int i = 0; i < employees.length(); i++) {
                JSONObject employee = employees.getJSONObject(i);
                double score = calculateScore(employee, reportJsonData);
                if (isTopPerformance(employee, reportJsonData, score)) {
                    csvWriter.writeNext(new String[]{employee.getString("name"), "" + score});
                }
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static double calculateScore(JSONObject data, JSONObject reportDef) {
        boolean useExperienceMultiplier = reportDef.getBoolean("useExperienceMultiplier");
        int totalSales = data.getInt("totalSales");
        int salesPeriod = data.getInt("salesPeriod");
        double experienceMultiplier = data.getDouble("experienceMultiplier");

        if (useExperienceMultiplier) {
            return totalSales / (double) salesPeriod * experienceMultiplier;
        }
        return totalSales / (double) salesPeriod;
    }

    private static boolean isTopPerformance(JSONObject data, JSONObject reportDef, double score) {
        int salesPeriod = data.getInt("salesPeriod");
        int periodLimit = reportDef.getInt("periodLimit");
        int topPerformersThreshold = reportDef.getInt("topPerformersThreshold");

        return salesPeriod <= periodLimit && score >= topPerformersThreshold;
    }


}













