package nl.cwi.swat;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dagger.Module;
import dagger.Provides;
import io.usethesource.capsule.Set;
import nl.cwi.swat.smtlogic.Formula;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.smtlogic.SimpleFormulaFactory;
import nl.cwi.swat.smtlogic.SimplificationFactory;
import nl.cwi.swat.translation.TranslationCache;
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
  public SimplificationFactory provideSimplificationFactory() {
    Cache<SimplificationFactory.FormulaCacheKey, Set.Transient<Formula>> formulaCache = Caffeine.newBuilder().recordStats().build();

    return new SimplificationFactory(reductionDepth, formulaCache);
  }

  @Provides
  @Singleton
  public FormulaFactory provideFormulaFactory() {
    return new SimpleFormulaFactory();
  }
}

