# Lab 05 - Persistence Capability

For implementing our Shopping List functionality we will need some kind of persistence. In this lab we're first going to add the technical capability of talking to a database from our Spring Boot app.

We'll choose Postgres as a common database solution.

## Run Postgres locally

When developing locally we'll run postgres through a container container, this can be easily done by running this command. The first time we run this the postgres container image will be downloaded:

```
podman run -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres
```

Example successful output:
```
Resolving "postgres" using unqualified-search registries (/etc/containers/registries.conf.d/999-podman-machine.conf)
Trying to pull docker.io/library/postgres:latest...
Getting image source signatures
Copying blob sha256:6530357dda9a856600bb73f3894189a51697b46e7ddaac67782bc3fa150f3d6c
Copying blob sha256:1fe172e4850f03bb45d41a20174112bc119fbfec42a650edbbd8491aee32e3c3
Copying blob sha256:c2bb685f623fe10169c69d4ec74547c0dd090744f323a73e91ec298ae49198cd
Copying blob sha256:b1d302dc78c63f82eeaf3acc796a480b1c2ba421d011a73e7ea03a2f629bf86d
Copying blob sha256:3027ff70541005e6dc087e541c306590f84da7ab2737fa8ee9e4acb2ef7b54d8
Copying blob sha256:062371e3461d10dd17b37b174c5204709e4a828de004ae80ef44a9776c3b693a
Copying blob sha256:39d54e944de74ab373566c980af765f066053a6c2f12274a7ee808e7cfc56158
Copying blob sha256:b1d302dc78c63f82eeaf3acc796a480b1c2ba421d011a73e7ea03a2f629bf86d
Copying blob sha256:6530357dda9a856600bb73f3894189a51697b46e7ddaac67782bc3fa150f3d6c
Copying blob sha256:c2bb685f623fe10169c69d4ec74547c0dd090744f323a73e91ec298ae49198cd
Copying blob sha256:39d54e944de74ab373566c980af765f066053a6c2f12274a7ee808e7cfc56158
Copying blob sha256:062371e3461d10dd17b37b174c5204709e4a828de004ae80ef44a9776c3b693a
Copying blob sha256:f6d91cb1d3c135c4731590eb6940aa236226e03bc47569827209a515fcc7a085
Copying blob sha256:3027ff70541005e6dc087e541c306590f84da7ab2737fa8ee9e4acb2ef7b54d8
Copying blob sha256:9bbd62b0af2872ac19989dd6d99985e04efad1768505c10bced7b7dcf5b1b65f
Copying blob sha256:3cfdfc8fbef3077a84eab008599ccf444967f5dc6c8b8bac3c40421ac6b2f5c2
Copying blob sha256:635f8fae1d0617c496c561c228d8642c2c4823d945c647cf6a6ffb8e6c7cc7a0
Copying blob sha256:9bbd62b0af2872ac19989dd6d99985e04efad1768505c10bced7b7dcf5b1b65f
Copying blob sha256:f6d91cb1d3c135c4731590eb6940aa236226e03bc47569827209a515fcc7a085
Copying blob sha256:96b6711661dda05f9b7808313c5467d8cfd56c8f2d9903307a181457544b2b2d
Copying blob sha256:3cfdfc8fbef3077a84eab008599ccf444967f5dc6c8b8bac3c40421ac6b2f5c2
Copying blob sha256:c08147da7b54ad4311cf3ebb22f8d68da5e5c67b911ba6cef66126605a55f501
Copying blob sha256:635f8fae1d0617c496c561c228d8642c2c4823d945c647cf6a6ffb8e6c7cc7a0
Copying blob sha256:96b6711661dda05f9b7808313c5467d8cfd56c8f2d9903307a181457544b2b2d
Copying blob sha256:c08147da7b54ad4311cf3ebb22f8d68da5e5c67b911ba6cef66126605a55f501
Copying config sha256:74b0c105737a84fd66dc0fbe8e6cf670eba092c9bcdd02430ad059615372376f
Writing manifest to image destination
Storing signatures
87add553c5f529ce1addfc3ba44e8d5b1b834003671bb1c1ff81098a9ae34ce1
```

