Development Instructions
------------------------

Requirements for full installation:
- local clone of playframework, with remote called origin, symlinked from data/main.
- local clone of play-generated-docs, with remote called origin, symlinked from data/generated.
  - `git clone https://github.com/playframework/play-generated-docs.git $PWD/data/generated`
- local clone of each doc translation in data/translationname, e.g. data/ja, data/tr, data/fr, data/bg. See application.conf for list of translation repos.

Git should ignore the symlinked repos but you may need to tell your IDE to exclude them.

You may see exceptions in the logs about failing to authenticate, ignore them, they are just the periodic git fetch failing because the Play app doesn't have your GitHub credentials.

Requirements for minimal local installation (e.g. for previewing changes when releasing a new version of Play):

