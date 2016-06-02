package biz.fesenmeyer;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.Map.Entry;

import com.espertech.esper.client.EPRuntime;


public class Machine {
	
	public String status = "frei";
	public int countWS = 0;
	public double downTime = 0.0;
	public Queue<Integer> qualityQueue = new LinkedList<Integer>();
	
	public void arrival(){
		System.out.println("Ankunft "+ String.format(Simulation.FORMAT, Simulation.simulationTime));
		double arrivalTime = Simulation.simulationTime+RandomGenerator.generateNextArrival();
		Simulation.addToFel(arrivalTime, "Ankunft");
		System.out.println("Ankunftsereignis wurde geplant: "+String.format(Simulation.FORMAT, arrivalTime));
		if(countWS == 0 && status.equalsIgnoreCase("frei")){
			double processingEnd = Simulation.simulationTime+RandomGenerator.generateNextProcessingEnd();
			status = "aktiv";
			Simulation.addToFel(processingEnd, "Bearbeitungsende");
			System.out.println("WS leer, Bearbeitung hat begonnen, Bearbeitungsendeereignis wurde geplant: "+String.format(Simulation.FORMAT, processingEnd));
		} else {
			countWS++;
			System.out.println("Das Teil wurde der Warteschlange hinzugefügt");
		}
	}

	public void processingEnd(EPRuntime cepRT){
		System.out.println("Bearbeitungsende "+ String.format(Simulation.FORMAT, Simulation.simulationTime));
		RandomGenerator.generateQuality(cepRT);
		
		if (!status.equalsIgnoreCase("wartung")){
			if(countWS > 0){
				countWS--;
				double processingEndTime = Simulation.simulationTime+RandomGenerator.generateNextProcessingEnd();
				Simulation.addToFel(processingEndTime, "Bearbeitungsende");
				System.out.println("Teil wurde nachgezogen, Bearbeitungsende wurde geplant: "+String.format(Simulation.FORMAT, processingEndTime));
			} else {
				status = "frei";
			}
		}
		
	}
	
	public void maintenanceEnd(){
		System.out.println("Wartungsende "+ String.format(Simulation.FORMAT, Simulation.simulationTime));
		Simulation.lastMaintenanceEnd = Simulation.simulationTime;
		
		if(countWS >= 1){
			countWS--;
			status = "aktiv";
			double processingEndTime = Simulation.simulationTime+RandomGenerator.generateNextProcessingEnd();
			Simulation.addToFel(processingEndTime, "Bearbeitungsende");
			System.out.println("Teil wurde nachgezogen, Bearbeitungsende wurde geplant: "+String.format(Simulation.FORMAT, processingEndTime));
		} else {
			status = "frei";
		}
	}
}

