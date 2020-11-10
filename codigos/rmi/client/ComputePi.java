package rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.math.BigDecimal;

import rmi.compute.Compute;

public class ComputePi {
    public static void main(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
           // *** Remote execution
            String name = "Compute";
    		//Get a time stamp
    		Date ts1 = new Date();
    		//Locate registry
    		Registry registry = LocateRegistry.getRegistry(args[0]);
            Date ts2 = new Date();
            //Lookup
            Compute comp = (Compute) registry.lookup(name);
            Date ts3 = new Date();
            //Execute task
            Pi task = new Pi(Integer.parseInt(args[1]));
            //Remote execution
            BigDecimal pi = comp.executeTask(task);
		 //Get a new time stamp and calculate elapsed time
            Date now = new Date();
            long elapsed1 = ts2.getTime() - ts1.getTime();
		  long elapsed2 = ts3.getTime() - ts2.getTime();
		 long elapsed3 = now.getTime() - ts3.getTime();			       System.out.println();
            System.out.println("*** REMOTE EXECUTION ***");
            System.out.println("Pi: "+pi);
		 System.out.println("Locate RMI registry: "+elapsed1+" miliseconds.");			
            System.out.println("Lookup remote object by name in the registry: "+elapsed2+" miliseconds.");			
            System.out.println("Remote execution of the task: "+elapsed3+" miliseconds.");
            System.out.println("Total elapsed time: "+(elapsed1+elapsed2+elapsed3)+" miliseconds.");

            // *** Local execution
		 Date local = new Date();
            Pi task2 = new Pi(Integer.parseInt(args[1]));
            BigDecimal pi2 = task2.execute();
            System.out.println();
            System.out.println("*** LOCAL EXECUTION ***");
            System.out.println("Pi: "+pi2);
            long elapsed = (new Date()).getTime() - local.getTime();
            System.out.println("Total elapsed time: "+elapsed+" miliseconds.");            
        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }    
}
