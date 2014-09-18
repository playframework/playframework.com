## Session Injection

### Date

06 Aug 2013

### Description

A vulnerability has been found in Play's session encoding.

An attacker may inject arbitrary data into a session, by tricking Play to place a specially crafted value containing null bytes into the Play session.

### Impact

Any application that places user input data into Play's stateless session mechanism may be vulnerable.

Typically, this will impact applications that store the username in the session for authentication purposes, and will allow an attacker to identify themselves as another user.

### Affected Versions

* Play 2.1.0 - 2.1.2
* Play 2.0 - 2.0.5
* Play 1.2 - 1.2.5
* Play 1.1 - 1.1.2
* Play 1.0 - 1.0.3.3

### Workarounds

Validate that no values being placed into a session contain null bytes.

### Fixes

Upgrade to the appropriate version below:

* [Play 2.1.3](http://downloads.typesafe.com/play/2.1.3/play-2.1.3.zip)
* [Play 2.0.6](http://downloads.typesafe.com/play/2.0.6/play-2.0.6.zip)
* [Play 1.2.6](http://downloads.typesafe.com/play/1.2.6/play-1.2.6.zip)
* [Play 1.1.3](http://downloads.typesafe.com/play/1.1.3/play-1.1.3.zip)
* [Play 1.0.3.4](http://downloads.typesafe.com/play/1.0.3.4/play-1.0.3.4.zip)

### CVSS metrics (<a href="http://www.first.org/cvss/cvss-guide">more info</a>)

* **Base: 6.4**
 AV:N/AC:L/Au:N/C:P/I:P/A:N
* **Temporal: 5.6**
 E:H/RL:OF/RC:C
* **Environmental: 6.8** 
 CDP:ND/TD:H/CR:H/IR:H/AR:ND
 *Environmental scores are assuming typical internet systems. Actual environmental scores for your organisation may differ.*

### Acknowledgements

Credit for finding this vulnerability goes to the National Australia Bank Security Assurance Team.
