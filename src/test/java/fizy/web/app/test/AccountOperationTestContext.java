package fizy.web.app.test;

import fizy.web.app.test.strategy.AccountOperationTestStrategy;
import org.junit.jupiter.api.Test;

/**
 * Test context that uses different testing strategies for account operations
 * This class should be extended by concrete test classes that provide a strategy
 */
public abstract class AccountOperationTestContext {
    protected AccountOperationTestStrategy strategy;

    public void setStrategy(AccountOperationTestStrategy strategy) {
        this.strategy = strategy;
        strategy.setUp();
    }

    @Test
    public void testSuccessfulOperation() throws Exception {
        strategy.testSuccessfulExecution();
    }

    @Test
    public void testOperationErrorHandling() {
        strategy.testErrorHandling();
    }
}