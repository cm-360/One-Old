
package com.github.cm360.onegame.api;

import java.util.Map;

import com.google.gson.JsonObject;

public interface ApiFunction {

	public JsonObject run(Map<String, String> arguments, String callerID);

}
