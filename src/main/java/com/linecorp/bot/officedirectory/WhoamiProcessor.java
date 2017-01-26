
package com.linecorp.bot.officedirectory;

import java.util.List;
import java.util.ArrayList;
import com.linecorp.bot.model.action.*;
import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.event.message.*;
import com.linecorp.bot.model.message.template.*;
import com.linecorp.bot.model.event.source.*;
import static com.linecorp.bot.officedirectory.model.Action.*;
import com.linecorp.bot.officedirectory.model.Employee;
import com.linecorp.bot.officedirectory.model.OfficeDept;

public class WhoamiProcessor
{
    private final static String pictureURL = "https://res.cloudinary.com/jedidiahwahana/image/upload/v1481606864/sample.jpg";
    
    private EmployeeDao mEmployeeDao;
    
    private UserValidator mValidator;
    
	public WhoamiProcessor(EmployeeDao aEmployeeDao)
    {
        mEmployeeDao=aEmployeeDao;
        mValidator=new UserValidator(aEmployeeDao);
    }
    
    public Message execute(String aUserId)
    {
        String txt="";
        boolean valid=mValidator.validate(aUserId, READ_SELF);
        if(valid)
        {
            System.out.println("WHOAMI valid");
            Employee emp=mEmployeeDao.getByUserId(aUserId);
            if(emp!=null)
            {
//                txt=getEmployeeString(emp);
                TemplateMessage tMessage = makeButtonsTemplate(emp);
                return tMessage;
            }
            else
            {
                txt="You are not registered. Please register by typing \"REG <emplpoyee id>\".\nExample: \"REG LW10315\"";
                return new TextMessage(txt);
            }
        }
        else
        {
            System.out.println("WHOAMI invalid");
            txt="You don't have sufficient permission.";
            return new TextMessage(txt);
        }
    }
    
    private String getEmployeeString(Employee aEmployee)
    {
        return String.format("%s\nEmployee ID: %s\nDept: %s\nLINE ID: %s\nMobile No: %s\nOffice No: %s\nOffice Floor: %d",
            aEmployee.mName, aEmployee.mEmployeeId, OfficeDept.toString(aEmployee.mOfficeDept),
            aEmployee.mLineId, aEmployee.mMobileNo, aEmployee.mOfficeNo, aEmployee.mOfficeFloor);
    }
    
    private TemplateMessage makeButtonsTemplate(Employee data)
    {
        List<Action> bList = new ArrayList<Action>();
        if(data.mLineId != null && data.mLineId.length() > 0)
        {
            bList.add(new URIAction(data.mLineId, "http://line.me/ti/p/~"+data.mLineId));
        }
            
        if(data.mOfficeNo != null && data.mOfficeNo.length() > 0)
        {
            bList.add(new URIAction(data.mOfficeNo, "tel:"+data.mOfficeNo));
        }
            
        if(data.mMobileNo != null && data.mMobileNo.length() > 0)
        {
            bList.add(new URIAction(data.mMobileNo, "tel:"+data.mMobileNo));
        }
        String subtext = data.mEmployeeId + "\n" + OfficeDept.toString(data.mOfficeDept);
        ButtonsTemplate buttonsTemplate = new ButtonsTemplate(pictureURL, data.mName, subtext, bList);
        TemplateMessage templateMessage = new TemplateMessage("Your search result", buttonsTemplate);
        return templateMessage;
    }
};
