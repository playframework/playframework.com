## XSS  injection from URL parameter

### Date

01 December 2015

### Description

An vulnerability has been found that allow a 3rd-party to acquire session information for another in-progress request. 

### Impact

Any application that uses Play's URL rendering.

### Affected Versions

* Play 1.2.0 - 1.2.6
* Play 1.3.0 - 1.3.2
* Play 1.4.0


### Fixes

Upgrade to the appropriate version below:

* [Play 1.2.5.6](http://downloads.typesafe.com/play/1.2.5.6/play-1.2.5.6.zip)
* [Play 1.2.6.2](http://downloads.typesafe.com/play/1.2.6.2/play-1.2.6.2.zip)
* [Play 1.3.3](http://downloads.typesafe.com/play/1.3.3/play-1.3.3.zip)
* [Play 1.4.2](http://downloads.typesafe.com/play/1.4.2/play-1.4.2.zip)

### Acknowledgements

Credit for finding this vulnerability goes to Codeborne (https://codeborne.com).