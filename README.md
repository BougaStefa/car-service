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
## How to Run

1. **Start Database**
   ```bash
   # Start all services using Docker Compose
   cd src/docker
   docker compose up -d
   ```

2. **Build & Run Application**
   - Ensure Java 11 or higher is installed
   - Build the project: `mvn clean install`
   - Run the application: `mvn javafx:run`

The application will automatically connect to the database using the configuration in `src/main/resources/database.properties` and create necessary tables on first run.
`.

## User Guidelines

### Getting Started

1. **Launching the Application**
   - Double-click the application icon or run through command line using `mvn javafx:run`
   - The application will open to the main dashboard

2. **Understanding the Dashboard**
   - The dashboard provides quick statistics showing:
     * Total number of customers
     * Number of cars in service
     * Active jobs
     * Available garages
   - Recent activity feed shows latest system updates
   - Quick action buttons for common tasks

### Managing Customers

1. **Adding a New Customer**
   - Click "Customers" tab
   - Click "Add New Customer" button
   - Fill in required fields:
     * Forename
     * Surname
     * Address
     * Post Code
     * Phone Number
   - Click "Save" to create customer record

2. **Finding Customers**
   - Use the search field to find customers by surname
   - Customer view shows their average service costs to date

3. **Updating Customer Information**
   - Select customer from the list
   - Click "Edit" button
   - Modify necessary fields
   - Click "Save" to update

### Vehicle Management

1. **Registering a New Car**
   - Select customer first
   - Click "Add Car" button
   - Enter required details:
     * Registration Number
     * Make
     * Model
     * Year
     * Customer
   - Click "Save" to register car

2. **Viewing Car History**
   - Select customer from the list
   - View complete list of their cars
   - See total service days

### Service Jobs

1. **Creating a New Job**
   - Click "Add New Job" button
   - Select car(by registration number)
   - Choose garage location
   - Enter job details:
     * Date in
     * Date out(if applicable)
     * Cost(if agreed upon during booking)

2. **Managing Active Jobs**
   - View all active jobs in Jobs tab

3. **Completing a Job**
   - Select active job
   - Click "Complete" button
   - Process payment
   - System automatically updates date out upon payment.

### Tips and Best Practices

1. **Search Functionality**
   - Use partial surnames for broader search results
   - Registration numbers must be exact matches

2. **Data Management**
   - Regular updates of customer information
   - Verify car details when creating new jobs

3. **System Navigation**
   - Use quick action buttons for common tasks
   - Tab interface allows easy switching between functions
   - Dashboard provides system overview
   - Shortcuts are configured for quick access to main features.

### Troubleshooting

1. **Common Issues**
   - If search isn't working, check spelling and case
   - Ensure all required fields are filled in forms
   - Verify customer exists before adding cars

2. **Error Messages**
   - Read error messages carefully
   - Check input data if form submission fails
   - Contact system administrator for persistent issues

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
