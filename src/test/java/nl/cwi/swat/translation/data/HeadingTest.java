package nl.cwi.swat.translation.data;

import nl.cwi.swat.ast.IdDomain;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HeadingTest {

  @Test
  public void noJoiningAttributesWithDistinctHeadings() {
    Heading h1 = new Heading.Builder().add("a", IdDomain.ID).add("b", IdDomain.ID).add("c", IdDomain.ID).build();
    Heading h2 = new Heading.Builder().add("d", IdDomain.ID).add("e", IdDomain.ID).add("f", IdDomain.ID).build();

    Heading result = h1.conjunct(h2);
    assertTrue(result.isEmpty());
  }

  @Test
  public void joinOnOneOverlappingFieldResultsInNewHeadingWithOneField() {
    Heading h1 = new Heading.Builder().add("a", IdDomain.ID).add("b", IdDomain.ID).add("c", IdDomain.ID).build();
    Heading h2 = new Heading.Builder().add("c", IdDomain.ID).add("e", IdDomain.ID).add("f", IdDomain.ID).build();

    Heading joined = h1.conjunct(h2);
    assertEquals(new Heading.Builder().add("c", IdDomain.ID).build(), joined);
  }

  @Test
  public void joinIsCommutative() {
    Heading h1 = new Heading.Builder().add("a", IdDomain.ID).add("b", IdDomain.ID).add("c", IdDomain.ID).build();
    Heading h2 = new Heading.Builder().add("c", IdDomain.ID).add("e", IdDomain.ID).add("f", IdDomain.ID).build();

    assertEquals(h1.conjunct(h2), h2.conjunct(h1));
  }

  @Test
  public void joinIsAssociative() {
    Heading h1 = new Heading.Builder().add("a", IdDomain.ID).add("b", IdDomain.ID).add("c", IdDomain.ID).build();
    Heading h2 = new Heading.Builder().add("b", IdDomain.ID).add("c", IdDomain.ID).add("d", IdDomain.ID).build();
    Heading h3 = new Heading.Builder().add("c", IdDomain.ID).add("d", IdDomain.ID).add("e", IdDomain.ID).build();

    assertEquals(h1.conjunct(h2).conjunct(h3), h1.conjunct(h2.conjunct(h3)));
  }
}