# Community Process

The purpose of this page is to make transparent the process by which decisions are made in Play Framework.  This is not a set of laws governing the Play project, nor is anything in this document new, it simply acknowledges the process that is already in place, and documents what it is.

The goal of this page is to increase community contribution and sense of ownership over the Play project, through doing the following:

* Making it clear and transparent to people who are new to the Play community how decisions are made, who makes those decisions, and how new people can attain any decision making responsibility.
* Providing a concrete definition of the decision making process in Play, so that it can be referred to and improved upon, should the need arise.

## Project ownership

The Play project source code is owned and licensed by [Lightbend](https://www.lightbend.com).  Lightbend employees a team of developers who work on Play full time, and has the final say on both product decisions and technical decisions for the project.

Though Lightbend wholly owns the Play project and has the final say in any decisions made, Lightbend does not own the Play community, nor does Lightbend take for granted its existence.  The Play community is arguably just as important, if not more important, than the Play project itself.  With over 400 community contributors to the core of Play, and hundreds more to the broader Play ecosystem, the Play community could easily survive without or in spite of Lightbend, if it ever came to that.

For this reason, Lightbend's relationship to the Play project is best described as a stewardship, Lightbend manages the Play project, but is held accountable by the Play community.

For a statement of how Lightbend views open source, see the [Lightbend Open Source Position Statement](http://lightbend.com/open-source-position-statement).

## Definitions

### Contributors

A contributor is anyone that makes a contribution to Play.  This does not necessarily mean code contributions, it could mean any of the following:

* Code fixes, improvements and new features
* Documentation fixes, improvements and new features
* Translating documentation into other languages
* Writing or maintaining plugins, modules or libraries for Play
* Code reviews in GitHub
* Raising, triaging and adding additional information to help resolve issues
* Taking part in design and feature discussions in the Discuss forum
* Answering questions in the Discuss forum and in stack overflow
* Running, speaking at or contributing to user groups focussed on Play Framework
* Speaking at conferences, blogging about or otherwise promoting Play Framework

### Integrators

An integrator is anyone with write access to the source code and documentation of the Play project, or one of the projects that come under the [playframework](https://github.com/playframework) GitHub organization.  A current list of all integrators can be found on the [code and contributors](/code) page.

It should be noted that you do not have to be an integrator to contribute to Play, and in fact there is nothing in the list of things considered to be contributions that you can't do if you are not a integrator.  In practice, the only thing that integrators can do that contributors can't are adminstration type tasks, such as merging contributions from other contributors, and house keeping tasks in the issue tracker such as closing fixed or invalid issues.

## Decision making

Decisions in the Play project fall in two broad categories:

* Implementation decisions, this includes whether a pull request is up to standard to be merged, how a feature or improvement should be implemented.
* Design and house keeping decisions, aka everything else.  This includes major design decisions, road maps, release schedules, decisions about how the project should be run and managed, what tools should be used, etc.

### Implementation decisions

Implementation decisions primarily happen in pull requests.  They are initiated by the pull request itself, and through reviews and iteration, a consensus is formed for how the given change should be implemented.

All interested parties are encouraged to involve themselves in reviewing pull requests and contributing to review discussions.

The amount of consensus required for whether a pull request is merged or not depends on how much impact the pull request has.  For trivial changes, such as corrections to documentation, an integrator may simply merge it with no feedback from any other integrators.  For larger changes, at least one person who is familiar with the part of the code being modified should review it, preferably more.  For big refactorings, the pull request should be reviewed by at least 2 or 3 other integrators before it is merged.

Whether a pull request is merged or not depends on many factors, including:

* Appropriate level of test coverage and documentation, where necessary
* Adherence to coding standards and other code quality factors
* Adherence to general architecture guidelines for Play, for example, feature parity between Java and Scala APIs
* Adherence to external specifications such as RFCs
* Consistency with the direction and philosophy of the Play project

### Design and house keeping decisions

The primary place for discussion about the design of Play and how the Play project is run is the [Play Framework Forum](https://github.com/playframework/playframework/discussions).  All major new features, refactorings or changes to the project should first be discussed in this forum.  The aim of the discussions is to reach an understanding on whether the task will be done, and how it will be done.  When a new topic is posted, interested parties are encouraged to comment with their affirmation or concerns.

While Lightbend ultimately has the last word on all decisions here, as much as possible we will endeavor to reach a consensus in the majority of the community.

## Integrator selection

Integrator selection is made by Lightbend.  Lightbend will offer contributors integrator status based on the following criteria:

* The contributor has made substantial contributions to Play.  What makes a substantial contribution is subjective, but for example, recent new integrators have been reviewing 3 or more pull requests per week, making one or more pull request per week, and triaging 3 or more issues a week.
* The contributor is an exemplar of both Play's [code of conduct](https://www.lightbend.com/conduct) and [contributor guidelines](/contributing).
* The contributor is well respected by other members of the Play community.
* The contributor agrees to the rules set out by Lightbend below for integrators.

Lightbend may also select integrators from its own staff.

If an integrator stops contributing regularly to Play, their write access may be removed, though their membership in the Play GitHub organisation will still be maintained.

## Rules for integrators

All integrators should follow the processes outlined on this page, and should be an example to follow with regards to Play's [code of conduct](https://www.lightbend.com/conduct) and [contributor guidelines](/contributing).  There are also a few specific rules, outlined below:

* An integrator must never push directly to a repository in the GitHub organisation.  All contributions, no matter how small, must be made through pull requests.  The only exceptions to this are when backporting changes, and changes that arise from cutting a release.
* Generally, pull requests should be made from integrators' personal forks, not from branches push to repositories in the playframework GitHub organisation.
* An integrator must never merge their own pull requests.  All pull requests must be reviewed and merged by another integrator.
* Documentation changes may be backported between releases as desired, however all other changes must by approved by Lightbend.
* Integrators must enforce Lightbend's CLA requirements when merging pull requests.
* When closing issues or pull requests, remember that we are dealing with people.  Be kind and helpful, pointing people in the right direction.  For example, if someone raises an issue that is really a question, when closing the issue, direct them to the [Play Framework Forum](https://github.com/playframework/playframework/discussions) and if possible, a brief answer to the question.

