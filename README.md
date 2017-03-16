# HauntedHouse1

CS340 - Operating Systems Project #1

Java multithreading project using synchronized methods 

Using java programming, synchronize the threads in the context of the problem. Do NOT use any semaphores, wait(), notify() or notifyAll()

PROBLEM DESCRIPTION:

- Suppose there are Npass passenger threads (default 11) and Ncars (default 3). 
- The passengers line up to take rides in cars to tour the Haunted house and busy wait for a car to start loading.
-Each car can hold a certain number of passengers P (default 4).
- When the car returns from a tour of the Haunted House and empties, it will let the passengers on line take a haunted house tour by taking
seats in the car upto the car's passenger capacity.
The cars wait and they will depart when the last passenger to take a seat will signal. (binary
semaphores).
- However as soon as the passenger is allowed to get in the car, as a polite gesture he will first yeild.
- The last passenger that gets in the car will have the car started.
- Throughout the ride the passengers sleep for a long time. The car thread will wake up the passengers sleeo by using interrupt().
- The car goes out on tour only when it is full, so if there is not enough passengers when the car returns, the car waits until it fills.
- There is a possibility that the last car will not be completely filled so an exception can be made.
- After a passenger completes three rides, he will wonder for a while and then attempt to leave.
- Passengers leave in a specific order, N will join N-1, N-1 joins N-2,..., first passenger doesn't join any thread.
- The last passenger to leave will also have the cars terminate.
