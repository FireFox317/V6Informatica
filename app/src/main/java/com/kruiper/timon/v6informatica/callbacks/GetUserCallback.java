package com.kruiper.timon.v6informatica.callbacks;

import com.kruiper.timon.v6informatica.objects.Gebruiker;

/**
 * Created by timon on 14-3-2016.
 */
public interface GetUserCallback {
	void done(Gebruiker gebruiker);
	void donemsg(String string);
}
