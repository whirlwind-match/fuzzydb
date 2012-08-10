package org.fuzzydb.client.internal;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Map;

import org.fuzzydb.client.DataOperations;
import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.marker.IWhirlwindItem;
import org.fuzzydb.client.whirlwind.CardinalAttributeMap;

import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultSet;
import com.wwm.db.query.RetrieveSpec;
import com.wwm.db.query.RetrieveSpecResult;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.expressions.LogicExpr;

/**
 * Proxy for DataOperations interface to allow dynamic resolution of the target (e.g.
 * target can be thread bound {@link Transaction}.
 * 
 * @author Neale Upstone
 */
public abstract class AbstractDataOperationsProxy implements DataOperations {

	abstract protected DataOperations getDataOperations();

	
	public void setNamespace(String namespace) {
		getDataOperations().setNamespace(namespace);
	}

	public final String getNamespace() {
		return getDataOperations().getNamespace();
	}

	public final void pushNamespace(String namespace) {
		getDataOperations().pushNamespace(namespace);
	}

	public final void popNamespace() throws EmptyStackException {
		getDataOperations().popNamespace();
	}

	public final <E> Ref<E> create(E obj) {
		return getDataOperations().create(obj);
	}

	public final Ref[] create(Object[] objs) {
		return getDataOperations().create(objs);
	}

	public final Ref[] create(Collection<Object> objs) {
		return getDataOperations().create(objs);
	}

	public final <E> E retrieve(Ref<E> ref) {
		return getDataOperations().retrieve(ref);
	}

	public final <E> E refresh(E obj) {
		return getDataOperations().refresh(obj);
	}

	public final <E> Map<Ref<E>, E> retrieve(Collection<Ref<E>> refs) {
		return getDataOperations().retrieve(refs);
	}

	public final <E> Ref<E> save(E obj) {
		return getDataOperations().save(obj);
	};
	
	public final <E> void update(E obj) {
		getDataOperations().update(obj);
	}

	public final void update(Object[] objs) {
		getDataOperations().update(objs);
	}

	public final void update(Collection<Object> objs) {
		getDataOperations().update(objs);
	}

	public final void modifyField(Object obj, String field, Object newval) {
		getDataOperations().modifyField(obj, field, newval);
	}

	public final void modifyAttributes(IWhirlwindItem obj,
			CardinalAttributeMap<IAttribute> add, Collection<Long> remove) {
		getDataOperations().modifyAttributes(obj, add, remove);
	}

	public final void modifyNomineeField(IAttributeContainer obj, String field,
			Object newval) {
		getDataOperations().modifyNomineeField(obj, field, newval);
	}

	public final void modifyNominee(IAttributeContainer obj, Object nominee) {
		getDataOperations().modifyNominee(obj, nominee);
	}

	public final void delete(Object obj) {
		getDataOperations().delete(obj);
	}

	public final void delete(Ref ref) {
		getDataOperations().delete(ref);
	}

	public final void delete(Ref[] ref) {
		getDataOperations().delete(ref);
	}

	public final void delete(Iterable<Ref> ref) {
		getDataOperations().delete(ref);
	}

	public final Object execute(String methodName, Ref ref, Object param) {
		return getDataOperations().execute(methodName, ref, param);
	}

	public final <E> E retrieveFirstOf(Class<E> clazz) {
		return getDataOperations().retrieveFirstOf(clazz);
	}

	public final RetrieveSpecResult retrieve(RetrieveSpec spec) {
		return getDataOperations().retrieve(spec);
	}

	public final <E> E retrieve(Class<E> clazz, String keyfield, Comparable<?> keyval) {
		return getDataOperations().retrieve(clazz, keyfield, keyval);
	}

	public final <E> Collection<E> retrieveAll(Class<E> clazz, String keyfield,
			Comparable<?> keyval) {
		return getDataOperations().retrieveAll(clazz, keyfield, keyval);
	}

	public final <E> ResultSet<E> query(Class<E> clazz, LogicExpr index,
			LogicExpr expr) {
		return getDataOperations().query(clazz, index, expr);
	}

	public final <E> ResultSet<E> query(Class<E> clazz, LogicExpr index,
			LogicExpr expr, int fetchSize) {
		return getDataOperations().query(clazz, index, expr, fetchSize);
	}

	public final <E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr) {
		return getDataOperations().queryCount(clazz, index, expr);
	}

	public final <E extends IAttributeContainer> ResultSet<Result<E>> query(
			Class<E> resultClazz, SearchSpec search) {
		return getDataOperations().query(resultClazz, search);
	}

	public final <E extends IAttributeContainer> ResultSet<Result<E>> query(
			Class<E> resultClazz, SearchSpec search, int fetchSize) {
		return getDataOperations().query(resultClazz, search, fetchSize);
	}

	public final <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz,
			SearchSpec search) {
		return getDataOperations().queryNominee(resultClazz, search);
	}

	public final <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz,
			SearchSpec search, int fetchSize) {
		return getDataOperations().queryNominee(resultClazz, search, fetchSize);
	}

	public final <E> long count(Class<E> clazz) {
		return getDataOperations().count(clazz);
	}

	public final long getVersion(Ref ref) {
		return getDataOperations().getVersion(ref);
	}

	public final String[] listNamespaces() {
		return getDataOperations().listNamespaces();
	}
}
