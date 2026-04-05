package org.batfish;

import net.sf.javabdd.BDD;
import org.batfish.BDDMatchersImpl.Intersects;
import org.batfish.BDDMatchersImpl.IsOne;
import org.batfish.BDDMatchersImpl.IsZero;
import org.hamcrest.Matcher;

public final class BDDMatchers {
  private BDDMatchers() {}

  public static Matcher<BDD> intersects(BDD other) {
    return new Intersects(other);
  }

  public static Matcher<BDD> isOne() {
    return new IsOne();
  }

  public static Matcher<BDD> isZero() {
    return new IsZero();
  }
}
