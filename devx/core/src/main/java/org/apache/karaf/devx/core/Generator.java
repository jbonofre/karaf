package org.apache.karaf.devx.core;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Jar;

public class Generator {

    public void generate() throws Exception {
        Builder builder = new Builder();
        // generate bundle jar including KARAF-INF/feature.json
        // map dependencies to feature via a DevX feature registry
        // other deps are private
        Jar jar = builder.build();
    }

}
