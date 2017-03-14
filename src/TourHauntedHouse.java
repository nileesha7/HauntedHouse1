import java.util.Random;
import java.util.Vector;

class TourHauntedHouse {
	
	private int capacity;
	private int countPassengers;
	private int countCars;
	private boolean carGoingHome; 

	private int numberOfRoundsPerPassenger;

	private boolean carsTurnToRide;
	
	private Vector<Integer> carLine; 	//FCFS queue that holds id's of the car threads
	private Vector<Integer> passengerLine; 	//FCFS queue that holds id's of the passenger threads
	
	private boolean [] passengerExitedBusyWait;
	private boolean [] passengerLoaded; //indicates whether passenger loaded car
	
	private Thread[] passengerThreads;
	private Thread[] carThreads;

	private boolean[] passengerCanExit;
	private boolean[] carCanExit;

	private final int lastCarRide;
	private int currentCarRide;
	
	private int passengerToGoHome;
	private int passengersCompleted;
	
	public static long time = System.currentTimeMillis();

	public void msg(String threadName, String m) {
		 System.out.println("["+(System.currentTimeMillis()-time)+"] "+threadName+": "+m);
	}
	
	
	public TourHauntedHouse(int num_passengers, int num_cars, int car_capacity, int numberOfRoundsPerPassenger){
		countPassengers = num_passengers;
		countCars = num_cars;
		capacity = car_capacity;
		this.numberOfRoundsPerPassenger = numberOfRoundsPerPassenger;
		passengerLine = new Vector<Integer>();
		carLine = new Vector<Integer>();
		
		carGoingHome = false;
		currentCarRide=0;

		passengerLoaded = new boolean[countPassengers];
		passengerExitedBusyWait = new boolean[countPassengers];
		
		passengerThreads = new Thread[countPassengers];
		carThreads = new Thread[countCars];

		passengerCanExit = new boolean[countPassengers];
		carCanExit = new boolean[countCars];

		lastCarRide = (int) Math.ceil((double)(countPassengers * numberOfRoundsPerPassenger)/capacity);
		passengerToGoHome = 0;
		passengersCompleted = 0;
	}	
	
	public int getNumberOfTurnsPerPassenger(){
		return numberOfRoundsPerPassenger;
	}
	public synchronized void addToQueue(String threadName, int id){
		passengerLine.addElement(id); 
		passengerThreads[id]=Thread.currentThread();
		String strQueue = convertPassengerLineToString();
		msg(threadName, "Passenger enters the passenger queue ");
		msg(threadName, strQueue);
	}

	public synchronized String convertArrayToString(int[] arr) {
		String str = "";
		for(int i=0; i<arr.length; i++){
			str+=arr[i]+", ";
		}
		str = " {"+str+"}";
		return str;
	}
	
	private synchronized String convertPassengerLineToString(){
		String str="";
		for(int i=0; i<passengerLine.size(); i++){
			str+="-> "+passengerLine.get(i);
		}
		str = "Passenger Line "+str;
		return str;
	}

	public synchronized String convertCarLineToString(){
		String str="";
		for(int i=0; i<carLine.size(); i++){
			str+="car "+i+": "+carLine.get(i)+", ";
		}
		str = "Car Line: {"+str+"}";
		return str;
	}
	public int getCapacity() {
		return capacity;
	}

	//Add car to the end of the car line
	public synchronized void addCarToLine(String threadName, int id) {
		carLine.addElement(id);
		carThreads[id]=Thread.currentThread();
		String str = convertCarLineToString();
		msg(threadName, "Car enters car queue");
		msg(threadName, str);
	}

	public synchronized void loadCar(String threadName) {
		msg(threadName, "Car is about to leave");
	}

	//return true if the current car is the car in the top of the queue
	public synchronized boolean carsTurn(int id) {
		return carLine.firstElement()==id;
	}


	public boolean passengersAreAvailable() {
		return !passengerLine.isEmpty();
	}

	public synchronized int getFirstPassenger(String threadName) {
		return passengerLine.remove(0);
	}

	public synchronized void setPassengerLoaded(int passengerID){
		passengerLoaded[passengerID]=true;
	}

	public synchronized boolean passengerLoadedCar(int passengerId) {
		return passengerLoaded[passengerId];
	}

	public synchronized void passengerExitedBW(String threadName, int id) {
		msg(threadName, "EXITS BUSY WAIT");
		passengerExitedBusyWait[id]=true;
	}

