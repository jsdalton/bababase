.DEFAULT_GOAL = run

test:
	lein caribou rollback resources/config/test.clj
	lein caribou migrate resources/config/test.clj
	export _JAVA_OPTIONS=-Denvironment=test; lein spec -a
	lein caribou rollback resources/config/test.clj

run:
	lein ring server

test-run:
	export _JAVA_OPTIONS=-Denvironment=test; lein ring server

# http://stackoverflow.com/questions/2145590/what-is-the-purpose-of-phony-in-a-makefile
.PHONY: test run

