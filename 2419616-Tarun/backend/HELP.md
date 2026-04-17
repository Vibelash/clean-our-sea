# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.3/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.3/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.3/reference/web/servlet.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.0.3/reference/using/devtools.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.3/reference/data/sql.html#data.sql.jpa-and-spring-data)

### Guides

### Using a MySQL database
By default the project runs against an in-memory H2 instance (see `application.properties`).
To switch to a persistent MySQL database:

1. create a schema (e.g. `CREATE DATABASE communities;`).
2. add the MySQL connector dependency (already included in the pom.xml).
3. provide connection information via environment variables or by
   activating the `mysql` profile (`--spring.profiles.active=mysql`).
   See the comments in `application.properties` and the new
   `application-mysql.properties` file for examples.
4. make sure a MySQL server is running and the user has privileges.

Tests continue to use H2 by default so you don't need to change them.

### Serving the front end from the backend
The Spring Boot application now exposes the files under the `front-end/` folder
as static resources. Run the app (`mvn spring-boot:run` or via your IDE) and
then open `http://localhost:8080/communities.html` (or `chat.html`, etc.) instead
of opening the HTML directly from disk. This avoids cross-origin errors, ensures
community names are fetched correctly, and allows chat messages to be posted to
the server.  A new CORS configuration still permits the API to be called from
other origins if desired.

### Chat improvements
If the backend is unavailable messages are now saved in `localStorage` and
re‑displayed on subsequent visits so you can still type even when offline. The
community name is also passed in the query string when joining and will display
on the chat page even if the server lookup fails.

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

