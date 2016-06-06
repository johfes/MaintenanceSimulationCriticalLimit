package biz.fesenmeyer;
import java.util.Random;

import com.espertech.esper.client.EPRuntime;


public class RandomGenerator {
    static Random random = new Random();

	public static double generateNextMaintenanceEnd(){
		int number = generateRandomInt(1, 3);
		return (double)number/10;
	}

	public static double generateNextArrival(){
		return exponential(0.5)/10.0;
	}	
	public static void generateQuality(EPRuntime cepRT){
		double timeSinceLastMaintenance = Simulation.getSimulationTime()-
										Simulation.getLastMaintenanceEnd();
		double number =  generateRandomInt(0, 4)+timeSinceLastMaintenance;
		int value;
		if(number < Simulation.getMtbf()){
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
	
	public static double exponential(double lambda){
		return (-1/lambda) * Math.log(1-random.nextDouble());
	}
	
	public static int generateRandomInt(int min, int max) {
	    int randomNum = random.nextInt((max - min) + 1) + min;
	    return randomNum;
	}

}