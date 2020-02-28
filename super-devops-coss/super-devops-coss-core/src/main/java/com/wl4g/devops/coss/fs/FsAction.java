package com.wl4g.devops.coss.fs;

/**
 * File system actions, e.g. read, write, etc.
 */
public enum FsAction {

	// POSIX style
	NONE("---"),

	EXECUTE("--x"),

	WRITE("-w-"),

	WRITE_EXECUTE("-wx"),

	READ("r--"),

	READ_EXECUTE("r-x"),

	READ_WRITE("rw-"),

	ALL("rwx");

	/** Retain reference to value array. */
	private final static FsAction[] vals = values();

	/** Symbolic representation */
	public final String SYMBOL;

	private FsAction(String s) {
		SYMBOL = s;
	}

	/**
	 * Return true if this action implies that action.
	 * 
	 * @param that
	 */
	public boolean implies(FsAction that) {
		if (that != null) {
			return (ordinal() & that.ordinal()) == that.ordinal();
		}
		return false;
	}

	/** AND operation. */
	public FsAction and(FsAction that) {
		return vals[ordinal() & that.ordinal()];
	}

	/** OR operation. */
	public FsAction or(FsAction that) {
		return vals[ordinal() | that.ordinal()];
	}

	/** NOT operation. */
	public FsAction not() {
		return vals[7 - ordinal()];
	}

	/**
	 * Get the FsAction enum for String representation of permissions
	 * 
	 * @param permission
	 *            3-character string representation of permission. ex: rwx
	 * @return Returns FsAction enum if the corresponding FsAction exists for
	 *         permission. Otherwise returns null
	 */
	public static FsAction getFsAction(String permission) {
		for (FsAction fsAction : vals) {
			if (fsAction.SYMBOL.equals(permission)) {
				return fsAction;
			}
		}
		return null;
	}

}
