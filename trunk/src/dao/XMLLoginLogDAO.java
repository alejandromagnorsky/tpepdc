package dao;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import model.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import dao.login.ObjectFactory;
import dao.login.XMLLoginLog;
import dao.login.XMLUserLogin;

public class XMLLoginLogDAO extends XMLAbstractDAO<XMLLoginLog> {

	private static XMLLoginLogDAO instance = null;

	public static XMLLoginLogDAO getInstance() {
		if (instance == null)
			instance = new XMLLoginLogDAO();
		return instance;
	}

	private XMLLoginLogDAO() {
		super("logins.xml", "src/loginLog.xsd");
		load();
	}

	@Override
	protected XMLLoginLog createRoot() {
		ObjectFactory objFact = new ObjectFactory();
		return objFact.createXMLLoginLog();
	}

	public XMLUserLogin getLoginByUser(User user) {
		for (XMLUserLogin login : rootElement.getUserLoginList())
			if (login.getUser().equals(user.getName()))
				return login;

		return null;
	}

	private LocalDate convertToLocalDate(XMLGregorianCalendar calendar) {
		if (calendar == null)
			return null;
		return new DateTime(calendar.toGregorianCalendar().getTimeInMillis())
				.toLocalDate();
	}

	public int getUserLogins(User user, LocalDate date) {
		XMLUserLogin login = getLoginByUser(user);

		if (login != null && user != null) {
			LocalDate lastDate = convertToLocalDate(login.getDate());
			int quantity = login.getQuantity();

			// If date doesn't exist, return 0 (this can be viewed as an auto
			// reset)
			if (!lastDate.equals(date)) {
				return 0;
			} else
				return quantity;
		}
		return 0;
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

	public void saveLogin(User user, LocalDate date, int quantity) {
		XMLUserLogin login = getLoginByUser(user);
		ObjectFactory objFact = new ObjectFactory();

		if (login == null) {
			login = objFact.createXMLUserLogin();
			login.setUser(user.getName());
			rootElement.getUserLoginList().add(login);
		}

		login.setDate(convertToXMLGregorianCalendar(date));
		login.setQuantity(quantity);
	}
}
