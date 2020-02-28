package com.wl4g.devops.coss.fs;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

/**
 * A class for file/directory permissions.
 */
public class FsPermission implements Writable {
	final protected static Logger log = getLogger(FsPermission.class);

	static final WritableFactory FACTORY = new WritableFactory() {
		@Override
		public Writable newInstance() {
			return new FsPermission();
		}
	};
	static { // register a ctor
		WritableFactories.setFactory(FsPermission.class, FACTORY);
		WritableFactories.setFactory(ImmutableFsPermission.class, FACTORY);
	}

	/** Maximum acceptable length of a permission string to parse */
	public static final int MAX_PERMISSION_LENGTH = 10;

	/** Create an immutable {@link FsPermission} object. */
	public static FsPermission createImmutable(short permission) {
		return new ImmutableFsPermission(permission);
	}

	// POSIX permission style
	private FsAction useraction;
	private FsAction groupaction;
	private FsAction otheraction;
	private boolean stickyBit = false;

	private FsPermission() {
	}

	/**
	 * Construct by the given {@link FsAction}.
	 * 
	 * @param u
	 *            user action
	 * @param g
	 *            group action
	 * @param o
	 *            other action
	 */
	public FsPermission(FsAction u, FsAction g, FsAction o) {
		this(u, g, o, false);
	}

	public FsPermission(FsAction u, FsAction g, FsAction o, boolean sb) {
		set(u, g, o, sb);
	}

	/**
	 * Construct by the given mode.
	 * 
	 * @param mode
	 * @see #toShort()
	 */
	public FsPermission(short mode) {
		fromShort(mode);
	}

	/**
	 * Copy constructor
	 * 
	 * @param other
	 *            other permission
	 */
	public FsPermission(FsPermission other) {
		this.useraction = other.useraction;
		this.groupaction = other.groupaction;
		this.otheraction = other.otheraction;
		this.stickyBit = other.stickyBit;
	}

	/**
	 * Construct by given mode, either in octal or symbolic format.
	 * 
	 * @param mode
	 *            mode as a string, either in octal or symbolic format
	 * @throws IllegalArgumentException
	 *             if <code>mode</code> is invalid
	 */
	public FsPermission(String mode) {
		this(new UmaskParser(mode).getUMask());
	}

	/** Return user {@link FsAction}. */
	public FsAction getUserAction() {
		return useraction;
	}

	/** Return group {@link FsAction}. */
	public FsAction getGroupAction() {
		return groupaction;
	}

	/** Return other {@link FsAction}. */
	public FsAction getOtherAction() {
		return otheraction;
	}

	private void set(FsAction u, FsAction g, FsAction o, boolean sb) {
		useraction = u;
		groupaction = g;
		otheraction = o;
		stickyBit = sb;
	}

	public void fromShort(short n) {
		FsAction[] v = FSACTION_VALUES;
		set(v[(n >>> 6) & 7], v[(n >>> 3) & 7], v[n & 7], (((n >>> 9) & 1) == 1));
	}

	/**
	 * Create and initialize a {@link FsPermission} from {@link DataInput}.
	 */
	public static FsPermission read(DataInput in) throws IOException {
		FsPermission p = new FsPermission();
		p.readFields(in);
		return p;
	}

	/**
	 * Encode the object to a short.
	 */
	public short toShort() {
		int s = (stickyBit ? 1 << 9 : 0) | (useraction.ordinal() << 6) | (groupaction.ordinal() << 3) | otheraction.ordinal();

		return (short) s;
	}

	/**
	 * Encodes the object to a short. Unlike {@link #toShort()}, this method may
	 * return values outside the fixed range 00000 - 01777 if extended features
	 * are encoded into this permission, such as the ACL bit.
	 *
	 * @return short extended short representation of this permission
	 */
	public short toExtendedShort() {
		return toShort();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FsPermission) {
			FsPermission that = (FsPermission) obj;
			return this.useraction == that.useraction && this.groupaction == that.groupaction
					&& this.otheraction == that.otheraction && this.stickyBit == that.stickyBit;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toShort();
	}

