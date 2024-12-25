/*
### Example 10: Propagation.MANDATORY

**Propagation.MANDATORY** ensures that a method is only executed within an active transaction. If called without a transaction, Spring throws an IllegalTransactionStateException. This is useful for ensuring data integrity and consistent transactional behavior.

**Behavior:**

#### updateOrderStatus
- **Transaction Dependency:** Must be called within an existing transaction.
- **Data Integrity:** Relies on the caller's transaction to ensure changes are persisted.

#### createSalesReport
- **Transaction Dependency:** Requires an active transaction for execution.
- **Data Integrity:** Ensures all sales report operations occur within a cohesive transactional context.

#### processOrder
- **Main Transaction:** Initiates a transaction and calls both updateOrderStatus and createSalesReport as part of the same transaction.
*/

// TransactionMandatoryExample.java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateOrderStatus(Long orderId, String status) {
        System.out.println("Updating order status for ID: " + orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found."));
        order.setStatus(status);
        orderRepository.save(order);

        System.out.println("Order status updated to: " + status);
    }
}

@Service
public class SalesReportService {

    @Autowired
    private SalesReportRepository salesReportRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void createSalesReport(Long orderId, double amount) {
        System.out.println("Creating sales report for order ID: " + orderId);

        SalesReport report = new SalesReport();
        report.setOrderId(orderId);
        report.setAmount(amount);
        report.setTimestamp(System.currentTimeMillis());
        salesReportRepository.save(report);

        System.out.println("Sales report created successfully.");
    }
}

@Service
public class OrderProcessingService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SalesReportService salesReportService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processOrder(Long orderId, double amount) {
        System.out.println("Starting order processing...");

        // Step 1: Update order status
        orderService.updateOrderStatus(orderId, "COMPLETED");

        // Step 2: Create sales report
        salesReportService.createSalesReport(orderId, amount);

        System.out.println("Order processing completed.");
    }
}

/*
### Detailed Behavior Analysis

#### processOrder
- **Main Transaction (REQUIRED):** Starts a transaction and ensures all operations are executed within the same transaction.
- **updateOrderStatus Call:** Executes successfully as part of the main transaction.
- **createSalesReport Call:** Executes successfully as part of the main transaction.

#### updateOrderStatus
- **Transaction Dependency (MANDATORY):** Throws an IllegalTransactionStateException if no transaction exists.
- **Data Integrity:** Relies on the caller's transaction to manage consistency.

#### createSalesReport
- **Transaction Dependency (MANDATORY):** Similar to updateOrderStatus, it requires an active transaction.
- **Data Integrity:** Ensures all operations are committed together.

**Output Example:**
1. **With Active Transaction:**
   ```
   Starting order processing...
   Updating order status for ID: 1
   Order status updated to: COMPLETED
   Creating sales report for order ID: 1
   Sales report created successfully.
   Order processing completed.
   ```
2. **Without Active Transaction (Direct Call to MANDATORY Method):**
   ```
   Exception in thread "main" org.springframework.transaction.IllegalTransactionStateException: No existing transaction found for transaction marked with propagation 'mandatory'
   ```

### Key Takeaways
1. **MANDATORY:** Ensures methods are always executed within an active transaction, preventing unintended standalone execution.
2. **Data Integrity:** Guarantees cohesive transactional behavior for all dependent methods.
*/
