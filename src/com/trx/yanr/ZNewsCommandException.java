package com.trx.yanr;

import java.io.IOException;

public class ZNewsCommandException extends IOException {
	/**
	 * Constructs a <code>ZNewsCommandException</code> with no detail message.
	 */
	public ZNewsCommandException() {
		super();
	}

	/**
	 * Constructs a <code>ZNewsCommandException</code> with the specified detail message.
	 *
	 * @param s  The detail message.
	 */
	public ZNewsCommandException(String s) {
		super(s);
	}
}  