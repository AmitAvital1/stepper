package project.java.stepper.flow.execution.runner;

import project.java.stepper.flow.execution.FlowExecution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static project.java.stepper.flow.execution.FlowExecutionResult.PROCESSING;

public class FlowsExecutionManager {
    private ExecutorService threadExecutor;
    private static int threadsNum = 1;
    public FlowsExecutionManager(){
        threadExecutor = Executors.newFixedThreadPool(threadsNum);
    }
    public static void setThreadExecutor(int threadN){
        threadsNum = threadN;
    }
    public void exeFlow(FlowExecution flow){
        flow.setFlowExecutionResult(PROCESSING);
        FLowExecutor fLowExecutor = new FLowExecutor(flow);
        threadExecutor.execute(fLowExecutor);
    }
    public void shutDown(){
        threadExecutor.shutdown();
    }
}
