package com.kruiper.timon.v6informatica.callbacks;

import com.kruiper.timon.v6informatica.objects.Auto;

/**
 * Created by timon on 14-3-2016.
 */
public interface GetAutoCallback {
	void done(Auto auto);
	void donemsg(String string);
}
