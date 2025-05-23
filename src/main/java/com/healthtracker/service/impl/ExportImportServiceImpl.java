package com.healthtracker.service.impl;

import com.healthtracker.model.*;
import com.healthtracker.service.ExportImportService;
import com.healthtracker.service.MeasurementService;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExportImportServiceImpl implements ExportImportService {

    private final MeasurementService measurementService;

    public ExportImportServiceImpl(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @Override
    public void exportMeasurementsToCsv(User user, File file) throws IOException {
        List<Measurement> measurements = measurementService.getMeasurementsByUser(user);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("ID,Data,Typ,Opis\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Measurement m : measurements) {
                writer.write(String.format("%d,%s,%s,%s\n",
                        m.getId(),
                        m.getTimestamp().format(formatter),
                        m.getClass().getSimpleName(),
                        m.getSummary()
                ));
            }
        }
    }

    @Override
    public void importMeasurementsFromCsv(User user, File file) throws IOException {
        List<Measurement> existingMeasurements = measurementService.getMeasurementsByUser(user);
        Set<String> existingKeys = new HashSet<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Measurement m : existingMeasurements) {
            String key = m.getTimestamp().format(formatter) + ":" + m.getClass().getSimpleName() + ":" + m.getSummary();
            existingKeys.add(key);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Pomijamy nagłówek
                }
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String timestampStr = parts[1];
                String type = parts[2];
                String summary = parts[3];
                String key = timestampStr + ":" + type + ":" + summary;

                if (existingKeys.contains(key)) {
                    System.out.println("Duplikat pominięty: " + key);
                    continue;
                }

                try {
                    Measurement m = null;
                    if (type.equals("WeightMeasurement")) {
                        double weight = Double.parseDouble(summary.replace(" kg", ""));
                        WeightMeasurement wm = new WeightMeasurement();
                        wm.setWeight(weight);
                        m = wm;
                    } else if (type.equals("BloodPressureMeasurement")) {
                        String[] bp = summary.replace(" mmHg", "").split("/");
                        int sys = Integer.parseInt(bp[0]);
                        int dia = Integer.parseInt(bp[1]);
                        BloodPressureMeasurement bpm = new BloodPressureMeasurement();
                        bpm.setSystolic(sys);
                        bpm.setDiastolic(dia);
                        m = bpm;
                    } else if (type.equals("HeartRateMeasurement")) {
                        int bpm = Integer.parseInt(summary.replace(" BPM", ""));
                        HeartRateMeasurement hrm = new HeartRateMeasurement();
                        hrm.setBpm(bpm);
                        m = hrm;
                    }

                    if (m != null) {
                        m.setUser(user);
                        m.setTimestamp(LocalDateTime.parse(timestampStr, formatter));
                        measurementService.addMeasurement(m);
                    }

                } catch (Exception e) {
                    System.err.println("Nieprawidłowy wiersz: " + line);
                }
            }
        }
    }
}