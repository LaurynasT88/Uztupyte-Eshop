# E-shop
A simple e-commerce backend application.

Admin Role:
Manage products (add, update, delete).
View all orders.
Manage users (view, block/unblock).

Customer Role:
Browse products.
Add products to cart.
Place orders.
View their order history.


## Setup:
todo: refine this maybe ask gpt how to automate running this
To run app, run the following cmd from project root : ./mvnw spring-boot:run
for local running application properties aws set to true "aws.s3.mock=true"
for local running application properties auto verification is enabled "uztupyte.backend.autoVerificationEnabled=true"
for local running MySQL database is required, password in application properties left blank "spring.datasource.password="
## Liquibase and hibernate

//set up is ready, but been using hibernate's dll-update for quick development


Initial schemas were created in a single changeset db/changelog/changes/01-create-initial-schemas.sql. Liquibase
migration is set to run on application startup. Hibernate will not attempt schema updates
spring.jpa.hibernate.ddl-auto=none. As we continue introducing new entities or amending the existing ones, we have to
generate a difference between an entity in question and the current state of db schemas, and use that diff to create
incremental changesets. Suggested ways for diff generation:

1. Liquibase maven plugin: Make changes to an entity class. Run mvn clean install then run mvn liquibase:diff.
   Review the generated diff and create a changeset.
2. JPA Buddy: Make changes to an entity class. Use JPA Buddy https://www.youtube.com/watch?v=26qri-FIwWo&ab_channel=JPABuddy to generate a diff. Review the generated diff and create a
   changeset.

// Add some future tasks
Use Liquibase with JPA/Hibernate entities in IntelliJ 


1. Do not use entity classes in service and controller layers - use separate dtos
2. Set up @Transactional boundaries properly (research db transaction management in spring boot)
3. Generate liquibase changesets and start using these instead of hibernate dll