# Transactional Attributes Demo

This repository demonstrates the usage of **Spring @Transactional** attributes with detailed examples for each **Propagation** and **Isolation** type. It is designed to help developers understand how transactional behaviors work in Spring-based applications.  

---

## **Important Note**

All the code and documentation in this repository have been written and documented with the help of **ChatGPT**. However, it’s important to mention:
1. During the research for this topic, I personally found very few reliable resources explaining these concepts clearly.
2. Some of the responses from ChatGPT were incorrect or partially inaccurate. I've customized and corrected them as much as possible to create meaningful examples.

### **Recommendation for Learning:**

To better understand this topic, follow the examples in the following order for **Propagation**:
1. `REQUIRED`
2. `REQUIRES_NEW`
3. `SUPPORTS`
4. `NOT_SUPPORTED`
5. `MANDATORY`
6. `NEVER`
7. `NESTED`

Before diving into Propagation types, make sure you have a good understanding of:
- **Transactions** in general.
- **Persistence Context** in JPA and how it interacts with transactions.

---

## **Project Structure**

```plaintext
transactional-attributes/
├── propagation/
│   ├── required/
│   ├── requires_new/
│   ├── nested/
│   ├── mandatory/
│   ├── supports/
│   └── never/
├── isolation/
│   ├── read_uncommitted/
│   ├── read_committed/
│   ├── repeatable_read/
│   └── serializable/
