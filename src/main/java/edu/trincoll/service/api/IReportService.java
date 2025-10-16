package edu.trincoll.service.api;

import java.util.Map;

/**
 * Interface defining report generation operations
 */
public interface IReportService {
    Map<String, Integer> generateBookStatusReport();
    Map<String, Integer> generateMembershipReport();
    Map<String, Double> generateLateFeeReport();
}