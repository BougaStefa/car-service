package com.bougastefa.app;

public class InputValidation {
  public boolean isNullOrEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }

  // Garage Table Validators
  public boolean isValidGarageId(String garageId) {
    return isValidGarageIdFormat(garageId) && isUniquePrimaryKey("placeholder");
  }

  public boolean isValidGarageIdFormat(String garageId) {
    // GarageID format is GAdddd where d is numbers. Can be a total of 50 characters long
    return !isNullOrEmpty(garageId) && garageId.matches("^GA\\d{1,48}$");
  }

  public boolean isValidGarageNameFormat(String garageName) {
    // Garage Names can contain alphanumerical characters, spaces, dots and dashes up to 100
    // characters.
    return !isNullOrEmpty(garageName) && garageName.matches("^[A-Za-z0-9\\s\\.\\-]{2,100}$");
  }

  // Job Table Validators
  public boolean isValidJobId(String jobId) {
    return isUniquePrimaryKey(jobId) && isValidJobIdFormat(jobId);
  }

  public boolean isValidJobIdFormat(String jobId) {
    return !isNullOrEmpty(jobId) && jobId.matches("JO\\d{1,48}$");
  }

  public boolean isValidDateFormat(String date) {
    // Date format of YYYY-MM-DD
    return !isNullOrEmpty(date)
        && date.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
  }

  public boolean isValidCostFormat(String cost) {
    // Cost with 2 decimal accuracy
    return !isValidJobIdFormat(cost) && cost.matches("^\\d+\\.\\d{2}$");
  }

  // Customer Table Validators
  public boolean isValidCustomerId(String customerId) {
    return isUniquePrimaryKey(customerId) && isValidCustomerIdFormat(customerId);
  }

  public boolean isValidCustomerIdFormat(String customerId) {
    // Customer ID stores as CU followed by numbers
    return !isNullOrEmpty(customerId) && customerId.matches("CU\\d{1,48}");
  }

  public boolean isValidCustomerNameFormat(String customerName) {
    // Customer names can only be letters with optional apostrophe, hyphens and spaces with optional
    // letters after
    return !isNullOrEmpty(customerName) && customerName.matches("^[A-Za-z]+([ '-][A-Za-z]+)*$");
  }

  // Car Table Validators
  public boolean isValidRegNo(String regNo) {
    return isUniquePrimaryKey(regNo) && isValidRegNoFormat(regNo);
  }

  public boolean isValidRegNoFormat(String regNo) {
    // UK Regno format, AA11 AAA or older format of AA11 AAA A
    return !isNullOrEmpty(regNo) && regNo.matches("^[A-Z]{2}\\d{2}\\s?[A-Z]{3}(\\s?[A-Z])?$");
  }

  public boolean isValidMakeFormat(String make) {
    // Car make has to be one or more words with optional spaces or hyphens but not numbers
    return !isNullOrEmpty(make) && make.matches("^[A-Za-z-]+(?: [A-Za-z-]+)*$");
  }

  public boolean isValidModelFormat(String model) {
    // Model can be one or more words with allowing hyphens and spaces and numbers
    return !isNullOrEmpty(model) && model.matches("^[A-Za-z0-9-]+(?: [A-Za-z0-9-]+)*$");
  }

  public boolean isValidYearFormat(String year) {
    // Year in the 20th or 21st century, 4 digits long
    return !isNullOrEmpty(year) && year.matches("^(19|20)\\d{2}");
  }

  // Other Validators
  public boolean isValidAddressFormat(String address) {
    // Address starts with numbers followed by a space and then any alphanumerical, comma, space,
    // dot or dash character, up to 100
    return !isNullOrEmpty(address) && address.matches("^\\d+\\s[A-Za-z0-9\\s,.-]{2,100}$");
  }

  public boolean isValidTownFormat(String town) {
    // Towns can be any character, with spaces, dots or dashes up to 50 characters.
    return !isNullOrEmpty(town) && town.matches("^[a-zA-Z\\s\\.-]{2,50}$");
  }

  public boolean isValidPostcodeFormat(String postcode) {
    // Postcodes have to match ANY UK format like AA11 1PA or A1A 1AA etc.
    return !isNullOrEmpty(postcode) && postcode.matches("^[A-Z]{1,2}\\d[A-Z\\d]?\\s?\\d[A-Z]{2}$");
  }

  public boolean isValidPhoneNumberFormat(String phoneNumber) {
    // Phone numbers have to match the UK format, with or without international code, landline,
    // London landline or mobile. Also checks for the spacing(optional)
    return !isNullOrEmpty(phoneNumber)
        && phoneNumber.matches(
            "^(\\+44\\s?7\\d{3}|\\(?0\\d{4}\\)?\\s?\\d{3}\\s?\\d{3}|\\(?0\\d{5}\\)?\\s?\\d{6}|\\(?0\\d{3}\\)?\\s?\\d{4}\\s?\\d{3})$");
  }

  public boolean isUniquePrimaryKey(String key) {
    return true;
  }
}
