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


