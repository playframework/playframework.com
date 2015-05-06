## XSS  injection from URL parameter

### Date

05 May 2015

### Description

An XSS vulnerability has been found in Play's URL rendering.

### Impact

Any application that uses Play's URL rendering.

### Affected Versions

* Play 1.2.0 - 1.2.7
* Play 1.3.0

### Workarounds

Encode parameter before using it, 

```java
@{Controller.action(parameterWithInjection?.urlEncode())}
```

### Fixes

Upgrade to the appropriate version below:

* [Play 1.2.5.5](http://downloads.typesafe.com/play/1.2.5.5/play-1.2.5.5.zip)
* [Play 1.2.6.1](http://downloads.typesafe.com/play/1.2.6.1/play-1.2.6.1.zip)
* [Play 1.2.7.2](http://downloads.typesafe.com/play/1.2.7.2/play-1.2.7.2.zip)
* [Play 1.3.1](http://downloads.typesafe.com/play/1.3.1/play-1.3.1.zip)

### CVSS metrics (<a href="http://www.first.org/cvss/cvss-guide">more info</a>)

* **Base: 5.8**
 AV:N/AC:M/Au:N/C:P/I:P/A:N
 AV:N/AC:L/Au:N/C:P/I:N/A:P
* **Temporal: 4.5**
 E:POC/RL:OF/RC:C
 E:POC/RL:OF/RC:C
* **Environmental: 4.2**
 CDP:ND/TD:M/CR:H/IR:H/AR:ND
 *Environmental scores are assuming typical internet systems. Actual environmental scores for your organisation may differ.*

### Acknowledgements

Credit for finding this vulnerability goes to the 11Paths Team (Spanish security company).
