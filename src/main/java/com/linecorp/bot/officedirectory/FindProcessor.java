
package com.linecorp.bot.officedirectory;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.linecorp.bot.model.action.*;
import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.message.template.*;
import com.linecorp.bot.model.event.message.*;
import com.linecorp.bot.model.event.source.*;
import static com.linecorp.bot.officedirectory.model.Action.*;
import com.linecorp.bot.officedirectory.model.Employee;
import com.linecorp.bot.officedirectory.model.OfficeDept;

public class FindProcessor
{
    private final static String pictureURL = "https://res.cloudinary.com/jedidiahwahana/image/upload/v1481606864/sample.jpg";
    
    private EmployeeDao mEmployeeDao;
    
    private UserValidator mValidator;
    
	public FindProcessor(EmployeeDao aEmployeeDao)
    {
        mEmployeeDao=aEmployeeDao;
        mValidator=new UserValidator(aEmployeeDao);
    }
    
    public Message execute(String aUserId, String aTarget)
    {
        String txt="";
        
        if(aTarget.length()<3)
        {
            return new TextMessage("Need more than 3 characters to find person.");
        }
        
        boolean valid=mValidator.validate(aUserId, READ_OTHERS);
        if(valid)
        {
            System.out.println("FIND valid");
            List<Employee> self=mEmployeeDao.getByName("%"+aTarget+"%");
            if(self.size() > 0)
            {
                TemplateMessage tMessage = makeCarouselTemplate(self.size(), self);
                return tMessage;
            }
            else
            {
                txt="Employee not found.";
                return new TextMessage(txt);
            }
        }
        else
        {
            System.out.println("FIND invalid");
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
    
    private TemplateMessage makeCarouselTemplate(int carouselQ, List<Employee> data)
    {
        CarouselColumn[] carouselColumn = new CarouselColumn[carouselQ];
        for (int i = 0; i < carouselQ; i++)
        {
            Employee emp = data.get(i);
            List<Action> caList = new ArrayList<Action>();
            if(emp.mLineId != null && emp.mLineId.length() > 0)
            {
                caList.add(new URIAction(emp.mLineId, "http://line.me/ti/p/~"+emp.mLineId));
            }
            else
            {
                caList.add(new PostbackAction("N/A", "{\"clicked\":\"line_id\"}"));            }
            
            if(emp.mOfficeNo != null && emp.mOfficeNo.length() > 0)
            {
                caList.add(new URIAction(emp.mOfficeNo, "tel:"+emp.mOfficeNo));
            }
            else
            {
                caList.add(new PostbackAction("N/A", "{\"clicked\":\"office_no\"}"));
            }

            if(emp.mMobileNo != null && emp.mMobileNo.length() > 0)
            {
                caList.add(new URIAction(emp.mMobileNo, "tel:"+emp.mMobileNo));
            }
            else
            {
                caList.add(new PostbackAction("N/A", "{\"clicked\":\"mobile_no\"}"));
            }
            
            String subtext = emp.mEmployeeId + "\n" + OfficeDept.toString(emp.mOfficeDept);
            carouselColumn[i] = new CarouselColumn(pictureURL, emp.mName, subtext, caList);
        }
        List ccList = Arrays.asList(carouselColumn);
        CarouselTemplate carouselTemplate = new CarouselTemplate(ccList);
        TemplateMessage templateMessage = new TemplateMessage("Your search result", carouselTemplate);
        return templateMessage;
    }
};
