## Session Hijack

### Date

30 Dec 2015

### Description

A vulnerability has been found in Play 1's session handling.

It is possible by a 3rd-party to acquire session information for another in-progress request.

### Impact

Any application that uses the session in the processing of a 500 error page is vulnerable to attack.

### Affected Versions

* Play 1.4.0
* Play 1.3.0 - 1.3.2
* Play 1.2.6 - 1.2.6.1
* Play 1.0 - 1.2.5.5

### Workarounds

Do not use the session when generating a 500 error page.

### Fixes

Upgrade to the appropriate version below:

* [Play 1.4.1](http://downloads.typesafe.com/play/1.4.1/play-1.4.1.zip)
* [Play 1.3.3](http://downloads.typesafe.com/play/1.3.3/play-1.3.3.zip)
* [Play 1.2.6.2](http://downloads.typesafe.com/play/1.2.6.2/play-1.2.6.2.zip)
* [Play 1.2.5.6](http://downloads.typesafe.com/play/1.2.5.6/play-1.2.5.6.zip)

### Acknowledgements

Credit for finding this vulnerability goes to [Codeborne](https://codeborne.com).
