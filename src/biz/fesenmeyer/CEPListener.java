package biz.fesenmeyer;

import java.util.LinkedList;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class CEPListener implements UpdateListener {

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) { 
		System.out.println("Zu viele schlechte Qualit�tswerte");
		Simulation.machine.status = "wartung";
		double timeToMaintenanceEnd = RandomGenerator.generateNextMaintenanceEnd();
		Simulation.machine.downTime += timeToMaintenanceEnd;
		double maintenanceEndTime = Simulation.simulationTime + timeToMaintenanceEnd;
		Simulation.addToFel(maintenanceEndTime, "Wartungsende");
		System.out.println("Wartung wird begonnen, Wartungsende wurde geplant: "+
							String.format(Simulation.FORMAT, maintenanceEndTime));
		Simulation.initialiseCEP();
	}

}
