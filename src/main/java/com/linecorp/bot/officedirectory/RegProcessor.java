package com.linecorp.bot.officedirectory;

import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.event.message.*;
import com.linecorp.bot.model.event.source.*;
import com.linecorp.bot.officedirectory.model.*;

public class RegProcessor
{
    private EmployeeDao mEmployeeDao;
    
    private UserValidator mValidator;
    
    public RegProcessor(EmployeeDao aEmployeeDao)
    {
        mEmployeeDao=aEmployeeDao;
        mValidator=new UserValidator(aEmployeeDao);
    }
    
    public Message execute(String aUserId, String aEmployeeId)
    {
        String txt="";
        
        String uid=mEmployeeDao.getUserIdByEmployeeId(aEmployeeId);
        if(uid.equals(""))
        {
            int update=mEmployeeDao.updateByEmployeeId(aUserId, aEmployeeId.toUpperCase());
            if(update==1)
            {
                txt=aEmployeeId + " registered successfully.";
            }
            else
            {
                txt="Registration process failed";
            }
        }
        else
        {
            txt=aEmployeeId + " is already registered";
        }
        
        Message msg=new TextMessage(txt);


        return new TextMessage(txt);
    }
}
