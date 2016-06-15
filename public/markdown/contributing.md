# Play contributor guidelines

## Reporting issues

If you wish to report an issue for Play Framework, please ensure you have done the following things:

* If it is a documentation issue with a simple fix, don't raise an issue, just edit the documentation yourself directly in GitHub and submit a pull request.  This will be quicker for you and everybody.
* If you are not 100% sure that it is a bug, then ask about it on the [mailing list](http://groups.google.com/forum/#!forum/play-framework) first.  You will get a lot more help a lot quicker on the mailing list if you raise it there.  The issue tracker is for verified bugs, not for questions.
* If you have a feature request, please raise it on the [developer mailing list](https://groups.google.com/forum/#!forum/play-framework-dev) first.  The mailing list is the best forum to discuss new features, and it may be that Play already provides something to achieve what you want to achieve and you didn't realise.
* If you are sure you have found a bug, then raise an issue.  Please be as specific as possible, including sample code that reproduces the problem, stack traces if there are any exceptions thrown, and versions of Play, OS, Java, etc.

When the above guidelines are not followed, a Play integrator may close the issue, directing you to the appropriate forum for further discussion.

## Contributing changes

### Prerequisites

Before making a contribution, it is important to make sure that the change you wish to make and the approach you wish to take will likely be accepted, otherwise you may end up doing a lot of work for nothing.  If the change is only small, for example, if it's a documentation change or a simple bugfix, then it's likely to be accepted with no prior discussion.  However, new features, or bigger refactorings should first be discussed on the [developer mailing list](https://groups.google.com/forum/#!forum/play-framework-dev).  Additionally, any issues with the [community label](https://github.com/playframework/playframework/issues?q=is%3Aopen+is%3Aissue+label%3Acommunity) have been agreed to be a change that will likely be accepted.

### Procedure

1. Make sure you have signed the [Lightbend CLA](http://www.lightbend.com/contribute/cla); if not, sign it online.
2. Ensure that your contribution meets the following guidelines:
    1. Live up to the current code standard:
        - Not violate [DRY](http://programmer.97things.oreilly.com/wiki/index.php/Don%27t_Repeat_Yourself).
        - [Boy Scout Rule](http://programmer.97things.oreilly.com/wiki/index.php/The_Boy_Scout_Rule) needs to have been applied.
    2. Regardless of whether the code introduces new features or fixes bugs or regressions, it must have comprehensive tests.  This includes when modifying existing code that isn't tested.
    3. The code must be well documented in the Play standard documentation format (see the [documentation guidelines](/documentation/latest/Documentation).)  Each API change must have the corresponding documentation change.
    4. Implementation-wise, the following things should be avoided as much as possible:
        * Global state
        * Public mutable state
        * Implicit conversions
        * ThreadLocal
        * Locks
        * Casting
        * Introducing new, heavy external dependencies
    5. The Play API design rules are the following:
        * Play is a Java and Scala framework, make sure any changes have feature parity in both the Scala and Java APIs.
        * Java APIs should go to `framework/play/src/main/java`, package structure is `play.myapipackage.xxxx`
        * Scala APIs should go to `framework/play/src/main/scala`, where the package structure is `play.api.myapipackage`
        * Features are forever, always think about whether a new feature really belongs to the core framework or if it should be implemented as a module
        * Code must conform to standard style guidelines and pass all tests (see [validatePullRequest](https://github.com/playframework/playframework/blob/master/framework/validatePullRequest))
    6. New files must:
        * Have a Lightbend copyright header in the style of ``Copyright (C) 2009-2016 Lightbend Inc. <http://www.lightbend.com>``.
        * Not use ``@author`` tags since it does not encourage [Collective Code Ownership](http://www.extremeprogramming.org/rules/collective.html).
3. Ensure that your commits are squashed.  See [working with git](/documentation/latest/WorkingWithGit) for more information.
4. Submit a pull request.

If the pull request does not meet the above requirements then the code should **not** be merged into master, or even reviewed - regardless of how good or important it is. No exceptions.

The pull request will be reviewed according to the [implementation decision process](community-process#Implementation-decisions).

## Backporting policy

Generally, all bug fixes, improvements and new features will go to the master branch.  Backports and other commits to stable branches will only be accepted if they meet the following conditions:

* The change only affects the documentation
* The change fixes a regression that was introduced in a previous stable release from that branch
* The change fixes a bug that impacts significant number of members of the open source community with no simple work arounds available
* Any other reason that Lightbend deems appropriate

All backports and other commits to stable branches, in addition to satisfying the regular contributor guidelines, must also be binary and source compatible with previous releases on that branch.  The only exception to this is if a serious bug is impossible to fix without breaking the API, for example, a particular feature is not possible to use due to flaws in the API.

