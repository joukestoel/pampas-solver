package nl.cwi.swat;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dagger.Module;
import dagger.Provides;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.MinimalReducingCircuitFactory;
import nl.cwi.swat.translation.Index;
import nl.cwi.swat.translation.TranslationCache;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Relation;

import javax.inject.Singleton;

@Module
public class Config {

  private static final int reductionDepth = 1;

  @Provides
  @Singleton
  public TranslationCache provideTranslationFormulaCache() {
    Cache<TranslationCache.TranslationCacheKey, Formula> formulaCache = Caffeine.newBuilder().recordStats().build();
    Cache<TranslationCache.TranslationCacheKey, Relation> expressionCache = Caffeine.newBuilder().recordStats().build();

    return new TranslationCache(formulaCache, expressionCache);
  }

  @Provides
  @Singleton
  public Index provideIndex() {
    Cache<AbstractRelation.IndexCacheKey, AbstractRelation.IndexedRows> index = Caffeine.newBuilder().recordStats().build();

    return new Index(index);
  }

  @Provides
  @Singleton
  public FormulaFactory provideFormulaFactory() {
    return new MinimalReducingCircuitFactory();
  }
}


