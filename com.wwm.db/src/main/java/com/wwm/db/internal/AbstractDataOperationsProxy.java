package com.wwm.db.internal;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Map;

import com.wwm.db.DataOperations;
import com.wwm.db.GenericRef;
import com.wwm.db.Ref;
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

public abstract class AbstractDataOperationsProxy implements DataOperations {

	abstract protected DataOperations getDataOperations();

	
	public void setNamespace(String namespace) {
		getDataOperations().setNamespace(namespace);
	}

	public String getNamespace() {
		return getDataOperations().getNamespace();
	}

	public void pushNamespace(String namespace) {
		getDataOperations().pushNamespace(namespace);
	}

	public void popNamespace() throws EmptyStackException {
		getDataOperations().popNamespace();
	}

	public <E> Ref create(E obj) {
		return getDataOperations().create(obj);
	}

	public <E> GenericRef<E> createGeneric(E obj) {
		return getDataOperations().createGeneric(obj);
	}

	public Ref[] create(Object[] objs) {
		return getDataOperations().create(objs);
	}

	public Ref[] create(Collection<Object> objs) {
		return getDataOperations().create(objs);
	}

	public Object retrieve(Ref ref) {
		return getDataOperations().retrieve(ref);
	}

	public <E> E retrieve(GenericRef<E> ref) {
		return getDataOperations().retrieve(ref);
	}

	public <E> E refresh(E obj) {
		return getDataOperations().refresh(obj);
	}

	public Map<Ref, Object> retrieve(Collection<Ref> refs) {
		return getDataOperations().retrieve(refs);
	}

	public <E> void update(E obj) {
		getDataOperations().update(obj);
	}

	public void update(Object[] objs) {
		getDataOperations().update(objs);
	}

	public void update(Collection<Object> objs) {
		getDataOperations().update(objs);
	}

	public void modifyField(Object obj, String field, Object newval) {
		getDataOperations().modifyField(obj, field, newval);
	}

	public void modifyAttributes(IWhirlwindItem obj,
			CardinalAttributeMap<IAttribute> add, Collection<Long> remove) {
		getDataOperations().modifyAttributes(obj, add, remove);
	}

	public void modifyNomineeField(IAttributeContainer obj, String field,
			Object newval) {
		getDataOperations().modifyNomineeField(obj, field, newval);
	}

	public void modifyNominee(IAttributeContainer obj, Object nominee) {
		getDataOperations().modifyNominee(obj, nominee);
	}

	public void delete(Object obj) {
		getDataOperations().delete(obj);
	}

	public void delete(Ref ref) {
		getDataOperations().delete(ref);
	}

	public void delete(Ref[] ref) {
		getDataOperations().delete(ref);
	}

	public void delete(Collection<Ref> ref) {
		getDataOperations().delete(ref);
	}

	public Object execute(String methodName, Ref ref, Object param) {
		return getDataOperations().execute(methodName, ref, param);
	}

	public <E> E retrieveFirstOf(Class<E> clazz) {
		return getDataOperations().retrieveFirstOf(clazz);
	}

	public RetrieveSpecResult retrieve(RetrieveSpec spec) {
		return getDataOperations().retrieve(spec);
	}

	public <E> E retrieve(Class<E> clazz, String keyfield, Comparable<?> keyval) {
		return getDataOperations().retrieve(clazz, keyfield, keyval);
	}

	public <E> Collection<E> retrieveAll(Class<E> clazz, String keyfield,
			Comparable<?> keyval) {
		return getDataOperations().retrieveAll(clazz, keyfield, keyval);
	}

	public <E> ResultSet<E> query(Class<E> clazz, LogicExpr index,
			LogicExpr expr) {
		return getDataOperations().query(clazz, index, expr);
	}

	public <E> ResultSet<E> query(Class<E> clazz, LogicExpr index,
			LogicExpr expr, int fetchSize) {
		return getDataOperations().query(clazz, index, expr, fetchSize);
	}

	public <E> long queryCount(Class<E> clazz, LogicExpr index, LogicExpr expr) {
		return getDataOperations().queryCount(clazz, index, expr);
	}

	public <E extends IAttributeContainer> ResultSet<Result<E>> query(
			Class<E> resultClazz, SearchSpec search) {
		return getDataOperations().query(resultClazz, search);
	}

	public <E extends IAttributeContainer> ResultSet<Result<E>> query(
			Class<E> resultClazz, SearchSpec search, int fetchSize) {
		return getDataOperations().query(resultClazz, search, fetchSize);
	}

	public <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz,
			SearchSpec search) {
		return getDataOperations().queryNominee(resultClazz, search);
	}

	public <E> ResultSet<Result<E>> queryNominee(Class<E> resultClazz,
			SearchSpec search, int fetchSize) {
		return getDataOperations().queryNominee(resultClazz, search, fetchSize);
	}

	public <E> long count(Class<E> clazz) {
		return getDataOperations().count(clazz);
	}

	public long getVersion(Ref ref) {
		return getDataOperations().getVersion(ref);
	}

	public String[] listNamespaces() {
		return getDataOperations().listNamespaces();
	}
}
