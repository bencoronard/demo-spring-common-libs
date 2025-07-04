.PHONY: test clean publish

test:
	./gradlew test

clean:
	./gradlew clean

publish:
	./gradlew publishToMavenLocal