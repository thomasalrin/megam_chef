/* 
 ** Copyright [2012-2013] [Megam Systems]
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 ** http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */
package org.megam.chef.cloudformatters;

import static org.megam.chef.parser.ComputeInfo.FLAVOR;
import static org.megam.chef.parser.ComputeInfo.GROUPS;
import static org.megam.chef.parser.ComputeInfo.IDENTITYFILE;
import static org.megam.chef.parser.ComputeInfo.IMAGE;
import static org.megam.chef.parser.ComputeInfo.SSHPUBLOCATION;
import static org.megam.chef.parser.ComputeInfo.SSHUSER;
import static org.megam.chef.parser.ComputeInfo.ZONE;

import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleCloudFormatter implements OutputCloudFormatter {

	private final Map<String, String> gceMap_key = new HashMap<String, String>();
	private Map<String, String> inputArgs;
	private List<String> unsatifiedReason;
	private String cc = "";
	private String email = "";
	private String bucket = "";
	private String req_type = "";

	public GoogleCloudFormatter(Map<String, String> tempArgs, String req_type) {
		this.req_type = req_type;
		this.inputArgs = tempArgs;
		this.gceMap_key.put(GROUPS, "-n");
		this.gceMap_key.put(IMAGE, "-I");
		this.gceMap_key.put(FLAVOR, "-m");
		this.gceMap_key.put(SSHUSER, "-x");
		if (!req_type.equals("delete")) {
			this.gceMap_key.put(IDENTITYFILE, "--identity-file");
		}
		this.gceMap_key.put(ZONE, "-Z");
	}

	private String getZone() {
		return inputArgs.get(ZONE);
	}

	private String getImage() {
		return inputArgs.get(IMAGE);
	}

	private String getGroup() {
		return inputArgs.get(GROUPS);
	}

	private String getFlavor() {
		return inputArgs.get(FLAVOR);
	}

	private String getSshUser() {
		return inputArgs.get(SSHUSER);
	}

	private String getIdentityFile() {
		if (req_type.equals("delete")) {
			return "";
		} else {
			return parserwithoutBucket(inputArgs.get(SSHPUBLOCATION)) + ".key";
		}
	}

	private boolean notNull(String str) {
		if (inputArgs.containsKey(str)) {
			return true;
		} else {
			unsatifiedReason.add(str + " is Missing");
		}
		return false;
	}

	public String parserwithoutBucket(String str) {
		if (str.length() > 0) {
			int lst = str.lastIndexOf("/");
			cc = str.substring(lst);
			str = str.replace(str.substring(lst), "");
			email = str.substring(str.lastIndexOf("/"));
			str = str.replace(str.substring(str.lastIndexOf("/")), "");
			bucket = str.substring(str.lastIndexOf("/") + 1);
			return bucket + email + cc;
		} else {
			return str;
		}
	}

	@Override
	public Map<String, String> format() {
		Map<String, String> gceMap_result = new HashMap<String, String>();

		for (Map.Entry<String, String> entry : inputArgs.entrySet()) {
			if (gceMap_key.containsKey(entry.getKey())) {
				if (entry.getKey().equals(IDENTITYFILE)) {
					gceMap_result.put(gceMap_key.get(entry.getKey()),
							getIdentityFile());
				} else {
					gceMap_result.put(gceMap_key.get(entry.getKey()),
							entry.getValue());
				}
			}
		}
		return gceMap_result;
	}

	public boolean ok() {
		boolean isOk = true;
		isOk = isOk && validate(ZONE, getZone());
		isOk = isOk && validate(IMAGE, getImage());
		isOk = isOk && validate(FLAVOR, getFlavor());
		isOk = isOk && validate(SSHUSER, getSshUser());
		isOk = isOk && validate(GROUPS, getGroup());
		// isOk = isOk && validate(IDENTITYFILE, getIdentityFile());
		return isOk;
	}

	public boolean validate(String key, String value) {
		for (Map.Entry<String, String> entry : inputArgs.entrySet()) {
			if (entry.getKey().equals(key)) {
				if (entry.getValue().equals(value)) {
					return true;
				} else {
					unsatifiedReason.add(key + " is not valid ");
				}
			}
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.megam.chef.core.Condition#inputAvailable()
	 */
	public boolean inputAvailable() {
		boolean isAvailable = true;
		isAvailable = isAvailable && notNull(ZONE);
		isAvailable = isAvailable && notNull(GROUPS);
		isAvailable = isAvailable && notNull(IMAGE);
		isAvailable = isAvailable && notNull(FLAVOR);
		isAvailable = isAvailable && notNull(SSHUSER);
		isAvailable = isAvailable && notNull(IDENTITYFILE);
		return isAvailable;
	}

	public String name() {
		return "gcf:";
	}

	public List<String> getReason() {
		return unsatifiedReason;
	}

	public String toString() {
		StringBuilder strbd = new StringBuilder();
		final Formatter formatter = new Formatter(strbd);
		for (Map.Entry<String, String> entry : inputArgs.entrySet()) {
			formatter.format("%10s = %s%n", entry.getKey(), entry.getValue());
		}
		formatter.close();
		return strbd.toString();
	}

}