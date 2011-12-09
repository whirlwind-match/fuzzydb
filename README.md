JOIN THE COMMUNITY!
===================

Resource for documentation are a bit thin on the ground, but there are some great places to go for ideas as to how use fuzzydb.

The GUI example below is great for a bit of hands on play. We're aiming to get an online example up and running soon too.

There are a couple of sites using the database.  Feel free to check out www.fridgemountain.com and www.likemynds.com for how it's used there.

Our Mailing List
----------------

There's always a friendly response at hand on a mail group.  Any questions, and we're sure there are lots, do post them and we'll get them answered pronto.

The mailing list is fuzzydb-users@googlegroups.com.  You can join at: https://groups.google.com/group/fuzzydb-users



GETTING STARTED
===============

Check out the latest stable snapshot and build it:

    git clone git://github.com/whirlwind-match/fuzzydb.git
    git checkout stable
    mvn install


OTHER STUFF
============

 * Example webapp - https://github.com/whirlwind-match/fuzzydb-samples
 * Cutting a release - https://github.com/whirlwind-match/fuzzydb/wiki/Release-Instructions
	
This should pass all tests.

RUNNING THE DEMO UI
===================

There is a configurable demo UI available, which is currently packaged with an
example of helping people share car journeys (e.g. to work, or one off events
like going to a music festival).

To run the UI having built as above, type

    mvn exec:java -pl com.wwm.indexer.gui

You'll now be presented with a Java UI (no frills here ... yet).

Try this:

 * Click "Random entries" to create 1000 random journeys - this should take around a second
 * Click "Search" - you'll see a page of results and the time it took to find them
 * Look on the Map tab.  You'll see a clearly random set of journeys with the start represented 
   by a green dot and the end by a blue one
 * Now enter a short London postcode, e.g. N1 into the StartPostcode box
 * Hit "Search" again.  You'll see the journeys start in London now
 * Insert say another 20,000 records
 * Enter a postcode for the end location (e.g. M1 for Manchester), 
   and click Search. With any luck you'll find a few matches
 * In the dropdown box showing LiftShare (this box selects matching styles),
   select something else, e.g. PathDeviation, and search again.
 * Have a play, enjoy and let us know how you get on