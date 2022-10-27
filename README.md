# The Play Framework website

[![Twitter Follow](https://img.shields.io/twitter/follow/playframework?label=follow&style=flat&logo=twitter&color=brightgreen)](https://twitter.com/playframework)
[![Discord](https://img.shields.io/discord/931647755942776882?logo=discord&logoColor=white)](https://discord.gg/g5s2vtZ4Fa)
[![GitHub Discussions](https://img.shields.io/github/discussions/playframework/playframework?&logo=github&color=brightgreen)](https://github.com/playframework/playframework/discussions)
[![StackOverflow](https://img.shields.io/static/v1?label=stackoverflow&logo=stackoverflow&logoColor=fe7a16&color=brightgreen&message=playframework)](https://stackoverflow.com/tags/playframework)
[![YouTube](https://img.shields.io/youtube/channel/views/UCRp6QDm5SDjbIuisUpxV9cg?label=watch&logo=youtube&style=flat&color=brightgreen&logoColor=ff0000)](https://www.youtube.com/channel/UCRp6QDm5SDjbIuisUpxV9cg)
[![Twitch Status](https://img.shields.io/twitch/status/playframework?logo=twitch&logoColor=white&color=brightgreen&label=live%20stream)](https://www.twitch.tv/playframework)
[![OpenCollective](https://img.shields.io/opencollective/all/playframework?label=financial%20contributors&logo=open-collective)](https://opencollective.com/playframework)

[![Build Status](https://github.com/playframework/playframework.com/actions/workflows/build-test.yml/badge.svg)](https://github.com/playframework/playframework.com/actions/workflows/build-test.yml)
[![Repository size](https://img.shields.io/github/repo-size/playframework/playframework.com.svg?logo=git)](https://github.com/playframework/playframework.com)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/playframework/playframework.com&style=flat)](https://mergify.com)

## Development Instructions

Requirements for full installation:
- Local clone of playframework, with remote called origin, symlinked from `data/main`.
- Local clone of play-generated-docs, with remote called origin, symlinked from `data/generated`.
  - `git clone -o origin https://github.com/playframework/play-generated-docs.git $PWD/data/generated`
- Local clone of each doc translation in `data/<translation-name>`, e.g. `data/ja`, `data/tr`, `data/fr`, `data/bg`. See `application.conf` for a list of translation repos.
- If you run tests locally, you might want to temporary move your `$HOME/.gitconfig` file to have a "clean" environment, like `mv .gitconfig .gitconfig.bak`. That's because JGit is used and by default reads that user config file. This can cause problems when running tests, e.g. when you have signing activated in your `~/.gitconfig` file. Unfortunatly there is no config nor environment variable to disable reading that config file, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=488777

Git should ignore the symlinked repos but you may need to tell your IDE to exclude them.

You may see exceptions in the logs about failing to authenticate, ignore them, they are just the periodic git fetch failing because the Play app doesn't have your GitHub credentials.

## Deployment

The deployment process is described as part of a Play Framework release. See https://github.com/playframework/.github/blob/main/RELEASING.md

## Server setup

Currently the website is hosted in an AWS Instance sponsored by Lightbend.
If necessary to rebuild the instance, this is how it's done:

```sh

# Assuming you are in the $HOME folder
cd ~

# Clone this website repo
git clone -o origin https://github.com/playframework/playframework.com.git

# Install docs and its translation files
mkdir ~/play-docs-translations
cd ~/play-docs-translations
git clone -o origin https://github.com/playframework/play-generated-docs.git generated
git clone -o origin https://github.com/playframework-ja/translation-project.git ja
git clone -o origin https://github.com/PlayFrameworkTR/translation-project tr
git clone -o origin https://github.com/cheleb/playframework-fr fr
git clone -o origin https://github.com/antonsarov/translation-project bg

# Now you need to link the translation files from the ~/playframework.com/data folder:
cd ~/playframework.com/data/
ln -s ../../play-docs-translations/generated generated

# This should not be necessary anymore, but I keep it anyway:
# Clone the Play repo
#git clone -o origin https://github.com/playframework/playframework.git playframework
# Prepare the resources folder + obtain the core resource code
#cd playframework.com/data/
#ln -s ../../playframework main
```

You also need to download the play1 modules.
Use https://github.com/playframework/playframework.com/pull/443 to regenerate the modules list.

<details>
  <summary>Click to expand</summary>

```sh
cd ~/play-docs-translations
mkdir modules
cd modules

curl -X GET https://downloads.typesafe.com/play1/modules/router-head.zip -o router-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/router-1.0.zip -o router-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/router-1.1.zip -o router-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/router-1.2.zip -o router-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/router-1.3.zip -o router-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/sass-0.1.zip -o sass-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/sass-1.0.zip -o sass-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/sass-1.1.zip -o sass-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/guice-1.0.zip -o guice-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/guice-1.1.zip -o guice-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/guice-1.1.1.zip -o guice-1.1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/guice-1.2.zip -o guice-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gwt-1.0.zip -o gwt-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-1.0.zip -o siena-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-1.1.zip -o siena-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-1.2.zip -o siena-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-1.3.zip -o siena-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-1.4.zip -o siena-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-1.5.zip -o siena-1.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-2.0.0.zip -o siena-2.0.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-2.0.1.zip -o siena-2.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-2.0.2.zip -o siena-2.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-2.0.3.zip -o siena-2.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-2.0.4_work.zip -o siena-2.0.4_work.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-2.0.4.zip -o siena-2.0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-2.0.5.zip -o siena-2.0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-2.0.6.zip -o siena-2.0.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/siena-2.0.7.zip -o siena-2.0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/bespin-1.0.1.zip -o bespin-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cobertura-1.0.zip -o cobertura-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cobertura-1.1.zip -o cobertura-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cobertura-1.2.zip -o cobertura-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cobertura-2.0.zip -o cobertura-2.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cobertura-2.1.zip -o cobertura-2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cobertura-2.2.zip -o cobertura-2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cobertura-2.3.zip -o cobertura-2.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cobertura-2.4.zip -o cobertura-2.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cobertura-2.5.zip -o cobertura-2.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scala-0.7.zip -o scala-0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scala-0.7.1.zip -o scala-0.7.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scala-0.7.2.zip -o scala-0.7.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scala-0.7.3.zip -o scala-0.7.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scala-0.8.zip -o scala-0.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scala-0.9.zip -o scala-0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scala-0.9.1.zip -o scala-0.9.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/ecss-1.0.zip -o ecss-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/grizzly-head.zip -o grizzly-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/spring-1.0.zip -o spring-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/spring-1.0.1.zip -o spring-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/spring-1.0.2.zip -o spring-1.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/spring-1.0.3.zip -o spring-1.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/search-1.0.zip -o search-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/search-1.1.zip -o search-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/search-1.2.zip -o search-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/search-head.zip -o search-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/search-1.3.zip -o search-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/search-1.4.zip -o search-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/search-2.0.zip -o search-2.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gae-1.0.zip -o gae-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gae-1.0.1.zip -o gae-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gae-1.0.2.zip -o gae-1.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gae-1.0.3.zip -o gae-1.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gae-1.1.zip -o gae-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gae-1.4.zip -o gae-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gae-1.6.0_b1.zip -o gae-1.6.0_b1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gae-1.6.0.zip -o gae-1.6.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.2.zip -o japid-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.3.1.zip -o japid-0.3.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.5.1.zip -o japid-0.5.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.6.0.zip -o japid-0.6.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.7.zip -o japid-0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.7.1.zip -o japid-0.7.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.0.zip -o japid-0.8.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.1.zip -o japid-0.8.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.1.2.zip -o japid-0.8.1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.2.zip -o japid-0.8.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.3.0.zip -o japid-0.8.3.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.3.1.zip -o japid-0.8.3.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.4.1.zip -o japid-0.8.4.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.4.6.zip -o japid-0.8.4.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.4.8.zip -o japid-0.8.4.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.5.zip -o japid-0.8.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.5.1.zip -o japid-0.8.5.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.6.zip -o japid-0.8.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.7.zip -o japid-0.8.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.7.1.zip -o japid-0.8.7.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.8.zip -o japid-0.8.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.8.1.zip -o japid-0.8.8.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.8.2.zip -o japid-0.8.8.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.8.3.zip -o japid-0.8.8.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.8.4.zip -o japid-0.8.8.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.9.zip -o japid-0.8.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.9.1.zip -o japid-0.8.9.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.9.2.zip -o japid-0.8.9.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.9.3.zip -o japid-0.8.9.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.9.4.zip -o japid-0.8.9.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.9.5.zip -o japid-0.8.9.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.9.6.zip -o japid-0.8.9.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.9.7.zip -o japid-0.8.9.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.9.8.zip -o japid-0.8.9.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.8.10.zip -o japid-0.8.10.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.0.zip -o japid-0.9.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.1.zip -o japid-0.9.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.2.zip -o japid-0.9.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.2.1.zip -o japid-0.9.2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.2.2.zip -o japid-0.9.2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.2.3.zip -o japid-0.9.2.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.2.4.zip -o japid-0.9.2.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.2.5.zip -o japid-0.9.2.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.2.6.zip -o japid-0.9.2.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.2.7.zip -o japid-0.9.2.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.2.8.zip -o japid-0.9.2.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.3.1.zip -o japid-0.9.3.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.3.2.zip -o japid-0.9.3.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.3.4.zip -o japid-0.9.3.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.3.6.zip -o japid-0.9.3.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.3.7.zip -o japid-0.9.3.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.4.1.zip -o japid-0.9.4.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.4.2.zip -o japid-0.9.4.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.4.3.zip -o japid-0.9.4.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.5.zip -o japid-0.9.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/japid-0.9.10.zip -o japid-0.9.10.zip
curl -X GET https://downloads.typesafe.com/play1/modules/netty-1.0.zip -o netty-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/netty-1.0.1.zip -o netty-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/netty-1.0.2.zip -o netty-1.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/netty-1.0.3.zip -o netty-1.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/netty-1.0.4.zip -o netty-1.0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/netty-1.0.5.zip -o netty-1.0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/netty-1.0.6.zip -o netty-1.0.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/netty-1.0.7.zip -o netty-1.0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/pdf-0.2.zip -o pdf-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/pdf-0.6.zip -o pdf-0.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/pdf-0.7.zip -o pdf-0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/pdf-0.8.zip -o pdf-0.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/pdf-0.9.zip -o pdf-0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scalate-0.1.zip -o scalate-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scalate-0.2.zip -o scalate-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scalate-0.3.zip -o scalate-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scalate-0.7.2.zip -o scalate-0.7.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/stax-1.0.zip -o stax-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/stax-1.0.1.zip -o stax-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/oauth-1.0.zip -o oauth-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/migrate-1.0.zip -o migrate-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/migrate-1.1.zip -o migrate-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/migrate-1.2.zip -o migrate-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/migrate-1.3.zip -o migrate-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/migrate-1.3.1.zip -o migrate-1.3.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/migrate-1.3.2.zip -o migrate-1.3.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/migrate-1.4.zip -o migrate-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-0.1.zip -o greenscript-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.0a.zip -o greenscript-1.0a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.1d.zip -o greenscript-1.1d.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2d.zip -o greenscript-1.2d.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.2.zip -o greenscript-1.2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.3.zip -o greenscript-1.2.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.4.zip -o greenscript-1.2.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.5.zip -o greenscript-1.2.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.6g.zip -o greenscript-1.2.6g.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.6h.zip -o greenscript-1.2.6h.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.6i.zip -o greenscript-1.2.6i.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.6j.zip -o greenscript-1.2.6j.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.6k.zip -o greenscript-1.2.6k.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.6l.zip -o greenscript-1.2.6l.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.6m.zip -o greenscript-1.2.6m.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.7.zip -o greenscript-1.2.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.8.zip -o greenscript-1.2.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.8a.zip -o greenscript-1.2.8a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/greenscript-1.2.8b.zip -o greenscript-1.2.8b.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-0.1.zip -o excel-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.0.1.zip -o excel-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.1.zip -o excel-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.2.zip -o excel-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.2x.zip -o excel-1.2x.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.2.1.zip -o excel-1.2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.2.1x.zip -o excel-1.2.1x.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.2.2.zip -o excel-1.2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.2.2x.zip -o excel-1.2.2x.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.2.3x.zip -o excel-1.2.3x.zip
curl -X GET https://downloads.typesafe.com/play1/modules/excel-1.2.3.zip -o excel-1.2.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/ivy-1.0.zip -o ivy-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/ivy-1.0.1.zip -o ivy-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mongo-1.1.zip -o mongo-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mongo-1.3.zip -o mongo-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/akka-head.zip -o akka-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/vhost-1.0.zip -o vhost-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/vhost-1.0.1.zip -o vhost-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/vhost-1.0.2.zip -o vhost-1.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/vhost-1.0.3.zip -o vhost-1.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/vhost-1.0.4.zip -o vhost-1.0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/ebean-1.0.zip -o ebean-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/ebean-1.0.1.zip -o ebean-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/ebean-1.0.2.zip -o ebean-1.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/ebean-1.0.4.zip -o ebean-1.0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/ebean-1.0.5.zip -o ebean-1.0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/ebean-1.0.6.zip -o ebean-1.0.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cas-1.0.zip -o cas-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cas-2.0.zip -o cas-2.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cas-3.0.zip -o cas-3.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cas-3.1.zip -o cas-3.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/db-1.1.zip -o db-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/db-1.0.zip -o db-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/db-1.0.1.zip -o db-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/db-1.1.1.zip -o db-1.1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/objectify-1.0.zip -o objectify-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gwt2-1.1.zip -o gwt2-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gwt2-1.2.zip -o gwt2-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gwt2-1.3.zip -o gwt2-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gwt2-1.5.zip -o gwt2-1.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gwt2-1.6.zip -o gwt2-1.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gwt2-1.7.zip -o gwt2-1.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gwt2-1.8.zip -o gwt2-1.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/maven-1.0.zip -o maven-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/maven-head.zip -o maven-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/playapps-1.0.zip -o playapps-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/playapps-1.1.zip -o playapps-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/playapps-1.2.zip -o playapps-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/playapps-1.3.zip -o playapps-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/playapps-1.4.zip -o playapps-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/logisimayml-1.0.zip -o logisimayml-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/logisimayml-1.3.zip -o logisimayml-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/logisimayml-1.4.zip -o logisimayml-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/logisimayml-1.5.zip -o logisimayml-1.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/logisimayml-1.8.zip -o logisimayml-1.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scaffold-head.zip -o scaffold-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scaffold-0.1.zip -o scaffold-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/multidb-1.0.zip -o multidb-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/multidb-1.1.zip -o multidb-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/multidb-1.2.zip -o multidb-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.zip -o press-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.1.zip -o press-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.2.zip -o press-1.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.3.zip -o press-1.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.4.zip -o press-1.0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.5.zip -o press-1.0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.6.zip -o press-1.0.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.7.zip -o press-1.0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.8.zip -o press-1.0.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.9.zip -o press-1.0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.10.zip -o press-1.0.10.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.11.zip -o press-1.0.11.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.12.zip -o press-1.0.12.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.13.zip -o press-1.0.13.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.14.zip -o press-1.0.14.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.15.zip -o press-1.0.15.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.16.zip -o press-1.0.16.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.17.zip -o press-1.0.17.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.18.zip -o press-1.0.18.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.19.zip -o press-1.0.19.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.20.zip -o press-1.0.20.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.21.zip -o press-1.0.21.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.22.zip -o press-1.0.22.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.23.zip -o press-1.0.23.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.24.zip -o press-1.0.24.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.25.zip -o press-1.0.25.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.26.zip -o press-1.0.26.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.27.zip -o press-1.0.27.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.28.zip -o press-1.0.28.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.29.zip -o press-1.0.29.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.30.zip -o press-1.0.30.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.31.zip -o press-1.0.31.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.32.zip -o press-1.0.32.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.33.zip -o press-1.0.33.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.34.zip -o press-1.0.34.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.35.zip -o press-1.0.35.zip
curl -X GET https://downloads.typesafe.com/play1/modules/press-1.0.36.zip -o press-1.0.36.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cnm-1.0.zip -o cnm-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cnm-1.1.zip -o cnm-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cnm-2.0.zip -o cnm-2.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-1.0.zip -o crudsiena-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-1.01.zip -o crudsiena-1.01.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-1.1.zip -o crudsiena-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-1.2.zip -o crudsiena-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-1.3.zip -o crudsiena-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-1.4.zip -o crudsiena-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-1.5.zip -o crudsiena-1.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-1.6_play1.1.zip -o crudsiena-1.6_play1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-1.6_play1.2.zip -o crudsiena-1.6_play1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-2.0.0.zip -o crudsiena-2.0.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-2.0.1.zip -o crudsiena-2.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-2.0.2.zip -o crudsiena-2.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/crudsiena-2.0.3.zip -o crudsiena-2.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.1c.zip -o morphia-1.1c.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2beta.zip -o morphia-1.2beta.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.1d.zip -o morphia-1.1d.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2beta2.zip -o morphia-1.2beta2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2beta3.zip -o morphia-1.2beta3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2beta4.zip -o morphia-1.2beta4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2beta5.zip -o morphia-1.2beta5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2beta6.zip -o morphia-1.2beta6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.1beta1.zip -o morphia-1.2.1beta1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.1beta2.zip -o morphia-1.2.1beta2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.1beta3.zip -o morphia-1.2.1beta3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.1beta4.zip -o morphia-1.2.1beta4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.1beta5.zip -o morphia-1.2.1beta5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.1beta6.zip -o morphia-1.2.1beta6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.2beta1.zip -o morphia-1.2.2beta1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.3beta1.zip -o morphia-1.2.3beta1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.3c.zip -o morphia-1.2.3c.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.3d.zip -o morphia-1.2.3d.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.4.zip -o morphia-1.2.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.4a.zip -o morphia-1.2.4a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.4b.zip -o morphia-1.2.4b.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.4c.zip -o morphia-1.2.4c.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.4d.zip -o morphia-1.2.4d.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.5a.zip -o morphia-1.2.5a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.6a.zip -o morphia-1.2.6a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.7.zip -o morphia-1.2.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.8-RC1.zip -o morphia-1.2.8-RC1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.9.zip -o morphia-1.2.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.10.zip -o morphia-1.2.10.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.11.zip -o morphia-1.2.11.zip
curl -X GET https://downloads.typesafe.com/play1/modules/morphia-1.2.12.zip -o morphia-1.2.12.zip
curl -X GET https://downloads.typesafe.com/play1/modules/menu-1.0.zip -o menu-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/menu-1.0b.zip -o menu-1.0b.zip
curl -X GET https://downloads.typesafe.com/play1/modules/menu-1.0c.zip -o menu-1.0c.zip
curl -X GET https://downloads.typesafe.com/play1/modules/menu-1.1.zip -o menu-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/menu-1.1a.zip -o menu-1.1a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/menu-1.2.zip -o menu-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/paginate-head.zip -o paginate-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/paginate-0.1.zip -o paginate-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/paginate-0.11.zip -o paginate-0.11.zip
curl -X GET https://downloads.typesafe.com/play1/modules/paginate-0.12.zip -o paginate-0.12.zip
curl -X GET https://downloads.typesafe.com/play1/modules/paginate-0.13.zip -o paginate-0.13.zip
curl -X GET https://downloads.typesafe.com/play1/modules/paginate-0.14.zip -o paginate-0.14.zip
curl -X GET https://downloads.typesafe.com/play1/modules/i18ntools-1.0.zip -o i18ntools-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/i18ntools-1.0.1.zip -o i18ntools-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/recaptcha-0.9.zip -o recaptcha-0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/recaptcha-1.03.zip -o recaptcha-1.03.zip
curl -X GET https://downloads.typesafe.com/play1/modules/recaptcha-1.2.zip -o recaptcha-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/recaptcha-1.3.zip -o recaptcha-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/googleclosure-1.0.zip -o googleclosure-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/googleclosure-1.1.zip -o googleclosure-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mustache-0.1.zip -o mustache-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mustache-head.zip -o mustache-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mustache-0.2.zip -o mustache-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbconnect-0.1.zip -o fbconnect-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbconnect-head.zip -o fbconnect-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbconnect-0.2.zip -o fbconnect-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbconnect-0.3.zip -o fbconnect-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbconnect-0.4.zip -o fbconnect-0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbconnect-0.5.zip -o fbconnect-0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbconnect-0.6.zip -o fbconnect-0.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-1.1.zip -o liquibase-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-1.0.zip -o liquibase-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-1.0.3.zip -o liquibase-1.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-1.1.3.zip -o liquibase-1.1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.0.3.zip -o liquibase-2.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.1.0.zip -o liquibase-2.1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.0.4.zip -o liquibase-2.0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.1.1.zip -o liquibase-2.1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.1.2.zip -o liquibase-2.1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.2.zip -o liquibase-2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.3.zip -o liquibase-2.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.3.1.zip -o liquibase-2.3.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.4.zip -o liquibase-2.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/liquibase-2.4.1.zip -o liquibase-2.4.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jqvalidate-0.1.zip -o jqvalidate-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jqvalidate-0.2.zip -o jqvalidate-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jqvalidate-head.zip -o jqvalidate-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jqvalidate-0.3.zip -o jqvalidate-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/html5validation-1.0.zip -o html5validation-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/html5validation-1.1.zip -o html5validation-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/html5validation-1.2.zip -o html5validation-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/html5validation-1.2.2.zip -o html5validation-1.2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.0.zip -o deadbolt-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.0.1.zip -o deadbolt-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.1.zip -o deadbolt-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.2.zip -o deadbolt-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.2.1.zip -o deadbolt-1.2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.3.zip -o deadbolt-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.3.1.zip -o deadbolt-1.3.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.4.zip -o deadbolt-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.4.1.zip -o deadbolt-1.4.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.4.2.zip -o deadbolt-1.4.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.4.3.zip -o deadbolt-1.4.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.4.4.zip -o deadbolt-1.4.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.4.5.zip -o deadbolt-1.4.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.5.zip -o deadbolt-1.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.5.1.zip -o deadbolt-1.5.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.5.2.zip -o deadbolt-1.5.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.5.3.zip -o deadbolt-1.5.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/deadbolt-1.5.4.zip -o deadbolt-1.5.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/riak-head.zip -o riak-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/httpmock-1.1.zip -o httpmock-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbgraph-0.1.zip -o fbgraph-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbgraph-0.2.zip -o fbgraph-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbgraph-0.3.zip -o fbgraph-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fbgraph-0.4.zip -o fbgraph-0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/tabularasa-0.1.zip -o tabularasa-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/tabularasa-0.2.zip -o tabularasa-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/less-0.1.zip -o less-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/less-0.2.zip -o less-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/less-0.3.zip -o less-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/less-0.3.compatibility.zip -o less-0.3.compatibility.zip
curl -X GET https://downloads.typesafe.com/play1/modules/less-0.9.zip -o less-0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/less-0.9.1.zip -o less-0.9.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/messages-0.9.zip -o messages-0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/messages-1.0.zip -o messages-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/messages-1.1.zip -o messages-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/messages-1.1.1.zip -o messages-1.1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/messages-1.2.zip -o messages-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/messages-1.3.zip -o messages-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/resteasycrud-1.1.zip -o resteasycrud-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/resteasy-1.3.1.zip -o resteasy-1.3.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/securepermissions-1.2.1.zip -o securepermissions-1.2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/linkedin-0.2.1.zip -o linkedin-0.2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/antify-1.0.zip -o antify-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.0.1.zip -o elasticsearch-0.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.0.2.zip -o elasticsearch-0.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.0.3.zip -o elasticsearch-0.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.0.4.zip -o elasticsearch-0.0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.0.5.zip -o elasticsearch-0.0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.0.6.zip -o elasticsearch-0.0.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.0.7.zip -o elasticsearch-0.0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.0.8.zip -o elasticsearch-0.0.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.0.9.zip -o elasticsearch-0.0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.1.zip -o elasticsearch-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.2.zip -o elasticsearch-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.3.zip -o elasticsearch-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/elasticsearch-0.4.zip -o elasticsearch-0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/featureflags-0.1.zip -o featureflags-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/featureflags-head.zip -o featureflags-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/table-1.0.zip -o table-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/table-1.2.zip -o table-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/table-1.2.1.zip -o table-1.2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/table-1.2.2.zip -o table-1.2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gravatar-1.0.zip -o gravatar-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gravatar-1.0.1.zip -o gravatar-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/gravatar-1.0.2.zip -o gravatar-1.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/carbonate-0.8.zip -o carbonate-0.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/carbonate-0.9.zip -o carbonate-0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/carbonate-0.9.1.zip -o carbonate-0.9.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/carbonate-0.9.2.zip -o carbonate-0.9.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cloudbees-0.1.zip -o cloudbees-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cloudbees-0.2.zip -o cloudbees-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cloudbees-0.2.1.zip -o cloudbees-0.2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cloudbees-0.2.2.zip -o cloudbees-0.2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/coffee-0.9.zip -o coffee-0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/coffee-1.0.zip -o coffee-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.0.1.zip -o rabbitmq-0.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.0.2.zip -o rabbitmq-0.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.0.3.zip -o rabbitmq-0.0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.0.4.zip -o rabbitmq-0.0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.0.5.zip -o rabbitmq-0.0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.0.6.zip -o rabbitmq-0.0.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.0.7.zip -o rabbitmq-0.0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.0.8.zip -o rabbitmq-0.0.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.0.9.zip -o rabbitmq-0.0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.2.zip -o rabbitmq-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rabbitmq-0.3.zip -o rabbitmq-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jpagen-1.0.zip -o jpagen-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jpagen-1.0.2.zip -o jpagen-1.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/dotcloud-0.1.zip -o dotcloud-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/dotcloud-0.2.zip -o dotcloud-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/dotcloud-0.3.zip -o dotcloud-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/log4play-0.0.1.zip -o log4play-0.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/log4play-0.0.2.zip -o log4play-0.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/log4play-0.3.zip -o log4play-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/log4play-0.4.zip -o log4play-0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/log4play-0.5.zip -o log4play-0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/betterlogs-1.0.zip -o betterlogs-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/qunit-1.0.zip -o qunit-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/hazelcast-0.1.zip -o hazelcast-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/hazelcast-0.2.zip -o hazelcast-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/hazelcast-0.2.1.zip -o hazelcast-0.2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/hazelcast-0.2.2.zip -o hazelcast-0.2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/hazelcast-0.2.3.zip -o hazelcast-0.2.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/hazelcast-0.3.zip -o hazelcast-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/hazelcast-0.4.zip -o hazelcast-0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/hazelcast-0.5.zip -o hazelcast-0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/hazelcast-0.6.zip -o hazelcast-0.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jersey-0.1.zip -o jersey-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/twig-0.3.zip -o twig-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/navigation-0.1.zip -o navigation-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/camel-0.1.zip -o camel-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/camel-0.1.1.zip -o camel-0.1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/camel-0.1.2.zip -o camel-0.1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/camel-0.2.zip -o camel-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/associations-1.0.zip -o associations-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/associations-1.0.1.zip -o associations-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/markdown-1.0.zip -o markdown-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/markdown-1.1.zip -o markdown-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/markdown-1.2.zip -o markdown-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/markdown-1.3.zip -o markdown-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/markdown-1.4.zip -o markdown-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/markdown-1.5.zip -o markdown-1.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/markdown-1.6.zip -o markdown-1.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/markdown-1.7.zip -o markdown-1.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/markdown-1.8.zip -o markdown-1.8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/playerrors-0.1.zip -o playerrors-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/playerrors-0.2.zip -o playerrors-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scalasecure-0.1.zip -o scalasecure-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/scalagen-0.1.zip -o scalagen-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cream-0.1.zip -o cream-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cargo-0.1.zip -o cargo-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/webdrive-0.1.zip -o webdrive-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/webdrive-0.2.zip -o webdrive-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/orientdb-0.1.zip -o orientdb-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/orientdb-0.1.1.zip -o orientdb-0.1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cloudfoundry-0.1.zip -o cloudfoundry-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cloudfoundry-0.2.zip -o cloudfoundry-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cloudfoundry-0.3.zip -o cloudfoundry-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cloudfoundry-0.4.zip -o cloudfoundry-0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cloudfoundry-0.5.zip -o cloudfoundry-0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/redis-0.2.zip -o redis-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/redis-0.3.zip -o redis-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mockito-0.1.zip -o mockito-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/useragentcheck-0.1.zip -o useragentcheck-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/useragentcheck-0.2.zip -o useragentcheck-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/useragentcheck-0.3.zip -o useragentcheck-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/useragentcheck-0.4.zip -o useragentcheck-0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/capistrano-1.0.0.zip -o capistrano-1.0.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/capistrano-1.0.1.zip -o capistrano-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jqueryui-1.0.zip -o jqueryui-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/nemrod-0.1.zip -o nemrod-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/nemrod-0.2.zip -o nemrod-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/nemrod-0.3.zip -o nemrod-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/nemrod-0.4.zip -o nemrod-0.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/nemrod-0.5.zip -o nemrod-0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jqvalidation-head.zip -o jqvalidation-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jqvalidation-1.1.zip -o jqvalidation-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/chronostamp-0.1.zip -o chronostamp-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/pusher-0.1.zip -o pusher-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/accesslog-1.0.zip -o accesslog-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/accesslog-1.1.zip -o accesslog-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/accesslog-1.2.zip -o accesslog-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/reverseproxy-0.1.zip -o reverseproxy-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/securesocial-0.1.zip -o securesocial-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/securesocial-0.2.zip -o securesocial-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/securesocial-0.2.1.zip -o securesocial-0.2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/securesocial-0.2.2.zip -o securesocial-0.2.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/securesocial-0.2.3.zip -o securesocial-0.2.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/securesocial-0.2.4.zip -o securesocial-0.2.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/securesocial-0.2.5.zip -o securesocial-0.2.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/securesocial-0.2.6.zip -o securesocial-0.2.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mocha-0.1.zip -o mocha-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/force-0.2.zip -o force-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/force-0.3.zip -o force-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/force-0.5.zip -o force-0.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/force-0.7.zip -o force-0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/shibboleth-1.1.zip -o shibboleth-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/shibboleth-head.zip -o shibboleth-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/shibboleth-1.2.zip -o shibboleth-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/cheese-head.zip -o cheese-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/s3blobs-0.1.zip -o s3blobs-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/s3blobs-0.2.zip -o s3blobs-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/heroku-0.2.zip -o heroku-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/heroku-0.3.zip -o heroku-0.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jpastats-0.9.zip -o jpastats-0.9.zip
curl -X GET https://downloads.typesafe.com/play1/modules/formee-0.1.zip -o formee-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/neo4j-1.0RC1.zip -o neo4j-1.0RC1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/neo4j-1.0RC2.zip -o neo4j-1.0RC2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/neo4j-1.0.zip -o neo4j-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/neo4j-1.0.1.zip -o neo4j-1.0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/neo4j-1.0.2.zip -o neo4j-1.0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jelastic-0.1.zip -o jelastic-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/jelastic-0.2.zip -o jelastic-0.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/constretto-0.1.1.zip -o constretto-0.1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/springtester-0.1.zip -o springtester-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/springtester-1.0.zip -o springtester-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/springtester-1.1.zip -o springtester-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/springtester-1.6.zip -o springtester-1.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/springtester-2.0.zip -o springtester-2.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/springtester-2.1.zip -o springtester-2.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/openebay-0.1.zip -o openebay-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/postmark-1.0.zip -o postmark-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/openshift-0.1.1.zip -o openshift-0.1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/openshift-0.1.2.zip -o openshift-0.1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/openshift-0.2.0-RC1.zip -o openshift-0.2.0-RC1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fastergt-1.0.zip -o fastergt-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fastergt-1.1.zip -o fastergt-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fastergt-1.2.zip -o fastergt-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fastergt-1.3.zip -o fastergt-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fastergt-1.4.zip -o fastergt-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fastergt-1.5.zip -o fastergt-1.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fastergt-1.6.zip -o fastergt-1.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/fastergt-1.7.zip -o fastergt-1.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/bhave-0.7.zip -o bhave-0.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/bhave-head.zip -o bhave-head.zip
curl -X GET https://downloads.typesafe.com/play1/modules/bhave-0.7.1.zip -o bhave-0.7.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/bhave-0.72.zip -o bhave-0.72.zip
curl -X GET https://downloads.typesafe.com/play1/modules/browserid-0.1.zip -o browserid-0.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/thymeleaf-1.2.zip -o thymeleaf-1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/thymeleaf-1.1.zip -o thymeleaf-1.1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/thymeleaf-1.0.zip -o thymeleaf-1.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/thymeleaf-1.3.zip -o thymeleaf-1.3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/thymeleaf-1.4.zip -o thymeleaf-1.4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mybatisplay-0.1.2.zip -o mybatisplay-0.1.2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mybatisplay-0.2.0.zip -o mybatisplay-0.2.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/mybatisplay-0.3.0.zip -o mybatisplay-0.3.0.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.4g.zip -o rythm-0.9.4g.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.5.zip -o rythm-0.9.5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.5a.zip -o rythm-0.9.5a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.5b.zip -o rythm-0.9.5b.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.6.zip -o rythm-0.9.6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.6a.zip -o rythm-0.9.6a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.6b.zip -o rythm-0.9.6b.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.6c.zip -o rythm-0.9.6c.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.7.zip -o rythm-0.9.7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-0.9.7a.zip -o rythm-0.9.7a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-RC1.zip -o rythm-1.0.0-RC1.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-RC2.zip -o rythm-1.0.0-RC2.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-RC3.zip -o rythm-1.0.0-RC3.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-RC4.zip -o rythm-1.0.0-RC4.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-RC5.zip -o rythm-1.0.0-RC5.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-RC6.zip -o rythm-1.0.0-RC6.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-RC7.zip -o rythm-1.0.0-RC7.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-RC8.zip -o rythm-1.0.0-RC8.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120630.zip -o rythm-1.0.0-20120630.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120703.zip -o rythm-1.0.0-20120703.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120704.zip -o rythm-1.0.0-20120704.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120704a.zip -o rythm-1.0.0-20120704a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120716.zip -o rythm-1.0.0-20120716.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120717.zip -o rythm-1.0.0-20120717.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120718.zip -o rythm-1.0.0-20120718.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120806b.zip -o rythm-1.0.0-20120806b.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120814a.zip -o rythm-1.0.0-20120814a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20120815a.zip -o rythm-1.0.0-20120815a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20121103a.zip -o rythm-1.0.0-20121103a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20121106.zip -o rythm-1.0.0-20121106.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20121106a.zip -o rythm-1.0.0-20121106a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20121110.zip -o rythm-1.0.0-20121110.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20121110a.zip -o rythm-1.0.0-20121110a.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20121128.zip -o rythm-1.0.0-20121128.zip
curl -X GET https://downloads.typesafe.com/play1/modules/rythm-1.0.0-20121210.zip -o rythm-1.0.0-20121210.zip
curl -X GET https://downloads.typesafe.com/play1/modules/externalconfig-0.2.zip -o externalconfig-0.2.zip
```
</details>
