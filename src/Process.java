package src;

public class Process {
    private final String processID;
    private int arrivalTime;
    private int burstTime;
    private int timeQuantum;
    private int propor;
    private int completionTime;
    private int waitingTime;
    private int turnAroundTime;

    //Use to initialize CPUIdle time for SRT
    public Process(String processID, int burstTime){ 
        this.burstTime = burstTime;
        this.processID = processID;
    }

    //Copy Constructor
    public Process(Process p){
        this.processID = p.processID;
        this.burstTime = p.burstTime;
        this.arrivalTime = p.arrivalTime;
        this.completionTime = p.completionTime;
        this.propor = p.propor;
        this.turnAroundTime = p.turnAroundTime;
        this.waitingTime = p.waitingTime;
    }

    // use for FCFS SJF SRT
    public Process(String processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        timeQuantum = 0;
        propor = 0;
    }

    // Use for RR
    public Process(String processID, int arrivalTime, int burstTime, int timeQuantum) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.timeQuantum = timeQuantum;
        propor = 0;
    }
    

    public String getProcessID() {
        return processID;
    }



    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }
    public void setBurstTime(int burstTime){
            this.burstTime = burstTime;
        }

    public void increBurstTime(){
        burstTime++;
    }

    public void decreBurstTime(){
        if (burstTime>0){
            burstTime--;
        }
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }



    public int getPropor() {
        return propor;
    }



    public void setPropor(int propor) {
        this.propor = propor;
    }



    public int getCompletionTime() {
        return completionTime;
    }



    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }



    public int getWaitingTime() {
        return waitingTime;
    }



    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }



    public int getTurnAroundTime() {
        return turnAroundTime;
    }



    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public void setTurnAroundTime(){
        turnAroundTime = completionTime - arrivalTime;
    }

    public void setWaitingTime(){
        waitingTime = turnAroundTime - burstTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Process other = (Process) obj;
        if (processID == null) {
            if (other.processID != null)
                return false;
        } else if (!processID.equals(other.processID))
            return false;
        return true;
    }
    
}
