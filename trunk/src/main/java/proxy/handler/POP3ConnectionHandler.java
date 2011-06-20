package proxy.handler;

import java.io.IOException;
import java.net.Socket;

import model.Message;

import org.apache.log4j.Logger;

import proxy.POP3Client;
import proxy.POP3Proxy;
import statistics.Statistics;
import filter.AccessRequestFilter;
import filter.EraseRequestFilter;
import filter.NullResponseFilter;
import filter.Request;
import filter.RequestFilter;
import filter.Response;
import filter.ResponseFilter;
import filter.SendRequestFilter;
import filter.StatisticsFilter;

public class POP3ConnectionHandler extends ConnectionHandler {

	private POP3Client POP3client;
	private RequestFilter requestFilterChain;
	private ResponseFilter responseFilterChain;
	private static Logger logger = Logger.getLogger("logger");

	public POP3ConnectionHandler(Socket socket) {
		super(socket);
		this.POP3client = new POP3Client();
	}

	private void addRequestFilter(RequestFilter filter) {
		if (requestFilterChain != null) {
			RequestFilter tmp = requestFilterChain;
			filter.setNext(tmp);
		}
		requestFilterChain = filter;
	}

	private void addResponseFilter(ResponseFilter filter) {
		if (responseFilterChain != null) {
			ResponseFilter tmp = responseFilterChain;
			filter.setNext(tmp);
		}
		responseFilterChain = filter;
	}

	public void run() {

		// Prepare filter chain
		addRequestFilter(new SendRequestFilter());
		addRequestFilter(new StatisticsFilter());
		addRequestFilter(new EraseRequestFilter());

		// Este filtro va al final, asi se ejecuta primero
		addRequestFilter(new AccessRequestFilter(this.socket));

		addResponseFilter(new NullResponseFilter());
//		addResponseFilter(new ImageTransformerFilter());
//		addResponseFilter(new TextTransformer());

		try {
			String request, response;

			writer.println("+OK Welcome");
			do {
				request = reader.readLine();
				
				
				if (request != null && request.toUpperCase().contains("CAPA")) {
					if(POP3client != null && POP3client.isConnected()) {
						response = POP3client.send(request);
						writer.println(response);
						writer.println(POP3client.getListOfMessage());
//						POP3Proxy.logger.info("[INFO]: " + "CAPA COMMAND USED");
					} else {
						//TODO ver que hacer en este caso, por ahora anda pero tira error la primera vez
//						POP3Proxy.logger.info("[INFO]: " + "CAPA NOT SUPPORTED BEFORE USER");
						writer.println("-ERR. Must use USER command first.");
					}
//					writer.println("+OK Capability list follows");
//					writer.println("USER");
//					writer.println(".");
					continue;
				}
					
				if (request != null && !request.isEmpty()) {
					Response rsp = null;
					try {
						rsp = requestFilterChain.doFilter(new Request(null,
								request), writer, POP3client);
					} catch (IllegalArgumentException e) {
						logger.info("POP3 Server disconnected. Disconnecting client...");
						disconnect();
						return;
					}

					response = rsp.getResponseString();
					writer.println(response);
					Statistics.addBytesTransfered(rsp.getUser(), (long)response.length());

					if (response != null && response.contains("+OK")) {
						if (request.toUpperCase().contains("LIST") 
								|| request.toUpperCase().contains("UIDL")
								|| request.toUpperCase().contains("TOP")) {
							String list = POP3client.getListOfMessage();
							writer.println(list);
							Statistics.addBytesTransfered(rsp.getUser(), (long)list.length());
						}
						else if (request.toUpperCase().contains("RETR")) {
							Message message = POP3client.getMessage(writer, rsp.getUser());
							responseFilterChain.doFilter(message, rsp.getUser());
						} 
					}
				}
			} while (isConnected()
					&& (request != null && !request.toUpperCase().contains(
							"QUIT")));

			if (POP3client.isConnected())
				POP3client.disconnect();
			disconnect();
		} catch (IOException e) {
			try {
				logger.info("POP3 Server disconnected. Disconnecting client...");
				disconnect();
			} catch (IOException e1) {
				logger.fatal("Error disconnecting client.");
			}
		}
	}

}
