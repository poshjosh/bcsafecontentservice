package com.bc.safecontent;

import com.bc.safecontent.service.ContentFlaggingService;
import com.bc.safecontent.service.ContentImpl;

/**
 * @author USER
 */
public class ReadMe {
    
    public static void main(String... args) {
        
        final ContentFlaggingService svc = ContentFlaggingService.defaultInstance();
        
        final String imageurl = "http://www.buzzwears.com/local/images/fashion/2018/03/2_16257b445c0.jpg";
        
        final String [] somecontent = {
            "This is some good content which we hope will not be flagged because I have a pussy cat",
            "Please don't flag me because I use the word sexist to describe him",
            "He is a fucking pervert and a loser. Will you flag me?",
            "He fingered the perpetrator. I hope you don't flag me",
            "Blood and gore is not the name of any game I know",
            "Bloodied limbs was scattered everywhere after the explosion"
        };
        
        final long timeout = 15_000;
        
        final String flags = svc.flag(new ContentImpl(imageurl, somecontent), timeout);
        
        System.out.println(flags);
        
        // Standard flags currently include: 
        // ADULT, VIOLENCE, RACY, MEDICAL, SPOOF, GRAPHIC
    }
}