	public synchronized boolean allPassengersExitedBusyWait(int id, int[] carSeats) {
	
		for(int i=0; i<carSeats.length; i++){
			int passengerID = carSeats[i];
			if(!passengerExitedBusyWait[passengerID]){
				return false;
			}
		}
		return true;
	}
	
	
	public void removeFirstCar(String threadName) {
		msg(threadName, "Removed car!");
		carLine.remove(0);
	}

	public synchronized void resetPassengersExitedBusyWait() {
		for(int i=0; i<passengerExitedBusyWait.length; i++){
			passengerExitedBusyWait[i]=false;
		}
	}
	public synchronized void resetPassengersLoadedCar() {
		for(int i=0; i<passengerLoaded.length; i++){
			passengerLoaded[i]=false;
		}
	}

	public synchronized Thread[] getPassengerThreads(){
		return passengerThreads;
	}

	public synchronized Thread[] getCarThreads(){
		return carThreads;
	}
	
	public synchronized boolean isCarsTurnToRide(int id) {
		return carsTurnToRide;
	}

	public synchronized void rideCar(String threadName, int [] carSeats, int carID) {
		resetPassengersExitedBusyWait();
		resetPassengersLoadedCar();
		removeFirstCar(threadName);
		msg(threadName, "GOING ON TOUR! PASSENGERS IN THE CAR: "+ convertArrayToString(carSeats));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}			
		incrementNumberOfCarRides(threadName);
		msg(threadName, "FINISHED HAUNTED HOUSE TOUR! PASSENGERS IN THE CAR"+convertArrayToString(carSeats));	
	}
	

	
	public synchronized void incrementNumberOfCarRides(String threadName){
		currentCarRide++;
	}


	public synchronized boolean lastCarRide() {
		return currentCarRide==lastCarRide-1;
	}



	public synchronized int getCurrentRide(){
		return currentCarRide;
	}





	public int[] resetSeats(int[] carSeats) {
		for(int i=0; i<carSeats.length; i++){
			carSeats[i]=0;
		}
		return carSeats;
	}


	public synchronized void incrementPassengerGoingHome(){
		passengerToGoHome++;
	}

	public synchronized boolean isCarGoingHome(){
		return carGoingHome;
	}
	
	public synchronized void setCarGoingHome(boolean val){
		carGoingHome = val;
	}


	public synchronized boolean carCanRide(String threadName) {
		return currentCarRide<lastCarRide;
	}


	public int getPassengerToGoHome() {
		return passengerToGoHome;
	}


	//Passenger rides car until it's interrupted by the car
	public  void passengerRideCar(String threadName) {
		try {
			Thread.sleep(100000000);
		} catch (InterruptedException e) {
			msg(threadName, "Car interrupted thread");
		}				
	}


	public void setPriorityOfThread(String threadName) {
		Random randInt = new Random();
		Thread.currentThread().setPriority(randInt .nextInt(Thread.MAX_PRIORITY)+Thread.MIN_PRIORITY);
		msg(threadName,"SET PRIORITY OF THE THREAD TO: "+Thread.currentThread().getPriority());
	}

	public void resetPriorityOfThread(String threadName) {
		try {
			Thread.sleep(1000);
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
			msg(threadName, "RESET PRIORITY OF THE THREAD TO: "+Thread.currentThread().getPriority());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}		
	}

	public int getPassengerCount() {
		return countPassengers;
	}

	
	public synchronized void incrementPassengersCompleted(){
		passengersCompleted++;
	}


	public int getPassengersCompleted() {
		return passengersCompleted;
	}


	public  void attemptToLeave(String threadName, int id) {
		if(passengerToGoHome == id){
				try {
					if(passengerThreads[id].isAlive()){
						msg(threadName, "WAITING FOR PASSENGER "+id+" TO LEAVE");
						passengerCanExit[id]=true;
						passengerThreads[id].join();
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
			incrementPassengerGoingHome();
		}else if(passengerToGoHome==countPassengers-1){
			try {
				for(int i = 0; i<countCars; i++){
					if(carThreads[i].isAlive()){
						msg(threadName, "WAITING FOR CAR "+i+" TO LEAVE");
						carCanExit[i]=true;
						carThreads[i].join();
					}
				}
				passengerCanExit[passengerToGoHome]=true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized boolean canPassengerQuit(int id){
		return passengerCanExit[id];
	}

	public synchronized boolean canCarQuit(int id) {
		return carCanExit[id];
	}
	
	public int getLastCarRideID(){
		return lastCarRide;
	}
	
}
