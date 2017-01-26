
package com.linecorp.bot.officedirectory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Vector;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import com.linecorp.bot.officedirectory.model.Employee;
import com.linecorp.bot.officedirectory.model.Role;

public class EmployeeDaoImpl implements EmployeeDao
{
    private final static String SQL_SELECT_ALL="SELECT id, employee_id, name, line_id, mid, user_id, mobile_no, office_no, office_floor, office_dept, user_type FROM employee";
    private final static String SQL_GET_BY_NAME=SQL_SELECT_ALL + " WHERE LOWER(name) LIKE LOWER(?) ORDER BY name ASC LIMIT 5";
    private final static String SQL_GET_BY_EMPLOYEE_ID=SQL_SELECT_ALL + " WHERE employee_id = ?";
    private final static String SQL_GET_BY_LINE_ID=SQL_SELECT_ALL + " WHERE line_id = ?";
    private final static String SQL_GET_BY_MID=SQL_SELECT_ALL + " WHERE mid = ?";
    private final static String SQL_GET_BY_USER_ID=SQL_SELECT_ALL + " WHERE user_id = ?";
    private final static String SQL_GET_BY_OFFICE_FLOOR=SQL_SELECT_ALL + " WHERE office_floor = ?";
    private final static String SQL_GET_BY_OFFICE_DEPT=SQL_SELECT_ALL + " WHERE office_dept = ?";
    private final static String SQL_GET_BY_OFFICE_FLOOR_AND_DEPT=SQL_SELECT_ALL + " WHERE office_floor = ? AND office_dept = ?";
    private final static String SQL_GET_BY_USER_TYPE=SQL_SELECT_ALL + " WHERE user_type = ?";
    
    private final static String SQL_SELECT_USER_TYPE="SELECT user_type FROM employee";
    private final static String SQL_GET_USER_TYPE_BY_MID=SQL_SELECT_USER_TYPE + " WHERE mid = ?";
    private final static String SQL_GET_USER_TYPE_BY_USER_ID=SQL_SELECT_USER_TYPE + " WHERE user_id = ?";
    private final static String SQL_GET_USER_TYPE_BY_EMPLOYEE_ID=SQL_SELECT_USER_TYPE + " WHERE employee_id = ?";
    
    private final static String SQL_SELECT_USER_ID_BY_EMPLOYEE_ID="SELECT user_id FROM employee WHERE employee_id = ?";
    private final static String SQL_UPDATE_USER_ID_BY_EMPLOYEE_ID="UPDATE employee SET user_id = ? WHERE employee_id = ?";
    
    private JdbcTemplate mJdbc;
    
    private final static ResultSetExtractor<Employee> SINGLE_RS_EXTRACTOR=new ResultSetExtractor<Employee>()
    {
        @Override
        public Employee extractData(ResultSet aRs)
				throws SQLException, DataAccessException
        {
            while(aRs.next())
            {
                Employee e=new Employee(
                    aRs.getLong("id"),
                    aRs.getString("employee_id"),
                    aRs.getString("name"),
                    aRs.getString("line_id"),
                    aRs.getString("mid"),
                    aRs.getString("user_id"),
                    aRs.getString("mobile_no"),
                    aRs.getString("office_no"),
                    aRs.getShort("office_floor"),
                    aRs.getInt("office_dept"),
                    aRs.getShort("user_type"));
                return e;
            }
            return null;
        }
    };

    private final static ResultSetExtractor< List<Employee> > MULTIPLE_RS_EXTRACTOR=new ResultSetExtractor< List<Employee> >()
    {
        @Override
        public List<Employee> extractData(ResultSet aRs)
            throws SQLException, DataAccessException
        {
            List<Employee> list=new Vector<Employee>();
            while(aRs.next())
            {
                Employee e=new Employee(
                aRs.getLong("id"),
                aRs.getString("employee_id"),
                aRs.getString("name"),
                aRs.getString("line_id"),
                aRs.getString("mid"),
                aRs.getString("user_id"),
                aRs.getString("mobile_no"),
                aRs.getString("office_no"),
                aRs.getShort("office_floor"),
                aRs.getInt("office_dept"),
                aRs.getShort("user_type"));
                list.add(e);
            }
            return list;
        }
    };

    private final static ResultSetExtractor<Short> SINGLE_USER_TYPE_RS_EXTRACTOR=new ResultSetExtractor<Short>()
    {
        @Override
        public Short extractData(ResultSet aRs)
            throws SQLException, DataAccessException
        {
            while(aRs.next())
            {
                Short s=aRs.getShort("user_type");
                if(s==null || (s!=Role.SUPER_USER && s!=Role.USER))
                {
                    return Role.BANNED;
                }
                else return s;
            }
            return Role.UNREGISTERED;
        }
    };

