
package com.linecorp.bot.officedirectory.model;

public class Employee
{
    public Long mId;
    public String mEmployeeId;
    public String mName;
    public String mLineId;
    public String mMid;
    public String mUserId;
    public String mMobileNo;
    public String mOfficeNo;
    public Short mOfficeFloor;
    public Integer mOfficeDept;
    public Short mUserType;
    
    public Employee(Long aId, String aEmployeeId, String aName, String aLineId, String aMid,
                    String aUserId, String aMobileNo, String aOfficeNo, Short aOfficeFloor,
                    Integer aOfficeDept, Short aUserType)
    {
        mId=aId;
        mEmployeeId=aEmployeeId;
        mName=aName;
        mLineId=aLineId;
        mMid=aMid;
        mUserId=aUserId;
        mMobileNo=aMobileNo;
        mOfficeNo=aOfficeNo;
        mOfficeFloor=aOfficeFloor;
        mOfficeDept=aOfficeDept;
        mUserType=aUserType;
    }
};
