/*
### Example 8: Changing NOT_SUPPORTED to SUPPORTS

This example demonstrates the behavior of a method annotated with SUPPORTS. When called within an existing transaction, it becomes part of that transaction. Otherwise, it executes without a transaction.

**Behavior:**

#### processOrder
- **Order Update:** Updates the order status within the main transaction.
- **Notification Sending:** Executes as part of the main transaction.
- **Error Simulation:** Simulates an error after sending the notification to test the transactional behavior.

#### sendNotification
- **Transaction Participation:** Participates in the main transaction if it exists.
- **Independent Execution:** Runs without a transaction if no active transaction exists.
*/

// TransactionSupportsExample.java
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

    @Transactional(propagation = Propagation.SUPPORTS)
    public void sendNotification(Long orderId, String message) {
        System.out.println("Sending notification for order ID: " + orderId);

        Notification notification = new Notification();
        notification.setOrderId(orderId);
        notification.setMessage(message);
        notificationRepository.save(notification);

        System.out.println("Notification saved for order ID: " + orderId);
    }
}

/*
### Detailed Behavior Analysis

#### processOrder
- **Main Transaction (REQUIRED):** Begins and encompasses the update of order status, notification sending, and error simulation.
- **Notification Sending:** Executes within the same transaction as the main method.
- **Error Handling:** The simulated error rolls back the main transaction, including changes made in sendNotification.

#### sendNotification
- **Transaction Participation (SUPPORTS):** Participates in the main transaction if one exists.
- **Independent Execution:** Executes without a transaction if called outside of a transactional context.

**Database State:**
| **Operation**           | **Final State in Database**                                                    |
|--------------------------|-------------------------------------------------------------------------------|
| **Order Update**         | Rolled back due to the simulated error in the main transaction.               |
| **Notification Saving**  | Rolled back because it is part of the main transaction.                       |

### Comparison Between NOT_SUPPORTED and SUPPORTS
| **Propagation**       | **Behavior in Main Transaction**                                              | **Changes in Database**                         |
|-----------------------|------------------------------------------------------------------------------|-----------------------------------------------|
| **NOT_SUPPORTED**     | Temporarily suspends the main transaction; executes independently.            | Committed independently; not rolled back.      |
| **SUPPORTS**          | Becomes part of the main transaction if one exists; otherwise runs standalone.| Rolled back if part of a rolled-back transaction. |

### Key Takeaways
1. **SUPPORTS:** Integrates with an existing transaction or runs independently if no transaction exists.
2. **Error Impact:** Errors in the main transaction roll back all participating methods, including SUPPORTS methods.
*/
