package org.faolrd.net;

import java.util.Objects;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public class Proxy {
	public static final int HTTP = 1;
	public static final int HTTPS = 2;
	public static final int SOCKS = 3;
	private String ip;
	private int port;
	private int type;

	public Proxy() {}

	public Proxy(String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.type = Proxy.HTTP;
	}

	public Proxy(String ip, int port, int type) {
		this.ip = ip;
		this.port = port;
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the ip
	 */
	public String getIP() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIP(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.ip);
		hash = 13 * hash + this.port;
		hash = 13 * hash + this.type;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj != null && obj instanceof Proxy )
			if ( this.ip.equals(((Proxy)obj).getIP()) && this.port == ((Proxy)obj).getPort() && this.type == ((Proxy)obj).getType() )
				return true;

		return false;
	}

	@Override
	public String toString() {
		return this.ip + ":" + this.port;
	}
}