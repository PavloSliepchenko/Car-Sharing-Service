# CAR SHARING API
## Description
This API allows your customers to register, login, check available cars, rent a car, pay for rented cars. Admin will receive telegram notifications about new rentals and payments; every day at 12pm API will check all overdue rentals and send a notification to telegram about them with full info.

## Features
- JWT token to authenticate a customer;
- Using roles of users to provide access to different API functions;
- Telegram bot to send notifications about new rentals, payments and overdue rentals;
- Stripe API to send payments;
- Get a list of all available cars;
- Rent a car;
- Update user's role;
- Get payments by user id;

## Implemented technologies
- Maven
- MySQL
- Lombok
- Docker
- Java 17
- Mapstruct
- Liquibase
- Stripe API
- Spring boot
- Telegram API
- Spring security
- Test containers
- Jackson web token

## ENDPOINTS
## Available for all users:
- GET: /cars (To get a list of all available cars)
- POST: /register (To register a new user)
- POST: /login (To get JWT tokens)
## Available for registered users
- POST: /rentals (To rent a car)
- GET: /rentals/{rentalId} (To get rental by id)
- POST: /rentals/return (To return a rented car)
- GET: /users/me (To get personal user's info)
- POST: /users/me (To update profile info)
- POST: /payments (To pay for rented car)
- GET: /payments/success (Success endpoint. Endpoint for stripe redirection)
- GET: /payments/cancel (Cancel endpoint. Endpoint for stripe redirection)
## Available for admin users
- POST: /cars (To add a new car)
- GET: /cars/{id} (To get a car by id)
- PATCH: /cars/{id} (To update car's info)
- DELETE: /cars/{id} (To delete a car)
- GET: /rentals?user_id=...&is_active=...(To get rentals by user ID and whether the rental is still active or not)
- GET: /rentals/status?isActive=... (To get rentals by rental status)
- GET: /payments/{userId} (To get payments by user id)
- PUT: /users/{userId}/role (To update user's role)
## Important notice
Please note that endpoints with POST, PUT and PATCH methods require JSON body as an argument.
## API using steps
1. Upload this API to your server using docker.
2. Add cars using admin user (please see an example below). This API implements liquibase so all needed tables will be created in the DB automatically after launching the API. Also, admin user will be added. Login: admin@user.com, password: 12345.
3. Add the following keys to .env file:
   - telegram bot token;
   - telegram bot username;
   - telegram chat id;
   - Stripe public key;
   - Stripe secret key.
4. Done! Now new users can register to your car sharing service and rent the cars.
# Examples
## Register
To register your customers will need to use their email, make up a password, user their name and surname. Email must be unique. Please see an example below.
![Register a new user](https://drive.google.com/uc?export=view&id=ENUpY2Ea9hzoNkYGK4OlhxXJw6x)
## Add a new car
To add a new car you will need to log in as admin user and use and authentication token to get access to endpoint. Then you will have to mention car's model, brand, type, inventory and a daily fee. Please see an example below.
![Add a new car](https://drive.google.com/uc?export=view&id=1r0YMFyovEkOdgWKv8E7_QBJBdzD-0VPW)
## Get rentals by status
To get rentals by status you will need to log in as admin user and use true/false as parameter. 'true' - still active. 'false' - returned.
![Get rentals by status](https://drive.google.com/uc?export=view&id=1EkWmPXaHrM16FoTfYRfn4iuu5rgD_cK5)
## Get payments by user id
To get rentals by status you will need to log in as admin user and use user id
![Get payments by user id](https://drive.google.com/uc?export=view&id=1uIa7dyWPm7r0mAbVg4FOKmmjbN6uSfbT)
# - Video presentation
[![Watch the video](https://img.youtube.com/vi/DVUqGmFQI7k/hqdefault.jpg)](https://www.youtube.com/watch?v=DVUqGmFQI7k)