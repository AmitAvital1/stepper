package project.java.stepper.flow.statistics;

import javafx.util.Pair;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;

public class FlowStats {
    private int executesRunTimes;
    private long averageExecutesTime;
    private List<stepStats> stepStatsList;

    public FlowStats(){
        executesRunTimes = 0;
        stepStatsList = new ArrayList<>();
    }

    private class stepStats{
        private final StepUsageDeclaration stepDeclaration;
        private int stepExecutesRunTimes;
        private long stepAverageExecutesTime;

        public stepStats(StepUsageDeclaration step, Duration time){
            stepDeclaration = step;
            stepExecutesRunTimes = 1;
            stepAverageExecutesTime = time.toMillis();

        }

        public int getStepExecutesRunTimes() {
            return stepExecutesRunTimes;
        }

        public long getStepAverageExecutesTime() {
            return stepAverageExecutesTime;
        }

        public void addStepExecutesRunTimes(Duration time) {
            this.stepExecutesRunTimes += 1;
            if(stepExecutesRunTimes == 1)
                stepAverageExecutesTime = (time.toMillis() / stepExecutesRunTimes);
            else
                stepAverageExecutesTime = ((stepAverageExecutesTime * (stepExecutesRunTimes - 1)) + time.toMillis()) / stepExecutesRunTimes;
        }
    }

    public void addFlowRunStats(Duration time){
        this.executesRunTimes += 1;
        if(executesRunTimes == 1)
            averageExecutesTime = time.toMillis();
        else
            averageExecutesTime = ((averageExecutesTime / (executesRunTimes - 1)) + time.toMillis()) / executesRunTimes;

    }
    public void addStepStats(StepUsageDeclaration step,Duration time) {
        boolean exsist = false;
        for(stepStats stepToStat : stepStatsList){
            if(stepToStat.stepDeclaration == step) {
                stepToStat.addStepExecutesRunTimes(time);
                exsist = true;
                break;
            }
        }
        if(!exsist)
            stepStatsList.add(new stepStats(step,time));
    }

    public int getStepTimesExecuted(StepUsageDeclaration step){
        int times = 0;
        for(stepStats stepToStat : stepStatsList){
            if(stepToStat.stepDeclaration == step) {
                times = stepToStat.getStepExecutesRunTimes();
            }
        }
        return times;
    }
    public long getStepAverageTimeExecuted(StepUsageDeclaration step){
        long avg = 0;
        for(stepStats stepToStat : stepStatsList){
            if(stepToStat.stepDeclaration == step) {
                avg = stepToStat.getStepAverageExecutesTime();
            }
        }
        return avg;
    }
    public int getExecutesRunTimes(){return executesRunTimes;}
    public long getAvgExecutesRunTimes(){return averageExecutesTime;}


}
