package src;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class SJF  {
    private int numberOfProcess;
    private int executionTime;
    private ArrayList <Integer> CPUidleTime = new ArrayList<>() ;
    public ArrayList <Process> processes = new ArrayList<>();
    public SJF(int numberOfProcess) {
        this.numberOfProcess = numberOfProcess;
    }

    // Allow users to set the value of processes
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
        processes.sort(Comparator.comparingInt(p -> p.getArrivalTime()));
        executeSJF();
    }

    // Helper method to execute Shortest-Job-First
    private void executeSJF() { // Like exectuing SJF
        ArrayList<Process> remainingProcesses = new ArrayList<>(processes);
    
        int currentTime = remainingProcesses.get(0).getArrivalTime();

        CPUidleTime = new ArrayList<>(Collections.nCopies(remainingProcesses.size(), 0));
    
        int index = 0;  // Index for tracking CPU idle times
    
        while (!remainingProcesses.isEmpty()) {
            ArrayList<Process> readyQueue = new ArrayList<>();
    
            // Collect processes that have arrived by currentTime
            for (Process p : remainingProcesses) {
                if (p.getArrivalTime() <= currentTime) {
                    readyQueue.add(p);
                }
            }
    
            if (readyQueue.isEmpty()) {
                // If no process is ready, find the next arriving process
                Process nextProcess = remainingProcesses.get(0);
                int idleTime = nextProcess.getArrivalTime() - currentTime;
    
                // Store the idle time before executing the next process
                CPUidleTime.set(index-1, idleTime);
    
                // Move time forward to the next available process
                currentTime = nextProcess.getArrivalTime();
            } else {
                // Select the shortest job from the ready queue
                readyQueue.sort(Comparator.comparingInt(Process::getBurstTime));
                Process shortestJob = readyQueue.get(0);

    
                if (shortestJob == null) {
                    break;  // Shouldn't happen, but prevents crashes
                }
    
                // Update CPU execution time
                if (CPUidleTime.get(index) == null) {
                    CPUidleTime.set(index, 0);
                }
    
                // Execute process
                currentTime += shortestJob.getBurstTime();
                shortestJob.setCompletionTime(currentTime);
                executionTime = currentTime;
    
                // Remove completed process
                remainingProcesses.remove(shortestJob);
                index++;
            }
        }
    }
    
    

    
    

    public void ganttChart(){
        // To render gantt chart correctly, we need to sort it based on completion time (SJF)
        processes.sort(Comparator.comparingInt(p -> p.getCompletionTime()));

        String display = "";
        int display_length = 0;
        ArrayList <Integer> CPUidlePropor = new ArrayList<>();
        for(int i = 0 ; i < processes.size() ; i++){
            processes.get(i).setPropor( ((int)(50*((double) processes.get(i).getBurstTime()/executionTime))));
            
            for(int j = 0; j < processes.get(i).getPropor() ;j++){
                if(j == processes.get(i).getPropor()/2 ){
                    display += String.format("%s(%d)",processes.get(i).getProcessID(),processes.get(i).getBurstTime());
                }
                display = display + Color.RED + "=" + Color.RESET;
                display_length = display_length - Color.RED.length() - Color.RESET.length();
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