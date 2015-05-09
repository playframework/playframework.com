## Play 2.3.9

*Released 8 May 2015*

**Vulnerabilities fixed**

* [CVE-2015-2156](/security/vulnerability/CVE-2015-2156-HttpOnlyBypass) Http only cookie bypass

**Other changes**

* [3484](https://github.com/playframework/playframework/issues/3484) Fix sub project run
* [2992](https://github.com/playframework/playframework/issues/2992) Fix IE11 websocket issues
* [4114](https://github.com/playframework/playframework/pull/4114) Ensure generated routes code doesn't emit scalac warnings
* [2559](https://github.com/playframework/playframework/pull/2259) Allow routes parameters with default values to be scala keywords
* [4412](https://github.com/playframework/playframework/pull/4412) Upgrade to Netty 3.9.8

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.3.9)

## Play 2.3.8

*Released 11 February 2015*

* [3723](https://github.com/playframework/playframework/pull/3723) Upgrade async-http-client
* [3752](https://github.com/playframework/playframework/pull/3752) Commas in etag break browser caching
* [3771](https://github.com/playframework/playframework/pull/3771) Log http wire
* [3773](https://github.com/playframework/playframework/pull/3773) Do not reveal db password in log
* [3805](https://github.com/playframework/playframework/pull/3805) Updated Javassist to 3.19.0-GA
* [3811](https://github.com/playframework/playframework/pull/3811) Upgrade to sbteclipse 3.0.0 which has several bug fixes for issues reported by Play users
* Add forked development-mode run for integration with sbt server

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.3.8)

## Play 2.3.7

*Released 3 December 2014*

* [3304](https://github.com/playframework/playframework/pull/3304) Close a security hole by using https to fetch artifacts
* [3546](https://github.com/playframework/playframework/pull/3546) Handle multipart fields with colon inside
* [3563](https://github.com/playframework/playframework/pull/3563) Backport #3291: Anorm support for joda-time DateTime and Instant
* [3567](https://github.com/playframework/playframework/pull/3567) Backport #3243: Anorm: more column conversions
* [3582](https://github.com/playframework/playframework/pull/3582) Backport #3576: Add anorm support for parsing UUIDs from JDBC Strings
* [3584](https://github.com/playframework/playframework/pull/3584) Backport #3574: Anorm: more column integer conversions
* [3646](https://github.com/playframework/playframework/pull/3646) Support OpenJDK 6 in WS SSL
* [3692](https://github.com/playframework/playframework/pull/3692) Upgrade to jboss-logging 3.2.0

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.3.7)

## Play 2.3.6

*Released 28 October 2014*

* [3506](https://github.com/playframework/playframework/issues/3506) Correct group id and version of sbt-run-support sub project
* [3342](https://github.com/playframework/playframework/pull/3342) Ensure HttpExecutionContext calls delegates prepare method
* [3533](https://github.com/playframework/playframework/pull/3533) Ensure F.Option can be used in reverse routes
* [3514](https://github.com/playframework/playframework/pull/3514) Fix NPE for null values in JavaScript reverse router and provide missing binders
* [3508](https://github.com/playframework/playframework/pull/3508) Providing missing UUID JavaScript reverse router binder
* [2959](https://github.com/playframework/playframework/pull/2959) Anorm error handling improvements
* [3049](https://github.com/playframework/playframework/pull/3049) Reinstate Anorm Row extractor
* [3062](https://github.com/playframework/playframework/pull/3062) Provide Anorm column parser for JDBC array

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.3.6)

## Play 2.3.5

*Released 7 October 2014*

**Vulnerabilities fixed**

* [CVE-2014-3630](/security/vulnerability/CVE-2014-3630-XmlExternalEntity) XML External Entity exploit

**Other changes**

* [2767](https://github.com/playframework/playframework/issues/2767) Gracefully handle hostname cannot be verified HTTPS errors.
* [3471](https://github.com/playframework/playframework/pull/3471) Ensure AddCsrfToken provides original context.

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.3.5)

## Play 2.3.4

*Released 1 September 2014*

* [3298](https://github.com/playframework/playframework/issues/3298) Upgrade to [Netty 3.9.3](http://netty.io/news/2014/08/06/3-9-3-Final.html).
* [3349](https://github.com/playframework/playframework/issues/3349) Use URI instead of URL to parse Java WS URLs.

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.3.4)

## Play 2.3.3

*Released 12 August 2014*

* [1186](https://github.com/playframework/playframework/issues/1186) Don't ignore logger configuration in application.conf in production
* [2620](https://github.com/playframework/playframework/issues/2620) Fix jnotify errors when SBT reloads
* [3037](https://github.com/playframework/playframework/issues/3037) Don't ignore status code when sending files
* [3206](https://github.com/playframework/playframework/issues/3206) Fix SLF4J warnings in SBT
* [3216](https://github.com/playframework/playframework/issues/3216) Provide a default message for error.date key
* [3227](https://github.com/playframework/playframework/issues/3227) Make it possible to upgrade to sbt-web 1.1 to allow better multi module support for assets
* [3253](https://github.com/playframework/playframework/issues/3253) Upgrade to jshint 1.0.1
* [3269](https://github.com/playframework/playframework/issues/3269) Ensure Play uses a shared mutex for providing exclusiveness on the current application in integration tests

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.3.3)

## Play 2.3.2

*Released 21 July 2014*

* [2747](https://github.com/playframework/playframework/issues/2747) Support for advanced TCP socket options such as keep alive.
* [2905](https://github.com/playframework/playframework/issues/2905) Ensure assets changes don't trigger reload of Play
* [2946](https://github.com/playframework/playframework/issues/2946) Ignore benign JNotify watch removal errors
* [3073](https://github.com/playframework/playframework/issues/3073) Ensure stability of ordering of reverse routes matching
* [3074](https://github.com/playframework/playframework/issues/3074) Remove double slashes from assets fingerprinting
* [3122](https://github.com/playframework/playframework/issues/3122) Remove dependence of Assets on Play.current
* [3129](https://github.com/playframework/playframework/issues/3129) Allow generating https URLs without a request object
* [3141](https://github.com/playframework/playframework/issues/3141) Don't duplicate Content-Type header when explicitly set in WS
* [3173](https://github.com/playframework/playframework/issues/3173) Add support for JDK7 WatchService in dev mode

**Upgrade notes**

* On Windows and Linux on JDK7, Play now uses the JDK7 WatchService.  This has some small differences from the old mechanism - JNotify.  If the JNotify behaviour is preferred, this can be configured in `build.sbt` using:

    ```scala
    PlayKeys.playWatchService := play.sbtplugin.run.PlayWatchService.jnotify(Keys.sLog.value)
    ```

    Alternatively, you can also use a pure SBT implementation, using:

    ```scala
    PlayKeys.playWatchService := play.sbtplugin.run.PlayWatchService.sbt(pollInterval.value)
    ```

    Note that while the JDK7 WatchService works on other platforms, including OSX, its use is not recommended for Play's dev mode because on those platforms it simply polls the filesystem at a 2 second interval.

[Full changelog](https://github.com/playframework/playframework/issues?milestone=17&state=closed)

## Play 2.3.1

*Released 25 June 2014*

* [2936](https://github.com/playframework/playframework/issues/2936) Fixed duplicate entry error when multiple sub projects depend on the same webjar
* [2967](https://github.com/playframework/playframework/issues/2967) Ensure SecurityHeadersFilter constructor doesn't prevent Application from starting
* [2973](https://github.com/playframework/playframework/issues/2973) Use minified assets in production automatically if they exist
* [2986](https://github.com/playframework/playframework/issues/2986) Fixed deadlock in tests
* [3012](https://github.com/playframework/playframework/issues/3012) Update to latest Twirl
* [3044](https://github.com/playframework/playframework/issues/3044) Provided JavaScript literal binder for Assets
* [3050](https://github.com/playframework/playframework/issues/3050) Fixed reverse routing routes priority
* [3057](https://github.com/playframework/playframework/issues/3057) Fixed excessive reloading
* [3064](https://github.com/playframework/playframework/issues/3064) Fixed HEAD support in CSRF filter
* [3066](https://github.com/playframework/playframework/issues/3066) Upgraded to Netty 3.9.2 to fix a [security vulnerability](http://netty.io/news/2014/06/11/3.html) in the SSL support

**Upgrade notes**

* The behaviour of the Assets reverse router has changed, if minified versions of assets exist, it now returns a URL for those instead.  To disable this behaviour, set `assets.checkForMinified=true` in `application.conf`.
* A change to the `SecurityHeadersFilter` constructor breaks binary compatibility.  This was done because the old constructor signature could not be used for its intended purpose without breaking it.

[Full changelog](https://github.com/playframework/playframework/issues?milestone=14&state=closed)

## Play 2.3.0

*Released 30 May 2014*

* Introducing the activator command. You can use activator exactly like you would use play, but Activator brings new features too. (More about the Activator change.)
* Better tooling for static assets. Play now uses sbt-web which gives faster asset processing, more features, and better extensibility.
* Support for Java 8 (and continued support for Java 6 and 7).
* Better Java performance. Simple Java controllers give 40–90% better throughput. (Thanks to YourKit for sponsoring licenses.)
* Support for Scala 2.11 (and continued support for Scala 2.10).
* Anorm enhancements: SQL string interpolation, multi-value parameters, new types, and more.
* Web Services enhancements: separate client, SSL configuration, and more.
* Play templates have become Twirl templates: separate project, new sbt plugin, still excellent integration with Play
* Actors for WebSockets
* Custom SSLEngine for HTTPS
* Asset performance: faster serving, better caching.
* One Result to rule them all: all the result types which were deprecated in 2.2 are now gone and only Result remains.
* Lots of bug fixes.

[Highlights](http://playframework.com/documentation/2.3.x/Highlights23)
[Migration Guide](http://playframework.com/documentation/2.3.x/Migration23)
[Full changelog](https://github.com/playframework/playframework/issues?milestone=3&state=closed)

## Play 2.2.5

*Released 7 October 2014*

* [3471](https://github.com/playframework/playframework/pull/3471) Ensure AddCsrfToken provides original context.

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.2.5)

## Play 2.2.4

*Released 21 July 2014*

* [2747](https://github.com/playframework/playframework/issues/2747) Support for advanced TCP socket options such as keep alive.

[Full changelog](https://github.com/playframework/playframework/issues?milestone=1r54&state=closed)

## Play 2.2.3

*Released 1 May 2014*

**Vulnerabilities fixed**

* [WebSockets](http://netty.io/news/2014/04/30/release-day.html) Netty WebSocket denial of service vulnerability

**Other changes**

* [2508](https://github.com/playframework/playframework/issues/2508) Gracefully handle bad URIs in request by returning 400 errors.
* [2499](https://github.com/playframework/playframework/issues/2499) When validation fails for a Java form, don't render duplicate repeat elements.
* [2535](https://github.com/playframework/playframework/issues/2535) Ensure exceptions thrown by JUnit get logged.
* [2509](https://github.com/playframework/playframework/issues/2509) Make WS SSL requests use a hostname verifier by default.
* [2683](https://github.com/playframework/playframework/pull/2683) Continuation frame handling for WebSockets

## Play 2.2.2

*Released 1 March 2014*

Highlights:

* fixed an issue with the sbt-eclipse integration we had
* incremental compiler improvements
* Fixed a problem preventing New Relic from working when GZip filters were being used
* fixed an issue where javascript assets where being repeated in the project's minified output
* sbt native packager updates
* Bonecp issue resolution around dropping connections
* update to Scala 2.10.3
* various bug fixes

A complete list of what was fixed for 2.2.2 can be found on [Github](https://github.com/playframework/playframework/issues?milestone=11&page=1&state=closed)


## Play 2.2.1

*Released 31 October 2013*

* [830](https://github.com/playframework/playframework/issues/830) - Integration tests now run out of the box in Eclipse
* [1159](https://github.com/playframework/playframework/issues/1159) - OAuth calculator no longer ignores query string
* [1704](https://github.com/playframework/playframework/issues/1704) - Fixed performance degradation with SQL queries in Play 2.2
* [1722](https://github.com/playframework/playframework/issues/1722) - Accept all WebSocket subprotocols
* [1734](https://github.com/playframework/playframework/issues/1734) - Reinstate custom token generator functionality in CSRF filter
* [1737](https://github.com/playframework/playframework/issues/1737) - Ensure CSRF filter can be instantiated without a running application
* [1750](https://github.com/playframework/playframework/issues/1750) - Fixed Promise.timeout exception handling
* [1778](https://github.com/playframework/playframework/issues/1778) - Fixed 505 body sending bug
* [1786](https://github.com/playframework/playframework/issues/1786) - Fixed closure compiler on windows - made the CommonJs functionality optional
* [1815](https://github.com/playframework/playframework/issues/1815) - Ensure echo is not disabled when play run finishes
* [1819](https://github.com/playframework/playframework/issues/1819) - Cache the result, not the iteratee in cached action
* [1841](https://github.com/playframework/playframework/issues/1841) - Ensured test results propogate to test runner
* [1856](https://github.com/playframework/playframework/issues/1856) - Ensure filters are executed when no handler is found
* [1898](https://github.com/playframework/playframework/issues/1898) - Upgrade bonecp to 0.8.0.RELEASE

**Upgrade notes**

* The signature of the [CSRFFilter](/documentation/2.2.1/api/scala/index.html#play.filters.csrf.CSRFFilter) constructor has changed.  Any code that used this previously should still be source compatible, but may need to be recompiled.
* CommonJS module support used to be provided by default when using the JavaScript compiler. It no longer is as there were problems with its support on Windows. CommonJS module support can be re-enabled by using a `commonJs` option for the JavaScript compiler.

[Full changelog](https://github.com/playframework/playframework/issues?milestone=9&state=closed)

## Play 2.2.0

*Released 20 September 2013*

* New results structure for Java and Scala
* Better control over buffering and keep alive
* New action composition and action builder methods
* Improved Java Promise API
* Iteratee library execution context passing
* SBT 0.13 support
* New stage and dist tasks
* Built in gzip support

[Highlights](http://playframework.com/documentation/2.2.0/Highlights22)
[Full changelog](https://github.com/playframework/playframework/issues?milestone=2&state=closed)

## Play 2.1.5

*Released 20 September 2013*

**Vulnerabilities fixed**

* [20130920-XmlExternalEntity](/security/vulnerability/20130920-XmlExternalEntity) XML External Entity exploit

**Other changes**

* [1623](https://github.com/playframework/playframework/issues/1623) Fixed Netty deadlock when SSL is enabled
* [1356](https://github.com/playframework/playframework/issues/1356) Ignore spurious errors when max body length is exceeded

[Full changelog](https://github.com/playframework/playframework/issues?milestone=8&state=closed)

## Play 2.1.4

*Released 11 September 2013*

**Vulnerabilities fixed**

* [20130911-XmlExternalEntity](/security/vulnerability/20130911-XmlExternalEntity) XML External Entity exploit

**Other changes**

* [1440](https://github.com/playframework/playframework/issues/1440) Play run hooks for monitoring tool integration
* [1451](https://github.com/playframework/playframework/issues/1451) Fixed the junit tests and have a test to make sure junit tests run
* [1055](https://github.com/playframework/playframework/issues/1055) Configurable HTTP request parameters - max request length, max header size, chunk size
* [1498](https://github.com/playframework/playframework/issues/1498) Prevent the setting of null cookie values
* [1496](https://github.com/playframework/playframework/issues/1496) Allow ehcache.xml to be overridden

[Full changelog](https://github.com/playframework/playframework/issues?milestone=6&state=closed)

## Play 2.1.3

*Released 06 August 2013*

**Vulnerabilities fixed**

* [20130806-SessionInjection](/security/vulnerability/20130806-SessionInjection) Session injection

**Other changes**

* [1329](https://github.com/playframework/playframework/issues/1329) Made If-None-Since and If-Modified-Since interaction spec compliant
* [1332](https://github.com/playframework/playframework/issues/1332) Fixed regression where SSE disconnects were not detected
* [1346](https://github.com/playframework/playframework/issues/1346), [1360](https://github.com/playframework/playframework/issues/1360) Fixed race conditions with Expect: 100-continue
* [1347](https://github.com/playframework/playframework/issues/1347) Changed URL path segment encoding to follow spec rather than use query string encoding
* [1359](https://github.com/playframework/playframework/issues/1359) Removed binary dependency on Akka 2.1; can now use Akka 2.2
* [1361](https://github.com/playframework/playframework/issues/1361) Fixed regression with Actions running in the wrong ExecutionContext
* [1370](https://github.com/playframework/playframework/issues/1370) Reduced memory used when pipelining
* [1402](https://github.com/playframework/playframework/issues/1402) Fixed memory leak on app reload in dev mode
* [1406](https://github.com/playframework/playframework/issues/1406) Upgraded junit-interface to 0.10

[Full changelog](https://github.com/playframework/playframework/issues?milestone=5&page=1&state=closed)

## Play 2.1.2

*Released 5 July 2013*

* [810](https://github.com/playframework/playframework/issues/810) - Fixed XPath.selectText regression
* [820](https://github.com/playframework/playframework/issues/820), [1229](https://github.com/playframework/playframework/issues/1229) - Made matching of Accept-Language headers spec compliant
* [839](https://github.com/playframework/playframework/issues/839) - Ensured application loading is not done on a Netty thread
* [851](https://github.com/playframework/playframework/issues/851) - Ensured Java actions use the right context classloader
* [924](https://github.com/playframework/playframework/issues/924) - Allowed charset parameter to have quotes
* [945](https://github.com/playframework/playframework/issues/945) - Fixed HTTP pipelining support
* [959](https://github.com/playframework/playframework/issues/959) - Ensured chunked and stream results terminate
* [978](https://github.com/playframework/playframework/issues/978) - Safe handling of incorrectly encoded URL paths
* [984](https://github.com/playframework/playframework/issues/984) - Support for config.resource system property in dev mode
* [1050](https://github.com/playframework/playframework/issues/1050) - Switched to Scala ForkJoin pool
* [1090](https://github.com/playframework/playframework/issues/1090) - Performance improvements
* [1152](https://github.com/playframework/playframework/issues/1152) - Fixed 100-continue support

[Full changelog](https://github.com/playframework/playframework/issues?milestone=4&state=closed)

## Play 2.1.1

*Released 13 April 2013*

* Reverted changes to multipart/form-data API so that no file submitted can be easily detected
* Reverse router now escapes String path parameters
* Invalid escape combinations in the query string and path now return 400 errors
* Routes files that only contain includes now compile
* Javadoc generation is now working
* Compiled assets are no longer part of watched sources
* Fixed some compilation problems on Windows
* Improved routes helpers implementation
* [884](https://github.com/playframework/playframework/issues/884) Play can once again handle file uploads greater than 2GB (this was a regression in Netty, fixed by upgrading to 3.6.3)
* [889](https://github.com/playframework/playframework/issues/889) Performance/concurrency bottleneck fix for JSON serialisation in Scala
* [888](https://github.com/playframework/playframework/issues/888) Fix Javascript reverse router default values
* JSON macro fixes and improvements

## Play 2.1.0

*Released 6 February 2013*

* Migration to Scala 2.10
* Migration to Scala concurrent Futures
* Modularisation of Play itself
* Modularisation of routes files
* Better thread management and HTTP context propagation in Java projects
* Managed controller instantiation
* New Scala JSON API
* New Filter API with built in CSRF protection
* RequireJS support
* Content negotiation
* Improved Iteratee API
* 182 resolved bugs and improvements in our issue tracker:

[Highlights](http://playframework.com/documentation/2.1.0/Highlights)
[Full changelog](https://play.lighthouseapp.com/projects/82401-play-20/milestones/137248-21)

## Play 2.0.8

*Released 20 September 2013*

**Vulnerabilities fixed**

* [20130920-XmlExternalEntity](/security/vulnerability/20130920-XmlExternalEntity) XML External Entity exploit

## Play 2.0.7

*Released 11 September 2013*

**Vulnerabilities fixed**

* [20130911-XmlExternalEntity](/security/vulnerability/20130911-XmlExternalEntity) XML External Entity exploit

**Other changes**

* [1502](https://github.com/playframework/playframework/issues/1502) Semicolon escaping in evolutions
* [1503](https://github.com/playframework/playframework/issues/1503) Configurable HTTP request parameters - max request length, max header size, chunk size

[Full changelog](https://github.com/playframework/playframework/issues?milestone=7&state=closed)

## Play 2.0.6

*Released 06 August 2013*

**Vulnerabilities fixed**

* [20130806-SessionInjection](/security/vulnerability/20130806-SessionInjection) Session injection

## Play 2.0.5

*Released 01 August 2013*

* Fixed 100-continue behaviour
* Removed broken Jaxen dependencies
* Upgraded to Fluentlenium 0.8
* Fixed If-None-Match handling when releases are rolled back
* Configured timeouts and redirects for WS API
* Stopped plugins in reverse order to started
* Removed circular dependencies in dev mode ClassLoaders

## Play 1.3.0

*Released 15 January 2015*

* Fixed multiple continuations/await bugs
* Fixed multiple test bugs (auto-test/async/htmlunit)
* Numerous libraries upgraded (a.o. netty, hibernate, etc)
* Numerous i18n fixes
* Experimental java 8 support (however, hibernate does not support it, so some edge cases might now work)
* Improved intellij support
* Multiple databases support
* Customisable netty pipeline
* Customisable template name resolving
* Introduce filters in the plugin API
* Project documentation viewer in dev mode
* Improved Job support – added afterRequest() support
* Improved Mailer

**Migration notes**

* Java 1.5 is no longer supported
* Modules dependencies resolution change from http://www.playframework.org to https://www.playframework.com
* Run ‘play deps’ to make sure your dependencies are up-to-date

**Known issues**

* Cobertura does not work
