## bcsafecontentservice

#### API for checking the safety of content (links and text) vis-a-vis violence, adult, profanity etc

### Usage

Imports

```java
import com.bc.safecontent.service.SafeContentService;
import com.bc.safecontent.service.SafeContentServiceImpl;
import java.io.File;
```

Create the service instance

```java
        
        final File cacheDir = null; // Provide value
        final int maxCacheSizeBytes = 10_000_000; // Provide value        
        
        final SafeContentService svc = new SafeContentServiceImpl(cacheDir, maxCacheSizeBytes);  
```

Use the created service instance to flag content

```java
        
        final String imageurl = "http://www.buzzwears.com/local/images/fashion/2018/03/2_16257b445c0.jpg";
        
        final String [] somecontent = {
            "This is some good content which we hope will not be flagged because I have a pussy cat",
            "Please don't flag me because I use the word sexist to describe him",
            "He is a fucking pervert and a loser",
            "He fingered the perpetrator",
            "His dick in her pussy",
            "Blood and gore is not the name of any game I know",
            "Bloodied limbs was scattered everywhere after the explosion"
        };
        
        final String flags = svc.requestFlags(imageurl, somecontent);
        
        System.out.println(flags);
```

### Flags

- Standard flags currently include: ADULT, VIOLENCE, RACY, MEDICAL, SPOOF, GRAPHIC

- For complete list of standard flags see the:

[StandardFlags interface](../master/src/main/java/com/bc/safecontent/StandardFlags.java)

### Configuration

- For this api to work you need to save googlecloud application credentials in the following file on your classpath:

```
    META-INF/secure/com.bc.safecontent.googlecloud.application_credentials.properties
```

In the above file there should be a property named ```api_key``` with the value being the api key obtained as described

[here](https://cloud.google.com/docs/authentication/production#obtaining_and_providing_service_account_credentials_manually "Google cloud api key - obtaining and providing service account credentials manually")


