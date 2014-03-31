ScriptTranslator
======

Translator for converting jpf-android scripts into monkeyrunner scripts.

Build status: [![Build Status](https://drone.io/bitbucket.org/heila/jpf-android/status.png)](https://drone.io/bitbucket.org/heila/jpf-android/latest)

Building
--------

This tool uses `gradle` as build tool. There is no need to manually install
`gradle`, a wrapper is provided. To build and install the tool, run

```
./gradlew installApp
```

You can then add to your `PATH` the following directory:

```bash
$project_base_directory/build/install/translator/bin
```

in order to have the `translator` tool on your path.

Usage
-----

The tool is invoked on the command line in the following way:

```
translate jpf_script monkey_script
```

where `jpf_script` is a jpf-android configuration file and `monkey_script` is the name of the output monkryrunner script.

Configuration
-------------


License
-------

This sowtware is distributed under the terms of the Apache Software License 2.0.
See the LICENSE file for further details.
