Inventory & User Management System (IUIMS) - ICT711 Assessment 3 Version
=========================================================================

Overview
--------
This is a text-based Java application designed to satisfy the requirements of
ICT711 Assessment 3. It demonstrates:
- inheritance, polymorphism and abstraction
- multiple interacting objects
- CRUD and query operations for users and inventory entities
- Java Collections API (ArrayList, LinkedList, Queue)
- text-file persistence
- try-catch based exception handling
- periodic evaluation with reward and penalty actions
- a menu-driven command-line user interface

Key requirement alignment
-------------------------
1. User management:
   - Add, view, update, delete, and query users
   - users stored in data/users.txt with at least 10 records
2. OOP:
   - BaseModel abstract class and multiple subclasses
   - polymorphic printDetails() implementation
3. Collections:
   - ArrayList for entity storage
   - LinkedList for FIFO batch deduction
   - Queue (ArrayDeque) for evaluation event processing
4. File handling:
   - all entity records saved in data/*.txt files
5. Periodic evaluations:
   - full evaluation checks low stock, over-limit users, inactive suppliers
   - triggers reward/penalty/status transactions
   - saves evaluation history to data/evaluation_events.txt

Project structure
-----------------
src/
  Main.java
  ims/model/
  ims/service/
  ims/storage/
  ims/ui/
  ims/exception/

data/
  categories.txt
  suppliers.txt
  products.txt
  batches.txt
  users.txt
  evaluation_events.txt
  query_results.txt


Compile and run
---------------
1. Open terminal in the project root.
2. Compile:
      javac -d out -sourcepath src src/Main.java
3. Run:
      java -cp out Main