For more information about the usage of this container image see:

https://hub.docker.com/_/postgres

## Connect our Spring Boot application

Add a postgres Maven dependency for the JDBC driver:
```
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.2.5</version>
</dependency>
```

We'll use Spring Data JPA, add the appropriate Spring Boot starter:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

In our application.properties we can add all the JDBC connection data:

```
## Spring datasource
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=mysecretpassword

# The SQL dialect makes Hibernate generate better SQL for the chosen database
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto=validate
```

Restart your ShoppingListApplication, if it starts up successfully check the logs for some indication that database connections are working.

To further verify correct connection we're going to enable extra information on our actuator healthpoint, add this in your application.properties file:
```
# Actuator
management.endpoint.health.show-details=always
```

Restart your application and go the the health endpoint:

http://localhost:8080/actuator/health

You'll see that now our health endpoint also includes database connectivity out of the box, neat!

Example:
```
{
"db": {
    "status": "UP",
    "details": {
        "database": "PostgreSQL",
        "hello": 1
}
```

## SQL Client (Optional)

To interact with our database it's convenient to use a SQL client for troubleshooting and interacting with our data. For example in Intellij we can easily set-up a datasource to connect to our local Postgres instance:

![](intellij-datasource.png)

Make sure to first download the appropriate drivers, set-up in other SQL clients will be very similar.

## Flyway

Version controlling a database schema is always a challenge. In our application we want to tightly control and evolve our database schema alongside our code.

To accomplish this we're going to use flyway, give yourself a small introduction by checking out their website:

https://flywaydb.org/documentation/

Let's start by adding Flyway to our project, we need to add this dependency:
```
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
``` 

Spring provides integration with Flyway out of the box, for configuration see:

https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html#howto-execute-flyway-database-migrations-on-startup

## Shopping List Schema

Let's define our Shopping List schema using flyway migration scripts, the first script is provided:

Location: ````src/main/resources/db/migration/V0.1__Add_Shopping_List_Table.sql````

Content:
```
create table SHOPPING_LIST (
  ID UUID PRIMARY KEY,
  NAME TEXT
);
```

IMPORTANT: Pay attention to use only upper cased names for tables and columns, to avoid incompatibility issues when using HSQLDB later. 

Start our ShoppingListApplication, take a look at the logging, you'll see Flyway executing the script:

```
o.f.c.i.s.JdbcTableSchemaHistory         : Creating Schema History table: "public"."flyway_schema_history"
o.f.core.internal.command.DbMigrate      : Current version of schema "public": << Empty Schema >>
o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version 0.1 - Add Shopping List Table
```

After successful start-up, use your SQL Client to have a look at the flyway_schema_history table. Make sure you understand it's role, have a look at the Flyway documentation if necessary.

To implement our Shopping List functionality in the next lab, we'll first need the entire schema. This just involves a many-to-many relationship between cocktails and shopping lists. 

Add appropriate Flyway migration scripts to end up with this schema, find out how to best define the foreign key relationships in postgres:

![](shopping-list-schema.png)

## Embedded database

To keep development options open we also want to enable quick local development by adding the option to start our application using an embedded in-memory database.

In this case we'll use HSQLDB, add this dependency:

```
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
</dependency>
```

To be able to easily switch we'll use Spring's Profile support. Alongside the application.properties provide a new file called application-hsqldb.properties, contents:

```
spring.datasource.url=jdbc:hsqldb:mem:testdb;sql.syntax_pgs=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

This is a convenient Spring naming convention, if we start our application and set "hsqldb" as the active profile these properties will override the ones from the standard application.properties.

You can test running the application with this profile, in IntelliJ you can add an extra run configuration by duplicating the ShoppingListApplication one and setting "hsqldb" in the active profiles textbox.

## Commit and tag your work

Make sure to add, commit and push all your files at least once at the end of every lab. After the lab has been completed completely please tag it with the appropriate lab number:

````
git tag -a lab05 -m "lab05"
```` 