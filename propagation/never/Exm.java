/*
### Example 9: Propagation.NEVER

This example demonstrates the behavior of a method annotated with NEVER. It ensures the method is never executed within a transaction. If a transaction is active, Spring throws an IllegalTransactionStateException.

**Behavior:**

#### saveSetting
- **No Transaction Allowed:** Executes independently and throws an exception if a transaction is active.
- **Independent Execution:** Changes are saved immediately and are not affected by any transactional context.

#### performAdminTask
- **Main Transaction:** Starts a transaction and attempts to call saveSetting, which fails due to the active transaction.
*/

// TransactionNeverExample.java
@Service
public class SystemSettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Transactional(propagation = Propagation.NEVER)
    public void saveSetting(String key, String value) {
        System.out.println("Saving system setting: " + key + " -> " + value);

        Setting setting = new Setting();
        setting.setKey(key);
        setting.setValue(value);
        settingRepository.save(setting);

        System.out.println("System setting saved successfully.");
    }
}

@Service
public class AdminService {

    @Autowired
    private SystemSettingService systemSettingService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void performAdminTask() {
        System.out.println("Starting admin task...");

        // Step 1: Perform some transactional operations
        System.out.println("Performing transactional operation...");

        // Step 2: Attempt to save a system setting
        systemSettingService.saveSetting("maintenance_mode", "on");

        System.out.println("Admin task completed.");
    }
}

/*
### Detailed Behavior Analysis

#### performAdminTask
- **Main Transaction (REQUIRED):** Begins and executes transactional operations.
- **saveSetting Call:** Fails because saveSetting is annotated with NEVER, which does not allow execution within a transaction.

#### saveSetting
- **No Transaction Allowed (NEVER):** Throws an IllegalTransactionStateException if called within a transactional context.
- **Independent Execution:** Executes successfully and immediately commits changes if no transaction is active.

**Output Example:**
1. **With Active Transaction:**
   ```
   Starting admin task...
   Performing transactional operation...
   Exception in thread "main" org.springframework.transaction.IllegalTransactionStateException: Existing transaction found for transaction marked with propagation 'never'
   ```
2. **Without Active Transaction:**
   ```
   Saving system setting: app_theme -> dark
   System setting saved successfully.
   ```

### Comparison Between NEVER and NOT_SUPPORTED
| **Propagation**       | **Behavior in Transaction**                                           | **Use Cases**                                    |
|-----------------------|----------------------------------------------------------------------|------------------------------------------------|
| **NEVER**             | Throws an exception if a transaction is active.                      | For methods that must never run within a transaction. |
| **NOT_SUPPORTED**     | Suspends the transaction and executes independently.                 | For operations that should not be affected by transactional context. |

### Key Takeaways
1. **NEVER:** Ensures methods run independently of any transaction and fails if a transaction exists.
2. **NOT_SUPPORTED:** Allows independent execution by temporarily suspending an active transaction.
*/
