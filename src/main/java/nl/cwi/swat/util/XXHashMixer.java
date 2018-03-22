package nl.cwi.swat.util;

public class XXHashMixer {
  private static final int PRIME1 = (int) 2654435761L;
  private static final int PRIME2 = (int) 2246822519L;
  private static final int PRIME3 = (int) 3266489917L;
  private static final int PRIME4 = 668265263;
  private static final int PRIME5 = 0x165667b1;

  public static int mix(int k) {
    int h = PRIME1;

    h += k * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h ^= h >>> 15;
    h *= PRIME2;
    h ^= h >>> 13;
    h *= PRIME3;
    h ^= h >>> 16;

    return h;
  }

  public static int mix(int k1, int k2) {
    int h = PRIME1;

    h += k1 * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h += k2 * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h ^= h >>> 15;
    h *= PRIME2;
    h ^= h >>> 13;
    h *= PRIME3;
    h ^= h >>> 16;

    return h;
  }


  public static int mix(int k1, int k2, int k3) {
    int h = PRIME1;

    h += k1 * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h += k2 * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h += k3 * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h ^= h >>> 15;
    h *= PRIME2;
    h ^= h >>> 13;
    h *= PRIME3;
    h ^= h >>> 16;

    return h;
  }

  public static int mix(int k1, int k2, int k3, int k4) {
    int h = PRIME1;

    h += k1 * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h += k2 * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h += k3 * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h += k4 * PRIME3;
    h = Integer.rotateLeft(h, 17) * PRIME4;

    h ^= h >>> 15;
    h *= PRIME2;
    h ^= h >>> 13;
    h *= PRIME3;
    h ^= h >>> 16;

    return h;
  }

  public static int mix(Object[] values) {
    int n = values.length;
    int seed = n;
    int i = 0;
    int h;

    // do the big loop
    if (n >= 4) {
      int v1 = seed + PRIME1 + PRIME2;
      int v2 = seed + PRIME2;
      int v3 = seed + 0;
      int v4 = seed - PRIME1;

      int max = n - 3;
      do {
        v1 += values[i++].hashCode() * PRIME2;
        v1 = Integer.rotateLeft(v1, 13) * PRIME2;
        v1 *= PRIME1;

        v2 += values[i++].hashCode() * PRIME2;
        v2 = Integer.rotateLeft(v2, 13) * PRIME2;
        v2 *= PRIME1;

        v3 += values[i++].hashCode() * PRIME2;
        v3 = Integer.rotateLeft(v3, 13) * PRIME2;
        v3 *= PRIME1;

        v4 += values[i++].hashCode() * PRIME2;
        v4 = Integer.rotateLeft(v4, 13) * PRIME2;
        v4 *= PRIME1;
      } while (i < max);

      h = Integer.rotateLeft(v1, 1)
              + Integer.rotateLeft(v2, 7)
              + Integer.rotateLeft(v3, 12)
              + Integer.rotateLeft(v4, 18)
      ;
    }
    else {
      h = seed * PRIME5;
    }

    // finish of the rest
    while (i < n) {
      h += values[i++].hashCode() * PRIME3;
      h = Integer.rotateLeft(h, 17) * PRIME4;
    }


    h ^= h >>> 15;
    h *= PRIME2;
    h ^= h >>> 13;
    h *= PRIME3;
    h ^= h >>> 16;

    return h;
  }
}