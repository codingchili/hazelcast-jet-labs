/**
 * @author Robin Duda
 *
 * wrapper for client services
 * should contain authentication info per process etc.
 */
public class ProcessContextImpl implements ProcessContext {

    public MyStupidPojo transform(MyStupidPojo pojo) {
        pojo.name = pojo.name + "_TRANSFORMED";
        System.out.println("TRANSFORMED");
        return pojo;
    }

    public void save(MyStupidPojo pojo) {
        System.out.println("saved pojo " + pojo.toString());
    }

}
