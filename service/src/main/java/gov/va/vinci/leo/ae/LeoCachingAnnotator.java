package gov.va.vinci.leo.ae;

/*
 * #%L
 * Leo
 * %%
 * Copyright (C) 2010 - 2014 Department of Veterans Affairs
 * %%
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
 * #L%
 */

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Base annotator with methods that implement caching via EHCache.
 * <p/>
 * Usage:
 * <p/>
 * In the simplest case, extend this annotator, then in the new annotator caching can be used via:
 * <p/>
 * if (getResultFromCache(myKey)!= null) { return getResultFromCache(myKey); }
 * else { // do a bunch of work; addResultToCache(myKey, myResult); return myResult;}
 * <p/>
 * The cache name will be set as the full classname of the annotator. In this way, each annotator
 * can have its cache configured differently via an ehcache.xml file in the classpath with:
 * <p/>
 * <pre>
 * <cache name="<my annotator class with full path>" .... />
 * </pre>
 */
public abstract class LeoCachingAnnotator extends LeoBaseAnnotator {

    /**
     * Logger for this class.
     */
    private final static Logger LOG = Logger.getLogger(LeoCachingAnnotator.class.getName());

    /**
     * Cache manager for getting a cache.
     */
    protected CacheManager cacheManager;

    /**
     * Default constructor.
     */
    public LeoCachingAnnotator() {
        super();
        createCache();

    }

    /**
     * Returns the ehcache name for this annotator. This, by default,
     * is the full classname to prevent collisions.
     *
     * @return the name of the ehcache.
     */
    public final String getCacheName() {
        return this.getClass().getCanonicalName();
    }

    /**
     * Get the cache.
     *
     * @return the ehcache for this annotator.
     */
    public final Cache getCache() {
        return cacheManager.getCache(getCacheName());
    }

    /**
     * Create the cache to be used for storing results.
     */
    protected void createCache() {
        LOG.info("Creating EHCache: " + getCacheName());
        cacheManager = CacheManager.getInstance();

        /** Only create the cache once per VM **/
        if (cacheManager.cacheExists(getCacheName())) {
            return;
        }
        cacheManager.addCache(getCacheName());

        CacheConfiguration config = this.getCache().getCacheConfiguration();

        LOG.info("--> MaxEntriesLocalDisk:" + config.getMaxEntriesLocalDisk());
        LOG.info("--> MaxEntriesLocalHeap:" + config.getMaxEntriesLocalHeap());
        LOG.info("--> TimeToIdleSeconds:" + config.getTimeToIdleSeconds());
        LOG.info("--> TimeToLiveSeconds:" + config.getTimeToLiveSeconds());

    }

    /**
     * Add a key/value to the cache.
     *
     * @param key   the key of the result to be cached.
     * @param value the value of the result to be cached.
     */
    protected final void addResultToCache(final Serializable key, final Serializable value) {
        cacheManager.getCache(this.getCacheName()).put(new Element(key, value));
    }

    /**
     * Look in the cache for input and return the result.
     *
     * @param key the get to get result for.
     * @return the result, if the input has a cached result, or null
     *         if no result could be found.
     */
    protected final Object getResultFromCache(final Serializable key) {
        Element elt = cacheManager.getCache(this.getCacheName()).get(key);
        return (elt == null ? null : elt.getObjectValue());
    }

}
