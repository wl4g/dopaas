package com.wl4g.devops.esm.console;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.esm.console.args.ScalingArgument;
import com.wl4g.devops.esm.service.EsmScalingService;
import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;

/**
 * ESM console controller.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月2日
 * @since
 */
@ShellComponent
public class EsmScalingConsole {

	final public static String GROUP = "Esm operation commands.";

	@Autowired
	protected EsmScalingService scalingService;

	@ShellMethod(keys = "scaler", group = GROUP, help = "Manual execution of scaling scheduling control.")
	public String deploy(ScalingArgument arg) {
		// TODO
		//

		return null;
	}

}
