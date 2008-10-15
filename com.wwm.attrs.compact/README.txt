This project contains two different approaches to storing attributes in a more compact form.

The benefits are:
- Serialisation between client and server is quicker
- Serialisation to/from disk is quicker
- Scoring is likely to be quicker, as the data being scored will be on the same cache line 
  (assuming Compact-aware scorers are used)
  
  
TODO:
- Write more Compact-aware scorers
- Tests