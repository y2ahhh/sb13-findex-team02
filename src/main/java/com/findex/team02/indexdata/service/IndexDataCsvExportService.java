package com.findex.team02.indexdata.service;

import java.time.LocalDate;

public interface IndexDataCsvExportService {

    byte[] exportCsv(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            String sortField,
            String sortDirection
    );
}
