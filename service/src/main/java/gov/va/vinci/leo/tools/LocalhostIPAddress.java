package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Service
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalhostIPAddress {
	
	public static final String IPADDRESS = LocalhostIPAddress.getIp();
	/**
     * Logger for this class.
     */
    public static final Logger LOGGER = Logger.getLogger(LocalhostIPAddress.class.getCanonicalName());
    
	private static String getIp() {
		String address = null;
		InetAddress localhost;
		InetAddress hostInet;
	
		try {
			hostInet = InetAddress.getByName(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {	
			LOGGER.error("Could not get local address from hostname", e);
			return address;
		}
		
		try {
			localhost = InetAddress.getByAddress(InetAddress.getLocalHost().getAddress());
		} catch (UnknownHostException e) {	
			LOGGER.error("Could not get host name from local address", e);
			return address;
		}
		
		if (hostInet.equals(localhost)) {
			address =  localhost.getHostAddress();
		}
		return address;
		
	}

}
