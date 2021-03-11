/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.erm.es;

import com.wl4g.devops.erm.es.exception.GetActiveClientException;
import com.wl4g.devops.erm.es.pool.ElasticsearchClientPool;
import org.apache.http.Header;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

/**
 * es 高级客户端连接池版本实现，完全覆盖了官方 ${@link RestHighLevelClient} 的public方法.
 * <p>
 * 使用同步接口的使用方式和官方没有任何区别，同步接口在调用完成后会自动调用 {@link #releaseClient} 方法来释放client
 * 到资源池中<br>
 * 其中异步接口完成后需要主动调用 {@link #releaseClient} 方法来释放client
 * 到资源池中，否则将导致大量连接被占用，新的线程获取连接的时候没有可用连接。<br>
 * 在使用同步/异步API的过程中，如果当前线程正在使用client，没有释放，再一次使用API(同步/异步)的时候回主动释放当前线程持有的client资源到连接池，这个请特别注意。
 *
 */
public class EnhancedRestHighLevelClient {
	private final ThreadLocal<RestHighLevelClient> threadLocal = new ThreadLocal<>();
	private ElasticsearchClientPool esClientPool;

	public EnhancedRestHighLevelClient(ElasticsearchClientPool esClientPool) {
		this.esClientPool = esClientPool;
	}

	private RestHighLevelClient getClient() {
		if (threadLocal.get() != null) {
			releaseClient();
		}
		try {
			RestHighLevelClient restHighLevelClient = esClientPool.borrowObject();
			threadLocal.set(restHighLevelClient);
			return restHighLevelClient;
		} catch (Exception e) {
			throw new GetActiveClientException(e);
		}

	}

	/**
	 * 使用异步api接口的时候需要在使用完成之后主动调用该方法，释放连接池的连接，否则可能导致连接池无可用的新连接
	 */
	public void releaseClient() {
		RestHighLevelClient restHighLevelClient = threadLocal.get();
		if (restHighLevelClient != null) {
			esClientPool.returnObject(restHighLevelClient);
			threadLocal.remove();
		}
	}

	/**
	 *
	 * 执行方法，执行前从连接池获取一个连接，执行完后归还连接到连接池
	 * 
	 * @param call
	 *            {@link Call}
	 * @return {@link Object}
	 */
	public Object exec(Call call) {
		return exec(call, true);
	}

	/**
	 *
	 * 执行方法，执行前从连接池获取一个连接
	 * 
	 * @param call
	 *            {@link Call}
	 * @param releaseClient
	 *            该方法执行完成后是否释放client到资源池
	 * @return {@link Object}
	 */

	public Object exec(Call call, boolean releaseClient) {
		RestHighLevelClient restHighLevelClient = getClient();
		try {
			return call.hanl(restHighLevelClient);
		} catch (IOException e) {
			return null;
		} finally {
			if (releaseClient) {
				releaseClient();
			}
		}
	}

	/**
	 * 执行方法，执行前从连接池获取一个连接，该方法执行完成后将释放client到资源池
	 * 
	 * @param call
	 *            {@link VoidCall}
	 */
	public void execReturnVoid(VoidCall call) {
		execReturnVoid(call, true);
	}

	/**
	 * 执行方法，执行前从连接池获取一个连接，执行完后归还连接到连接池
	 * 
	 * @param call
	 *            {@link VoidCall}
	 * @param releaseClient
	 *            该方法执行完成后是否释放client到资源池
	 */
	public void execReturnVoid(VoidCall call, boolean releaseClient) {
		RestHighLevelClient restHighLevelClient = getClient();
		try {
			call.hanl(restHighLevelClient);
		} catch (IOException e) {

		} finally {
			if (releaseClient) {
				releaseClient();
			}
		}
	}

	/**
	 * 有参数返回的执行接口
	 */
	interface Call {
		public Object hanl(RestHighLevelClient restHighLevelClient) throws IOException;
	}

	/**
	 * 无参数返回的执行接口
	 */
	interface VoidCall {
		public void hanl(RestHighLevelClient restHighLevelClient) throws IOException;
	}

	/**
	 * 获取低级客户端，使用完该客户端后需要主动调用 ${@link #releaseClient()} 方法释放client到资源池
	 * 
	 * @return
	 */
	public RestClient getLowLevelClient() {
		return (RestClient) exec((r) -> r.getLowLevelClient(), false);
	}

