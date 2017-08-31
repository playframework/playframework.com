## WS invalid URI parsing

### Date

28 Aug 2017

### Description

A low-severity vulnerability has been found in the URI parsing of AsyncHttpClient, which is used by the Play WS client and by the play-ws-standalone library.

This library causes the WS API to improperly parse the URI's authority component if it is followed by `#`. For example, passing `http://example.com#@evil.com/foo.txt` will actually make a request to `evil.com`.

The AsyncHttpClient issue is also described in [AsyncHttpClient issue 1455](https://github.com/AsyncHttpClient/async-http-client/issues/1455)

### Impact

If users are allowed to pass arbitrary URI strings, this vulnerability could be used to circumvent whitelists or blacklists of host names. An RFC-compliant parser would correctly parse `http://example.com#@evil.com/foo.txt`. Note that this issue does not affect URIs like `http://example.com/#@evil.com/foo.txt`, with the slash at the beginning of the path.

### Affected Versions

 - Play 2.6.0-2.6.3
 - play-ws-standalone 1.0.0-1.0.6
 - Play 2.5.0-2.5.16
 - All previous 2.x versions

### Workarounds

Parse the URI using a compliant parser like `java.net.URI`. If the path is empty, replace the empty path with a single slash.

### Fixes

Upgrade to play-ws-standalone 1.0.7, or, if using Play 2.5.x, upgrade to async-http-client 2.0.35. The issue has not been fixed in the AsyncHttpClient used by Play 2.4.x and earlier.

The correct version will automatically be provided in Play 2.6.4 and higher, and Play 2.5.17 and higher.

### Acknowledgements

Credit for finding this vulnerability in AsyncHttpClient goes to Nicolas Grégoire from Agarri.
