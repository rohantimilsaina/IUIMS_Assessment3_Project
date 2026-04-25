# ICT711 Programming and Algorithms
## Assessment 3 Project Report
## Project Title: Inventory & User Management System (IUIMS)

## 1. Introduction
This project is a text-based Java application developed for ICT711 Assessment 3.
The system manages users, categories, suppliers, products, and stock batches.
It also performs periodic evaluations that trigger reward, penalty, and status
transactions.

## 2. How the project addresses the scenario
The assignment requires user records with CRUD, querying, file handling, and
periodic evaluation. In this project, the `Customer` entity acts as the managed
user account. The UI presents these accounts as **Users / Customer Accounts**,
and all user records are stored in `data/users.txt`.

## 3. OOP concepts used
### Inheritance
`BaseModel` is the parent abstract class for:
- Category
- Supplier
- Product
- ProductBatch
- Customer
- EvaluationEvent

### Abstraction
`BaseModel` defines common methods such as:
- `toFileLine()`
- `printDetails()`
- `getSummary()`

### Polymorphism
The `ReportsMenu` contains a polymorphism demonstration where mixed subclasses
are stored as `BaseModel` objects and the same `printDetails()` call produces
different outputs depending on the runtime type.

### Encapsulation
All major fields are private and accessed through getters/setters.

## 4. System functionality
### User management
- Add user
- View user by ID
- Search user by ID, name, email, or phone
- Update user
- Delete user
- Block/unblock user
- Record payment
- Report users over credit limit

### Inventory management
- Add/update/delete/search categories
- Add/update/delete/search suppliers
- Add/update/delete/search products
- Receive stock batches
- Deduct stock using FIFO
- Generate inventory reports

### Periodic evaluation and transactions
The assignment requires periodic evaluations with positive and negative feedback
and reward/penalty actions. This has been implemented as follows:
- Low stock products -> negative feedback and penalty event recorded
- Over-limit users -> penalty action recorded and user blocked
- Good standing users -> positive feedback and reward action recorded
- Inactive suppliers -> status review event recorded
- Evaluation history saved to `data/evaluation_events.txt`

## 5. Data structures and collections justification
### ArrayList
Used in service classes for storing categories, suppliers, products, users, and
evaluation history. This suits list-based traversal and simple CRUD operations.

### LinkedList
Used in `BatchService` because stock is deducted using FIFO. LinkedList supports
queue-like behaviour efficiently when working with oldest-first stock batches.

### Queue (ArrayDeque)
Used in `EvaluationService` to process pending evaluation events in FIFO order.
This demonstrates an advanced data structure beyond basic lists and shows a
clear benefit for transaction-style event processing.

## 6. File handling
The project reads and writes text files using the `FileStorage` class.
Files used:
- `categories.txt`
- `suppliers.txt`
- `products.txt`
- `batches.txt`
- `users.txt`
- `evaluation_events.txt`
- `query_results.txt`

The program supports initial loading from file and saving back after add,
update, delete, and evaluation operations.

## 7. Exception handling
The project handles exceptions using try-catch blocks for:
- file I/O
- invalid numeric input
- duplicate records
- record not found situations
- invalid email/phone validation for users

## 8. Testing summary
The following cases were tested:
- application compiles and launches
- user file loads with 10+ records
- add/update/delete/search user
- low stock report displays products needing reorder
- over-limit users are detected
- full evaluation records reward/penalty events
- evaluation history displays saved transactions

## 9. Conclusion
This project satisfies the major technical requirements of ICT711 Assessment 3,
including text-based UI, OOP concepts, collections, file handling, exception
handling, and periodic evaluations with recorded transactions.

## 10. Final submission note
Replace any placeholder group member names in `data/users.txt` and complete the
weekly activity log using the actual group members' contributions before final
submission.
