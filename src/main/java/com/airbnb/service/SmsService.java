package com.airbnb.service;

import com.airbnb.config.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

// Logger --> see details at last
@Service
public class SmsService {

    // below logger output will come on console --> but it should come on a FILE

    // below logger is --> SLF4J --> which is bydefault --> So it is like a diary
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

//    private final TwilioConfig twilioConfig;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Value("${twilio.whatsapp.phone.number}")
    private String twilioWhatsAppNumber;  // For WhatsApp


//    @Autowired
//    public SmsService(TwilioConfig twilioConfig) {
//        this.twilioConfig = twilioConfig;
//    }

    public void sendSms(String to, String messageBody) {
        // if SMS is sent --> below code will work but if SMS is not sent it will crash
        // so put it inside --> try catch block
        try {
            // below code --> everytime there will not be an error --> there may be some message
            // so below we are giving some message --> like --> SMS sending started, sms sending ended
            // How are you calculating the SMS sending time means when a particular SMS is
            // delivered --> so use --> +new Date()
            logger.info("Starting to sent SMS - "+new Date());
            Message message = Message.creator(
                            new PhoneNumber(to), // Receiver's phone number
                            new PhoneNumber(twilioPhoneNumber), // Twilio phone number
                            messageBody) // Message content
                    .create();

            logger.info("sent SMS - "+new Date());
            // so above it is generating a log --> so above top log is giving SMS Sending time
            // and just above log is giving --> SMS delivered time
            // and if any exception occures in the SMS --> that will will be captured as a --> logger.error

//            return message.getSid(); // Return the SID of the sent message
        } catch (Exception e){
            logger.error(e.getMessage());

            // Log the exception details
//            logger.error("Error sending SMS to {}: {}", to, e.getMessage(), e); // Log the full exception

            // logger.error(e.getMessage()); -->  what logger will do --> if while setting sms
            // something went wrong (mobile number entered is invalid number), exception occur
            // then logger.error will run and that will capture this message --> e.getMessage(); -->
            // and it will put that into a logger

//            e.printStackTrace();
            // in production development we do not have --> console so we can not use --> e.printStackTrace(); --> so
            // we will use --> Logs --> so search --> Logs in Spring Boot --> ex --> SL4J (it comes by default)
            // Logger --> is like a diary (so its like a complain capturing mechanism) --> if other
            // developer will come he can see logs and understand the project
            // we can use it for reminder or some mistake
        }
    }

    // for whatsap we need to pay for services

    // Send WhatsApp message
    public void sendWhatsApp(String to, String messageBody) {
        try {
            logger.info("Starting to send WhatsApp message - " + new Date());
            // The 'whatsapp:' prefix is required for WhatsApp messages in Twilio
//            Message message = Message.creator(
//                            new PhoneNumber("whatsapp:" + to), // WhatsApp recipient number
//                            new PhoneNumber("whatsapp:" + twilioPhoneNumber), // Twilio WhatsApp number
//                            messageBody) // Message content
//                    .create();

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + to),
                    new PhoneNumber("whatsapp:" + twilioWhatsAppNumber), // Corrected
                    messageBody
            ).create();

            logger.info("Sent WhatsApp message - " + new Date());
        } catch (Exception e) {
            logger.error("Error sending WhatsApp message: " + e.getMessage());
        }
    }
}


// Logger --> This is very beneficial --> becuase when you deploy your application in server -->
// it is a loggers tha will track --> what is happening in every line

// the logger which we have implemented --> will still print the information in console
// But actually this SL4J --> Should generate a file --> a log file --> and that log file
// will have that information
// so periodically it will keep generating this log file --> whenever any crash happens -->
// it generate a new log file --> when you open the log file --> instead of the information
// be in the console --> it will be permanently in the file

                        // Console Output

// 2024-11-26T21:25:53.354+05:30  INFO 12364 --- [nio-8080-exec-8] com.airbnb.service.SmsService            : Starting to sent SMS - Tue Nov 26 21:25:53 IST 2024
// 2024-11-26T21:25:57.118+05:30 ERROR 12364 --- [nio-8080-exec-8] com.airbnb.service.SmsService            : 'To' number cannot be a Short Code: +9188503XXXX

// so above we can see there is an error --> ERROR --> because we have given wrong number -->
// in JSON --> only 9 digit number --> but if we will give right number --> output will be:

// 21:30:59.104+05:30  INFO 12364 --- [nio-8080-exec-1] com.airbnb.service.SmsService            : Starting to sent SMS - Tue Nov 26 21:30:59 IST 2024
// 2024-11-26T21:30:59.886+05:30  INFO 12364 --- [nio-8080-exec-1] com.airbnb.service.SmsService            : sent SMS - Tue Nov 26 21:30:59 IST 2024

// now there is no error

// but if error is coming --> SMS is not going to customer --> Customer can see email --> if
// both are not coming --> then customer will call customer care --> and he can send mannually

// INFO  only when sms has started
// ERROR  When something goes wrong

// after applying log --> we will see all the exception message in --> log --> not in
// console

// SL4J --> is like an ADMIN --> and its diary is --> logGER --> here all the exceptions are stored
// DIARY IS -->  private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

// Loggers file are inside  C Drive  bnb  logs



