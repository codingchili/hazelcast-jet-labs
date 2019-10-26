/**
 * @author Robin Duda
 */
public class TestPluginSave implements ProcessPlugin<ProcessContextImpl, MyStupidPojo> {

    @Override
    public MyStupidPojo process(ProcessContextImpl context, MyStupidPojo item) {
        context.save(item);
        return item;
    }
}
