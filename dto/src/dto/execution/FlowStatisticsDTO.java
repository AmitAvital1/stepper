package dto.execution;

import dto.StepUsageDeclarationImplDTO;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.statistics.FlowStats;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FlowStatisticsDTO {

    private String flowName;
    private List<StepUsageDeclarationImplDTO> flowSteps;

    private Integer executesRunTimes;

    private Long averageExecutesTime;

    private List<stepStatsDTO> stepStatsList;

    public FlowStatisticsDTO(String name, List<StepUsageDeclaration> steps, FlowStats flowStats){
        flowName = name;
        flowSteps = converToStepDTO(steps);
        executesRunTimes = flowStats.getExecutesRunTimes();
        stepStatsList = createStepStatsDTO(flowStats.getStepStatsList());
        averageExecutesTime = flowStats.getAvgExecutesRunTimes();
    }

    private List<StepUsageDeclarationImplDTO> converToStepDTO(List<StepUsageDeclaration> steps) {
        List<StepUsageDeclarationImplDTO> res = new ArrayList<>();
        for(StepUsageDeclaration step : steps)
            res.add(new StepUsageDeclarationImplDTO(step));
        return res;
    }

    private List<stepStatsDTO> createStepStatsDTO(List<FlowStats.stepStats> stepStatsList) {
        List<stepStatsDTO> res = new ArrayList<>();
        for(FlowStats.stepStats stat : stepStatsList){
            res.add(new stepStatsDTO(stat.getStepDeclaration(),stat.getStepExecutesRunTimes(),stat.getStepAverageExecutesTime()));
        }
        return res;
    }

    public class stepStatsDTO{
        private final StepUsageDeclarationImplDTO stepDeclaration;

        private Integer stepExecutesRunTimes;

        private Long stepAverageExecutesTime;

        public stepStatsDTO(StepUsageDeclaration step, Integer stepExecutesRunTimes, Long stepAverageExecutesTime){
            stepDeclaration = new StepUsageDeclarationImplDTO(step);
            this.stepExecutesRunTimes = stepExecutesRunTimes;
            this.stepAverageExecutesTime = stepAverageExecutesTime;
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
    public String getFlowName() {
        return flowName;
    }
    public int getExecutesRunTimes(){return executesRunTimes;}
    public Long getAvgExecutesRunTimes(){return averageExecutesTime;}

    public List<StepUsageDeclarationImplDTO> getFlowSteps() {
        return flowSteps;
    }
    public int getStepTimesExecuted(StepUsageDeclarationImplDTO step){
        int times = 0;
        for(stepStatsDTO stepToStat : stepStatsList){
            if(stepToStat.stepDeclaration.equals(step)) {
                times = stepToStat.getStepExecutesRunTimes();
            }
        }
        return times;
    }
    public long getStepAverageTimeExecuted(StepUsageDeclarationImplDTO step){
        long avg = 0;
        for(stepStatsDTO stepToStat : stepStatsList){
            if(stepToStat.stepDeclaration.equals(step)) {
                avg = stepToStat.getStepAverageExecutesTime();
            }
        }
        return avg;
    }
}
