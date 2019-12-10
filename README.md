# CS474 - Course Project
## Team members
1. Maithreyi Rajagopalan
2. Sherryl Mathew George
## Overview
The requirements of this project are:

1. An instrumentation program that takes syntactically correct source code of some Java application and using the Eclipse Java Abstract Syntax Tree (AST) parser parse this application into an AST

2. For each expression and statement in each scope, the program will insert an instrumenting statement to capture the values of the variables. 

3. Once the application is instrumented, it will be compiled and run using a build script

4. Once instrumented and compiled, the rogram will run multiple instances of the instrumented application with different input values by starting the JVM. The trace information from the executing Java application is send to the launcher program where a hashtable is used to keep track of the variables, their bindings and values at different points of the program execution.
## Prerequisites
1. JAVA SDK 11 or higher
2. Scala 2.13 or above

**Note:** The project is developed and tested on a Linux based OS(Ubuntu to be exact).
## How to run
The project has two main files:

1. ServerLaunch - Launches the IPC server to listen to communications

2. InstrumLauch - Launches the instrumentation for a selected project

Below are the steps to be followed to run the project in Intellij:

1. Run the file `ServerLauch.scala`. This should start a server at `127.0.0.1:8080`. Make sure that this port is free  or else the server won't start. This file should be run first and has to be started only once for running any number of instrumentations. Donot close the terminal window in which this file is run

2. Run the file `InstrumLaunch.scala` to run the instrumentation. You will be provided with a list of project configuration files available to the instrumenter. You can choose which project to run. Running the instrumenter for more than once on thesame source file may cause unexpected results. This project already has a `Pathfinder` and `Matrix Rotation` program which can be used for test purposes. 

3. After each run two timestamped files will be found in the `tracefiles` directory in the project root. The file with `binding_` will have the formatted data of the hashtable for each variable and `trace_` will have running data of eachinstrum statement executed.

`sbt` command line can also be used to run the project. Running the project from the root will provide you with option to select which class has to run. Run both the files in two separate terminals in the order described above.

**Note** In case you encounter an error during compilation regarding missing dependencis of `com.sun.jdi.*` make sure your project is set to use JAVA 11.
To run from command line use:
`sbt clean compile run`

To test from command line use:
`sbt clean compile test`

## Basic Troubleshooting
1. This project needs port `8080` to be free to run. If you get an `Address already in use` make sure to free the port. For a Unix system you can run the command `sudo lsof -i:8080` and then kill the PID blocking the port.
2. If you get a `Failed to connect to 'localhost:8080'` error when running the `InstrumLaunch` make sure you have run `ServerLaunch` and the same is running.

## Applications used to run the instrumentations
1. [Shortest Path Finding Algorithm](https://github.com/Suwadith/A-Star-Shortest-Pathfinding-Algorithm-Square-Grid-Java)
2. [Inplace Matrix Rotation](https://www.geeksforgeeks.org/inplace-rotate-square-matrix-by-90-degrees/)

## Detailed Documentation
Detailed design documentation can be found [here](report/report.pdf)

## Known Issues
1. The code is not tested on a Mac OS. We tried testing on a Mac based VM and the JVM launch failed with a "VM failed to initialize error". We could not find a solution for this as we do not own a MAC.
2. There was an issue with launching the JVM from the code when running on a Windows OS. We figured out that this was due to windows using ; in the class lib path as opposed to a : on LINUX. This has been fixed. 