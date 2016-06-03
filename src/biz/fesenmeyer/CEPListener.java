package biz.fesenmeyer;

import java.util.LinkedList;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class CEPListener implements UpdateListener {

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) { 
		System.out.println("Zu viele schlechte Qualitätswerte");
		Simulation.machine.setStatus("wartung");
		double timeToMaintenanceEnd = RandomGenerator.generateNextMaintenanceEnd();
		Simulation.machine.setDownTime(Simulation.machine.getDownTime()+timeToMaintenanceEnd);
		double maintenanceEndTime = Simulation.getSimulationTime() + timeToMaintenanceEnd;
		Simulation.addToFel(maintenanceEndTime, "Wartungsende");
		System.out.println("Wartung wird begonnen, Wartungsende wurde geplant: "+
							String.format(Simulation.getFormat(), maintenanceEndTime));
		Simulation.initialiseCEP();
	}

}
