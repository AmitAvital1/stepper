package project.java.stepper.flow.statistics;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.util.Pair;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;

public class FlowStats {
    private int executesRunTimes;
    private SimpleIntegerProperty executesRunTimesProp;

    private long averageExecutesTime;
    private SimpleLongProperty averageExecutesTimeProp;

    private List<stepStats> stepStatsList;

    public FlowStats(){
        executesRunTimes = 0;
        stepStatsList = new ArrayList<>();
        executesRunTimesProp = new SimpleIntegerProperty(0);
        averageExecutesTimeProp = new SimpleLongProperty(0);
    }

    public class stepStats{
        private final StepUsageDeclaration stepDeclaration;

        private int stepExecutesRunTimes;
        private SimpleIntegerProperty stepExecutesRunTimesProp;

        private long stepAverageExecutesTime;

        private SimpleLongProperty stepAverageExecutesTimeProp;

        public stepStats(StepUsageDeclaration step, Duration time){
            stepDeclaration = step;
            stepExecutesRunTimes = 1;
            stepAverageExecutesTime = time.toMillis();

            stepExecutesRunTimesProp = new SimpleIntegerProperty(1);
            stepAverageExecutesTimeProp = new SimpleLongProperty(time.toMillis());
        }

        public StepUsageDeclaration getStepDeclaration() {
            return stepDeclaration;
        }

        public IntegerProperty stepExecutesRunTimesPropProperty() {return stepExecutesRunTimesProp;}

        public long getStepAverageExecutesTimeProp() {return stepAverageExecutesTimeProp.get();}

        public LongProperty stepAverageExecutesTimePropProperty() {
            return stepAverageExecutesTimeProp;
        }

        public int getStepExecutesRunTimes() {
            return stepExecutesRunTimes;
        }

        public long getStepAverageExecutesTime() {
            return stepAverageExecutesTime;
        }

        public void addStepExecutesRunTimes(Duration time) {
            this.stepExecutesRunTimes += 1;
            this.stepExecutesRunTimesProp.set(stepExecutesRunTimes);
            if(stepExecutesRunTimes == 1)
                stepAverageExecutesTime = (time.toMillis() / stepExecutesRunTimes);
            else
                stepAverageExecutesTime = ((stepAverageExecutesTime * (stepExecutesRunTimes - 1)) + time.toMillis()) / stepExecutesRunTimes;

            stepAverageExecutesTimeProp.set(stepAverageExecutesTime);
        }
    }

    public void addFlowRunStats(Duration time){
        this.executesRunTimes += 1;
        if(executesRunTimes == 1)
            averageExecutesTime = time.toMillis();
        else
            averageExecutesTime = ((averageExecutesTime / (executesRunTimes - 1)) + time.toMillis()) / executesRunTimes;

        this.executesRunTimesProp.set(executesRunTimes);
        this.averageExecutesTimeProp.set(averageExecutesTime);

    }
    public void addStepStats(StepUsageDeclaration step,Duration time) {
        boolean exist = false;
        for(stepStats stepToStat : stepStatsList){
            if(stepToStat.stepDeclaration == step) {
                stepToStat.addStepExecutesRunTimes(time);
                exist = true;
                break;
            }
        }
        if(!exist)
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

    public IntegerProperty executesRunTimesPropProperty() {
        return executesRunTimesProp;
    }

    public LongProperty averageExecutesTimePropProperty() {
        return averageExecutesTimeProp;
    }

    public List<stepStats> getStepStatsList() {
        return stepStatsList;
    }
}
