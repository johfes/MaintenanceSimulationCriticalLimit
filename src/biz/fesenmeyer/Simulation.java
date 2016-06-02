package biz.fesenmeyer;
import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TreeMap;
import java.util.TreeSet;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;


public class Simulation {
	/**
	 * Future Event List.
	 */
	public static final SortedMap<Double, ArrayList<String>> FEL = new TreeMap<>();
	public static double simulationTime = 0.0;
	/**
	 * Mean Time Between Failure.
	 */
	public static final double MTBF = 6.0;
	public static final String FORMAT = "%.1f";
	public static final double SIMULATION_DURANCE = 14.0;
	public static double lastMaintenanceEnd = 0.0;
	public static Machine machine;
	public static EPRuntime cepRT;
	public static EPServiceProvider cep;
	
	
	public static void main(String[] args) {
		initialiseCEP();
		
		machine = new Machine();
	    addToFel(0.1, "Ankunft");

	    System.out.println("Simulationsbeginn");
	    
		while(true){
			System.out.println("*****************************************");
			
		    for(Entry<Double, ArrayList<String>> entry : FEL.entrySet()) {
		    	  final Double key = entry.getKey();
		    	  final Object value = entry.getValue();

		    	  System.out.println(String.format(FORMAT, key) + " => " + value);
		    }
		    
	    	  System.out.println("WS" + " => " + machine.countWS);
	    	  simulationTime = FEL.firstKey();
	    	  if(simulationTime > SIMULATION_DURANCE){
	    		  break;
	    	  }
			System.out.println("*****************************************");
		    
		    ArrayList<String> eventTypes = FEL.get(FEL.firstKey());
		    for (String eventType: eventTypes){
		    	execute(eventType, machine);
		    }
		    FEL.remove(simulationTime);
		    
		}

		System.out.println("*****************************************");
		System.out.println("Simulationsende");
		System.out.println("*****************************************");
		System.out.println("Statistik");
		System.out.println("Verf�gbarkeit: "+
				String.format(FORMAT, calculateAvailability(machine.downTime))+"%");
		
	}
	
	public static void initialiseCEP(){
	    Configuration cepConfig = new Configuration();
	    // register Qualities as objects the engine will have to handle
	    cepConfig.addEventType("Quality",Quality.class.getName());
		// setup the engine
		cep = EPServiceProviderManager.getProvider("CEPEngine",cepConfig);
		cep.initialize();
		cepRT = cep.getEPRuntime();
		//  register an EPL statement
		EPAdministrator cepAdm = cep.getEPAdministrator();
		EPStatement cepStatement = cepAdm.createEPL("select * from " +
		                                "Quality.win:length(10) " +
		                                "having sum(value) > 1");
		
		cepStatement.addListener(new CEPListener());
	}
	
	
	private static void execute(String eventType, Machine machine) {
		switch(eventType){
		case "Ankunft":
			machine.arrival();
			break;
		case "Bearbeitungsende":
			machine.processingEnd(cepRT);
			break;
		case "Wartungsende":
			machine.maintenanceEnd();
			break;
		default:
			throw new IllegalStateException("Unknown eventType: "+eventType);
	}
}
	
	public static void addToFel(Double key, String type){
		if(FEL.containsKey(key)){
			ArrayList<String> eventList= FEL.get(key);
			eventList.add(type);
		} else {
			ArrayList<String> eventList = new ArrayList<String>();
			eventList.add(type);
			FEL.put(key,eventList);
		}
	}
	
	public static void removeFromFel(Double key, String type){
  	  ArrayList<String> list = (ArrayList<String>) FEL.get(key);
  	  if(list.size() > 1){
  		list.remove(type);
  		FEL.replace(key, list);
  	  } else{
		  FEL.remove(key);
  	  }
	}
	
	public static double calculateAvailability(Double downTime){
		double availabilityTime = SIMULATION_DURANCE-downTime;
		return (availabilityTime/SIMULATION_DURANCE)*100;
	}

	public static int getQualitySum(Queue<Integer> queue){
		int sum = 0;
		for(Integer quality : queue){
			sum+=quality;
		}
		return sum;
	}
}