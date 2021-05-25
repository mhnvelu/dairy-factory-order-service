# dairy-factory-order-service
Spring Boot Microservices with Spring Cloud

Please refer my [Notes](https://github.com/mhnvelu/dairy-factory/blob/master/NOTES.md) to know about Sagas.

A repository on Spring State Machine is available at [spring-state-machine-project](https://github.com/mhnvelu/spring-state-machine-project)

### Order Allocation Orchestration Saga
![Order Allocation Orchestration Saga](Order-Allocation-Orchestration-Saga.png)

- Saga Execution Coordinator is implemented using Spring State Machine.
- Events:
  - VALIDATE_ORDER, VALIDATION_PASSED, VALIDATION_FAILED, ALLOCATION_SUCCESS, 
  ALLOCATION_NO_INVENTORY, ALLOCATION_FAILED, BUTTER_ORDER_PICKED_UP, CANCEL_ORDER
- States:
  - NEW, VALIDATED, VALIDATION_EXCEPTION, ALLOCATED, ALLOCATION_ERROR, PENDING_INVENTORY, 
  PICKED_UP, DELIVERED, DELIVERY_EXCEPTION, CANCELLED
