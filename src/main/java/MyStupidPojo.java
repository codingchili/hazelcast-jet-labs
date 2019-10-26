import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Robin Duda
 */
public class MyStupidPojo implements Serializable {
    public String ref = "$/root/cases/stuff/";
    public String name = "wowza";
    public Set<String> objs = new HashSet<>();
    {
        objs.add("one");
        objs.add("two");
        objs.add("three");
    }

    public MyStupidPojo(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
