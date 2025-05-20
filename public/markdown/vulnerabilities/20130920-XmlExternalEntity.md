## XML External Entities

### Date

20 Sep 2013

### Description

A vulnerability has been found in Play's XML processing.

An attacker may use XML external entities to read files from the file system, internal network, or DoS the application.

### Impact

Any application that uses either the default any content parsers, or specifically the XML parser, may be vulnerable.

### Affected Versions

* Play 2.1.0 - 2.1.4
* Play 2.0 - 2.0.7

### Workarounds

Change the default `SAXParserFactory` implementation used by the JDK to be one that disables external entities.

For example, if using the Oracle JDK, add the following class to your application:

```java
package xml;

import org.xml.sax.*;
import javax.xml.parsers.*;

public class SecureSAXParserFactory extends SAXParserFactory {
    private final SAXParserFactory platformDefault = new com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl();

    public SecureSAXParserFactory() throws SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
        platformDefault.setFeature("http://xml.org/sax/features/external-general-entities", false);
        platformDefault.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        platformDefault.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    }

    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        return platformDefault.newSAXParser();
    }

    public void setFeature(String name, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        platformDefault.setFeature(name, value);
    }

    public boolean getFeature(String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        return platformDefault.getFeature(name);
    }
}
```

And then when starting the application in production mode, add the following system property to the command line arguments:

    -Djavax.xml.parsers.SAXParserFactory=xml.SecureSAXParserFactory

### Fixes

Upgrade to the appropriate version below:

* [Play 2.1.5](https://github.com/playframework/playframework/releases/download/2.1.5/play-2.1.5.zip)
* [Play 2.0.8](https://github.com/playframework/playframework/releases/download/2.0.8/play-2.0.8.zip)

### CVSS metrics (<a href="https://www.first.org/cvss/user-guide">more info</a>)

* **Base: 6.4**
 AV:N/AC:L/Au:N/C:P/I:N/A:P
* **Temporal: 5.0**
 E:POC/RL:OF/RC:C
* **Environmental: 5.6**
 CDP:ND/TD:H/CR:H/IR:H/AR:ND
 *Environmental scores are assuming typical internet systems. Actual environmental scores for your organisation may differ.*

### Acknowledgements

Credit for finding this vulnerability goes to the Australia Post Digital Mailbox Security Team and Reginaldo Silva of <http://www.ubercomp.com/>.
