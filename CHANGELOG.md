# 1.0.0.RELEASE

## New Features

- Implement @EnableFuzzyRepositories
- Support mapping java.util.Date into a fuzzydb record
- Support mapping org.bson.types.ObjectId into a fuzzydb record if on classpath
- Add fuzzydb.tld function taglib to ease use of displaying results


## Improvements

- Base Spring Data Support on Spring Data Commons 1.4.0
- Clean up java package structure - all now org.fuzzydb
- Introduce use of JSR305 @Nonnull, etc annotations
- Core cleanup:
    - Move Spring support to https://github.com/whirlwind-match/fuzzydb-spring/
    - Move Atom Publishing Protocol and old 'indexer' app to https://github.com/whirlwind-match/fuzzydb-extras/
- Add 2012 Olympics location to built-in UK postcode database (something to spin out to another project :)

## Defects

- Fix fuzzydb XSD incorrect tool:exports class


# 1.0.0.M4

## New Features

- Auto-expose Atom ReSTful support at http://localhost:9090/fuzz/ if Jetty and AtomServer are on classpath
- Support mapping of String containing "{lat,lon}" to GeoInformation for search and persistence
- Provide OptionsScorer for scoring enum sets against each other (e.g. how well do our hobbies match)
- Expose Random* and OptionsSource in util module - as not specific to our implementation, but often useful for others
- Add UUID support: RandomUuid + converters for String <-> UUID
- Allow command timeout to be configured, and default to 30 secs instead of previous 5 mins!
- When configured for persistence, ensure directories are writeable early - better for things like CloudFoundry


## Improvements

- Base Spring Data support on Spring Data Commons 1.3.0
- Use Google Guice for server runtime dependency injection
- Extract FileRepositoryStorageManager from Database and introduce interface to allow pluggable implementations
- Extract version state responsibility from Database to ServerTransactionCoordinator
- Make TxLog shutdown the responsibility of the ServerTransactionCoordinator
- Update various Maven plugins and provide versions such that releases possible using Maven 3

## Defects

- Fix PageUtils so hasNextPage works correctly


