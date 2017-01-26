
package com.linecorp.bot.officedirectory.model;

public final class OfficeDept
{
    public final static int ENGINEERING=1;
    public final static int DESIGN=2;
    public final static int ECOMMERCE=3;
    public final static int MARKETING=4;
    public final static int PR=5;
    public final static int GAMES=6;
    public final static int HR=7;
    public final static int NEWS=8;
    public final static int WEBTOON=9;
    public final static int BD=10;
    public final static int JOBPORTAL=11;
    
    public static String toString(Integer aOfficeDept)
    {
        if(aOfficeDept==null)
        {
            return "";
        }
        
        switch(aOfficeDept)
        {
            case ENGINEERING: return "Engineering";
            case DESIGN: return "Design";
            case ECOMMERCE: return "E-Commerce";
            case MARKETING: return "Marketing";
            case PR: return "Public Relations";
            case GAMES: return "Games";
            case HR: return "Human Resources";
            case NEWS: return "News";
            case WEBTOON: return "Webtoon";
            case BD: return "Business Development";
            case JOBPORTAL: return "Job Portal";
            default: return "";
        }
    }
};
