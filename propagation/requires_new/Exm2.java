/*
### Example 5: Handling Errors with Try-Catch vs Without Try-Catch

This example demonstrates the behavior of two different methods: one that handles errors using a try-catch block, and another that does not handle errors. The difference in transactional outcomes is analyzed.

**Behavior:**

#### processOrderWithTryCatch
- **Saving Order:** Successfully saves the order.
- **Generating Report:** Attempts to generate a report in an independent transaction. If an error occurs, the transaction for the report rolls back, but the main transaction continues.
- **Success Message:** Prints the success message for the overall process.

#### processOrderWithoutTryCatch
- **Saving Order:** Successfully saves the order.
- **Generating Report:** Attempts to generate a report in an independent transaction. If an error occurs, it propagates to the main transaction, causing a full rollback.
- **No Success Message:** The process stops due to the error, and no success message is printed.
*/

// TransactionTryCatchComparisonExample.java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processOrderWithTryCatch() {
        // Start main transaction and save order
        Order order = new Order();
        order.setProduct("Smartphone");
        order.setQuantity(2);
        order.setPrice(800.00);
        orderRepository.save(order);
        System.out.println("Order saved successfully!");

        // Generate report independently and handle errors
        try {
            generateReport(order.getId());
        } catch (Exception e) {
            System.out.println("Error while generating report: " + e.getMessage());
        }

        System.out.println("Order processed with try-catch successfully!");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void processOrderWithoutTryCatch() {
        // Start main transaction and save order
        Order order = new Order();
        order.setProduct("Laptop");
        order.setQuantity(1);
        order.setPrice(1200.00);
        orderRepository.save(order);
        System.out.println("Order saved successfully!");

        // Generate report independently without handling errors
        generateReport(order.getId());

        System.out.println("Order processed without try-catch successfully!");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateReport(Long orderId) {
        System.out.println("Generating report for order ID: " + orderId);

        // Simulate report saving
        Report report = new Report();
        report.setOrderId(orderId);
        report.setContent("Report for order ID: " + orderId);
        reportRepository.save(report);

        // Simulate an error
        throw new RuntimeException("Simulated report error");
    }
}

/*
### Analysis of Behavior

#### processOrderWithTryCatch
1. The order is saved successfully.
2. The generateReport method runs in an independent transaction and throws an error, causing its transaction to roll back.
3. The error is caught and handled, allowing the main transaction to continue.
4. A success message is printed, indicating the process completed.

**Database State:**
- Order: Saved
- Report: Not saved (rolled back due to error in independent transaction)

#### processOrderWithoutTryCatch
1. The order is saved successfully.
2. The generateReport method runs in an independent transaction and throws an error, causing its transaction to roll back.
3. The error propagates to the main transaction, causing the main transaction to roll back.
4. No success message is printed as the process stops.

**Database State:**
- Order: Not saved (rolled back due to error)
- Report: Not saved (rolled back due to error in independent transaction)

### Comparison Table:
| **Feature**               | **processOrderWithTryCatch**         | **processOrderWithoutTryCatch**    |
|---------------------------|--------------------------------------|------------------------------------|
| **Error Handling**         | Handled in try-catch                | Not handled; propagated upwards   |
| **Main Transaction State** | Continues                           | Rolled back                       |
| **Independent Transaction**| Rolled back due to error            | Rolled back due to error          |
| **Data Saved**             | Order saved, report not saved       | Neither order nor report saved    |
| **Success Message**        | Printed                             | Not printed                       |

### Key Takeaways
- Handling errors with try-catch allows the main transaction to continue.
- Errors in independent transactions do not affect the main transaction unless propagated.
*/
