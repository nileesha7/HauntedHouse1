public class Main {
	
	private static int passengerThreadCount=11;
	private static int carThreadCount=3;
	private static int carCapacity=4;
	private  static int numberOfRoundsPerPassenger = 3;
	private static String threadName = "Main";
	
	public static void msg(String m) {
		 System.out.println("["+(System.currentTimeMillis()-TourHauntedHouse.time)+"] "+threadName+": "+m);
	}
	
	public static void main(String [] args){
		if(args.length==3){
			passengerThreadCount = Integer.parseInt(args[0]);
			carThreadCount = Integer.parseInt(args[1]);
			carCapacity = Integer.parseInt(args[2]);
		}
	
		TourHauntedHouse tourHauntedHouse = new TourHauntedHouse(passengerThreadCount, carThreadCount, carCapacity, numberOfRoundsPerPassenger);
		
		Thread passenger;
		//Create and start passenger threads
		for(int i=0; i<passengerThreadCount; i++){
			passenger = new Thread(new PassengerThread(i, tourHauntedHouse));
			passenger.start();
		}
		
		
		Thread car;
		//Create and start car threads
		for(int i=0; i<carThreadCount; i++){
			car = new Thread(new CarThread(i, tourHauntedHouse));
			car.start();
		}
		
	
		
	}

	
}  