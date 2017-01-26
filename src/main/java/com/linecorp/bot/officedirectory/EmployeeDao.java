
package com.linecorp.bot.officedirectory;

import java.util.List;
import com.linecorp.bot.officedirectory.model.Employee;

public interface EmployeeDao
{
    public Long post(Employee aEmployee);
    public List<Employee> get();
    public List<Employee> getByName(String aName);
    public Employee getByEmployeeId(String aEmployeeId);
    public Employee getByLineId(String aLineId);
    public Employee getByMid(String aMid);
    public Employee getByUserId(String aUserId);
    public List<Employee> getByOffice(Short mOfficeFloor, Integer mOfficeDept);
    public List<Employee> getByUserType(Short aUserType);
    
    // specific
    public Short getUserTypeByMid(String aMid);
    public Short getUserTypeByUserId(String aUserId);
    public Short getUserTypeByEmployeeId(String aEmployeeId);
    public String getUserIdByEmployeeId(String aEmployeeId);
    public int updateByEmployeeId(String aUserId, String aEmployeeId);
};
