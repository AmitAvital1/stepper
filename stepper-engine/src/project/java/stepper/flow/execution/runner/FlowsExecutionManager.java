package project.java.stepper.flow.execution.runner;

import project.java.stepper.flow.execution.FlowExecution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static project.java.stepper.flow.execution.FlowExecutionResult.PROCESSING;

public class FlowsExecutionManager {
    ExecutorService threadExecutor;

    public FlowsExecutionManager(){
        threadExecutor = Executors.newFixedThreadPool(3);
    }
    public void setThreadExecutor(int threadsNum){
        threadExecutor = Executors.newFixedThreadPool(threadsNum);
    }
    public void exeFlow(FlowExecution flow){
        flow.setFlowExecutionResult(PROCESSING);
        FLowExecutor fLowExecutor = new FLowExecutor(flow);
        threadExecutor.execute(fLowExecutor);
    }
}
