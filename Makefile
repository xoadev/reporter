install-local:
	@./gradlew build publishToMavenLocal

deploy:
	@./gradlew build publish