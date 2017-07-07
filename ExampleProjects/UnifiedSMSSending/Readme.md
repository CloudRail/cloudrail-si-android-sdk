# Unified Email Sending Example
This is a simple app that demonstrates how you can use the CloudRail SI API to integrate different SMS sending services.
## Prerequisites
You need to have Android Studio ([installation manual](https://developer.android.com/studio/install.html)) installed and need developer credentials for the services you want to use. Instructions on how they can be acquired can be found on our [Unified SMS Sending API site](https://cloudrail.com/integrations/interfaces/SMS;serviceIds=Nexmo%2CTwilio%2CTwizo). You also need a CloudRail API key that you can [get fro free here](https://cloudrail.com/signup).

Find the following piece of code in your *MainActivity.java* file and replace the placeholders with your credentials:

```java
CloudRail.setAppKey("[Your CloudRail API key]");
twilio = new Twilio(this, "[Twilio API ID]", "[Twilio API Secret]");
twizo = new Twizo(this, "[SendGrid API Key]");
nexmo = new Nexmo(this, "[Nexmo API ID]", "[Nexmo API Secret]");
```
If you don't want to use a specific service or don't have credentials for it, you can comment it out.


## The App
The app consists of a single screen that lets users enter the sender's name, the receiving phone number and a message. Clicking on Send will send the SMS, using the Service that was chosen by the user.

Please be aware that the different services accept different inputs from sender and receiver. For Twizo, you can use phone numbers or names as sender. For the other services, you need phone numbers. Use international phone numbers.

![screenshot1](https://github.com/CloudRail/cloudrail.github.io/raw/master/img/android_demo_sms.png)