	@Override
	public String toString() {
		String str = useraction.SYMBOL + groupaction.SYMBOL + otheraction.SYMBOL;
		if (stickyBit) {
			StringBuilder str2 = new StringBuilder(str);
			str2.replace(str2.length() - 1, str2.length(), otheraction.implies(FsAction.EXECUTE) ? "t" : "T");
			str = str2.toString();
		}

		return str;
	}

	/**
	 * Apply a umask to this permission and return a new one.
	 *
	 * The umask is used by create, mkdir, and other Hadoop filesystem
	 * operations. The mode argument for these operations is modified by
	 * removing the bits which are set in the umask. Thus, the umask limits the
	 * permissions which newly created files and directories get.
	 *
	 * @param umask
	 *            The umask to use
	 * 
	 * @return The effective permission
	 */
	public FsPermission applyUMask(FsPermission umask) {
		return new FsPermission(useraction.and(umask.useraction.not()), groupaction.and(umask.groupaction.not()),
				otheraction.and(umask.otheraction.not()));
	}

	/**
	 * umask property label deprecated key and code in getUMask method to
	 * accommodate it may be removed in version .23
	 */
	public static final String DEPRECATED_UMASK_LABEL = "dfs.umask";
	public static final String UMASK_LABEL = CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY;
	public static final int DEFAULT_UMASK = CommonConfigurationKeys.FS_PERMISSIONS_UMASK_DEFAULT;
	private static final FsAction[] FSACTION_VALUES = FsAction.values();

	public boolean getStickyBit() {
		return stickyBit;
	}

	/**
	 * Returns true if there is also an ACL (access control list).
	 *
	 * @return boolean true if there is also an ACL (access control list).
	 */
	public boolean getAclBit() {
		// File system subclasses that support the ACL bit would override this.
		return false;
	}

	/**
	 * Returns true if the file is encrypted or directory is in an encryption
	 * zone
	 */
	public boolean getEncryptedBit() {
		return false;
	}

	/**
	 * Get the user file creation mask (umask)
	 * 
	 * {@code UMASK_LABEL} config param has umask value that is either symbolic
	 * or octal.
	 * 
	 * Symbolic umask is applied relative to file mode creation mask; the
	 * permission op characters '+' clears the corresponding bit in the mask,
	 * '-' sets bits in the mask.
	 * 
	 * Octal umask, the specified bits are set in the file mode creation mask.
	 * 
	 * {@code DEPRECATED_UMASK_LABEL} config param has umask value set to
	 * decimal.
	 */
	public static FsPermission getUMask(Configuration conf) {
		int umask = DEFAULT_UMASK;

		// To ensure backward compatibility first use the deprecated key.
		// If the deprecated key is not present then check for the new key
		if (conf != null) {
			String confUmask = conf.get(UMASK_LABEL);
			int oldUmask = conf.getInt(DEPRECATED_UMASK_LABEL, Integer.MIN_VALUE);
			try {
				if (confUmask != null) {
					umask = new UmaskParser(confUmask).getUMask();
				}
			} catch (IllegalArgumentException iae) {
				// Provide more explanation for user-facing message
				String type = iae instanceof NumberFormatException ? "decimal" : "octal or symbolic";
				String error = "Unable to parse configuration " + UMASK_LABEL + " with value " + confUmask + " as " + type
						+ " umask.";
				log.warn(error);

				// If oldUmask is not set, then throw the exception
				if (oldUmask == Integer.MIN_VALUE) {
					throw new IllegalArgumentException(error);
				}
			}

			// Property was set with old key
			if (oldUmask != Integer.MIN_VALUE) {
				if (umask != oldUmask) {
					log.warn(DEPRECATED_UMASK_LABEL + " configuration key is deprecated. " + "Convert to " + UMASK_LABEL
							+ ", using octal or symbolic umask " + "specifications.");
					// Old and new umask values do not match - Use old umask
					umask = oldUmask;
				}
			}
		}

		return new FsPermission((short) umask);
	}

