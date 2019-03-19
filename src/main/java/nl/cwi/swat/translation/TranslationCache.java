package nl.cwi.swat.translation;

import com.github.benmanes.caffeine.cache.Cache;
import nl.cwi.swat.ast.relational.Expression;
import nl.cwi.swat.ast.relational.Node;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.translation.data.relation.Relation;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class TranslationCache {
  private final Cache<TranslationCacheKey, Formula> formulaCache;
  private final Cache<TranslationCacheKey, Relation> expressionCache;

  public TranslationCache(@NonNull Cache<TranslationCacheKey, Formula> formulaCache, @NonNull Cache<TranslationCacheKey, Relation> expressionCache) {
    this.formulaCache = formulaCache;
    this.expressionCache = expressionCache;
  }

  public void invalidate() {
    formulaCache.invalidateAll();
    expressionCache.invalidateAll();
  }

  public Optional<Formula> fetch(nl.cwi.swat.ast.relational.Formula formula, Environment env) {
    return Optional.ofNullable(formulaCache.getIfPresent(new TranslationCacheKey(formula, env)));
  }

  public Formula storeAndReturn(nl.cwi.swat.ast.relational.Formula formula, Environment env, Formula result) {
    formulaCache.put(new TranslationCacheKey(formula,env), result);
    return result;
  }

  public Optional<Relation> fetch(Expression expr, Environment env) {
    return Optional.ofNullable(expressionCache.getIfPresent(new TranslationCacheKey(expr, env)));
  }

  public Relation storeAndReturn(Expression expr, Environment env, Relation result) {
    expressionCache.put(new TranslationCacheKey(expr, env), result);
    return result;
  }

  public static class TranslationCacheKey {
    private final Node node;
    private final Environment env;

    public TranslationCacheKey(Node node, Environment env) {
      if (node == null || env == null) {
        throw new IllegalArgumentException("Node and environment properties can not be null when creating a new Cache Key");
      }

      this.node = node;
      this.env = env;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TranslationCacheKey that = (TranslationCacheKey) o;

      if (!node.equals(that.node)) return false;
      return env.equals(that.env);
    }

    @Override
    public int hashCode() {
      int result = node.hashCode();
      result = 31 * result + env.hashCode();
      return result;
    }
  }

}
