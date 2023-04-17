#!/bin/bash

sbt compile

sbt flywayMigrate

sbt run
