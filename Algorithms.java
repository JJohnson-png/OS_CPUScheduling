import javax.swing.*;
import java.util.*;

public class Algorithms {

    public static void main(String args[]){

        /* FOR TESTING, CALL DESIRED ALGORITHM BELOW WITH THE NUMBER OF PROCESSES YOU WOULD LIKE TO SIMULATE  */

        srtfAlgorithm("3");
        mlfqAlgorithm("3");

    }

    //SHORTEST REMAINING TIME FIRST
    public static void srtfAlgorithm(String userInput) {
        int np = Integer.parseInt(userInput);

        double[] arrivalTime = new double[np];
        double[] burstTime = new double[np];
        double[] remainingTime = new double[np];
        double[] completionTime = new double[np];
        double[] waitingTime = new double[np];
        boolean[] isComplete = new boolean[np];

        int completed = 0, currentTime = 0;
        double totalWaitingTime = 0.0, averageWaitingTime = 0.0;

        int result = JOptionPane.showConfirmDialog(null, "Shortest Remaining Time First Scheduling", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            for (int i = 0; i < np; i++) {
                arrivalTime[i] = Double.parseDouble(JOptionPane.showInputDialog(null, "Enter arrival time: ", "Arrival time for P" + (i + 1), JOptionPane.QUESTION_MESSAGE));
                burstTime[i] = Double.parseDouble(JOptionPane.showInputDialog(null, "Enter burst time: ", "Burst time for P" + (i + 1), JOptionPane.QUESTION_MESSAGE));
                remainingTime[i] = burstTime[i];
            }

            while (completed != np) {
                int idx = -1;
                double minTime = Double.MAX_VALUE;

                for (int i = 0; i < np; i++) {
                    if (arrivalTime[i] <= currentTime && !isComplete[i] && remainingTime[i] < minTime && remainingTime[i] > 0) {
                        minTime = remainingTime[i];
                        idx = i;
                    }
                }

                if (idx != -1) {
                    remainingTime[idx]--;
                    currentTime++;

                    if (remainingTime[idx] == 0) {
                        isComplete[idx] = true;
                        completed++;
                        completionTime[idx] = currentTime;
                        waitingTime[idx] = completionTime[idx] - arrivalTime[idx] - burstTime[idx];

                        if (waitingTime[idx] < 0)
                            waitingTime[idx] = 0;

                        JOptionPane.showMessageDialog(null, "Waiting time for P" + (idx + 1) + " = " + waitingTime[idx], "", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    currentTime++;
                }
            }

            for (int i = 0; i < np; i++) {
                totalWaitingTime += waitingTime[i];
            }

            averageWaitingTime = totalWaitingTime / np;

            double totalBurstTime = 0.0;
            double totalTurnaroundTime = 0.0;
            double totalTime = 0.0;

            for (int i = 0; i < np; i++) {
                totalBurstTime += burstTime[i];
                double turnaroundTime = completionTime[i] - arrivalTime[i];
                totalTurnaroundTime += turnaroundTime;
            }

            totalTime = Arrays.stream(completionTime).max().getAsDouble();

            double avgTurnaroundTime = totalTurnaroundTime / np;
            double cpuUtilization = (totalBurstTime / totalTime) * 100.0;
            double throughput = np / totalTime;

            String summary = "SRTF Summary:\n\n"
                    + "Average Waiting Time (AWT): " + String.format("%.2f", averageWaitingTime) + " sec\n"
                    + "Average Turnaround Time (ATT): " + String.format("%.2f", avgTurnaroundTime) + " sec\n"
                    + "CPU Utilization: " + String.format("%.2f", cpuUtilization) + " %\n"
                    + "Throughput: " + String.format("%.2f", throughput) + " process/sec";

            JOptionPane.showMessageDialog(null, summary, "SRTF - Final Metrics", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    //MULTI-LEVEL FEEDBACK QUEUE

    public static void mlfqAlgorithm(String userInput) {
        int np = Integer.parseInt(userInput);
        int maxProcesses = 10;

        double[] arrivalTime = new double[maxProcesses];
        double[] burstTime = new double[maxProcesses];
        double[] remainingTime = new double[maxProcesses];

        int[] queueLevel = new int[maxProcesses];
        double[] completionTime = new double[maxProcesses];
        double[] waitingTime = new double[maxProcesses];

        int result = JOptionPane.showConfirmDialog(null, "Multi-Level Feedback Queue Scheduling", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            for (int i = 0; i < np; i++) {
                arrivalTime[i] = Double.parseDouble(JOptionPane.showInputDialog(null, "Enter arrival time: ", "Arrival time for P" + (i + 1), JOptionPane.QUESTION_MESSAGE));
                burstTime[i] = Double.parseDouble(JOptionPane.showInputDialog(null, "Enter burst time: ", "Burst time for P" + (i + 1), JOptionPane.QUESTION_MESSAGE));
                remainingTime[i] = burstTime[i];
                queueLevel[i] = 0; // Start in highest priority queue
            }

            int time = 0, completed = 0;
            double totalWaitTime = 0.0;

            int[] timeQuantum = {4, 8, 12}; // Each lower queue gets a larger quantum

            while (completed < np) {
                boolean didWork = false;

                for (int q = 0; q < 3; q++) {
                    for (int i = 0; i < np; i++) {
                        if (queueLevel[i] == q && arrivalTime[i] <= time && remainingTime[i] > 0) {
                            int tq = timeQuantum[q];
                            int actualRunTime = (int) Math.min(tq, remainingTime[i]);

                            time += actualRunTime;
                            remainingTime[i] -= actualRunTime;

                            if (remainingTime[i] == 0) {
                                completionTime[i] = time;
                                waitingTime[i] = completionTime[i] - arrivalTime[i] - burstTime[i];
                                if (waitingTime[i] < 0) waitingTime[i] = 0;
                                completed++;
                                JOptionPane.showMessageDialog(null, "Waiting time for P" + (i + 1) + " = " + waitingTime[i], "", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                queueLevel[i] = Math.min(2, queueLevel[i] + 1); // Move to lower priority queue
                            }

                            didWork = true;
                            break;
                        }
                    }

                    if (didWork) break;
                }

                if (!didWork) time++; // Idle CPU
            }

            for (int i = 0; i < np; i++) {
                totalWaitTime += waitingTime[i];
            }

            double totalBurstTime = 0.0;
            double totalTurnaroundTime = 0.0;
            double totalTime = Arrays.stream(completionTime).max().getAsDouble();

            for (int i = 0; i < np; i++) {
                totalBurstTime += burstTime[i];
                double turnaroundTime = completionTime[i] - arrivalTime[i];
                totalTurnaroundTime += turnaroundTime;
            }

            double avgWait = totalWaitTime / np;
            double avgTurnaroundTime = totalTurnaroundTime / np;
            double cpuUtilization = (totalBurstTime / totalTime) * 100.0;
            double throughput = np / totalTime;

            String summary = "MLFQ Summary:\n\n"
                    + "Average Waiting Time (AWT): " + String.format("%.2f", avgWait) + " sec\n"
                    + "Average Turnaround Time (ATT): " + String.format("%.2f", avgTurnaroundTime) + " sec\n"
                    + "CPU Utilization: " + String.format("%.2f", cpuUtilization) + " %\n"
                    + "Throughput: " + String.format("%.2f", throughput) + " process/sec";

            JOptionPane.showMessageDialog(null, summary, "MLFQ - Final Metrics", JOptionPane.INFORMATION_MESSAGE);


        }
    }



    //GIVEN ALGORITHMS BELOW
    //_____________________________________________________________________________________________


    public static void fcfsAlgorithm(String userInput) {
        int np = Integer.parseInt(userInput);
        int npX2 = np * 2;

        double[] bp = new double[np];
        double[] wtp = new double[np];
        String[] output1 = new String[npX2];
        double twt = 0.0, awt;
        int num;

        int result = JOptionPane.showConfirmDialog(null, "First Come First Serve Scheduling", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            for (num = 0; num < np; num++) {
                String input = JOptionPane.showInputDialog(null, "Enter Burst time: ", "Burst time for P" + (num + 1), JOptionPane.QUESTION_MESSAGE);
                bp[num] = Double.parseDouble(input);
            }

            for (num = 0; num < np; num++) {
                if (num == 0) {
                    wtp[num] = 0;
                } else {
                    wtp[num] = wtp[num - 1] + bp[num - 1];
                    JOptionPane.showMessageDialog(null, "Waiting time for P" + (num + 1) + " = " + wtp[num], "Job Queue", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            for (num = 0; num < np; num++) {
                twt += wtp[num];
            }

            awt = twt / np;
            JOptionPane.showMessageDialog(null, "Average waiting time for " + np + " processes = " + awt + " sec(s)", "Average Awaiting Time", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void sjfAlgorithm(String userInput) {
        int np = Integer.parseInt(userInput);

        double[] bp = new double[np];
        double[] wtp = new double[np];
        double[] p = new double[np];
        double twt = 0.0, awt;
        int x, num;
        double temp = 0.0;
        boolean found;

        int result = JOptionPane.showConfirmDialog(null, "Shortest Job First Scheduling", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            for (num = 0; num < np; num++) {
                String input = JOptionPane.showInputDialog(null, "Enter burst time: ", "Burst time for P" + (num + 1), JOptionPane.QUESTION_MESSAGE);
                bp[num] = Double.parseDouble(input);
            }

            for (num = 0; num < np; num++) {
                p[num] = bp[num];
            }

            // Sort p array
            Arrays.sort(p);

            found = false;
            for (num = 0; num < np; num++) {
                if (num == 0) {
                    for (x = 0; x < np; x++) {
                        if (p[num] == bp[x] && !found) {
                            wtp[num] = 0;
                            JOptionPane.showMessageDialog(null, "Waiting time for P" + (x + 1) + " = " + wtp[num], "Waiting time", JOptionPane.INFORMATION_MESSAGE);
                            bp[x] = 0;
                            found = true;
                        }
                    }
                    found = false;
                } else {
                    for (x = 0; x < np; x++) {
                        if (p[num] == bp[x] && !found) {
                            wtp[num] = wtp[num - 1] + p[num - 1];
                            JOptionPane.showMessageDialog(null, "Waiting time for P" + (x + 1) + " = " + wtp[num], "Waiting time", JOptionPane.INFORMATION_MESSAGE);
                            bp[x] = 0;
                            found = true;
                        }
                    }
                    found = false;
                }
            }

            for (num = 0; num < np; num++) {
                twt += wtp[num];
            }

            awt = twt / np;
            JOptionPane.showMessageDialog(null, "Average waiting time for " + np + " processes = " + awt + " sec(s)", "Average waiting time", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void priorityAlgorithm(String userInput) {
        int np = Integer.parseInt(userInput);

        int result = JOptionPane.showConfirmDialog(null, "Priority Scheduling", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            double[] bp = new double[np];
            double[] wtp = new double[np + 1];
            int[] p = new int[np];
            int[] sp = new int[np];
            int x, num;
            double twt = 0.0;
            double awt;
            int temp = 0;
            boolean found = false;

            for (num = 0; num < np; num++) {
                String input = JOptionPane.showInputDialog(null, "Enter burst time: ", "Burst time for P" + (num + 1), JOptionPane.QUESTION_MESSAGE);
                bp[num] = Double.parseDouble(input);
            }

            for (num = 0; num < np; num++) {
                String input2 = JOptionPane.showInputDialog(null, "Enter priority: ", "Priority for P" + (num + 1), JOptionPane.QUESTION_MESSAGE);
                p[num] = Integer.parseInt(input2);
            }

            for (num = 0; num < np; num++) {
                sp[num] = p[num];
            }

            // Sort the priorities
            Arrays.sort(sp);

            found = false;
            for (num = 0; num < np; num++) {
                if (num == 0) {
                    for (x = 0; x < np; x++) {
                        if (sp[num] == p[x] && !found) {
                            wtp[num] = 0;
                            JOptionPane.showMessageDialog(null, "Waiting time for P" + (x + 1) + " = " + wtp[num], "Waiting time", JOptionPane.INFORMATION_MESSAGE);
                            temp = x;
                            p[x] = 0;
                            found = true;
                        }
                    }
                    found = false;
                } else {
                    for (x = 0; x < np; x++) {
                        if (sp[num] == p[x] && !found) {
                            wtp[num] = wtp[num - 1] + bp[temp];
                            JOptionPane.showMessageDialog(null, "Waiting time for P" + (x + 1) + " = " + wtp[num], "Waiting time", JOptionPane.INFORMATION_MESSAGE);
                            temp = x;
                            p[x] = 0;
                            found = true;
                        }
                    }
                    found = false;
                }
            }

            for (num = 0; num < np; num++) {
                twt += wtp[num];
            }

            awt = twt / np;
            JOptionPane.showMessageDialog(null, "Average waiting time for " + np + " processes = " + awt + " sec(s)", "Average waiting time", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void roundRobinAlgorithm(String userInput) {
        int np = Integer.parseInt(userInput);
        int i, counter = 0;
        double total = 0.0;
        double timeQuantum;
        double waitTime = 0, turnaroundTime = 0;
        double averageWaitTime, averageTurnaroundTime;
        double[] arrivalTime = new double[10];
        double[] burstTime = new double[10];
        double[] temp = new double[10];
        int x = np;

        int result = JOptionPane.showConfirmDialog(null, "Round Robin Scheduling", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            for (i = 0; i < np; i++) {
                String arrivalInput = JOptionPane.showInputDialog(null, "Enter arrival time: ", "Arrival time for P" + (i + 1), JOptionPane.QUESTION_MESSAGE);
                arrivalTime[i] = Double.parseDouble(arrivalInput);

                String burstInput = JOptionPane.showInputDialog(null, "Enter burst time: ", "Burst time for P" + (i + 1), JOptionPane.QUESTION_MESSAGE);
                burstTime[i] = Double.parseDouble(burstInput);

                temp[i] = burstTime[i];
            }

            String timeQuantumInput = JOptionPane.showInputDialog(null, "Enter time quantum: ", "Time Quantum", JOptionPane.QUESTION_MESSAGE);
            timeQuantum = Double.parseDouble(timeQuantumInput);

            for (total = 0, i = 0; x != 0;) {
                if (temp[i] <= timeQuantum && temp[i] > 0) {
                    total += temp[i];
                    temp[i] = 0;
                    counter = 1;
                } else if (temp[i] > 0) {
                    temp[i] -= timeQuantum;
                    total += timeQuantum;
                }
                if (temp[i] == 0 && counter == 1) {
                    x--;
                    JOptionPane.showMessageDialog(null, "Turnaround time for Process " + (i + 1) + " : " + (total - arrivalTime[i]), "Turnaround time for Process " + (i + 1), JOptionPane.INFORMATION_MESSAGE);
                    JOptionPane.showMessageDialog(null, "Wait time for Process " + (i + 1) + " : " + (total - arrivalTime[i] - burstTime[i]), "Wait time for Process " + (i + 1), JOptionPane.INFORMATION_MESSAGE);
                    turnaroundTime += (total - arrivalTime[i]);
                    waitTime += (total - arrivalTime[i] - burstTime[i]);
                    counter = 0;
                }
                if (i == np - 1) {
                    i = 0;
                } else if (arrivalTime[i + 1] <= total) {
                    i++;
                } else {
                    i = 0;
                }
            }

            averageWaitTime = waitTime / np;
            averageTurnaroundTime = turnaroundTime / np;

            JOptionPane.showMessageDialog(null, "Average wait time for " + np + " processes: " + averageWaitTime + " sec(s)", "", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(null, "Average turnaround time for " + np + " processes: " + averageTurnaroundTime + " sec(s)", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
