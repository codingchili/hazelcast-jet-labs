/**
 * @author Robin Duda
 */
public class TestPluginTransform implements ProcessPlugin<ProcessContextImpl, MyStupidPojo> {

    @Override
    public MyStupidPojo process(ProcessContextImpl context, MyStupidPojo item) {
        return context.transform(item);
    }
}
