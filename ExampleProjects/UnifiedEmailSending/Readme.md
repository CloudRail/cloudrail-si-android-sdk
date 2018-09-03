# Unified Email Sending Example
This is a simple app that demonstrates how you can use the CloudRail SI API to integrate different Email sending services.
## Prerequisites
You need to have Android Studio ([installation manual](https://developer.android.com/studio/install.html)) installed and need developer credentials for the services you want to use. Instructions on how they can be acquired can be found on our [Unified Email Sending API site](https://cloudrail.com/integrations/interfaces/Email;serviceIds=SendGrid%2CMailJet). You also need a CloudRail API key that you can [get here](https://cloudrail.com/signup).

Find the following piece of code in your *MainActivity.java* file and replace the placeholders with your credentials:

```java
CloudRail.setAppKey("[Your CloudRail API key]");
mailJet = new MailJet(this, "[Public MailJet API Key]", "[Secret MailJet API Key]");
sendGrid = new SendGrid(this, "[SendGrid API Key]");
```
If you don't want to use a specific service or don't have credentials for it, you can comment it out.


## The App
The app consists of a single screen that lets users enter sending and receiving Email adresses, a subject and a message. Clicking on Send will send it to the specified recipients, using the Service that was chosen by the user.

When testing the app, please be aware that the mailing services may restrict the usable sender adresses to adresses connected to your account.

![screenshot1](https://github.com/CloudRail/cloudrail.github.io/raw/master/img/android_demo_email.png)
