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
	public void setNamespace(String namespace);
	
	
	/**Get the current namespace this Transaction is using.
	 * @return The namespace string.
	 */
	public String getNamespace();
	
	/**Sets the current namespace for this transaction.
	 * The previous namespace is pushed onto a stack, for later retrieval.
	 * @param namespace The new namespace.
	 * @throws ArchException 
	 * @see popNamepsace()
	 */
	public void pushNamespace(String namespace);
	
	/**Pops a previously stored namespace off the stack and restores it as the current namespace.
	 * If no namespace was pushed this throws an API error.
	 * @throws ArchException 
	 * @throws EmptyStackException 
	 * 
	 */
	public void popNamespace() throws EmptyStackException;
	
	// Create api
	
	/**
	 * @throws KeyCollisionException if {@link Key}(unique=true) is specified and there is a clash
	 */
	public <E> Ref create(E obj);
	public <E> GenericRef<E> createGeneric(E obj);
	public Ref[] create(Object[] objs);
	public Ref[] create(Collection<Object> objs);
	
	// Retrieve api
	/**
	 * @throws UnknownObjectException if the object was not found (e.g. deleted by another transaction)
	 */
	public Object retrieve(Ref ref);
	public <E> E retrieve(GenericRef<E> ref);
	public <E> E refresh(E obj);	// check dirty flag and latest version, refresh if needed
	public Map<Ref, Object> retrieve(Collection<Ref> refs);
	
	// Update api
	public <E> void update(E obj);
	public void update(Object[] objs);
	public void update(Collection<Object> objs);
	
	// Modify api
	public void modifyField(Object obj, String field, Object newval);
	
	public void modifyAttributes(IWhirlwindItem obj, CardinalAttributeMap<IAttribute> add, Collection<Long> remove);
//	@Deprecated public void modifyAttributes(WhirlwindItem obj, StringAttributeMap add, Collection<String> remove);
	public void modifyNomineeField(IAttributeContainer obj, String field, Object newval);
	public void modifyNominee(IAttributeContainer obj, Object nominee);
	
	// Delete api
	public void delete(Object obj);
	public void delete(Ref ref);
	public void delete(Ref[] ref);
	public void delete(Collection<Ref> ref);
	
	// Stored Procedures
	public Object execute(String methodName, Ref ref, Object param);

	// Querying - indexless
	public <E> E retrieveFirstOf(Class<E> clazz);
	
	// Querying - standard index, retrieve all
	public RetrieveSpecResult retrieve(RetrieveSpec spec);
	public <E> E retrieve(Class<E> clazz, String keyfield, Comparable<?> keyval);
	public <E> Collection<E> retrieveAll(Class<E> clazz, String keyfield, Comparable<?> keyval);
	
	// Querying - standard index, iterating
	public <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr);
	public <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize);
	public <E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr);
	
	// Searching - whirlwind index, iterating
	public <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search);
	public <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search, int fetchSize);
	public <E extends Object> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search);
	public <E extends Object> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search, int fetchSize);
	
	public <E> long count(Class<E> clazz);
	public long getVersion(Ref ref);
	public String[] listNamespaces();

}
