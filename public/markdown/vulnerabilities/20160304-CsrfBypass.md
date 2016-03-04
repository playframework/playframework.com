## CSRF Bypass

### Date

4 Mar 2016

### Description

A vulnerability has been found in Play's CSRF support.

The Chrome [Beacon](https://www.chromestatus.com/feature/5517433905348608) extension allows non preflighted, non form cross origin POST requests, which invalidates the Play CSRF filters assumption that only form POST requests are subject to CSRF attacks.

### Impact

Play's CSRF check can be bypassed when the victims browser is Chrome.

### Affected Versions

* Play 2.2.0 - 2.4.6

### Workarounds

Set the following configuration in `application.conf`:

```
play.filters.csrf.contentType {
  blackList = []
  whiteList = ["none"]
}
```

Note that this will, for example, cause all `POST` `application/json` requests to need a CSRF check.

### Fixes

Upgrade to Play 2.5.0.  Details on how Play 2.5 has been modified to provide better CSRF protection and what needs to be done to upgrade can be found [here](https://www.playframework.com/documentation/2.5.x/Migration25#CSRF-changes).

### Acknowledgements

Credit for finding this vulnerability goes to David Black from [Atlassian](https://atlassian.com).
