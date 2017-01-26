
package com.linecorp.bot.officedirectory;

import java.util.ArrayList;
import java.util.List;

import com.linecorp.bot.model.action.*;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.event.message.*;
import com.linecorp.bot.model.event.source.*;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.Template;
import com.linecorp.bot.officedirectory.model.*;
import static com.linecorp.bot.officedirectory.model.Action.*;

public class CallProcessor
{
    private EmployeeDao mEmployeeDao;
    
    private UserValidator mValidator;
    
	public CallProcessor(EmployeeDao aEmployeeDao)
    {
        mEmployeeDao=aEmployeeDao;
        mValidator=new UserValidator(aEmployeeDao);
    }
    
    public Message execute(String aUserId, String aEmployeeId)
    {
        String txt="";
        boolean valid=mValidator.validate(aUserId, CALL_OTHERS);
        if(valid)
        {
            System.out.println("CALL valid");
            Employee self=mEmployeeDao.getByEmployeeId(aEmployeeId);
            if(self!=null)
            {
                List<Action> actions=new ArrayList<>();
                if(self.mMobileNo!=null && self.mMobileNo.length()>0)
                {
                    System.out.println("mobile no: " + self.mMobileNo);
                    actions.add(new URIAction(self.mMobileNo, "tel:"+self.mMobileNo));
                }
                if(self.mOfficeNo!=null && self.mOfficeNo.length()>0)
                {
                    System.out.println("office no: " + self.mOfficeNo);
                    actions.add(new URIAction(self.mOfficeNo, "tel://"+self.mOfficeNo));
                }
                Template tmp=new ButtonsTemplate("http://static1.squarespace.com/static/54ca877ce4b014ea90e14bda/54ca9f5de4b021f8b6d68cc1/54caa17fe4b021f8b6d6c447/1422565759395/Sunset-3-by-2.jpg", self.mName, self.mEmployeeId, actions);
                return new TemplateMessage("Phone List", tmp);
            }
            else
            {
                txt="Employee not found.";
            }
        }
        else
        {
            System.out.println("CALL invalid");
            txt="You don't have sufficient permission.";
        }
        return new TextMessage(txt);
    }
    
    private String getEmployeeString(Employee aEmployee)
    {
        return String.format("%s\nEmployee ID: %s\nDept: %s\nLINE ID: %s\nMobile No: %s\nOffice No: %s\nOffice Floor: %d",
            aEmployee.mName, aEmployee.mEmployeeId, OfficeDept.toString(aEmployee.mOfficeDept),
            aEmployee.mLineId, aEmployee.mMobileNo, aEmployee.mOfficeNo, aEmployee.mOfficeFloor);
    }
};
