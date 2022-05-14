package account.model;

import account.validation.CorporateEmail;
import account.validation.PeriodValidation;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;

public class Payment {
  private int id;

  @CorporateEmail
  @JsonProperty("employee")
  private String employeeEmail;

  @PeriodValidation
  private String period;



  @Min(0)
  private int salary;



  public Payment() {
  }
  

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getEmployeeEmail() {
    return employeeEmail;
  }

  public void setEmployeeEmail(String employeeEmail) {
    this.employeeEmail = employeeEmail;
  }


  public String getPeriod() {
    return period;
  }

  public void setPeriod(String period) {
    this.period = period;
  }

  public int getSalary() {
    return salary;
  }

  public void setSalary(int salary) {
    this.salary = salary;
  }


  @Override
  public String toString() {
    return "Payment{" +
        "employeeEmail='" + employeeEmail + '\'' +
        ", period='" + period + '\'' +
        ", salary=" + salary +
        '}';
  }
}