	/**
	 * 获取索引操作client ,使用完该客户端后需要主动调用 ${@link #releaseClient()} 方法释放client到资源池
	 * 
	 * @return
	 */
	public final IndicesClient indices() {
		return (IndicesClient) exec((r) -> r.indices(), false);
	}

	/**
	 * Executes a bulk request using the Bulk API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html">Bulk
	 * API on elastic.co</a>
	 */
	public final BulkResponse bulk(BulkRequest bulkRequest, Header... headers) throws IOException {
		return (BulkResponse) exec((r) -> r.bulk(bulkRequest, headers));
	}

	/**
	 * Asynchronously executes a bulk request using the Bulk API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html">Bulk
	 * API on elastic.co</a>
	 */
	public final void bulkAsync(BulkRequest bulkRequest, ActionListener<BulkResponse> listener, Header... headers) {
		execReturnVoid((r) -> r.bulkAsync(bulkRequest, listener, headers), false);
	}

	/**
	 * Pings the remote Elasticsearch cluster and returns true if the ping
	 * succeeded, false otherwise
	 */
	public final boolean ping(Header... headers) throws IOException {
		return (Boolean) exec((r) -> r.ping(headers));
	}

	/**
	 * Get the cluster info otherwise provided when sending an HTTP request to
	 * port 9200
	 */
	public final MainResponse info(Header... headers) throws IOException {
		return (MainResponse) exec((r) -> r.info(headers));
	}

	/**
	 * Retrieves a document by id using the Get API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html">Get
	 * API on elastic.co</a>
	 */
	public final GetResponse get(GetRequest getRequest, Header... headers) throws IOException {
		return (GetResponse) exec((r) -> r.get(getRequest, headers));
	}

	/**
	 * Asynchronously retrieves a document by id using the Get API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html">Get
	 * API on elastic.co</a>
	 */
	public final void getAsync(GetRequest getRequest, ActionListener<GetResponse> listener, Header... headers) {
		execReturnVoid((r) -> r.getAsync(getRequest, listener, headers), false);
	}

	/**
	 * Retrieves multiple documents by id using the Multi Get API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-multi-get.html">Multi
	 * Get API on elastic.co</a>
	 */
	public final MultiGetResponse multiGet(MultiGetRequest multiGetRequest, Header... headers) throws IOException {
		return (MultiGetResponse) exec((r) -> r.multiGet(multiGetRequest, headers));
	}

	/**
	 * Asynchronously retrieves multiple documents by id using the Multi Get API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-multi-get.html">Multi
	 * Get API on elastic.co</a>
	 */
	public void multiGetAsync(MultiGetRequest multiGetRequest, ActionListener<MultiGetResponse> listener, Header... headers) {
		execReturnVoid((r) -> r.multiGetAsync(multiGetRequest, listener, headers), false);
	}

	/**
	 * Checks for the existence of a document. Returns true if it exists, false
	 * otherwise
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html">Get
	 * API on elastic.co</a>
	 */
	public final boolean exists(GetRequest getRequest, Header... headers) throws IOException {
		return (Boolean) exec((r) -> r.exists(getRequest, headers));
	}

	/**
	 * Asynchronously checks for the existence of a document. Returns true if it
	 * exists, false otherwise
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html">Get
	 * API on elastic.co</a>
	 */
	public final void existsAsync(GetRequest getRequest, ActionListener<Boolean> listener, Header... headers) {
		execReturnVoid((r) -> r.existsAsync(getRequest, listener, headers), false);
	}

	/**
	 * Index a document using the Index API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html">Index
	 * API on elastic.co</a>
	 */
	public final IndexResponse index(IndexRequest indexRequest, Header... headers) throws IOException {
		return (IndexResponse) exec((r) -> r.index(indexRequest, headers));
	}

	/**
	 * Asynchronously index a document using the Index API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html">Index
	 * API on elastic.co</a>
	 */
	public final void indexAsync(IndexRequest indexRequest, ActionListener<IndexResponse> listener, Header... headers) {
		execReturnVoid((r) -> r.indexAsync(indexRequest, listener, headers), false);
	}

