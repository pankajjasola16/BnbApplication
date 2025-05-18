package com.airbnb.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.io.File;

@Service
public class EmailService {

    @Autowired
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailWithAttachment(String to, String subject, String text, File attachment) throws MessagingException {
        // Create a MimeMessage
        MimeMessage message = mailSender.createMimeMessage();

        // Use MimeMessageHelper for handling attachments
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
//        helper.setFrom("your-email@gmail.com");

        // Attach the file
        FileSystemResource fileResource = new FileSystemResource(attachment);
        helper.addAttachment(fileResource.getFilename(), fileResource);

        // Send the email
        mailSender.send(message);

//        return "Email sent successfully!";
    }
}

//                    document.open()

//            1 --> Prepare the Document for Content Addition:

//Before adding content (like the table in your case), the Document must be explicitly opened using the
// open() method. Without this, the Document remains in a closed state, and any attempt to add content will
// result in errors.

//            2 --> Initialize PDF Metadata and Structure:

//When the document.open() method is called, it initializes the document's internal structures for accepting
// content and sets up the document's metadata (like headers or placeholders for future content).


//            3 --> If document.open() Is Missing:

//        The PDF file will not be properly created, and attempts to add content (like the table) will throw exceptions.
//        The email may be sent successfully, but the PDF attachment will be either missing or corrupted.
//        By calling document.open();, the program ensures that the document is ready to accept
//        content and that a proper PDF file is generated.
