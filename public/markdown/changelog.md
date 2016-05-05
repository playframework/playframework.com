## Play 2.5.3 "Streamy"

*Released 27 April 2016*

* [6050](https://github.com/playframework/playframework/pull/6050): Upgrade Akka to version 2.4.4
* [6051](https://github.com/playframework/playframework/pull/6051): Add withCookies to Java Result API
* [6052](https://github.com/playframework/playframework/pull/6052): Optional entity length for range requests
* [6053](https://github.com/playframework/playframework/pull/6053): Refactoring to use Java 8 streams API
* [6058](https://github.com/playframework/playframework/pull/6058): Do not add Content-Disposition header when serving assets
* [6068](https://github.com/playframework/playframework/pull/6068): Doc fix: Correctly look up validation errors in messages
* [6069](https://github.com/playframework/playframework/pull/6069): Fixed/improved ning deprecation messages
* [6072](https://github.com/playframework/playframework/pull/6072): Get host properly even if full URI is invalid
* [6077](https://github.com/playframework/playframework/pull/6077): Allow request filters to modify outbound request
* [6080](https://github.com/playframework/playframework/pull/6080): Update compile-time DI docs
* [6083](https://github.com/playframework/playframework/pull/6083): Handle null response headers
* [6087](https://github.com/playframework/playframework/pull/6087): Upgrade async-http-client to version 2.0.2
* [6092](https://github.com/playframework/playframework/pull/6092): Support non-ISO8859-1 filename in Content-Disposition header
* [6093](https://github.com/playframework/playframework/pull/6093): Trigger 400 error if path is null
* [6095](https://github.com/playframework/playframework/pull/6095): Don't throw NPE when variables in a Form or a Field are null

## Play 2.5.2 "Streamy"

*Released 14 April 2016*

* [5974](https://github.com/playframework/playframework/pull/5974): Add idleTimeout support for 2.5.x
* [5982](https://github.com/playframework/playframework/pull/5982): Better implementation of Accumulator.flatten
* [5984](https://github.com/playframework/playframework/pull/5984): Upgrade Akka to version 2.4.3
* [5995](https://github.com/playframework/playframework/pull/5995): Range requests support
* [6003](https://github.com/playframework/playframework/pull/6003): Made it possible to specify a Content-Length header for a StreamedBody
* [6009](https://github.com/playframework/playframework/pull/6009): Add check for play.editor setting
* [6022](https://github.com/playframework/playframework/pull/6022): Replace PushStage based methods with GraphStage in WebSocketHandler
* [6031](https://github.com/playframework/playframework/pull/6031): Don't catch exceptions in LogbackLoggerConfigurator
* [6032](https://github.com/playframework/playframework/pull/6032): Fix header parsing in Akka server
* [6037](https://github.com/playframework/playframework/pull/6037): Upgrade AsyncHttpClient to 2.0.0

## Play 2.5.1 "Streamy"

*Released 29 March 2016*

* [5860](https://github.com/playframework/playframework/pull/5860): Ensure data parts don't take 5 seconds to parse
* [5963](https://github.com/playframework/playframework/pull/5963), [5899](https://github.com/playframework/playframework/pull/5899): Update netty-reactive-streams (Fixes [5821](https://github.com/playframework/playframework/issues/5821), [5874](https://github.com/playframework/playframework/issues/5874))
* [5926](https://github.com/playframework/playframework/pull/5926): Make sure Context is set for Java WebSocket methods
* [5856](https://github.com/playframework/playframework/pull/5856): Fix runtime injector provided by BuiltInComponents so that Crypto is injectable
* [5920](https://github.com/playframework/playframework/pull/5920): Fix default charset in Java FormUrlEncoded parser
* [5698](https://github.com/playframework/playframework/pull/5698): Add WS method for POSTing multipart/form-data

## Play 2.5.0 "Streamy"

*Released 4 March 2016*

* Switched from iteratees to Akka streams for all asynchronous IO and streaming
* Replaced Play functional types such as `Promise` and `Option` with Java 8 functional types such as `CompletionStage` and `Optional`
* Introduced equivalent Java APIs for features that previously only existing in the Scala API, such as implementing filters and custom body parsers
* Increased performance by up to 20%

[Highlights](https://playframework.com/documentation/2.5.x/Highlights25)
[Migration Guide](https://playframework.com/documentation/2.5.x/Migration25)
[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.5.0)

## Play 2.4.6 "Damiya"

*Released 14 December 2015*

* [5326](https://github.com/playframework/playframework/pull/5326) Fixes to forward header parsing
* [5364](https://github.com/playframework/playframework/pull/5364) Build routes compiler library for Scala 2.11
* [5368](https://github.com/playframework/playframework/pull/5368) Allow CSRF to be configured with compile-time dependency injection

## Play 2.4.5 "Damiya"

*Unreleased*

## Play 2.4.4 "Damiya"

*Released 19 November 2015*

* [Support infinite WS request timeouts](https://github.com/playframework/playframework/commit/d5e42a5db6e3335a3728949cf46fd5de167d5134)
* [Updated typetools version to support latest JDK](https://github.com/playframework/playframework/commit/baeceb8337d37d81f08a8f5f0b5b87e794104ece)
* [Support for temporary files with compile-time dependency injection](https://github.com/playframework/playframework/commit/45b8188451d282c758c5a66643b3ecc3bbd3eb31)
* [Use SHA256-RSA for self-signed certificates](https://github.com/playframework/playframework/commit/c599afd96c391d1f1a6f556e7635c215f78ff604)
* [Exclude some transitive dependencies from Maven POM](https://github.com/playframework/playframework/commit/c876d215afc7291f08b51a18aa349588a001e73d)
* [Improved Forward and X-Forwarded handling](https://github.com/playframework/playframework/commit/48f6772c857a851617c7170a74b8fa630ca4994c)

## Play 2.4.3 "Damiya"

*Released 7 September 2015*

* [2188](https://github.com/playframework/playframework/issues/2188) Allow Authenticator for Security.Authenticated to be injected
* [4649](https://github.com/playframework/playframework/issues/4649) Fixes for dev mode memory leaks
* [4834](https://github.com/playframework/playframework/issues/4834) Made Helpers.invokeWithContext static
* [4792](https://github.com/playframework/playframework/issues/4792) Netty upgrade from 3.10.3 to 3.10.4
* [4935](https://github.com/playframework/playframework/issues/4935) Scala JSON Reads for java.time.Instant
* [4939](https://github.com/playframework/playframework/issues/4939) Fix packaging when externalizeResources or executableScriptName settings are used
* [4940](https://github.com/playframework/playframework/issues/4940) Support for HikariCP connectionInitSql
* [4975](https://github.com/playframework/playframework/issues/4975) Akka-http upgrade from 1.0-RC2 to 1.0
* [5025](https://github.com/playframework/playframework/issues/5025) Demoted akka initialization logging to debug level

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.4.3)

## Play 2.4.2 "Damiya"

*Released 3 July 2015*

* [4498](https://github.com/playframework/playframework/issues/4498) Provide JsReadable.validateOpt to replace Option reads
* [4615](https://github.com/playframework/playframework/issues/4615) Post binary data as is with play-ws client
* [4707](https://github.com/playframework/playframework/issues/4707) Clean temporary files on app shutdown
* [4736](https://github.com/playframework/playframework/issues/4736) Fix non extraction of some webjars
* [4753](https://github.com/playframework/playframework/issues/4753) Ensure flash cookie cleanup doesn't lose other cookies

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.4.2)

## Play 2.4.1 "Damiya"

*Released 23 June 2015*

* [3532](https://github.com/playframework/playframework/issues/3532) Don't throw exceptions from Assets.versioned controller on Windows
* [4429](https://github.com/playframework/playframework/issues/4429) Ensure message source name is not ignored when messages are passed
* [4488](https://github.com/playframework/playframework/issues/4488) Support primitive types in CacheApi
* [4522](https://github.com/playframework/playframework/issues/4522) Exclude externalised resources from application jar
* [4523](https://github.com/playframework/playframework/issues/4523) Char binding for path/query string parameters in routes
* [4524](https://github.com/playframework/playframework/issues/4524) Ensure CORS headers get added to failed responses
* [4616](https://github.com/playframework/playframework/issues/4616) Ensure routes is initialised with the right context classloader
* [4633](https://github.com/playframework/playframework/issues/4633) Revert changes to Json.deepMerge
* [4648](https://github.com/playframework/playframework/issues/4648) Improved Call fragment support in form template helpers
* [4699](https://github.com/playframework/playframework/issues/4699) Ensure Default and ExternalAssets controllers are injectable
* [4704](https://github.com/playframework/playframework/issues/4704) Ensure entity too long errors go through error handler
* [4705](https://github.com/playframework/playframework/issues/4705) Add convenience method for handling various 4xx client errors
* [4706](https://github.com/playframework/playframework/issues/4706) Propogate Java Action context changes to controller

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.4.1)

## Play 2.4.0 "Damiya"

*Released 26 May 2015*

* Dependency injection out of the box.
* Testing is easier thanks to better support for mocking.
* It is now straightforward to embed Play in your application.
* You can now aggregate reverse routers from multiple projects.
* More Java 8 integration —- Java 8 is now required.
* Choice of standard project layout.
* Many new anorm features. Anorm is now its own project!
* Upgraded to Ebean 4. Ebean is (also) its own project!
* HikariCP is the default connection pool
* WS supports Server Name Identification (SNI).

[Highlights](https://playframework.com/documentation/2.4.x/Highlights24)
[Migration Guide](https://playframework.com/documentation/2.4.x/Migration24)
[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.4.0)

## Play 2.3.10

*Released 3 August 2015*

* [3459](https://github.com/playframework/playframework/issues/3459) Assets controller returns 404 for directories
* [4240](https://github.com/playframework/playframework/issues/4240) Disabled RC4 ciphers in default WS SSL configuration
* [4432](https://github.com/playframework/playframework/issues/4432) Fix cookie max age greater than 24 days regression
* [4719](https://github.com/playframework/playframework/issues/4719) Correctly parse content type in test helpers

[Full changelog](https://github.com/playframework/playframework/issues?q=milestone%3A2.3.10)

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

## Play 1.4.2

*Released 16 March 2016*

**Vulnerabilities fixed**

* [20160301-XssSecureModule](/security/vulnerability/20160301-XssSecureModule) XSS vulnerability in the Secure module login page.

**Other changes**

* [#2008](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2008-error-while-binding-an-enumset) Error while binding an EnumSet
* [#2011](https://play.lighthouseapp.com/projects/57987/tickets/2011-support-double-semicolon-escaping-in-evolution-scripts) Support "double semicolon" escaping in evolution scripts
* [#2007](https://play.lighthouseapp.com/projects/57987/tickets/2007) JPA classloader issues with precompiled app
* [#2014](https://play.lighthouseapp.com/projects/57987/tickets/2014) Message not appear corretly in secure module
* [#1939](https://play.lighthouseapp.com/projects/57987/tickets/1939-update-to-groovy-24x) chore(lib): Update to groovy from 2.3.9 to 2.4.5
* [#1934](https://play.lighthouseapp.com/projects/57987/tickets/1934) feat(controller): Support for non-static controller methods - they are easier to mock/unit-test
* [#2009](https://play.lighthouseapp.com/projects/57987/tickets/2009) Field name detection in 'attachment'
* [#2017](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2017) Use StringBuilder instead of StringBuffer
* [#2016](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2016) default value of "XForwardedOverwriteDomainAndPort" setting should be false
* [#1979](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/1979) Improve performance of Play!
* [#2021](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2021) Optimize usage of StringBuilder
* [#2022](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2022) Add missing @Override annotations
* [#2020](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2020) Avoid multiple creation of new arrays/maps
* [#2029](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2029) Upgrade to groovy 2.4.6
* [#2027](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2027) Cache method Lang.getLocale()
* [#2015](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2015) i18n tag only restore the first % character
* [#2034](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2034) add hibernate-ehcache jar to be able to use ehcache as second level cache
* [#2026](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2026) Blob: missing methods implemented to support (2nd level) caching
* [#1947](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/1948) Job::withinFilter Only Calls First PlayPlugin Filter It Finds. (Invoker Calls All Filters)
* [#1948](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/1948) Invoker.Invocation::run executes action once per plugin
* [#2035](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2035) MakeWS.getString() returning same result on every call
* [#2019](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2019) Make method Cache.clear() null-safe
* [#2018](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2018) Class caches added
* [#2036](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2036) Force secure reverse routes config
* [#2039](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2039) Update some libraires

## Play 1.4.1

*Released 30 December 2015*

**Vulnerabilities fixed**

* [20151230-SessionHijack](/security/vulnerability/20151230-SessionHijack) Session hijack

**Other changes**

* Add PATCH support
* Update to htmlUnit v 2.19
* Add ability to define enabled ssl protocols
* Make DB properties configurable
* Fix putting property to customer DB configuration
* Add method Plugin.onActionInvocationFinally()
* Fix javadoc tools errors

## Play 1.4.0

*Released 30 October 2015*

* Compatible Java 7. No longer support for Java 6
* Upgrade to async-http-client v1.9.31
* Upgrade to netty 3.10.4
* Update HtmlUnit to v2.16

## Play 1.3.4

*Released 16 March 2016*

**Vulnerabilities fixed**

* [20160301-XssSecureModule](/security/vulnerability/20160301-XssSecureModule) XSS vulnerability in the Secure module login page.

**Other changes**

* [#2008](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2008-error-while-binding-an-enumset) Error while binding an EnumSet
* [#2011](https://play.lighthouseapp.com/projects/57987/tickets/2011-support-double-semicolon-escaping-in-evolution-scripts) Support "double semicolon" escaping in evolution scripts
* [#2007](https://play.lighthouseapp.com/projects/57987/tickets/2007) JPA classloader issues with precompiled app
* [#2014](https://play.lighthouseapp.com/projects/57987/tickets/2014) Message not appear corretly in secure module
* [#1934](https://play.lighthouseapp.com/projects/57987/tickets/1934) feat(controller): Support for non-static controller methods - they are easier to mock/unit-test
* [#2009](https://play.lighthouseapp.com/projects/57987/tickets/2009)  Field name detection in 'attachment'
* [#2017](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2017) Use StringBuilder instead of StringBuffer
* [#2016](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2016) default value of "XForwardedOverwriteDomainAndPort" setting should be false
* [#1979](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/1979) Improve performance of Play!
* [#2021](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2021) Optimize usage of StringBuilder
* [#2022](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2022) Add missing @Override annotations
* [#2020](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2020) Avoid multiple creation of new arrays/maps
* [#2027](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2027) Cache method Lang.getLocale()
* [#2015](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2015) i18n tag only restore the first % character
* [#2034](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2034) add hibernate-ehcache jar to be able to use ehcache as second level cache
* [#2026](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2026) Blob: missing methods implemented to support (2nd level) caching
* [#1947](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/1948) Job::withinFilter Only Calls First PlayPlugin Filter It Finds. (Invoker Calls All Filters)
* [#1948](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/1948) Invoker.Invocation::run executes action once per plugin
* [#2035](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2035) MakeWS.getString() returning same result on every call
* [#2019](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2019) Make method Cache.clear() null-safe
* [#2018](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2018) Class caches added
* [#2036](https://play.lighthouseapp.com/projects/57987-play-framework/tickets/2036) Force secure reverse routes config


## Play 1.3.3

*Released 30 December 2015*

**Vulnerabilities fixed**

* [20151230-SessionHijack](/security/vulnerability/20151230-SessionHijack) Session hijack

**Other changes**

* Add ability to define enabled ssl protocols
* Make DB properties configurable
* Fix putting property to customer DB configuration
* Add method Plugin.onActionInvocationFinally()
* Fix javadoc tools errors

## Play 1.3.2

*Released 30 October 2015*

* Add ability to define the timeout in testRunner module
* Add ability to manually set the VirtualHost of WS
* Improve performance of Router.reverse()
* Allow upload a 0B file
* add '--server' arg to install command commands python to specify just ONE custom repository for module installation
* Fix redirect to wrong domain and port behind apache
* Customize JSON rendering by passing the Gson serializer object json render
* Getting Static Initialization Deadlock in class DataParser dataparser
* Allow zero-length blobs binder
* OpenID discovery fails in 1.3.x in some cases openid
* Problem to run specific tests selenium testrunner tests
* 500.html template rendering issue
* play.libs.image.crop don't close the OutputStream image

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

## Play 1.2.6.2

*Released 30 December 2015*

**Vulnerabilities fixed**

* [20151230-SessionHijack](/security/vulnerability/20151230-SessionHijack) Session hijack


## Play 1.2.5.6

*Released 30 December 2015*

**Vulnerabilities fixed**

* [20151230-SessionHijack](/security/vulnerability/20151230-SessionHijack) Session hijack

