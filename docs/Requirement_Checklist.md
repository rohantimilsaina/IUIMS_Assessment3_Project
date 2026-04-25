# ICT711 Assessment 3 Requirement Checklist

| Requirement | Status in this version | Evidence |
|---|---|---|
| Text-based Java application | Met | Menu-driven CLI in `ims/ui/*` |
| Multiple interacting objects | Met | Product, Supplier, User(Customer), Batch, EvaluationEvent |
| Inheritance | Met | `BaseModel` extended by all entity classes |
| Polymorphism | Met | Overridden `printDetails()` and `getSummary()` |
| Abstraction | Met | `BaseModel` is abstract |
| Encapsulation | Met | Private fields with getters/setters |
| Add/view/update/delete records | Met | Menus for users, products, suppliers, categories, batches |
| Query by ID/name/attribute | Met | User search by ID/name/email/phone |
| Periodic evaluation | Met | `ReportsMenu.runFullEvaluation()` |
| Positive/negative feedback events | Met | `EvaluationService` records reward/penalty/status events |
| Reward or penalty action | Met | Reward = credit limit increase; Penalty = user block + low-stock flags |
| Collections API | Met | ArrayList, LinkedList, Queue |
| Advanced data structure justification | Met | FIFO queue/linked list documented in report |
| Read/write text files | Met | `FileStorage` and `data/*.txt` |
| Input file with at least 10 users | Met* | `data/users.txt` includes 12 records |
| File includes group member names | Partial | One real name included; placeholders should be replaced before final submission |
| Exception handling | Met | File I/O and invalid input handling |
| Text-based UI | Met | CLI menus and prompts |
| UML diagrams | Met | `docs/UML_Class_Diagram.*` |
| Project report | Met | `docs/Project_Report.md` |
| Weekly activity log | Met (template) | `docs/Weekly_Activity_Log_Template.md` |

\* Replace placeholder group member names with real names before submission.
