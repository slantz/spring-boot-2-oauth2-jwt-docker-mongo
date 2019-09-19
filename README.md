# spring-boot-2-oauth2-mongo-jwt
This is the boilerplate Spring Boot repository introducing microservice architecture for Authorization service, implementing OAuth2, JWT tokens and Mongo DBs for authorization and running in Docker.

## How to run
### Prerequisites
This specific sample requires mongo db, it could be anything, even users set on a startup in the main class.

1. Install mongo
    1. Docker - start mongo using Kitematic
        1. Search for `mongo:latest` image on Docker Hub
        1. Hit the `CREATE` button and viola!
    2. Using docker command line
        1. `docker search mongo`
        2. `docker pull mongo`
        3. `docker run -d -p 27017:27017 --name mongo mongo`
    3. Download mongo using internet and install locally on the machine.

2. Secure the database
    1. `use auth`
    2. create desired db user `db.createUser({user: "test",pwd: "test",roles:[{role: "readWrite", db: "auth"}]})`

### Run with Java < 9

So looks like you are oldschool enterprise dude, well, that's fine, no one will judge you and moreover there's an option to run with Java 8 for instance.

#### `auth-service/build.gradle`

1. change `sourceCompatibility=1.10` to `sourceCompatibility=1.8`
2. comment out
```groovy
bootRun {
    jvmArgs = ["--add-modules", "java.xml.bind"]
}
```
this is necessary only starting from Java 9 as it supports modules.

#### `build.gradle`

1. change `sourceCompatibility=1.10` to `sourceCompatibility=1.8`

#### `.java-version`

1. change `1.10` to `1.8`

## Authorization fetching flow

### Authenticate as our application aka `grant_type=client_credentials`

This flow is necessary for the authroization service distinguish the authorized applications aka our app. On the application start it'd get the OAuth2 token to access some restricted endpoints like `sign-up` and other possible `POST` and `PUT` endpoints. This will keep our app secure and exclude the possibility to externally DDos our DBs as the spring security will filter out all tokenless requests or insufficient Oauth2 scopes requests.

#### Request
```export AUTHORIZATION_TOKEN=$(curl -X POST -vu web:111 -H "Accept: application/json" "http://localhost:9999/oauth/token" -d "grant_type=client_credentials"  | jq -r '.access_token')```

where `web` is `clientId` and `111` is `clientSecret` set in OAuth2Configuration class in memory in this example.

#### Response

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MjgxNjQxMDgsInVzZXJfbmFtZSI6ImxhbGthIiwiYXV0aG9yaXRpZXMiOlsiV0FTVEVEX1VTRVIiXSwianRpIjoiNGUyMTVkZWEtNjU0OS00ZDNmLThlMGEtMjVkYWI0OWQxYTM4IiwiY2xpZW50X2lkIjoid2ViX2FwcCIsInNjb3BlIjpbIldBU1RFRCIsIldBU1RFRF9SRUFEIiwiV0FTVEVEX1dSSVRFIl19.nyt6m_XuDQD1O_DJSqqesEtQTPgZSfr0v7Y8IgYT7P_6le0EAKVJ_MyJEbL4mp9B_kQIvlC-D3J9TIUPSflLH6gwFD7qJrLlK0r8teUS-mY4GETNVqyFlWm3HH8O88NPcKJ_-sTUTkpoVl-WM6CGPEUjEASqVyzyq7VN8MEWD09ioTVBQyp3KHCuZ6MyDVD2OrYMrWImv_zA4-FUm8hDkYc4P-hthbYE-vXy9xTzLmySnC1i9V2n2orIQBUKlx0v4J_nwlhGLoYLzGi6pGkNun0rlEcLn411QPwilllCRj5_fO4N01rMwwvIyZEnrE2xj-y8YHHbYpfivui0b4O-EQ",
  "token_type": "bearer",
  "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJsYWxrYSIsInNjb3BlIjpbIldBU1RFRCIsIldBU1RFRF9SRUFEIiwiV0FTVEVEX1dSSVRFIl0sImF0aSI6IjRlMjE1ZGVhLTY1NDktNGQzZi04ZTBhLTI1ZGFiNDlkMWEzOCIsImV4cCI6MTUzMDcxMjkwOCwiYXV0aG9yaXRpZXMiOlsiV0FTVEVEX1VTRVIiXSwianRpIjoiMjUyNGJiNGEtMDIzMi00NzZjLWIyMWYtM2FkMGEzM2FjNWEwIiwiY2xpZW50X2lkIjoid2ViX2FwcCJ9.pGpdiy4GdqAB4Q6geh6nowKUI5H84-kcHmUy6Nm28lPOK3pUq8HBgo9kKKzcVOtDceU7LWtDO4IhREe6bEChPIC2Vqf9B0eI4Lulncc97pxH0eMklJvp0Nco-Uv5Z2Mpfs3hLqZ4vyfPPP4QQAKShhhR8TV2zTONxk_jRfNRRhqC6wCggYzY0HGGKz7t7ryXD98fa-22wNupi_S_hirUWnLRVQL-ZajRuJBHKa53nTFzDkx-nXfQXGoP86bvaQ93bhgqjrGzXOpbFY32NR4C2U8WjFxMPHD3Zr1dWU8MElOPt83VtnhqBvm4G8rxR53I7jxBwKhrFSiKnNlQIart2g",
  "expires_in": 43199,
  "scope": "SPRING_BOOT SPRING_BOOT_READ SPRING_BOOT_WRITE",
  "jti": "4e215dea-6549-4d3f-8e0a-25dab49d1a38"
}
```

here, for instance, the `scope: SPRING_BOOT_WRITE` is returned then on any Authorization service or resource one the rights are checked via `@PreAuthorize("#oauth2.hasScope('SPRING_BOOT_WRITE')")`.

### Authenticate as user with `grant_type=password`

This flow is necessary to sign in as user of our application. It requires `clientId` and `clientSecret` to create Basic authenticaion token to access the authorization service and `username` and `password` to sign in as user.

The generated token will provide access to fetching data for resource services via `java.security.Principal`. Any endpoint relying on java security will get Principal object that has `#getName()` method that will return _unique_ `username` provided via sign in flow. This `username` will be used to filter all mongo collections like resource one to show docuemtns only for this user filtering out the rest ones.

