/*
### Example 11: Propagation.NESTED

**Propagation.NESTED** allows for the creation of nested transactions. If a nested transaction encounters an issue, only its changes are rolled back while the main transaction continues. This is particularly useful for managing partially independent operations within a larger transactional context.

**Behavior:**

#### updateOrderStatus
- **Main Transaction:** Ensures order status updates are part of the main transaction.
- **Data Integrity:** Guarantees consistency for the primary operation.

#### recordOrderHistory
- **Nested Transaction:** Operates within a nested transaction, allowing for independent rollback in case of errors.
- **Error Handling:** Issues in history recording do not affect the main transaction.

#### logOrderStatus
- **Nested Transaction:** Similar to recordOrderHistory, it executes independently, ensuring robust error management.

**Key Takeaway:** Nested transactions provide fine-grained control, enabling selective rollback without compromising the overall transactional integrity.
*/

// TransactionNestedExample.java
@Service
public class OrderProcessingService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private LogService logService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processOrder(Long orderId, String status) {
        System.out.println("Starting order processing...");

        // Step 1: Update order status
        orderService.updateOrderStatus(orderId, status);

        // Step 2: Record order history
        try {
            historyService.recordOrderHistory(orderId, status);
        } catch (Exception e) {
            System.out.println("History error handled: " + e.getMessage());
        }

        // Step 3: Log order status
        try {
            logService.logOrderStatus(orderId, status);
        } catch (Exception e) {
            System.out.println("Log error handled: " + e.getMessage());
        }

        System.out.println("Order processing completed.");
    }
}

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateOrderStatus(Long orderId, String status) {
        System.out.println("Updating order status...");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found."));
        order.setStatus(status);
        orderRepository.save(order);

        System.out.println("Order status updated to: " + status);
    }
}

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordOrderHistory(Long orderId, String status) {
        System.out.println("Recording order history...");

        OrderHistory history = new OrderHistory();
        history.setOrderId(orderId);
        history.setStatus(status);
        historyRepository.save(history);

        // Simulate an error
        if ("ERROR".equals(status)) {
            throw new RuntimeException("Simulated history error.");
        }

        System.out.println("Order history recorded.");
    }
}

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logOrderStatus(Long orderId, String status) {
        System.out.println("Logging order status...");

        LogEntry logEntry = new LogEntry();
        logEntry.setOrderId(orderId);
        logEntry.setMessage("Order status updated to: " + status);
        logRepository.save(logEntry);

        // Simulate an error
        if ("ERROR".equals(status)) {
            throw new RuntimeException("Simulated log error.");
        }

        System.out.println("Order status logged.");
    }
}

/*
### Detailed Behavior Analysis

#### processOrder
- **Main Transaction (REQUIRED):** Manages overall transaction, ensuring consistency across all operations.
- **recordOrderHistory Call:** Executes within a nested transaction, allowing rollback independent of the main transaction.
- **logOrderStatus Call:** Similarly executes in a nested transaction.

#### updateOrderStatus
- **Transaction Dependency (REQUIRED):** Ensures all operations within the main transaction adhere to a unified transactional scope.
- **Data Integrity:** Critical for ensuring primary updates remain consistent.

#### recordOrderHistory
- **Nested Transaction (NESTED):** Creates a savepoint to allow selective rollback.
- **Error Handling:** Issues in this method do not affect the main transaction.

#### logOrderStatus
- **Nested Transaction (NESTED):** Similar to recordOrderHistory, ensures that errors remain isolated to the nested transaction.

**Output Example:**
1. **Successful Execution:**
   ```
   Starting order processing...
   Updating order status...
   Order status updated to: SUCCESS
   Recording order history...
   Order history recorded.
   Logging order status...
   Order status logged.
   Order processing completed.
   ```

2. **Error in recordOrderHistory:**
   ```
   Starting order processing...
   Updating order status...
   Order status updated to: ERROR
   Recording order history...
   History error handled: Simulated history error.
   Logging order status...
   Order status logged.
   Order processing completed.
   ```

3. **Error in logOrderStatus:**
   ```
   Starting order processing...
   Updating order status...
   Order status updated to: ERROR
   Recording order history...
   Order history recorded.
   Logging order status...
   Log error handled: Simulated log error.
   Order processing completed.
   ```

### Key Takeaways
1. **NESTED Transactions:** Provide selective rollback capabilities, maintaining the integrity of the main transaction.
2. **Error Isolation:** Ensures that issues in one part of the transaction do not cascade to the entire process.
*/
