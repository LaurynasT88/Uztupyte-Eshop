# E-shop

## Setup:
To run app, run the following cmd from project root : `./mvnw spring-boot:run`

## Liquibase and hibernate

Initial schemas were created in a single changeset `db/changelog/changes/01-create-initial-schemas.sql`. Liquibase
migration is set to run on application startup. Hibernate will not attempt schema updates
`spring.jpa.hibernate.ddl-auto=none`. As we continue introducing new entities or amending the existing ones, we have to
generate a difference between an entity in question and the current state of db schemas, and use that diff to create
incremental changesets. Suggested ways for diff generation:

1. Liquibase maven plugin: Make changes to an entity class. Run `mvn clean install` then run `mvn liquibase:diff`.
   Review the generated diff and create a changeset.
2. JPA Buddy: Make changes to an entity class. Use JPA Buddy `https://www.youtube.com/watch?v=26qri-FIwWo&ab_channel=JPABuddy` to generate a diff. Review the generated diff and create a
   changeset.

