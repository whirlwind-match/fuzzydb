package com.wwm.db;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Map;

import com.wwm.db.annotations.Key;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.KeyCollisionException;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultSet;
import com.wwm.db.query.RetrieveSpec;
import com.wwm.db.query.RetrieveSpecResult;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.expressions.LogicExpr;

/**
 * Interface against which CRUD operations can be performed in Whirlwind
 * 
 * @author Neale
 */
public interface DataOperations {

// Namespace
	
	/**Sets the namespace filter for this transaction to the specified value.
	 * New transactions use the default namespace from the creating Store, this state may be restored by calling this function and passing null.
	 * The namespace selects the scope of create, search and query operations. Explicit retrivals, updates etc. that specify
	 * an object by ref or otherwise identify the namespace are unaffected.
	 * If you're very lucky you might be able to use a regex here to select a set of namespaces. In which case the name 'default' will refer to the default (usually null) namespace. 
	 * @param namespace
	 * @throws ArchException 
	 * @see pushNamespace()
	 */
	void setNamespace(String namespace);
	
	
	/**Get the current namespace this Transaction is using.
	 * @return The namespace string.
	 */
	String getNamespace();
	
	/**Sets the current namespace for this transaction.
	 * The previous namespace is pushed onto a stack, for later retrieval.
	 * @param namespace The new namespace.
	 * @throws ArchException 
	 * @see popNamepsace()
	 */
	void pushNamespace(String namespace);
	
	/**Pops a previously stored namespace off the stack and restores it as the current namespace.
	 * If no namespace was pushed this throws an API error.
	 * @throws ArchException 
	 * @throws EmptyStackException 
	 * 
	 */
	void popNamespace() throws EmptyStackException;
	
	
	 <E> Ref save(E obj);
	
	// Create api
	
	/**
	 * @throws KeyCollisionException if {@link Key}(unique=true) is specified and there is a clash
	 */
	<E> Ref create(E obj);
	<E> GenericRef<E> createGeneric(E obj);
	Ref[] create(Object[] objs);
	Ref[] create(Collection<Object> objs);
	
	// Retrieve api
	/**
	 * @throws UnknownObjectException if the object was not found (e.g. deleted by another transaction)
	 */
	Object retrieve(Ref ref);
	<E> E retrieve(GenericRef<E> ref);
	<E> E refresh(E obj);	// check dirty flag and latest version, refresh if needed
	Map<Ref, Object> retrieve(Collection<Ref> refs);
	
	// Update api
	<E> void update(E obj);
	void update(Object[] objs);
	void update(Collection<Object> objs);
	
	// Modify api
	void modifyField(Object obj, String field, Object newval);
	
	void modifyAttributes(IWhirlwindItem obj, CardinalAttributeMap<IAttribute> add, Collection<Long> remove);
//	@Deprecated void modifyAttributes(WhirlwindItem obj, StringAttributeMap add, Collection<String> remove);
	void modifyNomineeField(IAttributeContainer obj, String field, Object newval);
	void modifyNominee(IAttributeContainer obj, Object nominee);
	
	// Delete api
	void delete(Object obj);
	void delete(Ref ref);
	void delete(Ref[] ref);
	void delete(Iterable<Ref> refs);
	
	// Stored Procedures
	Object execute(String methodName, Ref ref, Object param);

	// Querying - indexless
	/**
	 * Useful when you expect only one object
	 * @return the earliest created item, or null if nothing found
	 */
	<E> E retrieveFirstOf(Class<E> clazz);
	
	// Querying - standard index, retrieve all
	RetrieveSpecResult retrieve(RetrieveSpec spec);
	<E> E retrieve(Class<E> clazz, String keyfield, Comparable<?> keyval);
	<E> Collection<E> retrieveAll(Class<E> clazz, String keyfield, Comparable<?> keyval);
	
	// Querying - standard index, iterating
	<E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr);
	<E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize);
	<E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr);
	
	// Searching - whirlwind index, iterating
	<E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search);
	<E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search, int fetchSize);
	<E extends Object> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search);
	<E extends Object> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search, int fetchSize);
	
	<E> long count(Class<E> clazz);
	long getVersion(Ref ref);
	String[] listNamespaces();

}
