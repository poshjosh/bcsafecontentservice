package com.bc.safecontent;

import com.bc.safecontent.service.SafeContentService;
import com.bc.safecontent.service.SafeContentServiceImpl;

/**
 * @author USER
 */
public class ReadMe {
    
    public static void main(String... args) {
        
        final SafeContentService svc = new SafeContentServiceImpl();  
        
        final String imageurl = "http://www.buzzwears.com/local/images/fashion/2018/03/2_16257b445c0.jpg";
        
        final String [] somecontent = {
            "This is some good content which we hope will not be flagged because I have a pussy cat",
            "Please don't flag me because I use the word sexist to describe him",
            "He is a fucking pervert and a loser. Will you flag me?",
            "He fingered the perpetrator. I hope you don't flag me",
            "Blood and gore is not the name of any game I know",
            "Bloodied limbs was scattered everywhere after the explosion"
        };
        
        final String flags = svc.requestFlags(imageurl, somecontent);
        
        System.out.println(flags);
        
        // Standard flags currently include: 
        // ADULT, VIOLENCE, RACY, MEDICAL, SPOOF, GRAPHIC
    }
}
