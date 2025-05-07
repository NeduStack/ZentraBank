package fizy.web.app.test;

import fizy.web.app.test.strategy.impl.TransferFundsTestStrategy;
import org.junit.jupiter.api.BeforeEach;

public class TransferFundsOperationTest extends AccountOperationTestContext {

    @BeforeEach
    public void setUp() {
        TransferFundsTestStrategy testStrategy = new TransferFundsTestStrategy();
        setStrategy(testStrategy);
    }
}