# Denial of Service in Play WS using OAuth 1.0

## Date

January 20th 2017

## Description

A denial of service (DoS) vulnerability has been found in the WS HTTP Client in Play. This affects all versions of Play from 2.0.0 through 2.5.10.

Play WS includes integration with OAuth 1.0 through Signpost OAuth, allowing HTTP requests to include a signed OAuth header to talk to remote services.  Signpost OAuth uses commons-httpclient4 under the hood as an OAuth provider.

If a WS request uses OAuth and is over an HTTPS connection and if the server does not respond to the TLS handshake, then the HTTP request connection will hang because it ignores http.socket.timeout.

## Impact

Signpost OAuth itself is not multi-threaded, so the call itself is a blocking operation. Given the right parameters, this could lead to a denial of service attack to the remote service, as all the WS request handling threads are exhausted.  

This vulnerability is filed as CVE-2015-5262.

## Affected Versions

Play 2.0.0 - 2.5.10

## Workarounds

The workaround for this vulnerability is to upgrade the HTTPClient library to version 4.5.2, which does not have these issues.

```scala
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.2"
```

## Fixes

Upgrade to Play 2.5.11 or 2.4.9, or upgrade the library if you are on previous versions of Play.

## Acknowledgements

Thanks to Denny Ma for reporting this issue.