	/** Set the user file creation mask (umask) */
	public static void setUMask(Configuration conf, FsPermission umask) {
		conf.set(UMASK_LABEL, String.format("%1$03o", umask.toShort()));
		conf.setInt(DEPRECATED_UMASK_LABEL, umask.toShort());
	}

	/**
	 * Get the default permission for directory and symlink. In previous
	 * versions, this default permission was also used to create files, so files
	 * created end up with ugo+x permission. See HADOOP-9155 for detail. Two new
	 * methods are added to solve this, please use
	 * {@link FsPermission#getDirDefault()} for directory, and use
	 * {@link FsPermission#getFileDefault()} for file. This method is kept for
	 * compatibility.
	 */
	public static FsPermission getDefault() {
		return new FsPermission((short) 00777);
	}

	/**
	 * Get the default permission for directory.
	 */
	public static FsPermission getDirDefault() {
		return new FsPermission((short) 00777);
	}

	/**
	 * Get the default permission for file.
	 */
	public static FsPermission getFileDefault() {
		return new FsPermission((short) 00666);
	}

	/**
	 * Get the default permission for cache pools.
	 */
	public static FsPermission getCachePoolDefault() {
		return new FsPermission((short) 00755);
	}

	/**
	 * Create a FsPermission from a Unix symbolic permission string
	 * 
	 * @param unixSymbolicPermission
	 *            e.g. "-rw-rw-rw-"
	 */
	public static FsPermission valueOf(String unixSymbolicPermission) {
		if (unixSymbolicPermission == null) {
			return null;
		} else if (unixSymbolicPermission.length() != MAX_PERMISSION_LENGTH) {
			throw new IllegalArgumentException(
					String.format("length != %d(unixSymbolicPermission=%s)", MAX_PERMISSION_LENGTH, unixSymbolicPermission));
		}

		int n = 0;
		for (int i = 1; i < unixSymbolicPermission.length(); i++) {
			n = n << 1;
			char c = unixSymbolicPermission.charAt(i);
			n += (c == '-' || c == 'T' || c == 'S') ? 0 : 1;
		}

		// Add sticky bit value if set
		if (unixSymbolicPermission.charAt(9) == 't' || unixSymbolicPermission.charAt(9) == 'T')
			n += 01000;

		return new FsPermission((short) n);
	}

	class PermissionParser {
		protected boolean symbolic = false;
		protected short userMode;
		protected short groupMode;
		protected short othersMode;
		protected short stickyMode;
		protected char userType = '+';
		protected char groupType = '+';
		protected char othersType = '+';
		protected char stickyBitType = '+';

		/**
		 * Begin parsing permission stored in modeStr
		 * 
		 * @param modeStr
		 *            Permission mode, either octal or symbolic
		 * @param symbolic
		 *            Use-case specific symbolic pattern to match against
		 * @throws IllegalArgumentException
		 *             if unable to parse modeStr
		 */
		public PermissionParser(String modeStr, Pattern symbolic, Pattern octal) throws IllegalArgumentException {
			Matcher matcher = null;

			if ((matcher = symbolic.matcher(modeStr)).find()) {
				applyNormalPattern(modeStr, matcher);
			} else if ((matcher = octal.matcher(modeStr)).matches()) {
				applyOctalPattern(modeStr, matcher);
			} else {
				throw new IllegalArgumentException(modeStr);
			}
		}

