/*
### Example 1: Transaction Propagation with REQUIRED

This example demonstrates the behavior when a method with Propagation.REQUIRED encounters an error and handles it inside a try-catch block.
*/

@Service
public class Exm {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLogRepository orderLogRepository;

    // Main method to place an order
    @Transactional(propagation = Propagation.REQUIRED)
    public void placeOrder() {
        // Saving the order
        Order order = new Order();
        order.setProduct("Laptop");
        order.setQuantity(1);
        order.setPrice(1200.00);
        orderRepository.save(order);
        System.out.println("Order saved successfully!");

        // Attempt to save order log and handle any exceptions
        try {
            saveOrderLog(order.getId());
        } catch (Exception e) {
            System.out.println("Error while saving log: " + e.getMessage());
        }

        // Continue with inventory update
        updateInventory(order.getProduct(), order.getQuantity());
    }

    // Saving order log with REQUIRED propagation
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrderLog(Long orderId) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setMessage("Order log created for order ID: " + orderId);
        orderLogRepository.save(log);

        // Simulating an error to test transaction rollback
        throw new RuntimeException("Simulated log save error");
    }

    // Updating inventory
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateInventory(String product, int quantity) {
        System.out.println("Inventory updated for product: " + product + " with quantity: " + quantity);
    }
}

/*
### Analysis of Behavior:
1. The order is successfully saved in the database.
2. The order log is attempted to be saved, but it throws an exception.
3. Since the exception is handled in the try-catch block, the transaction for `saveOrderLog` rolls back, but the overall transaction continues.
4. The inventory update is executed successfully as part of the main transaction.

### Expected Output:
1. "Order saved successfully!"
2. "Error while saving log: Simulated log save error"
3. "Inventory updated for product: Laptop with quantity: 1"

### Database State:
- Order: Saved
- OrderLog: Not saved (rolled back due to error)
- Inventory: Updated
*/
