package se.fnord.graph.orientdb;

import com.orientechnologies.common.util.OPair;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

class SparsePairs<T extends Comparable<T>, U> extends AbstractCollection<OPair<T, U>> {
    private final OPair<T, U>[] pairs;
    private long bitmap;

    public SparsePairs(OPair<T, U>[] pairs) {
        this.pairs = pairs.clone();
        this.bitmap = 0L;
    }

    public void set(int i, U value) {
        pairs[i].setValue(value);
        bitmap |= 1 << i;
    }

    @Override
    public void clear() {
        for (OPair<T, U> pair : pairs) {
            pair.setValue(null);
        }
        bitmap = 0L;
    }

    @Override
    public Iterator<OPair<T, U>> iterator() {
        return new Iterator<OPair<T, U>>() {
            long currentBitmap = SparsePairs.this.bitmap;
            @Override
            public boolean hasNext() {
                return currentBitmap != 0L;
            }

            @Override
            public OPair<T, U> next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                long lowestBit = Long.lowestOneBit(currentBitmap);
                currentBitmap &= ~lowestBit;
                return pairs[Long.numberOfTrailingZeros(lowestBit)];
            }
        };
    }

    @Override
    public int size() {
        return Long.bitCount(bitmap);
    }
}
