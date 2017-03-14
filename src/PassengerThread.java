
public class PassengerThread implements Runnable {

	private int id;
	private TourHauntedHouse tour;
	private String threadName;
	private int numberOfTurnsAllowed;
	private int numberOfTurns;
	public PassengerThread(int ThreadId, TourHauntedHouse tourHouse){
		this.id = ThreadId;
		this.tour = tourHouse;
		setName("Passenger-" + id);
		numberOfTurnsAllowed = tourHouse.getNumberOfTurnsPerPassenger();
	}
	
	private void setName(String name) {
		threadName = name;
	}
	
	public void msg(String m) {
		 System.out.println("["+(System.currentTimeMillis()-TourHauntedHouse.time)+"] "+threadName+": "+m);
	}
	
	public void run() {
		addPassengerToPassengerLine();
		for(numberOfTurns=0; numberOfTurns<numberOfTurnsAllowed;numberOfTurns++){
			waitForCarToLoad();
			tour.passengerRideCar(threadName);
			tour.setPriorityOfThread(threadName);
			goBackToPassengerLine();
			tour.resetPriorityOfThread(threadName);
		}
		attemptToLeave();
		
	}
	
	private void attemptToLeave() {
		while(true){
			if(tour.canPassengerQuit(id)){
				msg("PASSENGER "+id+" IS LEAVING");
				break;
			}
			tour.attemptToLeave(threadName, id-1);
		}
	}

	//Add the passenger back to the queue if it isn't the passenger's last turn
	private void goBackToPassengerLine() {
		if(numberOfTurns!=numberOfTurnsAllowed-1){
			tour.addToQueue(threadName, id); //add passenger to the queue
		}
	}
	
	//If the passenger has loaded the car, exit busy wait
	private void waitForCarToLoad() {
		while(true){
			if(tour.passengerLoadedCar(id)){
				tour.passengerExitedBW(threadName,id);
				break;
			}
		}	
		Thread.yield();
	}
	
	private void addPassengerToPassengerLine() {
		tour.addToQueue(threadName, id); //add passenger to the queue	
	}
	
	
}
