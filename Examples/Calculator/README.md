Calculator
========

This application is a simple Android Calculator application that has two modes: Simple and
Scientific mode. Each mode is represented by an Activity.

The application illustrates JPF-Android's ANY structures in its input
script to execute multiple event sequences. It also shows how jpf-android can
support multiple Activity switching.

The application is used to detect an Arithmetic exception, using jpf-android,
that is thrown by the application when we divide by zero or asks for the
square root of a negative number. Lastly it can detect a NullPointer
dereferencing exception when sending data between Activities.


Dependencies
-----------

The project makes use of [EvalEx](https://github.com/uklimaschewski/EvalEx), a Java Expression Evaluator to parse and evaluate expressions.

The project also depends on the jpf-android Checkpoint annotation. 



Building
--------

local.properties


This tool uses `gradle` as build tool. There is no need to manually install
`gradle`, a wrapper is provided. To build and install the tool, run

```
./gradlew

gradle build
```




Usage
-----



Configuration
-------------


License
-------

This sowtware is distributed under the terms of the Apache Software License 2.0.
See the LICENSE file for further details.