	/**
	 * Updates a document using the Update API
	 * <p>
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html">Update
	 * API on elastic.co</a>
	 */
	public final UpdateResponse update(UpdateRequest updateRequest, Header... headers) throws IOException {
		return (UpdateResponse) exec((r) -> r.update(updateRequest, headers));
	}

	/**
	 * Asynchronously updates a document using the Update API
	 * <p>
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html">Update
	 * API on elastic.co</a>
	 */
	public final void updateAsync(UpdateRequest updateRequest, ActionListener<UpdateResponse> listener, Header... headers) {
		execReturnVoid((r) -> r.update(updateRequest, headers), false);
	}

	/**
	 * Deletes a document by id using the Delete API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete.html">Delete
	 * API on elastic.co</a>
	 */
	public final DeleteResponse delete(DeleteRequest deleteRequest, Header... headers) throws IOException {
		return (DeleteResponse) exec((r) -> r.delete(deleteRequest, headers));
	}

	/**
	 * Asynchronously deletes a document by id using the Delete API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete.html">Delete
	 * API on elastic.co</a>
	 */
	public final void deleteAsync(DeleteRequest deleteRequest, ActionListener<DeleteResponse> listener, Header... headers) {
		execReturnVoid((r) -> r.deleteAsync(deleteRequest, listener, headers), false);
	}

	/**
	 * Executes a search using the Search API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html">Search
	 * API on elastic.co</a>
	 */
	public final SearchResponse search(SearchRequest searchRequest, Header... headers) throws IOException {
		return (SearchResponse) exec((r) -> r.search(searchRequest, headers));
	}

	/**
	 * Asynchronously executes a search using the Search API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html">Search
	 * API on elastic.co</a>
	 */
	public final void searchAsync(SearchRequest searchRequest, ActionListener<SearchResponse> listener, Header... headers) {
		execReturnVoid((r) -> r.searchAsync(searchRequest, listener, headers), false);
	}

	/**
	 * Executes a multi search using the msearch API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-multi-search.html">Multi
	 * search API on elastic.co</a>
	 */
	public final MultiSearchResponse multiSearch(MultiSearchRequest multiSearchRequest, Header... headers) throws IOException {
		return (MultiSearchResponse) exec((r) -> r.multiSearch(multiSearchRequest, headers));
	}

	/**
	 * Asynchronously executes a multi search using the msearch API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-multi-search.html">Multi
	 * search API on elastic.co</a>
	 */
	public final void multiSearchAsync(MultiSearchRequest searchRequest, ActionListener<MultiSearchResponse> listener,
			Header... headers) {
		execReturnVoid((r) -> r.multiSearchAsync(searchRequest, listener, headers), false);
	}

	/**
	 * Executes a search using the Search Scroll API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html">Search
	 * Scroll API on elastic.co</a>
	 */
	public final SearchResponse searchScroll(SearchScrollRequest searchScrollRequest, Header... headers) throws IOException {
		return (SearchResponse) exec((r) -> r.searchScroll(searchScrollRequest, headers));
	}

	/**
	 * Asynchronously executes a search using the Search Scroll API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html">Search
	 * Scroll API on elastic.co</a>
	 */
	public final void searchScrollAsync(SearchScrollRequest searchScrollRequest, ActionListener<SearchResponse> listener,
			Header... headers) {
		execReturnVoid((r) -> r.searchScrollAsync(searchScrollRequest, listener, headers), false);
	}

	/**
	 * Clears one or more scroll ids using the Clear Scroll API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html#_clear_scroll_api">
	 * Clear Scroll API on elastic.co</a>
	 */
	public final ClearScrollResponse clearScroll(ClearScrollRequest clearScrollRequest, Header... headers) throws IOException {
		return (ClearScrollResponse) exec((r) -> r.clearScroll(clearScrollRequest, headers));
	}

	/**
	 * Asynchronously clears one or more scroll ids using the Clear Scroll API
	 *
	 * See <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html#_clear_scroll_api">
	 * Clear Scroll API on elastic.co</a>
	 */
	public final void clearScrollAsync(ClearScrollRequest clearScrollRequest, ActionListener<ClearScrollResponse> listener,
			Header... headers) {
		execReturnVoid((r) -> r.clearScrollAsync(clearScrollRequest, listener, headers), false);
	}

}