#### Request

```json
export AUTHORIZATION_TOKEN=$(curl -X POST -vu web:111 -H "Accept: application/json" "http://localhost:9999/oauth/token" -d "grant_type=password&username=guest&password=guest"  | jq -r '.access_token')
```

```json
curl -X GET -H "Authorization: Bearer $AUTHORIZATION_TOKEN" "http://localhost:9999/users/me" -H "Content-Type: application/json" | jq .
```
#### Response

```json
{
  "username": "admin",
  "password": "{bcrypt}$2a$10$sz0maELyMdwfeHQWHF7HEOU51CYfQKcg.WB0wnBcZoxJLVqII008S",
  "accountNonExpired": true,
  "accountNonLocked": true,
  "credentialsNonExpired": true,
  "enabled": true,
  "authorities": [
    {
      "authority": "USER"
    }
  ]
}
```

### Authenticate as admin aka `grant_type=password`

This is the flow for authorization service and all resource service admins, this gives ability to operate with closed API endpoints like deleting users from DBs or adding new subscriptions etc.

#### Request

```json
export AUTHORIZATION_TOKEN=$(curl -X POST -vu web_app:111 -H "Accept: application/json" "http://localhost:9999/oauth/token" -d "grant_type=password&username=admin&password=admin"  | jq -r '.access_token')
```

```json
curl -X GET -H "Authorization: Bearer $AUTHORIZATION_TOKEN" "http://localhost:9999/users/me" -H "Content-Type: application/json" | jq .
```

#### Response

```json
{
  "username": "admin",
  "password": "{bcrypt}$2a$10$nRq9Tz9v0P.kaqOCyDZ0pOa1k3kd5u7NLkBaRFTlxqOI/dbIGwQk.",
  "accountNonExpired": true,
  "accountNonLocked": true,
  "credentialsNonExpired": true,
  "enabled": true,
  "authorities": [
    {
      "authority": "ADMIN"
    },
    {
      "authority": "USER"
    }
  ]
}
```

here the `authority: ADMIN` is returned then on any Authorization service or resource one the rights are checked via `@PreAuthorize("hasAuthority('ADMIN')")`.

## Dockerize all the things

### Use docker-compose to build a service image and run with mongo

### Docker network create only once

```
docker network create spring_boot_2_oauth2_default_network
```

#### Start
```
docker-compose up -d
```

#### Stop
```
docker-compose down -v
```

### Each authorization service in it's own image _WITHOUT_ common network

```
./gradlew build docker
```

```
docker run -d -p 9999:9999 -p 8081:8081 -e "spring.data.mongodb.host=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <mongo_container_name>)" -e "spring.data.mongodb.username=test" -e "spring.data.mongodb.password=test" --name 'auth-service' your-docker-repo/spring-boot-auth-service
```

it's important to pay attention here to `spring.data.mongodb.host` environment variable, since mongo container is not a part of the docker network here, it's IP should be defined first if it's in docker.

## Useful links

1. These are official docs to start
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide#authenticationmanager-bean
- https://github.com/spring-guides/tut-bookmarks/tree/master/security/src
2. These are links to proceed
- http://sivatechlab.com/secure-rest-api-using-spring-security-oauth2-part-4/
- https://github.com/spring-projects/spring-security-oauth/issues/685
- http://websystique.com/spring-security/secure-spring-rest-api-using-oauth2/
- https://stackoverflow.com/questions/49348551/could-not-autowire-authentication-manager-in-spring-boot-2-0-0?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
- https://stackoverflow.com/questions/3021200/how-to-check-hasrole-in-java-code-with-spring-security?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
- https://docs.spring.io/spring-security/site/docs/current/reference/html/el-access.html
- https://piotrminkowski.wordpress.com/2017/12/01/part-2-microservices-security-with-oauth2/
- https://hellokoding.com/registration-and-login-example-with-spring-security-spring-boot-spring-data-jpa-hsql-jsp/
- http://www.baeldung.com/spring-security-authentication-with-a-database


## Description how it works

1. sign in as application or other stuff with clientId and clientSecret
2. sign in as user via grant_type=password username and password
3. use sign in token to get user info from /users/me
4. use sign in token to get resource service endpoints received with grant_type=client_credentials to reach non-user specific endpoints
5. use sign in token to get resource service endpoints received with grant_type=password to reach user specific endpoints,
get username from OAuth2 token and filter resource service data from DBs bu username, not user id to match Spring Security UserDetails interface.

## Useful notes

- Spring interfaces and their implementations should live into same package for Spring to be able to autoconfigure those.
- http://stytex.de/blog/2016/02/01/spring-cloud-security-with-oauth2/
- https://spring.io/guides/gs/testing-web/ - testing
