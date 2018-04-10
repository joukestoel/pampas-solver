package nl.cwi.swat.translation;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.translation.data.Relation;

import java.util.Objects;

public class Environment {
  private Map.Immutable<String,Relation> relations;

  private Environment() {
    this.relations = PersistentTrieMap.of();
  }

  private Environment(Map.Immutable<String,Relation> parent) {
    this.relations = parent;
  }

  public static Environment base() {
    return new Environment();
  }

  public Environment extend(String name, Relation relation) {
    Environment extended = new Environment(this.relations);
    extended.add(name, relation);

    return extended;
  }

  public void add(String name, Relation relation) {
    Map.Transient<String, Relation> transRel = relations.asTransient();
    transRel.__put(name, relation);
    relations = transRel.freeze();
  }

  public Relation get(String name) {
    return relations.get(name);
  }

  public void invalidateIndexCaches() {
    relations.values().forEach(r -> r.invalidateCache());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Environment that = (Environment) o;
    return Objects.equals(relations, that.relations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(relations);
  }
}
