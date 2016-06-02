package biz.fesenmeyer;
import java.util.Random;

import com.espertech.esper.client.EPRuntime;


public class RandomGenerator {


	public static double generateNextMaintenanceEnd(){
		int number = generateRandomInt(1, 3);
		return (double)number/10;
	}

	public static double generateNextArrival(){
		int number = generateRandomInt(1, 2);
		return (double) number/10;
	}	
	public static void generateQuality(EPRuntime cepRT){
		double timeSinceLastMaintenance = Simulation.simulationTime-
										Simulation.lastMaintenanceEnd;
		double number =  generateRandomInt(0, 4)+timeSinceLastMaintenance;
		int value;
		if(number < Simulation.MTBF){
			value=0;
		}else{
			value=1;
		}
		Quality quality = new Quality(value);
		System.out.println(quality);
		cepRT.sendEvent(quality);
	}
	
	public static double generateNextProcessingEnd(){
		int number = generateRandomInt(1, 2);
		return (double) number/10;
	}
	
	public static int generateRandomInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}

}