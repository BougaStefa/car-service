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
    FOREIGN KEY (customerId) REFERENCES Customer(customerId)
);

-- Create Job table
CREATE TABLE Job (
    jobId BIGINT AUTO_INCREMENT PRIMARY KEY,
    garageId BIGINT NOT NULL,
    dateIn DATETIME NOT NULL,
    dateOut DATETIME,
    regNo VARCHAR(20) NOT NULL,
    cost DECIMAL(10,2),
    FOREIGN KEY (garageId) REFERENCES Garage(garageId),
    FOREIGN KEY (regNo) REFERENCES Car(regNo)
);

-- Add indexes for better performance
CREATE INDEX idx_customer_surname ON Customer(surname);
CREATE INDEX idx_car_customer ON Car(customerId);
CREATE INDEX idx_job_garage ON Job(garageId);
CREATE INDEX idx_job_car ON Job(regNo);
