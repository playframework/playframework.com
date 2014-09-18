Development Instructions
------------------------

To get the documentation served properly, clone Play to some location (can be the clone that you usually work with, playframework.com doesn't touch your working directory), then create a data directory in the repo (this will be ignored by git by default, but may not be ignored by your IDE so you may want to add it to your IDEs excluded folders), and then create a symlink in there called `main` that points to the Play clone.

You may see exceptions in the logs about failing to authenticate, ignore them, they are just the periodic git fetch failing because the Play app doesn't have your GitHub credentials.

If you want to test with Play 1 docs, Play 2.0.x docs, or the API docs, then you also need to create a symlink from that directory called `generated` that points to that clone.

Similarly for translations of the Play documentation, create a symlink for the name of the translation in that directory to a clone of the translation.

You will also need a mysql database called test setup locally.
