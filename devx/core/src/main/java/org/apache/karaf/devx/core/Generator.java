package org.apache.karaf.devx.core;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Jar;

public class Generator {

    public void generate() throws Exception {
        Builder builder = new Builder();

        Jar jar = builder.build();
    }

}
