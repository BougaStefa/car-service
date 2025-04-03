# Car Service Management System

A JavaFX application for managing a car service company's operations, including customers, cars, garages, and service jobs. This system provides a comprehensive solution for tracking service records and managing customer relationships.

## Features

- **Dashboard Overview**
  - Quick statistics on customers, cars, active jobs, and garages
  - Recent activity tracking
  - Quick access to main functions

- **Customer Management**
  - Add, edit, and delete customer records
  - Search customers by surname
  - View average service costs per customer

- **Car Management**
  - Register and manage cars with customer associations
  - Track service history
  - Calculate total service days per car

- **Garage Management**
  - Manage multiple garage locations
  - Track garage details and contact information
  - Search garages by name

- **Job Management**
  - Create and track service jobs
  - Record job completion dates
  - Process payments
  - Calculate service durations

## Technical Details

### Prerequisites

- Java 11 or higher
- MariaDB Server
- JavaFX

### Database Configuration

The system connects to a MariaDB database named `carservice`. Make sure to configure your database connection in the `DatabaseConfig` class.

### Key Components

1. **Model Layer**
   - Customer, Car, Garage, Job, Activity, and Payment entities
   - Data validation and business rules

2. **Service Layer**
   - Business logic implementation
   - Error handling

3. **DAO Layer**
   - Database operations
   - CRUD operations for all entities
   - SQL query execution

4. **Controller Layer**
   - JavaFX controllers for UI interaction
   - Form validation
   - User feedback

### Features Implementation

#### Payment Processing
- Payments are processed when jobs are completed
- Payment validation ensures jobs are paid upon completion

#### Service Duration Tracking
- Calculates total service days for each car
- Tracks active and completed jobs
- Identifies cars with longest service durations

#### Cost Analysis
- Calculates average service costs per customer
- Provides cost insights for business analysis

## Usage

1. Launch the application
2. Use the dashboard to navigate to different sections
3. Manage customers, cars, and garages through their respective interfaces
4. Create and track jobs
5. Process payments upon job completion

## Error Handling

- Input validation for all forms
- Database operation error handling
- User-friendly error messages

## Project Structure

```
com.carservice/
├── config/
│   └── DatabaseConfig.java
├── controller/
│   ├── CarsController.java
│   ├── CustomersController.java
│   ├── DashboardController.java
│   ├── GaragesController.java
│   ├── JobFormController.java
│   └── CustomerFormController.java
├── dao/
│   ├── ActivityDAO.java
│   ├── CarDAO.java
│   ├── CustomerDAO.java
│   ├── GarageDAO.java
│   ├── JobDAO.java
│   └── PaymentDAO.java
├── model/
│   ├── Activity.java
│   ├── Car.java
│   ├── Customer.java
│   ├── Garage.java
│   ├── Job.java
│   └── Payment.java
├── service/
│   ├── ActivityService.java
│   ├── CarService.java
│   ├── CustomerService.java
│   ├── GarageService.java
│   ├── JobService.java
│   └── PaymentService.java
├── ui/util/
│   └── DialogUtils.java
└── resources/
    ├── fxml/
    │   ├── cars-view.fxml
    │   ├── customer-form.fxml
    │   ├── customers-view.fxml
    │   ├── dashboard-view.fxml
    │   ├── garage-form.fxml
    │   ├── garages-view.fxml
    │   ├── job-form.fxml
    │   └── main-view.fxml
    ├── styles/
    │   └── application.css
    └── database.properties
```
## Contributors

- BougaStefa
- HND Software Development
- NESCOL

## License

This project is a college assignment and is not licensed for commercial use.
