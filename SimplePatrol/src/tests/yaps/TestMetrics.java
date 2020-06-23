package tests.yaps;

import yaps.metrics.FrequencyMetricsReport;
import yaps.metrics.IdlenessMetricsReport;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;


public class TestMetrics {

	public static void main(String[] args) {
		VisitsList list = new VisitsList();
		
		list.addVisit(1, 2);  //in instant 1, an agent visited node2
		list.addVisit(2, 1);
		list.addVisit(4, 1);
		list.addVisit(4, 2);
		list.addVisit(5, 3);
		list.addVisit(6, 1);
		list.addVisit(8, 1);
		list.addVisit(8, 2);
		list.addVisit(9, 2);
		
		IntervalMetricsReport intervalReport = new IntervalMetricsReport(4, 1, 10, list);

		System.out.println(" ===== INTERVALOS =====");
		System.out.println(intervalReport);

		System.out.println("Metricas:");
		System.out.printf(" - desvio padrao dos intervalos: %.3f \n", intervalReport.getStdDevOfIntervals());
		System.out.printf(" - intervalo medio: %.3f \n", intervalReport.getAverageInterval());
		System.out.printf(" - intervalo quadratico medio: %.3f \n", intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n", intervalReport.getMaximumInterval());

		System.out.println();
		System.out.println(" ===== OCIOSIDADES =====");
		IdlenessMetricsReport idlenessReport = new IdlenessMetricsReport(4, 1, 10, list);
		System.out.println(idlenessReport);
		
		System.out.println("Metricas:");
		System.out.printf(" - ociosidade media (global): %.3f \n", idlenessReport.getAverageIdleness());
		System.out.printf(" - ociosidade maxima: %.3f \n", idlenessReport.getMaxIdleness());

		System.out.println();
		System.out.println(" ===== FREQUENCIAS =====");
		FrequencyMetricsReport freqReport = new FrequencyMetricsReport(4, 1, 10, list);
		System.out.println(freqReport);
		
		System.out.println("Metricas:");
		System.out.printf(" - total de visitas: %d \n", freqReport.getTotalVisits());
		System.out.printf(" - visitacao e frequencia minimas: %d / %.3f \n", freqReport.getMinimumVisits(), freqReport.getMinimumFrequency());
		System.out.printf(" - visitacao e frequencia medias: %.3f / %.3f \n", freqReport.getAverageVisits(), freqReport.getAverageFrequency());
		System.out.printf(" - desvio padrão das visitacoes e das frequencias: %.3f / %.3f \n", freqReport.getStdDevOfVisits(), freqReport.getStdDevOfFrequencies());

	}

}
