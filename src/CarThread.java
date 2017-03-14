
public class CarThread implements Runnable {
	
	private String threadName;
	private int id;
	private TourHauntedHouse tour;
	private int[] carSeats;
	private int numberOfPassengersLoaded;
	private boolean carFull =false;

	public CarThread(int id, TourHauntedHouse t){
		setName("Car-"+id);
		tour = t;
		this.id = id;
		carSeats= new int[tour.getCapacity()];
		numberOfPassengersLoaded = 0;
		tour.setCarGoingHome(false);
	}
	
	public void msg(String m) {
		 System.out.println("["+(System.currentTimeMillis()-TourHauntedHouse.time)+"] "+threadName+" :"+m);
	}
	
	private void setName(String name) {
		threadName = name;
	}
	
	
	public void run() {
		while(!tour.isCarGoingHome()){
			tour.addCarToLine(threadName, id);		
			while(true){
				loadCar();
				if(carFull == true  && tour.carsTurn(id)){
					if(tour.lastCarRide()){
						int [] newCarSeats = new int[numberOfPassengersLoaded];
						for(int i=0; i<numberOfPassengersLoaded; i++){
							newCarSeats[i] = carSeats[i];
						}
						carSeats = newCarSeats;
					}
					if(tour.allPassengersExitedBusyWait(id, carSeats) && numberOfPassengersLoaded!=0){
						tour.rideCar(threadName, carSeats, id);
						msg("Completed round "+tour.getCurrentRide());
						interruptPassengers();
						carFull = false;
						numberOfPassengersLoaded = 0;
						carSeats = tour.resetSeats(carSeats);
						break;
					}
				}
				if(tour.getCurrentRide()>=tour.getLastCarRideID()){
					tour.setCarGoingHome(true);
					break;
				}
			}
		}
		attemptToLeave();

	}
		
	private void attemptToLeave() {
		while(true){
			if(tour.canCarQuit(id)){
				msg("CAR "+id+" IS LEAVING");
				break;
			}
		}		
	}

	private void loadCar() {
		if(carCanLoad()){
			seatPassenger();
			tellPassengerItCanExitBusyWait();
			numberOfPassengersLoaded++;
			if(numberOfPassengersLoaded == tour.getCapacity()){
				carFull = true;
			}else if(tour.lastCarRide() && !tour.passengersAreAvailable()){
				carFull = true;
			}
		}		
	}
	
	//If it's cars turn AND there's atleast one passenger in the queue AND car isn't full
	private boolean carCanLoad() {
		return tour.carsTurn(id) && tour.passengersAreAvailable() && !carFull;
	}
	
	//pop the first passenger from the passenger line and load the car
	private void seatPassenger() {
		carSeats[numberOfPassengersLoaded]=tour.getFirstPassenger(threadName);
	}
	
	private void tellPassengerItCanExitBusyWait() {
		msg("Passenger "+carSeats[numberOfPassengersLoaded]+" is ready to load car");		
		//This is to indicate to the passenger threads that the passenger has loaded the car
		tour.setPassengerLoaded(carSeats[numberOfPassengersLoaded]);
	}
	
	private void interruptPassengers() {
		for(int i=0; i<carSeats.length; i++){
			int passengerID = carSeats[i];
			tour.getPassengerThreads()[passengerID].interrupt();
		}
	}
}
