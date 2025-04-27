-- Create Garage table
CREATE TABLE Garage (
    garageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    garageName VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    town VARCHAR(100) NOT NULL,
    postCode VARCHAR(10) NOT NULL,
    phoneNo VARCHAR(20) NOT NULL
);

-- Create Customer table
CREATE TABLE Customer (
    customerId BIGINT AUTO_INCREMENT PRIMARY KEY,
    forename VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    postCode VARCHAR(10) NOT NULL,
    phoneNo VARCHAR(20) NOT NULL
);

-- Create Car table
CREATE TABLE Car (
    regNo VARCHAR(20) PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    customerId BIGINT NOT NULL,
    FOREIGN KEY (customerId) REFERENCES Customer(customerId) ON DELETE CASCADE
);

-- Create Job table
CREATE TABLE Job (
    jobId BIGINT AUTO_INCREMENT PRIMARY KEY,
    garageId BIGINT NOT NULL,
    dateIn DATETIME NOT NULL,
    dateOut DATETIME,
    regNo VARCHAR(20) NOT NULL,
    cost DECIMAL(10,2),
    FOREIGN KEY (garageId) REFERENCES Garage(garageId) ON DELETE RESTRICT,
    FOREIGN KEY (regNo) REFERENCES Car(regNo) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Activity (
    activityId BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    timestamp DATETIME NOT NULL,
    userId VARCHAR(100) NOT NULL,
    INDEX idx_timestamp (timestamp)
);
CREATE TABLE Payment (
    paymentId BIGINT PRIMARY KEY AUTO_INCREMENT,
    jobId BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    paymentDate DATETIME NOT NULL,
    paymentMethod VARCHAR(50) NOT NULL,
    paymentStatus VARCHAR(20) NOT NULL,
    FOREIGN KEY (jobId) REFERENCES Job(jobId) ON DELETE CASCADE
);


-- Insert Garages
INSERT INTO Garage (garageId, garageName, address, town, postCode, phoneNo) VALUES
(1, 'City Central Auto', '123 Main Street', 'London', 'SW1A 1AA', '020-7123-4567'),
(2, 'Express Motors', '456 High Road', 'Manchester', 'M1 1BB', '0161-765-4321'),
(3, 'Quality Car Care', '789 Station Road', 'Birmingham', 'B1 1CC', '0121-987-6543');

-- Insert Customers
INSERT INTO Customer (customerId, forename, surname, address, postCode, phoneNo) VALUES
(1, 'John', 'Smith', '10 Oak Avenue', 'NW1 6XE', '07700-900123'),
(2, 'Sarah', 'Johnson', '25 Elm Street', 'M4 5BY', '07700-900124'),
(3, 'Michael', 'Brown', '8 Pine Road', 'B15 2TT', '07700-900125'),
(4, 'Emily', 'Wilson', '15 Maple Lane', 'SW4 7DE', '07700-900126'),
(5, 'David', 'Taylor', '32 Birch Close', 'M20 1QQ', '07700-900127');

-- Insert Cars
INSERT INTO Car (regNo, make, model, year, customerId) VALUES
('AB12 CDE', 'Toyota', 'Corolla', 2020, 1),
('XY65 FGH', 'Ford', 'Focus', 2019, 1),
('BC23 IJK', 'BMW', '320i', 2021, 2),
('DE34 LMN', 'Volkswagen', 'Golf', 2018, 3),
('FG45 OPQ', 'Audi', 'A4', 2022, 4),
('HI56 RST', 'Mercedes', 'C-Class', 2020, 5),
('JK67 UVW', 'Honda', 'Civic', 2019, 3);

-- Insert Jobs (Including completed and ongoing jobs)
-- Completed jobs (with payment required)
INSERT INTO Job (jobId, garageId, dateIn, dateOut, regNo, cost) VALUES
(1, 1, '2025-03-30 09:00:00', '2025-04-01 16:30:00', 'AB12 CDE', 450.00),
(2, 2, '2025-03-29 10:15:00', '2025-04-01 14:45:00', 'BC23 IJK', 275.50);

-- Jobs completed earlier
INSERT INTO Job (jobId, garageId, dateIn, dateOut, regNo, cost) VALUES
(3, 1, '2025-03-15 08:30:00', '2025-03-16 17:00:00', 'XY65 FGH', 150.75),
(4, 3, '2025-03-20 11:00:00', '2025-03-22 12:30:00', 'DE34 LMN', 890.25),
(5, 2, '2025-03-25 09:45:00', '2025-03-26 16:15:00', 'FG45 OPQ', 325.00);

-- Ongoing jobs (no dateOut or cost yet)
INSERT INTO Job (jobId, garageId, dateIn, dateOut, regNo, cost) VALUES
(6, 1, '2025-04-01 08:00:00', NULL, 'HI56 RST', NULL),
(7, 3, '2025-03-31 14:30:00', NULL, 'JK67 UVW', NULL);

-- Insert Activities
INSERT INTO Activity (type, action, description, timestamp, userId) VALUES
('JOB', 'CREATE', 'New job created for car: AB12 CDE', '2025-03-30 09:00:00', 'BougaStefa'),
('JOB', 'UPDATE', 'Job completed for car: AB12 CDE', '2025-04-01 16:30:00', 'BougaStefa'),
('JOB', 'CREATE', 'New job created for car: BC23 IJK', '2025-03-29 10:15:00', 'BougaStefa'),
('JOB', 'UPDATE', 'Job completed for car: BC23 IJK', '2025-04-01 14:45:00', 'BougaStefa'),
('CAR', 'CREATE', 'New car registered: HI56 RST', '2025-03-15 10:00:00', 'BougaStefa'),
('CUSTOMER', 'CREATE', 'New customer added: John Smith', '2025-03-01 11:30:00', 'BougaStefa');
-- Insert some sample payments
INSERT INTO Payment (jobId, amount, paymentDate, paymentMethod, paymentStatus) VALUES
(3, 150.75, '2025-03-16 17:00:00', 'CARD', 'PAID'),
(4, 890.25, '2025-03-22 12:30:00', 'TRANSFER', 'PAID'),
(5, 325.00, '2025-03-26 16:15:00', 'CASH', 'PAID');
-- Add indexes for better performance
CREATE INDEX idx_customer_surname ON Customer(surname);
CREATE INDEX idx_car_customer ON Car(customerId);
CREATE INDEX idx_job_garage ON Job(garageId);
CREATE INDEX idx_job_car ON Job(regNo);
