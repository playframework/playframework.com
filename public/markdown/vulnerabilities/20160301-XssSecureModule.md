## XSS 

### Date

01 March 2016

### Description

An XSS vulnerability has been found in the Secure module login page.

### Impact

Any application that uses the default login page of the Secure module .

### Affected Versions

* Play 1.2.0 - 1.2.7
* Play 1.3.0 - 1.3.3
* Play 1.4.0 - 1.4.1

### Workarounds

Change modules\secure\app\views\Secure\login.html
```
&{flash.error} 
```
to 
```
${messages.get(flash.error)}
```
and 
```
&{flash.success} 
```
to 
```
${messages.get(flash.success)}
```

### Fixes

Upgrade to the appropriate version below:

* [Play 1.3.4](http://downloads.typesafe.com/play/1.3.1/play-1.3.4.zip)
* [Play 1.4.2](http://downloads.typesafe.com/play/1.4.2/play-1.4.2.zip)


### Acknowledgements

Credit for finding this vulnerability goes to Ricardo Martín from ElevenPaths.
