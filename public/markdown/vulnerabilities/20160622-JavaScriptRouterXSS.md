# JavaScript Router XSS

## Date

22 June 2016

## Description

A cross-site scripting (XSS) vulnerability has been found in the JavaScript reverse router in Play. This affects all versions of Play from 2.0.0 through 2.4.7. By default, the host is set to the value of the Host header, which is not escaped in the generated JavaScript. Since there are known vulnerabilities that allow an attacker to spoof the Host header, this can result in a reflected XSS in practice.

## Impact

In some environments it is possible for an attacker to spoof the Host header, such as through a cache poisoning attack. In those situations, an attacker can insert an arbitrary string that contains JavaScript to be executed on an unsuspecting user's machine.

## Affected Versions

Play 2.0.0 - 2.4.7

## Workarounds

The `Routes.javaScriptRouter` and `JavaScriptReverseRouter.apply` methods also have an alternate version that accepts a host. You can use this version with an explicit, sanitized host to ensure that an attacker cannot execute the attack, e.g.:

```scala
@Html(play.api.routing.JavaScriptReverseRouter(
  name = "Router",
  ajaxMethod = Some("jQuery.ajax"),
  host = "example.com", // should either be constant or sanitized value
  routes.javascript.Users.list,
  routes.javascript.Application.index
).body.replace("/", "\\/"))
```

## Fixes

Upgrade to Play 2.4.8 or 2.5.x.

## Acknowledgements

Thanks for Luca Carettoni of LinkedIn and Frans Rosén of Detectify for finding this vulnerability.
