package src;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class RR {
    private int numberOfProcess;
    private int executionTime;
    public ArrayList <Process> processes = new ArrayList<>();

    public RR(int numberOfProcess) {
        this.numberOfProcess = numberOfProcess;
    }

    // Allow users to input all processes based on the number of processes
    public void iniProcess(Scanner scan){
        System.out.print("Time Quantum: ");
        int timeQuantum = ValidateInput.validateNonNegativeInt(scan);
        for(int i = 0; i < numberOfProcess; i++){
            System.out.print("Process ID: ");
            String processID = scan.nextLine();
            System.out.print("Arrival Time: ");
            int arrivalTime = ValidateInput.validateNonNegativeInt(scan);
            System.out.print("Burst Time: ");
            int burstTime = ValidateInput.validateNonNegativeInt(scan);
            Process p = new Process(processID, arrivalTime, burstTime,timeQuantum);
            processes.add(p);
        }
        processes.sort(Comparator.comparingInt(p -> p.getArrivalTime()));
        executeRR();
    }

    // A helper method, used to execute Round Robin algorithm in iniProcess method
    private void executeRR() {
        ArrayList <Process> remainingProcesses = new ArrayList<>(processes); // Store remaining processes
        ArrayList <Process> readyQueue = new ArrayList<>();                  // Store arrived processes in queue
        ArrayList <Process> sortedProcesses = new ArrayList<>();

        int timeQuantum = processes.get(0).getTimeQuantum();            
        int currentTime = processes.get(0).getArrivalTime();         // Track the current time during execution

        // Check if all processes have been executed
        while (!remainingProcesses.isEmpty() || !readyQueue.isEmpty()) { 
            Process currentProcess = null;

            // prevent out of bound error when there is no remaining process
            if (!remainingProcesses.isEmpty()){ 
                currentProcess = remainingProcesses.get(0);
            }

            // Handle CPU idle time (Waiting for new process to arrive)
            if (currentProcess != null && currentTime < currentProcess.getArrivalTime() && readyQueue.isEmpty()){
                int cpuIdle = currentProcess.getArrivalTime() - currentTime;
                sortedProcesses.add(new Process("idle",cpuIdle));
                currentTime += cpuIdle;
            } else {
                // Adding all remaining process that have arrived to readyQueue
                while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).getArrivalTime() <= currentTime) {
                    readyQueue.add(remainingProcesses.remove(0));
                }

                Process currentQueue = readyQueue.get(0);

                // To use in sortedProcesses (Burst Time is required to render Gantt Chart)
                int decreasedBurstTime = 0; 

                // Decreasing burst time and tracking the decreased burst time for gantt Chart rendering
                for (int j = 1 ; j <= timeQuantum ; j++) {
                    currentQueue.decreBurstTime();
                    decreasedBurstTime++;
                    currentTime ++;
                    if(currentQueue.getBurstTime() == 0) {
                        break;
                    }
                }

                // If some burst time still remains, check first if there is newly arrived processes to add to 
                // ready queue before adding the unfinished process to the back of the queue
                if (currentQueue.getBurstTime() != 0){ 
                    //Add the current Process to the sortedProcesses with its already executed burst time for 
                    // gantt chart rendering later
                    sortedProcesses.add(new Process(currentQueue));
                    Process sortedProcess = sortedProcesses.get(sortedProcesses.size()-1);
                    sortedProcess.setBurstTime(decreasedBurstTime);

                    // Add the arrived process at the current time before pushing the unfinised
                    // current process to the back
                    while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).getArrivalTime() <= currentTime) {
                        readyQueue.add(remainingProcesses.remove(0));
                    }
                    readyQueue.add(new Process(currentQueue));
                    
                    // Remove the current process after adding it to the back
                    readyQueue.remove(0);
                    
                } else {
                    // If the process has been completed, add the completion time
                    sortedProcesses.add(new Process(currentQueue));
                    Process sortedProcess = sortedProcesses.get(sortedProcesses.size()-1);
                    sortedProcess.setBurstTime(decreasedBurstTime);
                    sortedProcess.setCompletionTime(currentTime);
                    readyQueue.remove(0);
                }
            }
            
        }
        processes = sortedProcesses;
        executionTime = currentTime;
    }

    

    public void ganttChart(){
        // Using string to store the output before outputting at the end so the string lenght can be track
        String display = "";
        int display_length = 0;

        for(int i = 0 ; i < processes.size() ; i++){
            // Mapping the proportion of the process to render its portion in the entire chart, Test run to
            // understand further
            Process currentProcess = processes.get(i);
            currentProcess.setPropor( ((int)(50*((double) processes.get(i).getBurstTime()/executionTime))));
            
            for(int j = 0; j < currentProcess.getPropor() ;j++){
                // Output The process ID and burst time value (also cpu idle time if exists) in the middle
                if(j == currentProcess.getPropor()/2 ){
                    if (currentProcess.getProcessID().equals("idle")) {
                        display += String.format("(%d)", currentProcess.getBurstTime());
                    } else {
                        display += String.format("%s(%d)",currentProcess.getProcessID(),currentProcess.getBurstTime());
                    }
                } 
                // Using color, to count display_length correctly, we need to substract the Color string length
                // Ex: RED = "\u001B[31m"; which shouldn't be counted in the display_length
                display = display + Color.RED + "=" + Color.RESET;
                display_length = display_length - Color.RED.length() - Color.RESET.length();
            }
            display += "|";
        }
        //The below code are just styling the chart
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
        
        // the finish time is used to print under the "|" in the output, Check test run to understand
        int finishTime = processes.get(0).getArrivalTime() ;
        System.out.print(finishTime);
        for(int j = 0 ; j <processes.get(0).getPropor() + processes.get(0).getProcessID().length()+2+String.valueOf(processes.get(0).getBurstTime()).length()  ; j++) {
            System.out.print(" ");
        }
        for(int i = 0 ; i < processes.size() ; i++){
            finishTime += processes.get(i).getBurstTime();
            System.out.print(finishTime);
            // i + 1 because process 0 has already been rendered before loop
            if(i != processes.size() -1 && !processes.get(i+1).getProcessID().equals("idle")){
                for(int j = 0 ; j <processes.get(i+1).getPropor() + processes.get(i+1).getProcessID().length() + 2 
                + String.valueOf(processes.get(i+1).getBurstTime()).length()  ; j++) {
                    System.out.print(" ");
                }
                if(String.valueOf(finishTime).length() > 1)
                {
                    System.out.print("\b");
                }
            } else if (i != processes.size() -1 && processes.get(i+1).getProcessID().equals("idle")) {
                for(int j = 0 ; j <processes.get(i+1).getPropor() + 2
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

    // Method for displaying all required output (Waiting time,...)
    public void disTimes(){ 
        System.out.println("\nWaiting Time:                   Turnaround Time: \n");

        // This loop is used to merge the same processes that got split during execution
        // Since you need to merge it to calculate the waiting time and turnaround time correctly
        for(int i = 0 ; i< processes.size(); i++){
            for(int j = 0; j< processes.size(); j++) {
                if(processes.get(i).getProcessID().equals(processes.get(j).getProcessID())
                && i != j 
                && (!(processes.get(i)).getProcessID().equals("idle") || !processes.get(j).getProcessID().equals("idle"))) {
                    // Adding the burst time of the same processes together before removing one of them
                    int iBurstTime = processes.get(i).getBurstTime();
                    int jBurstTIme = processes.get(j).getBurstTime();
                    processes.get(i).setBurstTime(iBurstTime+jBurstTIme);

                    // Since only the last process of the same process has completion time, we can setCompletion time
                    // Correctly 
                    if(processes.get(j).getCompletionTime() == 0){
                        processes.remove(j);
                    } else {
                        processes.get(i).setCompletionTime(processes.get(j).getCompletionTime());
                        processes.remove(j);
                    }
                    // After removing, check again to see if there is still duplication to merge
                    i--;
                    break;
                }
            }
            
        }

        // Output the Waiting time and turnaround time of each process
        for(int i = 0 ; i < processes.size() ; i++){
            if(!processes.get(i).getProcessID().equals("idle")){
                processes.get(i).setTurnAroundTime();
                processes.get(i).setWaitingTime();
                System.out.printf("%-3s : %-25s %-3s : %s\n",processes.get(i).getProcessID() ,processes.get(i).getWaitingTime() + 
                "ms",processes.get(i).getProcessID(),processes.get(i).getTurnAroundTime() + "ms");
            }
        }

        // Output the average times
        System.out.print("\nAverage Waiting Time:    ");
        float avgWaitTime = 0;
        float avgTurnTime = 0;
        int numProcess = 0;
        for(int i = 0 ; i < processes.size() ; i++){
            if (!processes.get(i).getProcessID().equals("idle")){
                numProcess++;
                processes.get(i).setTurnAroundTime();
                processes.get(i).setWaitingTime();
                avgWaitTime += processes.get(i).getWaitingTime();
                avgTurnTime += processes.get(i).getTurnAroundTime();
            }
        }
        avgWaitTime = (float) avgWaitTime/numProcess;
        avgTurnTime = (float) avgTurnTime/numProcess;
        System.out.printf("%s\n\n",String.format("%.2fms", avgWaitTime));
        System.out.print("Average Turnaround Time: ");
        System.out.printf("%s\n\n\n\n\n\n\n\n",String.format("%.2fms", avgTurnTime));
    }

}