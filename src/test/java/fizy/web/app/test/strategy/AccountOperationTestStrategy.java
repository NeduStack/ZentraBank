package fizy.web.app.test.strategy;

import fizy.web.app.entity.User;
import org.junit.jupiter.api.Test;

/**
 * Defines a strategy for testing different account operations
 */
public interface AccountOperationTestStrategy {
    /**
     * Sets up the test environment
     */
    void setUp();

    /**
     * Tests successful execution of the operation
     */
    void testSuccessfulExecution();

    /**
     * Tests error handling in the operation
     */
    void testErrorHandling();
}