
package com.linecorp.bot.officedirectory;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Response;
import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.officedirectory.model.Action;
import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.event.message.*;
import com.linecorp.bot.model.event.source.*;
import com.linecorp.bot.officedirectory.model.*;
import com.linecorp.bot.servlet.LineBotCallbackRequestParser;
import com.linecorp.bot.servlet.LineBotCallbackException;
import com.google.common.io.ByteStreams;

@RestController
@RequestMapping(value="/officedirectory")
public class CallbackController
{
    @Autowired
    EmployeeDao mDao;
    
    @Autowired
    @Qualifier("com.linecorp.channelSecret")
    String mChannelSecret;
    
    @Autowired
    @Qualifier("com.linecorp.channelToken")
    String mChannelToken;
    
    @Autowired
    LineMessagingService mLineMessagingService;
    
    @Autowired
    LineSignatureValidator mLineSignatureValidator;
    
    @RequestMapping(value="/test", method=RequestMethod.GET)
    public ResponseEntity<String> test()
    {
        System.out.println("channelSecret: " + mChannelSecret);
        System.out.println("channelToken: " + mChannelToken);
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value="/callback", method=RequestMethod.POST)
    public ResponseEntity<String> callback(
        @RequestHeader("X-Line-Signature") String aSignature,
        @RequestBody String aPayload)
    {
        LineBotCallbackRequestParser p=new LineBotCallbackRequestParser(mLineSignatureValidator);
        try
        {
            System.out.println("Payload: " + aPayload);
            CallbackRequest request=p.handle(aSignature, aPayload);
            System.out.println("event count: " + request.getEvents().size());
            Event e=request.getEvents().get(0);
            if(e instanceof MessageEvent)
            {
                MessageEvent me=(MessageEvent)(e);
                System.out.println("reply token: " + me.getReplyToken());

                // get source
                Source s=me.getSource();
                if(s instanceof UserSource)
                {
                    UserSource us=(UserSource)(s);
                    System.out.println("user id: " + s.getUserId());

                    // get content
                    MessageContent mc=me.getMessage();
                    if(mc instanceof TextMessageContent)
                    {
                        TextMessageContent tmc=(TextMessageContent)(mc);
                        System.out.println("Text: " + tmc.getText());

                        processText(me.getReplyToken(), us.getUserId(), tmc.getText());
                    }
                    else
                    {
                        System.out.println("Not TextMessageContent");
                    }
                }
                else
                {
                    System.out.println("Not UserSource");
                }


            }
            else
            {
                System.out.println("Not MessageEvent");
            }

        }
        catch(LineBotCallbackException e)
        {
            System.out.println("linebot callback exception raised: " + e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("i/o exception raised: " + e.getMessage());
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    
    
    private void processText(String aReplyToken, String aUserId, String aText)
    {
        System.out.println("message text: " + aText + " from: " + aUserId);
        
        UserValidator uv=new UserValidator(mDao);
        
        String [] words=aText.trim().split("\\s+");
        String intent=words[0];
        System.out.println("intent: " + intent);
        Message msg=null;
        if(intent.equalsIgnoreCase("find"))
        {
            String target=words.length>1 ? words[1] : "";
            msg=new FindProcessor(mDao).execute(aUserId, target);
        }
        else if(intent.equalsIgnoreCase("whoami"))
        {
            msg=new WhoamiProcessor(mDao).execute(aUserId);
        }
        else if(intent.equalsIgnoreCase("reg"))
        {
            String target=words.length>1 ? words[1] : "";
            msg=new RegProcessor(mDao).execute(aUserId, target);
        }
        else if(intent.equalsIgnoreCase("call"))
        {
            String target=words.length>1 ? words[1] : "";
            msg=new CallProcessor(mDao).execute(aUserId, target);
        }
    
        // if msg is invalid
        if(msg==null)
        {
            msg=new TextMessage("Invalid command.");
        }
        
        // reply
        ReplyMessage reply=new ReplyMessage(aReplyToken, msg);
        try
        {
            Response<BotApiResponse> response = mLineMessagingService.replyMessage(reply).execute();
            System.out.println("Code: " + response.code());
            System.out.println("Body: " + response.body());
        }
        catch(IOException e)
        {
            System.out.println("Failed to send message");
        }
    }
};
