The default root for postcode data is c:\apps\wwm-postcode or /lmdb/postcode on Linux. This can be changed with a .properties file.

Create the root directory, and add the following files:
PostZon.csv (extract and rename from PostZon_2005_2-PcodeComma.zip, which was in loki/files/Download/PAF Data at time of writing.)
jibble-postcodes.csv (in same location as above)

To set up for normal running:
Run the PostZonImporter launcher. The conversion should take a couple of minutes. Note any console output. Generates [postcode root]/postzon/*
Run the JibbleImporter launcher. Conversion takes a few seconds. Note any console output. Generates [postcode root]/jibble

To set up for random account generation and/or junit testing:
Run the above launchers.
Run RandomPostcodeImporter launcher. The conversion should take 30 secs. Note any console output. Generates [postcode root]/randomPostcodes

For updating what was once the Jibble database, I found this site useful for visualising the centre of a postcode 
inbound part (e.g. for CB23) http://www.doogal.co.uk/UKPostcodes.php?Search=CB23

