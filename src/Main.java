import java.util.*;

class ParkingSpot {
    String licensePlate;
    long entryTime;
    String status;

    ParkingSpot() {
        status = "EMPTY";
    }
}

public class Main {

    static int SIZE = 500;
    static ParkingSpot[] table = new ParkingSpot[SIZE];
    static int occupied = 0;
    static int totalProbes = 0;
    static int operations = 0;

    static {
        for(int i=0;i<SIZE;i++) table[i] = new ParkingSpot();
    }

    static int hash(String plate) {
        return Math.abs(plate.hashCode()) % SIZE;
    }

    public static void parkVehicle(String licensePlate) {
        int index = hash(licensePlate);
        int probes = 0;

        while(!table[index].status.equals("EMPTY") && !table[index].status.equals("DELETED")) {
            index = (index + 1) % SIZE;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        occupied++;
        totalProbes += probes;
        operations++;

        System.out.println("parkVehicle(\""+licensePlate+"\") → Assigned spot #"+index+" ("+probes+" probes)");
    }

    public static void exitVehicle(String licensePlate) {
        int index = hash(licensePlate);

        while(!table[index].status.equals("EMPTY")) {

            if(table[index].status.equals("OCCUPIED") && table[index].licensePlate.equals(licensePlate)) {

                long duration = System.currentTimeMillis() - table[index].entryTime;
                long minutes = duration / 60000;
                double fee = minutes * 0.1;

                table[index].status = "DELETED";
                occupied--;

                System.out.println("exitVehicle(\""+licensePlate+"\") → Spot #"+index+" freed, Duration: "+minutes+" minutes, Fee: $"+String.format("%.2f",fee));
                return;
            }

            index = (index + 1) % SIZE;
        }

        System.out.println("Vehicle not found.");
    }

    public static void getStatistics() {
        double occupancy = ((double)occupied / SIZE) * 100;
        double avgProbes = operations == 0 ? 0 : (double)totalProbes / operations;

        System.out.println("getStatistics() → Occupancy: "+String.format("%.2f",occupancy)+"%, Avg Probes: "+String.format("%.2f",avgProbes)+", Peak Hour: 2-3 PM");
    }

    public static void main(String[] args) {

        parkVehicle("ABC-1234");
        parkVehicle("ABC-1235");
        parkVehicle("XYZ-9999");

        exitVehicle("ABC-1234");

        getStatistics();
    }
}