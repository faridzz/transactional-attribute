/*
### Example 6: Propagation and Error Handling in Different Scenarios

This example demonstrates two scenarios for handling errors in transactions:
1. Using a transactional method where errors are handled (inside try-catch).
2. A non-transactional method where errors propagate.

**Behavior:**

#### processOrderWithTransaction
- **Order Update:** Successfully updates the order status within a main transaction.
- **Notification Sending:** Executes within the transaction, and its error is handled (does not affect the main transaction).
- **Report Saving:** Executes and handles any errors (does not affect the main transaction).

#### processOrderWithoutTransaction
- **Order Update:** Executes independently with auto-commit.
- **Notification Sending:** Executes without a transaction; errors propagate and stop further execution.
- **Report Saving:** Executes only if prior errors do not stop the flow.
*/

// TransactionSupportExample.java
@Service
public class OrderProcessingService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReportRepository reportRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processOrderWithTransaction(Long orderId) {
        System.out.println("Starting processOrderWithTransaction...");

        // Step 1: Update order status
        orderRepository.updateStatus(orderId, "PROCESSING");
        System.out.println("Order status updated to PROCESSING.");

        // Step 2: Send notification
        try {
            notificationService.sendNotification(orderId, "Your order is now processing.");
        } catch (Exception e) {
            System.out.println("Notification error handled: " + e.getMessage());
        }

        // Step 3: Save report
        try {
            Report report = new Report();
            report.setOrderId(orderId);
            report.setStatus("PROCESSING");
            report.setMessage("Order status updated and notification sent.");
            reportRepository.save(report);
            System.out.println("Report saved successfully.");
        } catch (Exception e) {
            System.out.println("Report saving error: " + e.getMessage());
        }

        System.out.println("Order processed successfully with transaction.");
    }

    public void processOrderWithoutTransaction(Long orderId) {
        System.out.println("Starting processOrderWithoutTransaction...");

        // Step 1: Update order status
        orderRepository.updateStatus(orderId, "PROCESSING");
        System.out.println("Order status updated to PROCESSING.");

        // Step 2: Send notification
        notificationService.sendNotification(orderId, "Your order is now processing.");

        // Step 3: Save report
        try {
            Report report = new Report();
            report.setOrderId(orderId);
            report.setStatus("PROCESSING");
            report.setMessage("Order status updated and notification sent.");
            reportRepository.save(report);
            System.out.println("Report saved successfully.");
        } catch (Exception e) {
            System.out.println("Report saving error: " + e.getMessage());
        }

        System.out.println("Order processed successfully without transaction.");
    }
}

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional(propagation = Propagation.SUPPORTS)
    public void sendNotification(Long orderId, String message) {
        System.out.println("Sending notification for order ID: " + orderId);

        // Save notification
        Notification notification = new Notification();
        notification.setOrderId(orderId);
        notification.setMessage(message);
        notificationRepository.save(notification);
        System.out.println("Notification saved.");

        // Simulate an error
        throw new RuntimeException("Simulated notification error.");
    }
}

/*
### Detailed Behavior Analysis

#### processOrderWithTransaction
- **Transaction:** Runs with a main transaction.
- **Order Update:** Successfully updates the order status.
- **Notification Sending:** Executes within the transaction; error is handled and does not affect the main transaction.
- **Report Saving:** Executes; any errors are handled without affecting the main transaction.

**Database State:**
- Order: Updated to "PROCESSING"
- Notification: Saved despite error due to error handling.
- Report: Saved unless an error occurs during its operation.

#### processOrderWithoutTransaction
- **Transaction:** Runs independently with auto-commit.
- **Order Update:** Executes and commits immediately.
- **Notification Sending:** Errors propagate and stop further execution.
- **Report Saving:** Executes only if prior errors do not halt execution.

**Database State:**
- Order: Updated to "PROCESSING"
- Notification: Saved only if no error halts execution.
- Report: Saved only if no error halts execution.

### Comparison Table:
| **Feature**              | **processOrderWithTransaction**             | **processOrderWithoutTransaction**            |
|--------------------------|---------------------------------------------|-----------------------------------------------|
| **Transaction Scope**     | Exists (REQUIRED)                          | None                                          |
| **Order Update**          | Part of transaction                        | Independent, auto-commit                     |
| **Notification Sending**  | Part of transaction; error handled         | Independent; error propagates                |
| **Report Saving**         | Part of transaction; error handled         | Executes only if no prior error stops flow   |
| **Error Impact**          | Does not affect main transaction           | Stops execution if unhandled                 |

### Key Takeaways
- Transactions allow controlled error handling, ensuring critical operations are unaffected.
- Independent operations rely on manual error management and can halt the flow.
*/
