package src;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class FCFS {
    private int numberOfProcess;
    private int executionTime;
    
    // Store CPU idle time (Its index is relative to the process index) and its value is either 0 or positive non 0
    // Example if from P1 to P2 there is a CPU idle time of 3ms, then idleTime[0] = 3 (Assuming P1 is in index 0)
    private ArrayList <Integer> CPUidleTime = new ArrayList<>() ;
    public ArrayList <Process> processes = new ArrayList<>();
    public FCFS(int numberOfProcess) {
        this.numberOfProcess = numberOfProcess;
    }

    // Allow users to input all processes properties
    public void iniProcess(Scanner scan){
        for(int i = 0; i < numberOfProcess; i++){
            System.out.print("Process ID: ");
            String processID = scan.nextLine();
            System.out.print("Arrival Time: ");
            int arrivalTime = ValidateInput.validateNonNegativeInt(scan);
            System.out.print("Burst Time: ");
            int burstTime = ValidateInput.validateNonNegativeInt(scan);
            
            Process p = new Process(processID, arrivalTime, burstTime);
            processes.add(p);
        }

        // Sort processes base on arrival
        processes.sort(Comparator.comparingInt(p -> p.getArrivalTime()));
        executionTime = executeFCFS();
    }
    
    // Helper method to find the total cpu execution time for gantt chart rendering
    // Setting completion time for all processes
    private int executeFCFS(){
        int currentTime = processes.get(0).getArrivalTime();
        for (Process p : processes) {
            if (currentTime < p.getArrivalTime()) {
                currentTime = p.getArrivalTime();
                CPUidleTime.add(p.getArrivalTime()-currentTime);
            } else {
                CPUidleTime.add(0);
            }
            currentTime += p.getBurstTime();
            p.setCompletionTime(currentTime);
        }
        return currentTime;
    }

    
   
    
    // Use to render gantt chart
    // The code seems complicate but it is not and is just time consuming
    public void ganttChart(){
        String display = "";
        int display_length = 0;

        // CPUidlePropor (proportion) is used to store each process portion on the chart (To render if how big it is)
        // Check test run to see its utilization
        ArrayList <Integer> CPUidlePropor = new ArrayList<>();
        for(int i = 0 ; i < processes.size() ; i++){
            processes.get(i).setPropor( ((int)(50*((double) processes.get(i).getBurstTime()/executionTime))));
            
            for(int j = 0; j < processes.get(i).getPropor() ;j++){
                if(j == processes.get(i).getPropor()/2 ){
                    display += String.format("%s(%d)",processes.get(i).getProcessID(),processes.get(i).getBurstTime());
                }
                display = display + Color.RED + "=" + Color.RESET;
                display_length = display_length - Color.RED.length() - Color.RESET.length();
                // RED AND RESET is a string whose length must not be counted in order to correctly render gantt chart
            }
            display += "|";
            if(CPUidleTime.get(i) != 0) {
                int idleTimePropor = ((int)(50*((double) CPUidleTime.get(i).intValue()/executionTime)));
                CPUidlePropor.add(idleTimePropor);
                for(int k = 0 ; k <CPUidlePropor.get(i).intValue() ; k++) {
                    if(k == CPUidlePropor.get(i).intValue()/2) {
                        display += String.format("(%d)", CPUidleTime.get(i).intValue());
                    }
                    
                    display = display + Color.RED + "=" + Color.RESET;
                display_length = display_length - Color.RED.length() - Color.RESET.length();
                }
                display += "|";
            } else {
                CPUidlePropor.add(0);
            }
        }
        display_length += display.length();
        System.out.print("\n\n\n\n+");
        for(int i = 0; i < display_length-1;i++){
            System.out.print("-");
        }
        System.out.print("+\n");
        System.out.print("|");
        System.out.print(display);
        System.out.print("\n+");
        for(int j = 0; j < display_length-1;j++){
            System.out.print("-");
        }
        System.out.print("+\n");
        int finishTime = processes.get(0).getArrivalTime() ;
        System.out.print(finishTime);
        for(int j = 0 ; j <processes.get(0).getPropor() + processes.get(0).getProcessID().length()+2+String.valueOf(processes.get(0).getBurstTime()).length()  ; j++) {
            System.out.print(" ");
        }
        for(int i = 0 ; i < processes.size() ; i++){
            finishTime += processes.get(i).getBurstTime();
            System.out.print(finishTime);
            if(CPUidleTime.get(i) != 0) {
                for( int j = 0 ; j < CPUidlePropor.get(i).intValue()+ 2 
                + CPUidleTime.get(i).toString().length() ; j++ ) {
                    System.out.print(" ");
                }
                if(String.valueOf(finishTime).length() > 1)
                {
                    System.out.print("\b");
                }
                finishTime += CPUidleTime.get(i).intValue();
                System.out.print(finishTime);
            }
            if(i != processes.size() -1){
                for(int j = 0 ; j <processes.get(i+1).getPropor() + processes.get(i+1).getProcessID().length() + 2 
                + String.valueOf(processes.get(i+1).getBurstTime()).length()  ; j++) {
                    System.out.print(" ");
                }
                if(String.valueOf(finishTime).length() > 1)
                {
                    System.out.print("\b");
                }
            }
            
        }
        System.out.println();
    }

    public void disTimes(){ 
        System.out.println("\nWaiting Time:                   Turnaround Time: \n");
        for(int i = 0 ; i < processes.size() ; i++){
            processes.get(i).setTurnAroundTime();
            processes.get(i).setWaitingTime();
            System.out.printf("%-3s : %-25s %-3s : %s\n",processes.get(i).getProcessID() ,processes.get(i).getWaitingTime() + "ms",processes.get(i).getProcessID(),processes.get(i).getTurnAroundTime() + "ms");
        }

        System.out.print("\nAverage Waiting Time:    ");
        float avgWaitTime = 0;
        float avgTurnTime = 0;
        for(int i = 0 ; i < processes.size() ; i++){
            processes.get(i).setTurnAroundTime();
            processes.get(i).setWaitingTime();
            avgWaitTime += processes.get(i).getWaitingTime();
            avgTurnTime += processes.get(i).getTurnAroundTime();
        }
        avgWaitTime = (float) avgWaitTime/processes.size();
        avgTurnTime = (float) avgTurnTime/processes.size();
        System.out.printf("%s\n\n",String.format("%.2fms", avgWaitTime));
        System.out.print("Average Turnaround Time: ");
        System.out.printf("%s\n\n\n\n\n\n\n\n",String.format("%.2fms", avgTurnTime));
    }

}