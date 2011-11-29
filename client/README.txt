
You'll notice that there is a package tree which has come across, almost identically, from DBv1 (DbShared).

Anything under the 'likemynds' package is, for now, stuck as is, such that we can see that it is identical to it's DBv1 version.

Objects of this sort were not DbObjects in themselves, but often were part of a composite DbObject (whether configuration, or part of the index)

For this reason, they have needed little or no modification, as they know nothing of database references and structure.  All they need to do is to clone themselves.

