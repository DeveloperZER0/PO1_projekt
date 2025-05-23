package com.healthtracker.service;

import com.healthtracker.model.Measurement;
import com.healthtracker.model.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ExportImportService {
    void exportMeasurementsToCsv(User user, File file) throws IOException;
    void importMeasurementsFromCsv(User user, File file) throws IOException;
}
