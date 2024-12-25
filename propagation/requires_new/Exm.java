/*
### Example 1: Propagation.REQUIRED with Handled Errors

This example demonstrates the behavior when a method with Propagation.REQUIRED encounters an error and handles it inside a try-catch block.

**Behavior:**
- **Saving Order:** The order is saved successfully.
- **Order Log:** The log operation encounters an error but is handled within a try-catch block, allowing the main transaction to continue.
- **Inventory Update:** The inventory update operation executes as expected.
*/

// TransactionRequiredExample.java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLogRepository orderLogRepository;

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

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrderLog(Long orderId) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setMessage("Order log created for order ID: " + orderId);
        orderLogRepository.save(log);

        // Simulating an error to test transaction rollback
        throw new RuntimeException("Simulated log save error");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateInventory(String product, int quantity) {
        System.out.println("Inventory updated for product: " + product + " with quantity: " + quantity);
    }
}

/*
### Example 2: Propagation.REQUIRES_NEW with Independent Transactions

This example demonstrates the behavior when a method with Propagation.REQUIRES_NEW creates an independent transaction.

**Behavior:**
- **Saving Order:** The order is saved successfully as part of the main transaction.
- **Order Log:** The log operation executes in an independent transaction and rolls back due to the simulated error, without affecting the main transaction.
- **Inventory Update:** The inventory update operation executes as expected.
*/

// TransactionRequiresNewExample.java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLogRepository orderLogRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void placeOrder() {
        // Saving the order
        Order order = new Order();
        order.setProduct("Laptop");
        order.setQuantity(1);
        order.setPrice(1200.00);
        orderRepository.save(order);
        System.out.println("Order saved successfully!");

        // Attempt to save order log in an independent transaction
        try {
            saveOrderLog(order.getId());
        } catch (Exception e) {
            System.out.println("Error while saving log: " + e.getMessage());
        }

        // Continue with inventory update
        updateInventory(order.getProduct(), order.getQuantity());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrderLog(Long orderId) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setMessage("Order log created for order ID: " + orderId);
        orderLogRepository.save(log);

        // Simulating an error to test transaction rollback
        throw new RuntimeException("Simulated log save error");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateInventory(String product, int quantity) {
        System.out.println("Inventory updated for product: " + product + " with quantity: " + quantity);
    }
}

/*
### Example 3: Full Rollback of Main Transaction

This example demonstrates the behavior when an exception causes the main transaction to roll back completely.

**Behavior:**
- **Saving Order:** The order operation is saved initially but rolls back due to the propagated exception.
- **Order Log:** The log operation also rolls back as part of the main transaction.
- **Inventory Update:** The inventory update operation does not execute as the transaction is fully rolled back.
*/

// TransactionFullRollbackExample.java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLogRepository orderLogRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void placeOrder() {
        // Saving the order
        Order order = new Order();
        order.setProduct("Laptop");
        order.setQuantity(1);
        order.setPrice(1200.00);
        orderRepository.save(order);
        System.out.println("Order saved successfully!");

        // Attempt to save order log with re-throwing the exception
        try {
            saveOrderLog(order.getId());
        } catch (Exception e) {
            System.out.println("Error while saving log: " + e.getMessage());
            throw e; // Re-throw exception to rollback the main transaction
        }

        // Continue with inventory update
        updateInventory(order.getProduct(), order.getQuantity());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrderLog(Long orderId) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setMessage("Order log created for order ID: " + orderId);
        orderLogRepository.save(log);

        // Simulating an error to test transaction rollback
        throw new RuntimeException("Simulated log save error");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateInventory(String product, int quantity) {
        System.out.println("Inventory updated for product: " + product + " with quantity: " + quantity);
    }
}

/*
### Example 4: Manual Rollback of Transaction

This example demonstrates the behavior when the transaction is manually rolled back using TransactionAspectSupport.

**Behavior:**
- **Saving Order:** The order operation is saved initially.
- **Order Log:** The log operation encounters an error and triggers a manual rollback of the main transaction.
- **Inventory Update:** The inventory update operation does not execute as the transaction is rolled back manually.
*/

// TransactionManualRollbackExample.java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLogRepository orderLogRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void placeOrder() {
        // Saving the order
        Order order = new Order();
        order.setProduct("Laptop");
        order.setQuantity(1);
        order.setPrice(1200.00);
        orderRepository.save(order);
        System.out.println("Order saved successfully!");

        // Attempt to save order log with manual rollback
        try {
            saveOrderLog(order.getId());
        } catch (Exception e) {
            System.out.println("Error while saving log: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // Manual rollback
        }

        // Continue with inventory update
        updateInventory(order.getProduct(), order.getQuantity());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrderLog(Long orderId) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setMessage("Order log created for order ID: " + orderId);
        orderLogRepository.save(log);

        // Simulating an error to test transaction rollback
        throw new RuntimeException("Simulated log save error");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateInventory(String product, int quantity) {
        System.out.println("Inventory updated for product: " + product + " with quantity: " + quantity);
    }
}
