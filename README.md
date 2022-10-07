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
- local clone of playframework, with remote called origin, symlinked from data/main.
- local clone of play-generated-docs, with remote called origin, symlinked from data/generated.
  - `git clone -o origin https://github.com/playframework/play-generated-docs.git $PWD/data/generated`
- local clone of each doc translation in data/translationname, e.g. data/ja, data/tr, data/fr, data/bg. See application.conf for list of translation repos.
- If you run tests locally, you might want to temporary move your $HOME/.gitconfig file to have a "clean" environment, like `mv .gitconfig .gitconfig.bak`. That's because JGit is used and by default reads that user config file. This can cause problems when running tests, e.g. when you have signing activated in your ~/.gitconfig file. Unfortunatly there is no config nor env-varible to disable reading that config file, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=488777

Git should ignore the symlinked repos but you may need to tell your IDE to exclude them.

You may see exceptions in the logs about failing to authenticate, ignore them, they are just the periodic git fetch failing because the Play app doesn't have your GitHub credentials.

## Rebuilding the AWS Instance

If it is necessary to rebuild the instance, check this document: <https://github.com/playframework/play-meta/blob/main/playframework.com-machine-setup.md>.
