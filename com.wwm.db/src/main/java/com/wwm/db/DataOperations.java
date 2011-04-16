package com.wwm.db;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Map;

import com.wwm.db.core.exceptions.ArchException;
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
	public void setNamespace(String namespace) throws ArchException;
	
	
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
	public void pushNamespace(String namespace) throws ArchException;
	
	/**Pops a previously stored namespace off the stack and restores it as the current namespace.
	 * If no namespace was pushed this throws an API error.
	 * @throws ArchException 
	 * @throws EmptyStackException 
	 * 
	 */
	public void popNamespace() throws EmptyStackException, ArchException;
	
	// Create api
	public <E> Ref create(E obj) throws ArchException;
	public <E> GenericRef<E> createGeneric(E obj) throws ArchException;
	public Ref[] create(Object[] objs) throws ArchException;
	public Ref[] create(Collection<Object> objs) throws ArchException;
	
	// Retrieve api
	public Object retrieve(Ref ref) throws ArchException;
	public <E> E retrieve(GenericRef<E> ref) throws ArchException;
	public <E> E refresh(E obj) throws ArchException;	// check dirty flag and latest version, refresh if needed
	public Map<Ref, Object> retrieve(Collection<Ref> refs) throws ArchException;
	
	// Update api
	public <E> void update(E obj) throws ArchException;
	public void update(Object[] objs) throws ArchException;
	public void update(Collection<Object> objs) throws ArchException;
	
	// Modify api
	public void modifyField(Object obj, String field, Object newval) throws ArchException;
	
	public void modifyAttributes(IWhirlwindItem obj, CardinalAttributeMap<IAttribute> add, Collection<Long> remove) throws ArchException;
//	@Deprecated public void modifyAttributes(WhirlwindItem obj, StringAttributeMap add, Collection<String> remove) throws ArchException;
	public void modifyNomineeField(IAttributeContainer obj, String field, Object newval) throws ArchException;
	public void modifyNominee(IAttributeContainer obj, Object nominee) throws ArchException;
	
	// Delete api
	public void delete(Object obj) throws ArchException;
	public void delete(Ref ref) throws ArchException;
	public void delete(Ref[] ref) throws ArchException;
	public void delete(Collection<Ref> ref) throws ArchException;
	
	// Stored Procedures
	public Object execute(String methodName, Ref ref, Object param) throws ArchException;

	// Querying - indexless
	public <E> E retrieveFirstOf(Class<E> clazz) throws ArchException;
	
	// Querying - standard index, retrieve all
	public RetrieveSpecResult retrieve(RetrieveSpec spec) throws ArchException;
	public <E> E retrieve(Class<E> clazz, String keyfield, Comparable<?> keyval) throws ArchException;
	public <E> Collection<E> retrieveAll(Class<E> clazz, String keyfield, Comparable<?> keyval) throws ArchException;
	
	// Querying - standard index, iterating
	public <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr) throws ArchException;
	public <E> ResultSet<E> query(Class<E> clazz, LogicExpr index, LogicExpr expr, int fetchSize) throws ArchException;
	public <E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr) throws ArchException;
	
	// Searching - whirlwind index, iterating
	public <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search) throws ArchException;
	public <E extends IAttributeContainer> ResultSet<Result<E>> query(Class<E> resultClazz, SearchSpec search, int fetchSize) throws ArchException;
	public <E extends Object> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search) throws ArchException;
	public <E extends Object> ResultSet<Result<E>> queryNominee(Class<E> resultClazz, SearchSpec search, int fetchSize) throws ArchException;
	
	public <E> long count(Class<E> clazz) throws ArchException;
	public long getVersion(Ref ref) throws ArchException;
	public String[] listNamespaces() throws ArchException;

}
