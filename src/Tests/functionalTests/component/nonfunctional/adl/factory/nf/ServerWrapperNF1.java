package functionalTests.component.nonfunctional.adl.factory.nf;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public class ServerWrapperNF1 implements BindingController, NFService, NFServiceEnd {

    final String CLIENT_ITF = "ref";
    NFService next = null;
    final String[] itfList = new String[] { CLIENT_ITF };
    final String name = "[server-wrapper-nf1]";

    @Override
    public StringWrapper walk() {

        StringWrapper ret;
        ret = new StringWrapper(name + next.walk());
        return ret;
    }

    @Override
    public void print(String msg) {

        next.print(msg + name);

    }

    @Override
    public StringWrapper walkEnd() {
        return new StringWrapper(name);
    }

    @Override
    public void printEnd(String msg) {
        String finalMsg = msg + name;
        System.out.println("Final message received: " + finalMsg);
    }

    @Override
    public void bindFc(String clientItf, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if (CLIENT_ITF.equals(clientItf)) {
            next = (NFService) serverItf;
        } else
            throw new NoSuchInterfaceException(clientItf);

    }

    @Override
    public String[] listFc() {
        return itfList;
    }

    @Override
    public Object lookupFc(String clientItf) throws NoSuchInterfaceException {
        if (CLIENT_ITF.equals(clientItf)) {
            return next;
        }
        throw new NoSuchInterfaceException(clientItf);
    }

    @Override
    public void unbindFc(String clientItf) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if (CLIENT_ITF.equals(clientItf)) {
            next = null;
        }
        throw new NoSuchInterfaceException(clientItf);
    }

}