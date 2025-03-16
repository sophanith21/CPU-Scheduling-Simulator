package src;
import java.util.Scanner;


public class Main {
    
    public static void main(String[] args) {
        int choice=0;
        Scanner scan = new Scanner(System.in);
        while(choice!=5){
            System.out.println("Welcome to CPU Scheduling Simulator\n");
            System.out.println("1. First Come First Served (FCFS)");
            System.out.println("2. Shortest Job First (SJF)");
            System.out.println("3. Shortest Remaining Time (SRT)");
            System.out.println("4. Round Robin (RR)");
            System.out.println("5. Exit (RR)");
            System.out.print("\nEnter the option: ");
            choice = scan.nextInt();
            
            switch (choice){
                case 1:{
                    System.out.print("Number of Process: ");
                    int numProcess = ValidateInput.validatePositiveInt(scan);
                    
                    FCFS fcfs = new FCFS(numProcess);
                    fcfs.iniProcess(scan);
                    fcfs.ganttChart();
                    fcfs.disTimes();
                    break;
                }
                case 2:{
                    System.out.print("Number of Process: ");
                    int numProcess = ValidateInput.validatePositiveInt(scan);

                    SJF sjf = new SJF(numProcess);
                    sjf.iniProcess(scan);
                    sjf.ganttChart();
                    sjf.disTimes();

                    break;
                }
                case 3:{
                    System.out.print("Number of Process: ");
                    int numProcess = ValidateInput.validatePositiveInt(scan);

                    SRT srt = new SRT(numProcess);
                    srt.iniProcess(scan);
                    srt.ganttChart();
                    srt.disTimes();

                    break;
                }
                case 4:{
                    System.out.print("Number of Process: ");
                    int numProcess = ValidateInput.validatePositiveInt(scan);

                    RR rr = new RR(numProcess);
                    rr.iniProcess(scan);
                    rr.ganttChart();
                    rr.disTimes();

                    break;
                }
                case 5:{
                    
                    break;
                }
                default:{
                    System.out.println("\n>> Please choose number from 1 - 5 <<\n\n\n\n\n");
                }
            }
        }
        scan.close();
    }
    
}   
