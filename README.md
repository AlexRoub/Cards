# Cards API

This project demonstrates the implementation of an application called Cards,
that allows users to create and manage tasks in the form of cards.

## Features

### Security

* User registration and login with JWT authentication
* Password encryption using BCrypt
* Refresh token
* Logout mechanism

### Cards

* Create card
* Get all cards based on user role
* Get single card based on user role
* Update fields of a card based on user role
* Delete a card based on user role

## Technologies

* Java 17
* Spring Boot 3.0
* Spring Security
* JSON Web Tokens (JWT)
* BCrypt
* Maven
* H2

## Getting Started

To get started with this project, you will need to have the following installed on your local machine:

* JDK 17+
* Maven 3+

To build and run the project, follow these steps:

* Clone the repository: `git clone https://github.com/AlexRoub/Cards.git`
* Build the project: mvn clean install
* Run the project: mvn spring-boot:run

> The application will be available at http://localhost:8080.

## Testing

### Manual Testing Guide

For the manual/e2e testing of the API, Postman app was used.  
Under path **postman/cards.postman_collection.json**
you will find a JSON Postman file with some saved curls that you can use for testing.

> Under path **src/main/resources/contract/cards.yaml** you will find the
> contract of the API. Load it in Swagger Editor here https://editor.swagger.io/
> in order to see the details of each endpoint.  
> Otherwise you can access the contract details via this link http://localhost:8080/swagger-ui/index.html
> when the application is up and running.

Before start consuming any of the endpoints of Cards API, a user must have firstly
been registered, otherwise you will always get 403 Forbidden Access.

In order to do that you can use the below curl:

```
curl --location 'http://localhost:8080/api/v1/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
"email": "adminUser@gmail.com",
"password": "password",
"role": "ADMIN"
}'
```

As a role, user must choose between ADMIN and MEMBER.   
Also, the email provided must be in email format, otherwise you will get 400 Bad Request.

After having registered a user, you must copy in your clipboard the value of **accessToken**.
This will be the token that will give the access in any further action.

Now, let's try to create a new card. Try using the below curl:

```
curl --location 'http://localhost:8080/api/v1/cards/create' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MTU0MjUyLCJleHAiOjE3MTUyNDA2NTJ9.61nDtNO0dzgkNSVwRuxYY8Fhn-1_EhTHBXeM0uaA7yU' \
--data '{
    "name": "Card 1",
    "description": "this is my first card",
    "color": "#ff0000"
}'
```

Under **Authoriation** header you must add the token value fetched from the previous call.
Description and color fields are optional. If color is provided, it must be in correct format,
having 6 alphanumerical values prefixed with #.  
In the response of this endpoint you will see all the information of the newly created card,
along with id, status and creation date. Null values are not visible in the final response.

> There is an option to see the H2 DB schema and data via http://localhost:8080/h2-console endpoint.
> As JDBC url, use **jdbc:h2:mem:cardsdb** with the predefined configuration. Keep in mind, that
> in each restart of the application, the previously inserted data will be lost.

In order to get the information of this singe card, we can use the below curl:

```
curl --location 'http://localhost:8080/api/v1/cards/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiQGMuY29tIiwiaWF0IjoxNzE1MTAzNzA2LCJleHAiOjE3MTUxOTAxMDZ9.45jy0qHrRc1aH1tcGsXjyp-RAdIMy7PfxMgoYy5W7NM'
```

Again, you must use your own token to fetch the previously created card.  
As a cardId, in the path, you must use the id received from the previous call.
If user is a MEMBER, they will only be able to see the cards that they have created.

In order to see all the cards or the cards a user has created, we can use the below curl:

```
curl --location 'http://localhost:8080/api/v1/cards' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MTU0MjUyLCJleHAiOjE3MTUyNDA2NTJ9.61nDtNO0dzgkNSVwRuxYY8Fhn-1_EhTHBXeM0uaA7yU' \
--data '{}'
```

This endpoint responds with a page. The default values are page: 0 and size: integer max(2147483647).  
If you want a specific page, then you must provide the desired page and size request parameters.  
Also, you have the ability to sort the response. The possible sorting values are by name, color, status and creationDate. The default
sorting, if not provided, is
creationDate.DESC, as you can also see in the contract.  
An extra feature of this endpoint is that you can filter the response based on your needs.
This can be achieved by providing a request body.
The available filters are by name, color, status and creation date.

A complete curl would look like this:

```
curl --location 'http://localhost:8080/api/v1/cards?page=0&size=10&sort=name%2Casc' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MTU3MDUzLCJleHAiOjE3MTUyNDM0NTN9.J3xkIQrB3UPlpBQ5TFmKX-i0aiL6an1xku_DOCMdymA' \
--data '{
    "name": "Card 1",
    "color": "#ff0000"
}'
```

Regarding the update of a card, you can do it adapting the below curl according to your needs:

```
curl --location --request PATCH 'http://localhost:8080/api/v1/cards/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiQGMuY29tIiwiaWF0IjoxNzE1MTAzNzA2LCJleHAiOjE3MTUxOTAxMDZ9.45jy0qHrRc1aH1tcGsXjyp-RAdIMy7PfxMgoYy5W7NM' \
--data '{
    "name": "Updated card 1",
    "color": "#000000"
}'
```

User can update the name, the description, the color and/or the status of a card they have access to.
Description and color can be cleared out. The available statuses are **To Do, In Progress and Done**.

In order to delete a card, you can use the below curl:

```
curl --location --request DELETE 'http://localhost:8080/api/v1/cards/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiQGMuY29tIiwiaWF0IjoxNzE1MTAzNzA2LCJleHAiOjE3MTUxOTAxMDZ9.45jy0qHrRc1aH1tcGsXjyp-RAdIMy7PfxMgoYy5W7NM'
```

### Mutation Testing Guide

For the mutation testing, PIT testing system(ref https://pitest.org/) was used.  
In order to run the mutation tests, in the toolbar of IntelliJ IDE, choose **Select Run/Debug Configuration**.
In the pop up window, try to add new configuration through '+' symbol. Choose Maven,
and under 'Run', add the below command:

```
clean compile compiler:testCompile resources:testResources org.pitest:pitest-maven:mutationCoverage -Dpitest.skip=false
```

Then run the mutations. After its completion, you must see in the logs that all the
generated mutations where killed with success rate of 100%. In the logs you will also
see the line coverage and the test strength percentage.  
Also, you will find under path **target/pit-reports/index.html** a better view of the generated statistics.
Choose to open it via any wished browser and navigate through the links of the packages and tested classes.

### Integration Testing

Under path **src/test/java/com/aroubeidis/cards/IT** you will find the integration
tests for each controller and endpoint.