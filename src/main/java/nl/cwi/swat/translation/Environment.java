package nl.cwi.swat.translation;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.translation.data.relation.Relation;

public class Environment {
  private Map.Immutable<String, Relation> relations;

  private Environment() {
    this.relations = PersistentTrieMap.of();
  }

  private Environment(Map.Immutable<String,Relation> parent) {
    if (parent == null) {
      throw new IllegalArgumentException("Parent environment can not be null");
    }

    this.relations = parent;
  }

  public static Environment base() {
    return new Environment();
  }

  Environment extend(String name, Relation relation) {
    Environment extended = new Environment(this.relations);
    extended.add(name, relation);

    return extended;
  }


  public void add(String name, Relation relation) {
    if (name == null || relation == null) {
      throw new IllegalArgumentException("Relation name and rows can not be null");
    }

    Map.Transient<String, Relation> transRel = relations.asTransient();
    transRel.put(name, relation);
    relations = transRel.freeze();
  }

  public Relation get(String name) {
    return relations.get(name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Environment that = (Environment) o;

    return relations.equals(that.relations);
  }

  @Override
  public int hashCode() {
    return relations.hashCode();
  }
}
