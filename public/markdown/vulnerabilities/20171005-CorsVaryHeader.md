## Improper Vary header handling in CORS filter

### Date

5 Oct 2017

### Description

Play's CORS filter will overwrite the `Vary` header in certain configurations, which in some circumstances can cause cache poisoning. This could expose sensitive information to unauthorized users.

### Impact

When the CORS filter is used to match against a specific set of origins (as opposed to allowing any origin), it adds the `Vary: Origin` header in the response. In doing so it also overwrites any existing values for the `Vary` header, making the application vulnerable to cache poisoning.

This only impacts applications that set `Vary` headers in actions filtered by the CORS filter.

### Affected Versions

 - Play 2.6.0-2.6.5 (fixed in 2.6.6)
 - Play 2.4.0-2.5.17 (fixed in 2.5.18)

### Workarounds

Disable the CORS filter (if it is not needed) or disable caching completely (`Cache-Control: no-cache`) for any pages that depend on additional headers being added to `Vary`.

### Fixes

This issue is fixed in Play 2.6.6 and Play 2.5.18.
