
Extracting "UK Full" functionality
===================================

* Add interface which provides
public PostcodeData lookup( String postcode );

* Have this as a registerable OSGI service, implemented by the Full postcode software in another bundle.

* Add a SimpleDAO instance as an OSGi Service which implements a DAO, and register it as the PostcodeCountDAO

* Change Postcode to lookup these OSGi services

* Change WWDemo, SearchDemo etc ... WEBSITE!!! to run within OSGi framework ...  ARGH... not easy!
** OR ** find a non-OSGi parallel we can use as a bodge




The default root for postcode data is c:\lmpostcode or /lmdb/postcode on Linux. This can be changed with a .properties file.

Create the root directory, and add the following files:
PostZon.csv (extract and rename from PostZon_2005_2-PcodeComma.zip, which was in loki/files/Download/PAF Data at time of writing.)
jibble-postcodes.csv (in same location as above)

To set up for normal running:
Run the PostZonImporter launcher. The conversion should take a couple of minutes. Note any console output. Generates [postcode root]/postzon/*
Run the JibbleImporter launcher. Conversion takes a few seconds. Note any console output. Generates [postcode root]/jibble

To set up for random account generation and/or junit testing:
Run the above launchers.
Run RandomPostcodeImporter launcher. The conversion should take 30 secs. Note any console output. Generates [postcode root]/randomPostcodes

- ac 14/9/05