		private void applyNormalPattern(String modeStr, Matcher matcher) {
			// Are there multiple permissions stored in one chmod?
			boolean commaSeperated = false;

			for (int i = 0; i < 1 || matcher.end() < modeStr.length(); i++) {
				if (i > 0 && (!commaSeperated || !matcher.find())) {
					throw new IllegalArgumentException(modeStr);
				}

				/*
				 * groups : 1 : [ugoa]* 2 : [+-=] 3 : [rwxXt]+ 4 : [,\s]*
				 */

				String str = matcher.group(2);
				char type = str.charAt(str.length() - 1);

				boolean user, group, others, stickyBit;
				user = group = others = stickyBit = false;

				for (char c : matcher.group(1).toCharArray()) {
					switch (c) {
					case 'u':
						user = true;
						break;
					case 'g':
						group = true;
						break;
					case 'o':
						others = true;
						break;
					case 'a':
						break;
					default:
						throw new RuntimeException("Unexpected");
					}
				}

				if (!(user || group || others)) { // same as specifying 'a'
					user = group = others = true;
				}

				short mode = 0;

				for (char c : matcher.group(3).toCharArray()) {
					switch (c) {
					case 'r':
						mode |= 4;
						break;
					case 'w':
						mode |= 2;
						break;
					case 'x':
						mode |= 1;
						break;
					case 'X':
						mode |= 8;
						break;
					case 't':
						stickyBit = true;
						break;
					default:
						throw new RuntimeException("Unexpected");
					}
				}

				if (user) {
					userMode = mode;
					userType = type;
				}

				if (group) {
					groupMode = mode;
					groupType = type;
				}

				if (others) {
					othersMode = mode;
					othersType = type;

					stickyMode = (short) (stickyBit ? 1 : 0);
					stickyBitType = type;
				}

				commaSeperated = matcher.group(4).contains(",");
			}
			symbolic = true;
		}

		private void applyOctalPattern(String modeStr, Matcher matcher) {
			userType = groupType = othersType = '=';

			// Check if sticky bit is specified
			String sb = matcher.group(1);
			if (!sb.isEmpty()) {
				stickyMode = Short.valueOf(sb.substring(0, 1));
				stickyBitType = '=';
			}

			String str = matcher.group(2);
			userMode = Short.valueOf(str.substring(0, 1));
			groupMode = Short.valueOf(str.substring(1, 2));
			othersMode = Short.valueOf(str.substring(2, 3));
		}

		protected int combineModes(int existing, boolean exeOk) {
			return combineModeSegments(stickyBitType, stickyMode, (existing >>> 9), false) << 9
					| combineModeSegments(userType, userMode, (existing >>> 6) & 7, exeOk) << 6
					| combineModeSegments(groupType, groupMode, (existing >>> 3) & 7, exeOk) << 3
					| combineModeSegments(othersType, othersMode, existing & 7, exeOk);
		}

		protected int combineModeSegments(char type, int mode, int existing, boolean exeOk) {
			boolean capX = false;

			if ((mode & 8) != 0) { // convert X to x;
				capX = true;
				mode &= ~8;
				mode |= 1;
			}

			switch (type) {
			case '+':
				mode = mode | existing;
				break;
			case '-':
				mode = (~mode) & existing;
				break;
			case '=':
				break;
			default:
				throw new RuntimeException("Unexpected");
			}

			// if X is specified add 'x' only if exeOk or x was already set.
			if (capX && !exeOk && (mode & 1) != 0 && (existing & 1) == 0) {
				mode &= ~1; // remove x
			}

			return mode;
		}
	}

	class UmaskParser extends PermissionParser {
		// no leading 1 for sticky bit
		private static Pattern chmodOctalPattern = Pattern.compile("^\\s*[+]?()([0-7]{3})\\s*$");
		/* not allow X or t */
		private static Pattern umaskSymbolicPattern = Pattern.compile("\\G\\s*([ugoa]*)([+=-]+)([rwx]*)([,\\s]*)\\s*");
		final short umaskMode;

		public UmaskParser(String modeStr) throws IllegalArgumentException {
			super(modeStr, umaskSymbolicPattern, chmodOctalPattern);
			umaskMode = (short) combineModes(0, false);
		}

		/**
		 * To be used for file/directory creation only. Symbolic umask is
		 * applied relative to file mode creation mask; the permission op
		 * characters '+' results in clearing the corresponding bit in the mask,
		 * '-' results in bits for indicated permission to be set in the mask.
		 * 
		 * For octal umask, the specified bits are set in the file mode creation
		 * mask.
		 * 
		 * @return umask
		 */
		public short getUMask() {
			if (symbolic) {
				// Return the complement of octal equivalent of umask that was
				// computed
				return (short) (~umaskMode & 0777);
			}
			return umaskMode;
		}
	}

}
