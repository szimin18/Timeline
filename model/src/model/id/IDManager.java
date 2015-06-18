package model.id;

import java.util.concurrent.atomic.AtomicLong;

public final class IDManager {
	private final static AtomicLong counter = new AtomicLong(-1);

	private IDManager() {
		throw new AssertionError();
	}

	public static final TID generateID() {
		final long newID = counter.incrementAndGet();
		if (newID >= 0) {
			return new TID(newID);
		} else {
			counter.decrementAndGet();
			throw new AssertionError("No more IDs in store");
		}
	}

	public static final class TID {
		private final long ID;

		private TID(long id) {
			ID = id;
		}

		public final long getID() {
			return ID;
		}
		
		@Override
		public String toString() {
			return String.valueOf(ID);
		}
	}
}
