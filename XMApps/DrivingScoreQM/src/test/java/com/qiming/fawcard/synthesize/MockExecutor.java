package com.qiming.fawcard.synthesize;


import java.util.concurrent.Executor;


public class MockExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
