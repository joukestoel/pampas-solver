package nl.cwi.swat.translation;

import com.github.benmanes.caffeine.cache.Cache;
import nl.cwi.swat.translation.data.relation.AbstractRelation;

public class Index {
  private final Cache<AbstractRelation.IndexCacheKey, AbstractRelation.IndexedRows> cache;

  public Index(Cache<AbstractRelation.IndexCacheKey, AbstractRelation.IndexedRows> cache) {
    this.cache = cache;
  }

  public Cache<AbstractRelation.IndexCacheKey, AbstractRelation.IndexedRows> getCache() {
    return cache;
  }

  public void invalidate() {
    this.cache.invalidateAll();
  }
}
