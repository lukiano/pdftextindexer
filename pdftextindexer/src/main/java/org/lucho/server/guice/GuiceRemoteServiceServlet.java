package org.lucho.server.guice;

import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.lucho.server.lucene.LuceneFactory;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class GuiceRemoteServiceServlet extends RemoteServiceServlet {

	private static final long serialVersionUID = 8487678721660420350L;
	
	@Inject
	private transient Injector injector;
	
	@Inject
	private LuceneFactory luceneFactory;

	@Override
	public String processCall(String payload) throws SerializationException {
		try {
			RPCRequest req = RPC.decodeRequest(payload, null, this);

			RemoteService service = getServiceInstance(req.getMethod()
					.getDeclaringClass());

			return RPC.invokeAndEncodeResponse(service, req.getMethod(), req
					.getParameters(), req.getSerializationPolicy());
		} catch (IncompatibleRemoteServiceException ex) {
			log(
					"IncompatibleRemoteServiceException in the processCall(String) method.",
					ex);
			return RPC.encodeResponseForFailure(null, ex);
		}
	}

	@SuppressWarnings( { "unchecked" })
	private RemoteService getServiceInstance(Class serviceClass) {
		return (RemoteService) injector.getInstance(serviceClass);
	}
	
	public void destroy() {
		try {
			luceneFactory.close();
		} catch (IOException e) {
			LogFactory.getLog(this.getClass()).error("Couldn't close lucene.", e);
		}
		super.destroy();
	}
}