    private final static ResultSetExtractor<String> SINGLE_USER_ID_RS_EXTRACTOR=new ResultSetExtractor<String>()
    {
        @Override
        public String extractData(ResultSet aRs)
            throws SQLException, DataAccessException
        {
            while(aRs.next())
            {
                String s=aRs.getString("user_id");
                if(s==null)
                {
                    return "";
                }
                else return s;
            }
            return "";
        }
    };

    public EmployeeDaoImpl(DataSource aDataSource)
    {
        mJdbc=new JdbcTemplate(aDataSource);
    }

    public Long post(Employee aEmployee)
    {
        SimpleJdbcInsert insert=new SimpleJdbcInsert(mJdbc)
            .withTableName("employee")
            .usingGeneratedKeyColumns("id");
        Map<String, Object> fields=new HashMap<String, Object>();
        fields.put("employee_id", aEmployee.mEmployeeId);
        fields.put("name", aEmployee.mName);
        fields.put("line_id", aEmployee.mLineId);
        fields.put("mid", aEmployee.mMid);
        fields.put("user_id", aEmployee.mUserId);
        fields.put("mobile_no", aEmployee.mMobileNo);
        fields.put("office_no", aEmployee.mOfficeNo);
        fields.put("office_floor", aEmployee.mOfficeFloor);
        fields.put("office_dept", aEmployee.mOfficeDept);
        fields.put("user_type", aEmployee.mUserType);
        return insert.executeAndReturnKey(fields).longValue();
    }

    public List<Employee> get()
    {
        return mJdbc.query(SQL_SELECT_ALL, MULTIPLE_RS_EXTRACTOR);
    }

    public List<Employee> getByName(String aName)
    {
        return mJdbc.query(SQL_GET_BY_NAME, new Object[]{aName}, MULTIPLE_RS_EXTRACTOR);
    }

    public Employee getByEmployeeId(String aEmployeeId)
    {
        return mJdbc.query(SQL_GET_BY_EMPLOYEE_ID, new Object[]{aEmployeeId}, SINGLE_RS_EXTRACTOR);
    }

    public Employee getByLineId(String aLineId)
    {
        return mJdbc.query(SQL_GET_BY_LINE_ID, new Object[]{aLineId}, SINGLE_RS_EXTRACTOR);
    }

    public Employee getByMid(String aMid)
    {
        return mJdbc.query(SQL_GET_BY_MID, new Object[]{aMid}, SINGLE_RS_EXTRACTOR);
    }

    public Employee getByUserId(String aUserId)
    {
        return mJdbc.query(SQL_GET_BY_USER_ID, new Object[]{aUserId}, SINGLE_RS_EXTRACTOR);
    }

    public List<Employee> getByOffice(Short aOfficeFloor, Integer aOfficeDept)
    {
        if(aOfficeFloor!=null && aOfficeDept!=null)
        {
            return mJdbc.query(SQL_GET_BY_OFFICE_FLOOR_AND_DEPT, new Object[]{aOfficeFloor, aOfficeDept}, MULTIPLE_RS_EXTRACTOR);
        }
        else if(aOfficeFloor!=null)
        {
            return mJdbc.query(SQL_GET_BY_OFFICE_FLOOR, new Object[]{aOfficeFloor}, MULTIPLE_RS_EXTRACTOR);
        }
        else if(aOfficeDept!=null)
        {
            return mJdbc.query(SQL_GET_BY_OFFICE_DEPT, new Object[]{aOfficeDept}, MULTIPLE_RS_EXTRACTOR);
        }
        else
        {
            return null;
        }
    }

    public List<Employee> getByUserType(Short aUserType)
    {
        return mJdbc.query(SQL_GET_BY_USER_TYPE, new Object[]{aUserType}, MULTIPLE_RS_EXTRACTOR);
    }

    public Short getUserTypeByMid(String aMid)
    {
        return mJdbc.query(SQL_GET_USER_TYPE_BY_MID, new Object[]{aMid}, SINGLE_USER_TYPE_RS_EXTRACTOR);
    }

    public Short getUserTypeByUserId(String aUserId)
    {
        return mJdbc.query(SQL_GET_USER_TYPE_BY_USER_ID, new Object[]{aUserId}, SINGLE_USER_TYPE_RS_EXTRACTOR);
    }

    public Short getUserTypeByEmployeeId(String aEmployeeId)
    {
        return mJdbc.query(SQL_GET_USER_TYPE_BY_EMPLOYEE_ID, new Object[]{aEmployeeId}, SINGLE_USER_TYPE_RS_EXTRACTOR);
    }

    public String getUserIdByEmployeeId(String aEmployeeId)
    {
        return mJdbc.query(SQL_SELECT_USER_ID_BY_EMPLOYEE_ID, new Object[]{aEmployeeId}, SINGLE_USER_ID_RS_EXTRACTOR);
    }

    public int updateByEmployeeId(String aUserId, String aEmployeeId)
    {
        return mJdbc.update(SQL_UPDATE_USER_ID_BY_EMPLOYEE_ID, new Object[]{aUserId, aEmployeeId});
    }
};
