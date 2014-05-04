package to.joe.strangeweapons.meta;

import java.util.List;
import java.util.Map;

import to.joe.strangeweapons.Statistic;

public class StrangeWeapon {

    private String quality;
    private String customName;
    private List<String> description;
    private Map<Statistic, Number> parts;

    /*
     * An optional custom name. Granted by a nametag item, one line max.
     * An optional custom description. Granted by a description tag item, 3 lines max.
     * A required quality.
     * An optional linked map of parts and values.
     */

}