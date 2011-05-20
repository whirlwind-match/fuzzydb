package whirlwind.demo.gui.searchtabs;

import java.util.ArrayList;

import javax.swing.JPanel;

import com.wwm.indexer.SearchResult;
import com.wwm.indexer.demo.internal.WhirlwindCommon;
import com.wwm.indexer.demo.internal.WhirlwindQuery;
import com.wwm.indexer.demo.internal.WhirlwindQuery.QueryConfig;

public abstract class SearchTab extends JPanel {

	private static final long serialVersionUID = 1L;

	private String tabName = "";

	private QueryConfig searchCfg;

	private ArrayList<SearchResult> results = new ArrayList<SearchResult>();

	protected WhirlwindCommon WCommon;

	protected WhirlwindQuery query;

	public SearchTab() {
		super();
	}

	// ---------------------------------
	// Java Panel Initialisation
	// ---------------------------------
	protected abstract void initialize();

	protected abstract void onReset();

	protected abstract void onSetSearchCfg(QueryConfig searchCfg);

	protected abstract void onSetResult(SearchResult result);

	protected abstract void onPageStart();

	protected abstract void onPageComplete();

	// ---------------------------------

	public void whirlwind_initialize(String tabName, WhirlwindCommon WCommon, WhirlwindQuery query) {
		this.tabName = tabName;
		this.WCommon = WCommon;
		this.query = query;
	}

	public void pageStart() {
		onPageStart();
	}

	public void pageComplete() {
		onPageComplete();
	}

	public void addSearch(QueryConfig searchCfg) {
		this.searchCfg = searchCfg;
		onSetSearchCfg(searchCfg);
	}

	public void addResult(SearchResult result) {
		results.add(result);
		onSetResult(result);
	}

	public void addResults(ArrayList<SearchResult> newresults) {
		results.addAll(newresults);
		for (SearchResult result : newresults) {
			onSetResult(result);
		}
	}

	public void reset() {
		searchCfg = null;
		results.clear();
		onReset();
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public QueryConfig getSearchCfg() {
		return searchCfg;
	}

	public ArrayList<SearchResult> getResults() {
		return results;
	}

	public void setResults(ArrayList<SearchResult> results) {
		this.results = results;
	}
}