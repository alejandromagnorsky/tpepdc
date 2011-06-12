package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import model.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import dao.login.ObjectFactory;
import dao.login.XMLLogin;
import dao.login.XMLLoginLog;
import dao.login.XMLUserLoginList;

public class XMLLoginLogDAO extends XMLAbstractDAO<XMLLoginLog> {

	public XMLLoginLogDAO(String dataFilename, String schemaFilename) {
		super(dataFilename, schemaFilename);
	}

	@Override
	protected XMLLoginLog createRoot() {
		ObjectFactory objFact = new ObjectFactory();
		return objFact.createXMLLoginLog();
	}

	public XMLUserLoginList getListByUser(User user) {
		for (XMLUserLoginList list : rootElement.getUserLoginList())
			if (list.getUser().equals(user.getName()))
				return list;
		return null;
	}

	private LocalDate convertToDateTime(XMLGregorianCalendar calendar) {
		if (calendar == null)
			return null;
		return new DateTime(calendar.toGregorianCalendar().getTimeInMillis())
				.toLocalDate();
	}

	public Map<LocalDate, Integer> getUserLogins(User user) {
		Map<LocalDate, Integer> out = new HashMap<LocalDate, Integer>();

		XMLUserLoginList list = getListByUser(user);

		for (XMLLogin login : list.getLogin())
			out.put(convertToDateTime(login.getDate()), login.getQuantity());

		return out;
	}

	private XMLGregorianCalendar convertToXMLGregorianCalendar(LocalDate date) {
		if (date == null)
			return null;
		DatatypeFactory dtf;
		try {
			dtf = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			return null;
		}
		XMLGregorianCalendar xgc = dtf.newXMLGregorianCalendar(date
				.toDateTimeAtStartOfDay().toGregorianCalendar());
		return xgc;
	}

	// O(n), I'm sorry
	private XMLLogin getLoginByDate(List<XMLLogin> list, LocalDate date) {
		for (XMLLogin login : list)
			if (login.getDate().equals(convertToXMLGregorianCalendar(date)))
				return login;
		return null;
	}

	public void addLoginLog(User user, LocalDate date) {
		XMLUserLoginList list = getListByUser(user);
		XMLLogin login = null;
		ObjectFactory objFact = new ObjectFactory();

		// Update
		if (list != null) {
			login = getLoginByDate(list.getLogin(), date);

			// If login entry doesn't exist, create it
			if (login == null) {
				login = objFact.createXMLLogin();
				list.getLogin().add(login);
				login.setDate(convertToXMLGregorianCalendar(date));
			}
		} else {
			// Create entire user list
			list = objFact.createXMLUserLoginList();
			list.setUser(user.getName());
			login = objFact.createXMLLogin();
			login.setDate(convertToXMLGregorianCalendar(date));

			// getLogin is a list, sorry for names...
			list.getLogin().add(login);
			rootElement.getUserLoginList().add(list);
		}

		// Now modify values
		int qty = login.getQuantity();
		login.setQuantity(qty + 1);
	}
}
