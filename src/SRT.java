package src;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class SRT {
    private int numberOfProcess;
    private int executionTime;
    public ArrayList <Process> processes = new ArrayList<>();

    public SRT(int numberOfProcess) {
        this.numberOfProcess = numberOfProcess;
    }

    // Allow users to input the processes and execute the SRT algorithm
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
        executeSRT();
    }

    // Helper method use to execute SRT
    private void executeSRT() {
        ArrayList<Process> remainingProcesses = new ArrayList<>(processes);
        ArrayList<Process> readyQueue = new ArrayList<>();
        ArrayList <Process> sortedProcesses = new ArrayList<>();
        int currentTime = remainingProcesses.get(0).getArrivalTime();
        Process runningProcess = null;
        while (!remainingProcesses.isEmpty() || !readyQueue.isEmpty() || runningProcess != null) {
            // Add processes that have arrived to the ready queue
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).getArrivalTime() <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
            }

            // Select the process with the shortest remaining time
            if (!readyQueue.isEmpty()) {
                readyQueue.sort(Comparator.comparingInt(Process::getBurstTime));
                runningProcess = readyQueue.get(0);

                // Adding the running process to sortedProcesses for rendering gantt chart
                if(!sortedProcesses.isEmpty() &&sortedProcesses.get(sortedProcesses.size()-1).equals(runningProcess)){
                    sortedProcesses.get(sortedProcesses.size()-1).increBurstTime();
                } else {
                    sortedProcesses.add(new Process(runningProcess));
                    sortedProcesses.get(sortedProcesses.size()-1).setBurstTime(1);
                }
            }

            if (runningProcess != null) {
                // Execute the selected process for one unit of time
                runningProcess.decreBurstTime();
                currentTime++;

                // If the process finishes, remove it
                if (runningProcess.getBurstTime() == 0) {
                    runningProcess.setCompletionTime(currentTime);
                    if(sortedProcesses.get(sortedProcesses.size()-1).equals(runningProcess)){
                        sortedProcesses.get(sortedProcesses.size()-1).setCompletionTime(currentTime);
                    }
                    readyQueue.remove(runningProcess);
                    runningProcess = null;
                }
            } else {
                // If no process is ready, move time forward
                currentTime++;
                if(!sortedProcesses.isEmpty() &&sortedProcesses.get(sortedProcesses.size()-1).getProcessID().equals("idle")){
                    sortedProcesses.get(sortedProcesses.size()-1).increBurstTime();
                } else {
                    sortedProcesses.add(new Process("idle",1));
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
            processes.get(i).setPropor( ((int)(50*((double) processes.get(i).getBurstTime()/executionTime))));
            
            for(int j = 0; j < processes.get(i).getPropor() ;j++){
                // Output The process ID and burst time value (also cpu idle time if exists) in the middle
                if(j == processes.get(i).getPropor()/2 ){
                    if (processes.get(i).getProcessID().equals("idle")) {
                        display += String.format("(%d)", processes.get(i).getBurstTime());
                    } else {
                        display += String.format("%s(%d)",processes.get(i).getProcessID(),processes.get(i).getBurstTime());
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


    public void disTimes(){ 
        System.out.println("\nWaiting Time:                   Turnaround Time: \n");
        // Merge any duplication
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
                    break;
                }
            }
            
        }
        for(int i = 0 ; i < processes.size() ; i++){
            if(!processes.get(i).getProcessID().equals("idle")){
                processes.get(i).setTurnAroundTime();
                processes.get(i).setWaitingTime();
                System.out.printf("%-3s : %-25s %-3s : %s\n",processes.get(i).getProcessID() ,processes.get(i).getWaitingTime() + 
                "ms",processes.get(i).getProcessID(),processes.get(i).getTurnAroundTime() + "ms");
            }
        }

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