
package com.linecorp.bot.officedirectory;

import com.linecorp.bot.officedirectory.model.Action;
import com.linecorp.bot.officedirectory.model.Role;

public class UserValidator
{
    private EmployeeDao mDao;
    
    public UserValidator(EmployeeDao aDao)
    {
        mDao=aDao;
    }
    
    public boolean validate(String aUserId, Short aAction)
    {
        Short role=mDao.getUserTypeByUserId(aUserId);
        
        if(role==Role.SUPER_USER)
        {
            return true;
        }
        else if(role==Role.USER)
        {
            if(aAction==Action.READ_SELF)
            {
                return true;
            }
            else if(aAction==Action.WRITE_SELF)
            {
                return true;
            }
            else if(aAction==Action.READ_OTHERS)
            {
                return true;
            }
            else if(aAction==Action.CALL_OTHERS)
            {
                return true;
            }
            else return false;
        }
        else
        {
            return false;
        }
    }
};
