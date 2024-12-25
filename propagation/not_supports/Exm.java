/*
### Example 7: Using NOT_SUPPORTED in a Transactional Method

This example demonstrates the behavior of a method annotated with NOT_SUPPORTED when executed within a transactional context, and how exceptions are managed.

**Behavior:**

#### processOrder
- **Order Update:** Updates the order status within the main transaction.
- **Notification Sending:** Temporarily suspends the main transaction and executes independently.
- **Error Simulation:** Simulates an error after sending the notification to test the transactional behavior.

#### sendNotification
- **Independent Execution:** Runs independently of the main transaction (due to NOT_SUPPORTED).
- **Auto-Commit Behavior:** Changes made within this method are immediately committed to the database, regardless of the main transaction's state.
*/

// TransactionNotSupportedExample.java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processOrder(Long orderId) {
        System.out.println("Starting processOrder...");

        // Step 1: Update order status
        orderRepository.updateStatus(orderId, "PROCESSING");
        System.out.println("Order status updated to PROCESSING.");

        // Step 2: Send notification
        notificationService.sendNotification(orderId, "Your order is now processing!");

        // Step 3: Simulate an error
        System.out.println("Simulating an error after notification...");
        throw new RuntimeException("Simulated error after notification!");

        // This line will not execute
        System.out.println("Order processed successfully.");
    }
}

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendNotification(Long orderId, String message) {
        System.out.println("Sending notification for order ID: " + orderId);

        // Save notification
        Notification notification = new Notification();
        notification.setOrderId(orderId);
        notification.setMessage(message);
        notificationRepository.save(notification);
        System.out.println("Notification sent and saved for order ID: " + orderId);
    }
}

/*
### Detailed Behavior Analysis

#### processOrder
- **Main Transaction (REQUIRED):** Begins and encompasses the update of order status and error simulation.
- **Notification Sending:** Temporarily suspends the main transaction. Changes made in this method are committed independently.
- **Error Handling:** The simulated error rolls back the main transaction but does not affect the independent notification.

#### sendNotification
- **Independent Execution (NOT_SUPPORTED):** Executes without participating in the main transaction.
- **Auto-Commit Behavior:** Changes are immediately committed to the database.

**Database State:**
| **Operation**           | **Final State in Database**                                                    |
|--------------------------|-------------------------------------------------------------------------------|
| **Order Update**         | Rolled back due to the simulated error in the main transaction.               |
| **Notification Saving**  | Persisted in the database because it is independent of the main transaction. |

### Key Takeaways
1. **NOT_SUPPORTED:** Methods annotated with this propagation suspend any active transactions and run independently.
2. **Error Impact:** Errors in the main transaction do not affect operations in NOT_SUPPORTED methods.
3. **Auto-Commit:** Changes made in NOT_SUPPORTED methods are immediately committed and cannot be rolled back by the main transaction.
*/
